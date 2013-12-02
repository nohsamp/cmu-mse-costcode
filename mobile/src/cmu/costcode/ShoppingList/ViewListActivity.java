package cmu.costcode.ShoppingList;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cmu.costcode.R;
import cmu.costcode.ProximityAlert.NotificationActivity;
import cmu.costcode.ProximityAlert.ProximityIntentReceiver;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.ShoppingList.objects.Customer;
import cmu.costcode.ShoppingList.objects.ShoppingListItem;
import cmu.costcode.Triangulation.TriangulationTask;
import cmu.costcode.simplifiedcheckout.nfc.CustomerNFC;
import cmu.costcode.simplifiedcheckout.qr.ScanditScanActivity;
import cmu.costcode.simplifiedcheckout.web.CustomerWeb;
import cmu.costcode.simplifiedcheckout.web.HttpJsonClient;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

public class ViewListActivity extends Activity  {
	private final static String TAG = "ViewListActivity";

	private DatabaseAdaptor db;
	private Customer cust;
	
	private static String triangulationStart;
	private static String triangulationStop;
	boolean isTaskStop = true; // flag for task start/stop
	
	final static String SERVER_URL = "http://cmu-mse-costco.herokuapp.com"; // "http://128.237.231.17:5000";
	private final String API_GET_PRODUCT = "/costco/api/product/";
	
	private ProximityIntentReceiver pReceiver = null;
	private TriangulationTask tTask = null;
	
	private ScrollView scroll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the text view as the activity layout
		setContentView(R.layout.activity_view_list);
		
		// Open database
		db = new DatabaseAdaptor(this);
		db.open();

		// Get the message from the intent
		Intent intent = getIntent();
		int memberId = intent.getIntExtra(LoginActivity.MEMBERID, 1);
		createDummyInfo();		// Create dummy user and product information

		// Load Customer and shoppingList from DB
		cust = db.dbGetCustomer(memberId);
		cust.setShoppingList(db.dbGetShoppingList(memberId));

		// Add list of ShoppingListItems
		scroll = (ScrollView)findViewById(R.id.viewListScroll);
		LinearLayout itemList = generateListView(this, cust.getShoppingList());
		scroll.addView(itemList);
		triangulationStart = getString(R.string.triangulation_start);
		triangulationStop = getString(R.string.triangulation_stop);
		
		// Prepare NFC for sending shopping list to the cashier
		CustomerNFC customer = new CustomerNFC(ViewListActivity.this, this, db.dbGetItemList(cust.getMemberId()));
	}

	@Override
	public void onResume() {
		super.onResume();
		if(tTask != null && !tTask.isBgrunFlag() && tTask.isCancelled() && !isTaskStop) {
			// Register Broadcaster receiver for proximity alert 
			IntentFilter proximityFilter = new IntentFilter();
			proximityFilter.addAction(ProximityIntentReceiver.PROXIMITY_ALERT);
			pReceiver = new ProximityIntentReceiver();
			registerReceiver(pReceiver, proximityFilter);

			// Create Background Triangulation Task --> asynchronous thread
			// create new since app has to reload the preference every time
			try {
				tTask = new TriangulationTask(this);
			} catch (Exception e) {
				return;
			}
			tTask.execute();
        }
		db.open();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		if(tTask != null && !tTask.isBgrunFlag() && !tTask.isCancelled()) {
			tTask.cancel(true);
			// wait till task is cancelled
			while(!tTask.isCancelled())
				;

			// Unregister Broadcaster receiver for proximity alert 
			unregisterReceiver(pReceiver);	// stop ProximityAlert
		}
		db.close();
	}


	/**
	 * Generate an Android View to display the ShoppingList
	 * 
	 * @param shoppingList
	 */
	private LinearLayout generateListView(Context ctx,
			Map<String, ShoppingList> shoppingList) {
		// Create the view that will be returned
		LinearLayout view = new LinearLayout(ctx);
		view.setOrientation(LinearLayout.VERTICAL);

		if (shoppingList.size() == 0) {
			TextView emptyRow = new TextView(ctx);
			emptyRow.setTextSize(24);
			emptyRow.setText("No items in Shopping List");
			view.addView(emptyRow);
			return view;
		}

		// Iterate through each item category
		for (String category : shoppingList.keySet()) {
			Log.i(TAG, "Iterating through category '" + category + "'.");

			if (shoppingList.get(category).getItems().size() > 0) {
				// Generate the TextView row to display the category name
				TextView catRow = new TextView(ctx);
				catRow.setTextSize(18);
				catRow.setText(category);
				view.addView(catRow);
//				catRow.setTextSize(10);
//				catRow.setText("Name\tPrice\tQty\tUPC");

				// Iterate through each item within the category
				for (final Item item : shoppingList.get(category).getItems()) {
					Log.i(TAG, "    Item: " + item.getName());
					view.addView(createItemCheckbox(ctx, item));
				}
			}
		}

		return view;
	}

	/**
	 * Create a checkbox/description row view to be added to the shopping list
	 * 
	 * @param ctx
	 * @param item
	 * @return CheckBox view
	 */
	private View createItemCheckbox(Context ctx, final Item inputItem) {
		// Get ShoppingListItem object for input "item"
		final ShoppingListItem listItem = db.dbGetShoppingListItem(inputItem);
		
		// Generate the CheckBox/text row for the item
		CheckBox checkbox = new CheckBox(ctx);
		checkbox.setText(inputItem.getName() + " -  $" + inputItem.getPrice() + ",  Qty:" + 
					inputItem.getQuantity()); // + ",\tUPC: " + inputItem.getUpc());
		if (listItem.isChecked()) {
			checkbox.setChecked(true);
			checkbox.setPaintFlags(checkbox.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
		}

		// Create a listener to change the item state when checked
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkBoxView,
					boolean isChecked) {
				Log.i(TAG, "Setting checkbox of item " + listItem.getItemId()
						+ " to " + isChecked);
				db.dbSetItemChecked(cust.getMemberId(), listItem.getItemId(),
						isChecked);
				if (isChecked) {
					checkBoxView.setPaintFlags(checkBoxView.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					checkBoxView.setPaintFlags(checkBoxView.getPaintFlags()
							& ~Paint.STRIKE_THRU_TEXT_FLAG);
				}
			}
		});

		return checkbox;
	}
	

	/**
	 * Switch to EditList activity
	 * 
	 * @param view
	 */
	public void editList(View view) {
		Intent intent = new Intent(this, EditListActivity.class);
		intent.putExtra(LoginActivity.MEMBERID, cust.getMemberId());
		startActivity(intent);
	}

	// Proximity Alert menu function
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_proximityalert, menu);
		return true;
	}
	
	 @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

