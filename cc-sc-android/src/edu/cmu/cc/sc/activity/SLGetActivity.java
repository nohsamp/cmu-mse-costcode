/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.activity;

import java.util.Map;

import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.qr.ScanditScanActivity;
import edu.cmu.cc.sc.web.CashierWeb;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * DESCRIPTION: Active shopping list activity
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 5, 2013
 */
public class SLGetActivity extends Activity implements ITabActivity {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------
	protected static final String TAG = "SLGetActivity";

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Map<Integer, MenuItem> menuItems;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_get);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return prepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return handleOptionsMenuItemSelection(item);
	}

	@Override
	public boolean prepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean handleOptionsMenuItemSelection(final MenuItem item) {
		return true;
	}

	@Override
	public void refresh() {
	}

	/**
	 * Called on Retrieve List button; sends async GET request to retrieve a
	 * customer's shopping list
	 * 
	 * @param view
	 */
	public void retrieveShoppingList(View view) {
		String customerIdText = ((EditText) findViewById(R.id.sl_get_customerId))
				.getText().toString();
		if (customerIdText.isEmpty()) {
			Toast.makeText(this, "Please enter a customer ID.",
					Toast.LENGTH_LONG).show();
			return;
		}

		int customerId = Integer.parseInt(customerIdText);

		CashierWeb cashier = new CashierWeb(customerId, this);
		cashier.retrieveShoppingList();

		// ApplicationState.getInstance().setCurrentSL(cashier.getShoppingList());
	}

	/**
	 * Scan item using Scandit API
	 * 
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && data != null) {
			String barcode = data.getStringExtra("barcode");
			try {
				// Send barcode to server to be processed
				// String requestUrl = SERVER_URL + API_GET_PRODUCT + barcode;
				Log.i(TAG, "Getting barcode from server at: " + barcode);
				CashierWeb cashier = new CashierWeb(barcode, this);
				cashier.retrieveShoppingList();
			} catch (Exception e) {
				Toast.makeText(this, "Scan error: " + barcode,
						Toast.LENGTH_SHORT).show();
				Log.e(TAG, "Item scan failed; Exception=" + e.toString());
			}
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "Scan Item Canceled", Toast.LENGTH_LONG)
					.show();
		}
	}
}
