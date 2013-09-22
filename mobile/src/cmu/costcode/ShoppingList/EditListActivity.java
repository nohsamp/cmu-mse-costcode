package cmu.costcode.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import cmu.costcode.R;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.ShoppingList.objects.Customer;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

public class EditListActivity extends Activity {
	
	private final static String TAG = "EditListActivity";
	
	private static final int VOICE_REQUEST_CODE = 1234;
	private boolean voiceButtonEnabled = true;
	private View addItemView;
	
	private DatabaseAdaptor db;
	private Customer cust;
	
	
	/**
	 * Run when the user wants to edit their shopping list
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the text view as the activity layout
		setContentView(R.layout.activity_edit_list);
		
		// Open database
		db = new DatabaseAdaptor(this);
		db.open();
		
		// Get the message from the intent
		Intent intent = getIntent();
		int memberId = intent.getIntExtra(LoginActivity.MEMBERID, 1);
		
		// Load Customer and shoppingList from DB
		cust = db.dbGetCustomer(memberId);
		cust.setShoppingList(db.dbGetShoppingList(memberId));
		
		// Prepare AddItem view; Inflate XML layout for adding new item, add to scrolling list
		addItemView = getLayoutInflater().inflate(R.layout.activity_edit_list_new, null);
		
		// Disable voice recognition button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			voiceButtonEnabled = false;
		}
				
		// Add list of ShoppingListItems
		ScrollView scroll = (ScrollView)findViewById(R.id.editListScroll);
		LinearLayout itemList = generateEditListView(this, cust.getShoppingList());
		scroll.addView(itemList);
		

	}
	
	@Override
	public void onResume() {
		super.onResume();
		db.open();
	}
	
	
	private LinearLayout generateEditListView(Context ctx, Map<String, ShoppingList> shoppingList) {
		// Create the view that will be returned
		LinearLayout view = new LinearLayout(ctx);
		view.setOrientation(LinearLayout.VERTICAL);
		
		// Iterate through each item category
		for(String category : shoppingList.keySet()) {
			Log.i(TAG, "Iterating through category '" + category + "'.");
			// Generate the TextView row to display the category name
			TextView catRow = new TextView(ctx);
			catRow.setTextSize(18);
			catRow.setText(category);
			view.addView(catRow);
			
			// Iterate through each item within the category
			for(final Item item : shoppingList.get(category).getItems()) {
				Log.i(TAG, "    Item: " + item.getName() + " - ");
				view.addView(createEditRow(ctx, item));
			}
		}
			
		return view;
	}
	
	/**
	 * Create a single row to edit a ShoppingListItem
	 * @param ctx
	 * @param item
	 * @return EditItemRow view
	 */
	private View createEditRow(Context ctx, final Item item) {
		// Create the view that will be returned
        View view = getLayoutInflater().inflate(R.layout.activity_edit_list_row, null);
        view.setTag(item.getId());
        
        // Add Delete Item listener
        Button deleteButton = (Button)view.findViewById(R.id.editItemDelete);
        deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Deleting ListItem id " + item.getId() + " from shopping list");
				db.dbDeleteItemRow(cust.getMemberId(), (int)item.getId());
				updateList();
				((ViewGroup)v.getParent()).removeAllViews();
			}
		});

        // Fill row with item text
        EditText text = (EditText)view.findViewById(R.id.editItemText);
		text.setText(item.getName());
		
		// Return the generated row view
		return view;
	}
	
	
	/**
	 * Reads through ListItems and updates DB contents to match them
	 */
	private void updateList() {
		LinearLayout shoppingItemList = (LinearLayout) ((ViewGroup)findViewById(R.id.editListScroll)).getChildAt(0);
		
		// Check if a new item is being added
		if(shoppingItemList.findViewById(R.id.editAddItem) != null) {
			ViewGroup prevItem = (ViewGroup)shoppingItemList.findViewById(R.id.editAddItem);
			addNewItemInfo(prevItem);
			shoppingItemList.removeView(prevItem);
		}
		
		// Iterate through item list's children
		String category = "Other";
		int childCount = shoppingItemList.getChildCount();
		for(int i=0; i<childCount; i++) {
			View listItem = shoppingItemList.getChildAt(i);
			if(listItem instanceof TextView) {
				// Add new category to new shopping list
				category = (String)((TextView)listItem).getText();
			} else {
				// If this button has been removed previously, skip it
				if(((ViewGroup)listItem).getChildCount() == 0) {
					continue;
				}
				
				// Add item to the new shopping list under previous category
				EditText itemTextView = (EditText)listItem.findViewById(R.id.editItemText);
				long itemId = (Long)listItem.getTag();
				//TODO: change this pronto; user shouldn't have to enter prices or UPCs
				db.dbUpdateItemName(itemId, category, itemTextView.getText().toString());//, 1, 0.0f, "111222333444"); 
			}
		}
	}
	
	/**
	 * Take info from NewItem view and add to DB and ShoppingList
	 * @param newItemView
	 */
	private void addNewItemInfo(View newItemView) {
		// Find the description and category views
		EditText prevItemDescView = (EditText)newItemView.findViewById(R.id.editNewItemDescription);
		Spinner prevItemCatView = (Spinner)newItemView.findViewById(R.id.editNewItemCategories);
		NumberPicker numpicQtyView = (NumberPicker)newItemView.findViewById(R.id.numPickerQuantity);
		NumberPicker numpicPriceView = (NumberPicker)newItemView.findViewById(R.id.numPickerPrice);
		
		// Convert the views into strings
		String newItemDesc = prevItemDescView.getText().toString();
		String newItemCat = prevItemCatView.getSelectedItem().toString();
		int newItemQty = numpicQtyView.getValue();
		float newItemPrice = (float)numpicPriceView.getValue();
		String newItemUpc = "1112222333444"; // TODO: layout change for UPC
		
		// Skip this item if no description filled in
		if(newItemDesc == null || newItemDesc.length() == 0) {
			return;
		}
		
		// Add new item to database and ShoppingList
		long newItemId = db.dbCreateItem(newItemDesc, newItemCat, newItemQty, newItemPrice, newItemUpc);
		db.dbCreateShoppingListItem(newItemId, cust.getMemberId(), false, 0);
		Log.i(TAG, "Adding item=("+newItemDesc+", "+newItemCat+" ("+newItemId+") )");
		
		Item newItem = new Item(newItemId, newItemCat, newItemDesc, newItemQty, newItemPrice, 0, newItemDesc, newItemUpc);
		Map<String, ShoppingList> newShoppingList = cust.getShoppingList();
		ShoppingList addedItem = newShoppingList.get(newItemCat);
		if(addedItem == null) {
			addedItem = new ShoppingList();
		}
		addedItem.addItem(newItem);
		newShoppingList.put(newItemCat, addedItem);
		cust.setShoppingList(newShoppingList);
	}
	
	/**
	 * Saves all items in list. If a ListItem is different than stored with Customer, update DB.
	 * @param view
	 */
	public void saveList(View view) {
		updateList();
		Intent intent = new Intent(this, ViewListActivity.class);
		intent.putExtra(LoginActivity.MEMBERID, cust.getMemberId());
		startActivity(intent);
	}
	
	/**
	 * Create view to add a new item to the Shopping List
	 * @param view
	 */
	public void addItem(View view) {
		ViewGroup parentView = ((ViewGroup)view.getParent().getParent());
		ScrollView scroll = (ScrollView)parentView.findViewById(R.id.editListScroll);
		
		// Update shopping list with new item if applicable
		updateList();
		
		// Update current edit list view
		scroll.removeAllViews();
		scroll.addView(generateEditListView(this, cust.getShoppingList()));
		
		// Disable voice button if speech recognition not supported
		Button voiceButton = (Button)addItemView.findViewById(R.id.editNewItemVoiceButton); 
		voiceButton.setEnabled(voiceButtonEnabled);
		
		// Add NewItem view to bottom of ScrollView and set focus to bottom
		((ViewGroup)scroll.getChildAt(0)).addView(addItemView);
		addItemView.requestFocus(View.FOCUS_DOWN);
	}
	
	
	/**
	 * Add a new item to the list via voice recognition
	 * @param view
	 */
	public void addItemByVoice(View view) {
		Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the item you need...");
		startActivityForResult(voiceIntent, VOICE_REQUEST_CODE);
	}
	
	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
			// Retrieve voice recognizer results from intent
			ArrayList<String> voiceResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			// Set text contents of new item to most likely result 
			TextView newItemText = (TextView)addItemView.findViewById(R.id.editNewItemDescription);
			newItemText.setText(voiceResult.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
} 