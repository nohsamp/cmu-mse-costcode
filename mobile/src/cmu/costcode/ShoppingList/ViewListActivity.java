package cmu.costcode.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cmu.costcode.ShoppingList.R;
import cmu.costcode.ProximityAlert.NotificationActivity;
import cmu.costcode.ProximityAlert.ProximityIntentReceiver;
import cmu.costcode.ProximityAlert.ShoppingListApplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.widget.ToggleButton;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.ShoppingList.objects.Category.Location;
import cmu.costcode.ShoppingList.objects.Customer;
import cmu.costcode.ShoppingList.objects.Item;
import cmu.costcode.ShoppingList.objects.ShoppingListItem;
import cmu.costcode.Triangulation.TriangulationTask;
import cmu.costcode.simplifiedcheckout.nfc.CustomerNFC;
import cmu.costcode.simplifiedcheckout.qr.ScanditScanActivity;
import cmu.costcode.simplifiedcheckout.web.CustomerWeb;

public class ViewListActivity extends Activity  {
	private final static String TAG = "ViewListActivity";

	private DatabaseAdaptor db;
	private Customer cust;
	
	private static String TRIANGULATION_START;
	private static String TRIANGULATION_STOP;
	boolean isTaskStop = true; // flag for task start/stop
	
	//TODO: do something real (this is kinda dumb). Make the Category object do something. Map {CategoryName->Loc}
//	private static Map<String, Location> categories = new HashMap<String, Location>(); 

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

		// Load Customer and shoppingList from DB
		cust = db.dbGetCustomer(memberId);
		cust.setShoppingList(db.dbGetShoppingListItems(memberId));

		// Add list of ShoppingListItems
		scroll = (ScrollView)findViewById(R.id.viewListScroll);
		LinearLayout itemList = generateListView(this, cust.getShoppingList());
		scroll.addView(itemList);
		TRIANGULATION_START = getString(R.string.triangulation_start);
		TRIANGULATION_STOP = getString(R.string.triangulation_stop);
		
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
				;		//TODO: is this locking?

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
			Map<String, ArrayList<ShoppingListItem>> shoppingList) {
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

			if (shoppingList.get(category).size() > 0) {
				// Generate the TextView row to display the category name
				TextView catRow = new TextView(ctx);
				catRow.setTextSize(18);
				catRow.setText(category);
				view.addView(catRow);

				// Iterate through each item within the category
				for (final ShoppingListItem item : shoppingList.get(category)) {
					Log.i(TAG, "    Item: " + item.getItem().getDescription()
							+ " - " + item.isChecked());
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
	private View createItemCheckbox(Context ctx, final ShoppingListItem item) {
		// Generate the CheckBox/text row for the item
		CheckBox checkbox = new CheckBox(ctx);
		checkbox.setText(item.getItem().getDescription());
		if (item.isChecked()) {
			checkbox.setChecked(true);
			checkbox.setPaintFlags(checkbox.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
		}

		// Create a listener to change the item state when checked
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkBoxView,
					boolean isChecked) {
				Log.i(TAG, "Setting checkbox of item " + item.getItemId()
						+ " to " + isChecked);
				db.dbSetItemChecked(cust.getMemberId(), item.getItemId(),
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
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		if (item.getItemId() == R.id.menu_nfc) {
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
				else if(!item.getTitle().equals(TRIANGULATION_STOP)) {
	
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
	
					item.setTitle(TRIANGULATION_STOP);
				}
				else {
					item.setTitle(TRIANGULATION_START);
	
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
	        	// barcode format should be "category/item description"
		        String[] splits = barcode.split("/");
		        // parameters: category, desc
		        addItem(splits[0], splits[1]);
		        
		        ADD NEW ITEM HERE; DONT CHECK FOR CATEGORY;
		        SEND BARCODE TO WEB SERVICE (MAKE ASYNC METHOD TO SIMPLIFY SENDING REQUESTS?)
		        RECEIVE PRODUCT NAME/PRICE, ADD TO LIST
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
	
	private void addItem(String category, String desc) {
		// Add new item to database and ShoppingList
		int newItemId = db.dbCreateItem(desc, category);
		db.dbCreateShoppingListItem(newItemId, cust.getMemberId(), false, 0);
		
		Item newItem = new Item(newItemId, desc, category);
		Map<String, ArrayList<ShoppingListItem>> newShoppingList = cust.getShoppingList();
		ArrayList<ShoppingListItem> addedItem = newShoppingList.get(category);
		if(addedItem == null) {
			addedItem = new ArrayList<ShoppingListItem>();
		}
		addedItem.add(new ShoppingListItem(newItemId, false, 0, newItem));
		newShoppingList.put(category, addedItem);
		cust.setShoppingList(newShoppingList);
		
		scroll.removeAllViewsInLayout();
		scroll.addView(this.generateListView(this,newShoppingList));
	}
}