//        if(tTask != null && !tTask.isBgrunFlag() && tTask.isCancelled()) {
//	        MenuItem someMenuItem = menu.findItem(R.id.menu_wifitriangulation);
//	        someMenuItem.setTitle(TRIANGULATION_START);
//        }
        return true;
    }

	// Add an item into the shopping list
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		if (item.getItemId() == R.id.menu_web) {
			// Send the list to the cashier for the server
			CustomerWeb customerWeb = new CustomerWeb(this, cust.getMemberId(), db.dbGetItemList(cust.getMemberId()));
			customerWeb.broadcastShoppingList(getCurrentFocus());
		}
		else {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Resources res = getResources();
			
			String remindMethod = prefs.getString("reminder_list", res.getStringArray(R.array.pref_reminder_list_values)[0]);
			
			if (item.getItemId() == R.id.menu_wifitriangulation) {
				// the second item in the array: GPS
				if(remindMethod.equals(res.getStringArray(R.array.pref_reminder_list_values)[1])) {
					// start another activity
					Intent intent = new Intent(this, NotificationActivity.class);
					startActivity(intent);
				}
				else if(!item.getTitle().equals(triangulationStop)) {
	
					// Register Broadcaster receiver for proximity alert 
					IntentFilter proximityFilter = new IntentFilter();
					proximityFilter.addAction(ProximityIntentReceiver.PROXIMITY_ALERT);
					pReceiver = new ProximityIntentReceiver();
					registerReceiver(pReceiver, proximityFilter);
	
					// Create Background Triangulation Task --> asynchronous thread
					// create new since app has to reload the preference every time
					try {
						tTask = new TriangulationTask(this);
					} catch (Exception e) {
						return false;
					}
					tTask.execute();
					isTaskStop = false;
	
					item.setTitle(triangulationStop);
				}
				else {
					item.setTitle(triangulationStart);
	
					// Unregister Broadcaster receiver for proximity alert 
					unregisterReceiver(pReceiver);	// stop ProximityAlert
	
					if(tTask != null) {
						tTask.cancel(true);
						isTaskStop = true;
	
					}
				}
			}
		}

		return true;
	}
	
	
	/**
	 * Send shopping list to server for processing
	 * @param view
	 */
	public void sendList(View view) {
		CustomerWeb customerWeb = new CustomerWeb(view.getContext(), cust.getMemberId(), db.dbGetItemList(cust.getMemberId()));
		customerWeb.broadcastShoppingList(getCurrentFocus());
	}

	/**
	 * Scan item using Scandit API
	 * @param view
	 */
	public void scanItem(View view) {
		Intent intent = new Intent(this, ScanditScanActivity.class);
		// Expect the activity to return results
		startActivityForResult(intent, 0);
	}
	
	@Override
	/**
	 * Post-activity with returned results
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    if(resultCode == RESULT_OK && data != null)
	    {
	        String barcode = data.getStringExtra("barcode");
	        try {
//	        	// barcode format should be "category/item description"
//		        String[] splits = barcode.split("/");
//		        // parameters: category, desc, quantity, price, upc
//		        addItem(splits[0], splits[1], 1, 10, "sampleupc");
		        
		    	// Send barcode to server to be processed
		        String requestUrl = SERVER_URL + API_GET_PRODUCT + barcode;
		        Log.i(TAG, "Getting barcode from server at: " + requestUrl);
		        sendAsyncGetRequest(requestUrl, this);
	        }
	        catch(Exception e) {
	        	Toast.makeText(this, "Scan fail: not an item." + barcode, Toast.LENGTH_SHORT).show();
	        	Log.e(TAG, "Item scan failed; Exception=" + e.toString());
	        }
		}
	    else if(resultCode == RESULT_CANCELED)
	    {
	    	Toast.makeText(this, "Scan Item Canceled", Toast.LENGTH_LONG).show();
	    }
	}
	
	/**
	 * Call an asynchronous GET request to the server to retrieve product information
	 * @param requestUrl
	 * @param ctx
	 */
	private void sendAsyncGetRequest(String requestUrl, final Context ctx) {
		new AsyncTask<String, Void, JSONObject>() {
			@Override
			protected JSONObject doInBackground(String... urls) {
				JSONObject jsonObjRecv = HttpJsonClient.sendHttpGet(urls[0]);
				return jsonObjRecv;
			}

			@Override
			protected void onPostExecute(JSONObject jsonObjRecv) {
				if(jsonObjRecv == null) {
					// Fail
					Toast.makeText(ctx, "Something broked. :( \nCheck the looogs.", Toast.LENGTH_LONG).show();
				} else {
					// Success
					//TODO: check to see if successful get; response code 201 in HttpJsonClient
					//TODO: catch exceptions
					Log.i(TAG, "Flask Server Response!: " + jsonObjRecv.toString());

					try {
						// Pull out parameters from JSON
						String name = jsonObjRecv.getString("name");
						String category = jsonObjRecv.getString("category");
						float price = (float)jsonObjRecv.getDouble("price");
						String upc = jsonObjRecv.getString("upc");
						// Create new item
						addItem(category, name, 1, price, upc);
						Toast.makeText(ctx, "Added new item ", Toast.LENGTH_LONG).show();
					} catch(JSONException e) {
						Toast.makeText(ctx, "Invalid response; did not add item. :(", Toast.LENGTH_LONG).show();
						Log.e(TAG, "Error adding item from server:" + e.toString());
					}
				}
			}
		}.execute(requestUrl);
	}
	
	private void addItem(String category, String desc, int quantity, float price, String upc) {
		// Add new item to database and ShoppingList
		long newItemId = db.dbCreateItem(desc, category, quantity, price, upc);
		db.dbCreateShoppingListItem(newItemId, cust.getMemberId(), false, 0);
		
		Item newItem = new Item(newItemId, category, desc, quantity, price, 0, desc, upc);
		Map<String, ShoppingList> newShoppingList = cust.getShoppingList();
		ShoppingList addedItem = newShoppingList.get(category);
		if(addedItem == null) {
			addedItem = new ShoppingList();
		}
		addedItem.addItem(newItem);
		newShoppingList.put(category, addedItem);
		cust.setShoppingList(newShoppingList);
		
		
		scroll.removeAllViewsInLayout();
		scroll.addView(this.generateListView(this,newShoppingList));
	}
	
	/**
     * TODO: Temp method, intended to generate a few dummy values and test user
     */
    final int DUMMY_MEMBER_ID = 1;
    private void createDummyInfo() {
    	if(db.dbGetCustomer(DUMMY_MEMBER_ID) == null) {
    		int memberId = db.dbCreateCustomer("Kevin", "Crane", "515 S Aiken Ave");
    		memberId = 1;
    		Log.d(TAG, "Created Dummy member Kevin with memberId " + memberId + ".");
    		
    		long itemId1 = db.dbCreateItem("Harry Potter, Book 3", "Books/Magazines", 1, 6.49f, "0038332164718");
    		db.dbCreateShoppingListItem(itemId1, memberId, false, 1);
    		Log.d(TAG, "Created dummy item Milk with itemId " + itemId1 + ".");

    		long itemId2 = db.dbCreateItem("Uncrustables", "Food", 1, 3.99f, "0051500048160");
    		db.dbCreateShoppingListItem(itemId2, memberId, false, 2);
    		Log.d(TAG, "Created dummy item Cheez-its with itemId " + itemId2 + ".");

    		long itemId3 = db.dbCreateItem("North Face Denali jacket", "Clothing", 1, 116.32f, "0032546198033");
    		db.dbCreateShoppingListItem(itemId3, memberId, false, 3);
    		Log.d(TAG, "Created dummy item New Jacket with itemId " + itemId3 + ".");
    	}
    }
}
