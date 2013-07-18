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
			CustomerNFC customer = new CustomerNFC(ViewListActivity.this, this, db.dbGetItemList(cust.getMemberId()));
			customer.broadcastShoppingList(getCurrentFocus());
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
	 * Creates a pretty comma-separated list of items in a given category as a string
	 * @param category
	 * @param items
	 * @return
	 */
	private String printListReminder(String category, List<ShoppingListItem> items) {
		String itemText = "";
		// Create a nice comma-separated list of items with an 'and' at the end; TODO: make cleaner
		if(items == null || items.isEmpty()) {
			itemText = "something";
		} else if(items.size() == 1) {
			itemText = items.get(0).getItem().getDescription();
		} else if(items.size() == 2) {
			itemText = items.get(0).getItem().getDescription()
					+ " and " + items.get(1).getItem().getDescription();
		} else {
			for(int i=0; i<items.size()-1; i++) {
				itemText += items.get(i).getItem().getDescription() + ", ";
			}
			itemText += "and " + items.get(items.size()-1).getItem().getDescription();
		}
		String message = "You are near the " + category + " section. Don't forget to buy " + itemText + "!";
		return message;
	}

	/**
	 * Create test locations in the DB
	 */
	private void createDummyAlerts() {
		//Near CMU
		db.dbCreateAlert("Electronics", 40.44563, -79.948727);	// Chinese food place
		db.dbCreateAlert("Clothing", 40.445375, -79.94866);	// Corner across Quiznos
		db.dbCreateAlert("Food", 40.444697, -79.94862);	// Pizza Guy

		//Test locations for Seattle's Costco HQ
		//db.dbCreateAlert("Electronics", 47.551336, -122.065294);
		//db.dbCreateAlert("Clothing", 47.551604, -122.065369);
		//db.dbCreateAlert("Food", 47.551376, -122.065149);
	}


	/**
	 * Toggle list Proximity Alerts on/off
	 * @param view
	 */
	public void toggleProximityAlerts(View view) {
		//TODO: register if toggle is on or off, some kind of persistent state

		// Is the toggle on?
		boolean proxAlertsOn = ((ToggleButton) view).isChecked();

		// Retrieve the customer's shopping list and proximity alerts
		Map<String, ArrayList<ShoppingListItem>> shoppingList = cust.getShoppingList();
		Map<String, Location> proximityAlerts = db.dbGetProxAlerts();

		// Make dummy alerts if you need to
		if(proximityAlerts.isEmpty()) {
			Log.i("proxalert", "No Proximity Alerts were present, creating dummy alerts.");
			createDummyAlerts();
			shoppingList = cust.getShoppingList();
		}

		if(proxAlertsOn) {
			// Add proximity alerts for every category on the user's list
			for (String category : proximityAlerts.keySet()) {
				Log.i(TAG, "Adding proximity alert for " + category + " at ("
						+ proximityAlerts.get(category).getLat() + ", " + proximityAlerts.get(category).getLon() + ").");

				// Build a message string
				List<ShoppingListItem> items = shoppingList.get(category);
				String message = printListReminder(category, items);

				// Add the proximity alert
				Location categoryLoc = proximityAlerts.get(category);
				ShoppingListApplication application = (ShoppingListApplication)getApplication();
				application.addProximityAlert(categoryLoc.getLat(), categoryLoc.getLon(), 15, -1, message);
			}
			Toast.makeText(this, "Activated " + proximityAlerts.size() + " proximity alerts.",
					Toast.LENGTH_LONG).show();
		} else {
			// Remove proximity alerts for every category on the user's list
			ShoppingListApplication application = (ShoppingListApplication)getApplication();
			application.removeAllProximityAlerts();
			Toast.makeText(this, "Deactivated all proximity alerts.", Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * Scan item using Scandit API
	 * @param view
	 */
	public void scanItem(View view) {
		Intent intent = new Intent(this, ScanditScanActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    if(resultCode == RESULT_OK && data != null)
	    {
	        String barcode = data.getStringExtra("barcode");
	        try {
		        String[] splits = barcode.split("/");
		        addItem(splits[0], splits[1]);
	        }
	        catch(Exception e) {
	        	Toast.makeText(this, "Scan fail: not an item." + barcode, Toast.LENGTH_SHORT).show();
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
