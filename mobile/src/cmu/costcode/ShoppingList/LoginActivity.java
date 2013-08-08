package cmu.costcode.ShoppingList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import cmu.costcode.R;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;

public class LoginActivity extends Activity {
	private DatabaseAdaptor dbHelper;
	public final static String MEMBERID = "MEMBERID";
	
	private static final String TAG = "LoginActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbHelper = new DatabaseAdaptor(this);
		dbHelper.open();
		
		setContentView(R.layout.activity_login);
    }
    
	@Override
	public void onResume() {
		super.onResume();
		dbHelper.open();
	}
    
    @Override
    public void onStop() {
    	super.onStop();
    	dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    // WiFi triangulation option menu load
    @Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		super.onOptionsItemSelected(item);
 		
 		if (item.getItemId() == R.id.menu_settings) {
 			Intent intent = new Intent(this, WiFiScanSettingsActivity.class);
 			startActivity(intent);
 		}
 		return true;
    }
    
    /** Called when the user clicks the Login button */
    public void login(View view) {
		Intent intent = new Intent(this, ViewListActivity.class);
		EditText emailText = (EditText) findViewById(R.id.loginEmailInput);
		String email = emailText.getText().toString();
		EditText pwordText = (EditText) findViewById(R.id.loginPwordInput);
		String password = pwordText.getText().toString();
		intent.putExtra("EMAIL", email);
		
		int memberId = validateUser(email, password);
		if(memberId != -1) {
			intent.putExtra(MEMBERID, memberId);
			startActivity(intent);
		} else {
			//TODO: Logging, error handling
		}
    }
    
    /** Return true if password is valid for given email */
    private int validateUser(String email, String password) {
    	/*
    	 * TODO: Query DB for rows wither EMAIL=email and PWORD=password.
    	 * - Take customerId from this row
    	 * - getCustomer with that ID
    	 * or - get the list 
    	 */
    	createDummyInfo(); // TODO add verification function and remove this
    	return 1;
    	
//    	return -1;
    }
    
	/**
     * TODO: Temp method, intended to generate a few dummy values and test user
     */
    final int DUMMY_MEMBER_ID = 1;
    private void createDummyInfo() {
    	if(dbHelper.dbGetCustomer(DUMMY_MEMBER_ID) == null) {
    		int memberId = dbHelper.dbCreateCustomer("Kevin", "Crane", "515 S Aiken Ave");
    		memberId = 1;
    		Log.d(TAG, "Created Dummy member Kevin with memberId " + memberId + ".");

    		
    		long itemId1 = dbHelper.dbCreateItem("Harry Potter, Book 3", "Books/Magazines", 1, 6.49f, "0038332164718");
    		dbHelper.dbCreateShoppingListItem(itemId1, memberId, false, 1);
    		Log.d(TAG, "Created dummy item Milk with itemId " + itemId1 + ".");

    		long itemId2 = dbHelper.dbCreateItem("Uncrustables", "Food", 1, 3.99f, "0051500048160");
    		dbHelper.dbCreateShoppingListItem(itemId2, memberId, false, 2);
    		Log.d(TAG, "Created dummy item Cheez-its with itemId " + itemId2 + ".");

    		long itemId3 = dbHelper.dbCreateItem("North Face Denali jacket", "Clothing", 1, 116.32f, "0032546198033");
    		dbHelper.dbCreateShoppingListItem(itemId3, memberId, false, 3);
    		Log.d(TAG, "Created dummy item New Jacket with itemId " + itemId3 + ".");
    	}
    }
	
} 