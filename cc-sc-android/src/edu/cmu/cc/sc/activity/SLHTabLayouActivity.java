/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.adapter.ActiveSLAdapter;
import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.dao.SLItemDAO;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;
import android.app.TabActivity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 11, 2013
 */
@SuppressWarnings("deprecation")
public class SLHTabLayouActivity extends TabActivity {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	private static final String TAB_SL_GET = "tab_sl_get";
	
	private static final String TAB_SL_ACTIVE = "tab_sl_active";

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private TabHost tabHost;
	private boolean readyForCustomer = true;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slh_tabhost);
		
		tabHost = getTabHost();
		
		setupTab(TAB_SL_ACTIVE, getString(R.string.tab_sl_active), 
				R.drawable.icon_songs_tab, SLActivity.class);
		
		setupTab(TAB_SL_GET, getString(R.string.tab_sl_get), 
				R.drawable.icon_photos_tab, SLGetActivity.class);
		

		
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				
				invalidateOptionsMenu();
				
				if (tabId.equals(TAB_SL_ACTIVE)) {
					ApplicationState.getInstance().setCurrentSL(
							ActiveSLAdapter.retrieveActiveSL());
					
					getCurrentTabActivity().refresh();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		ITabActivity tabActivity = getCurrentTabActivity();
		
		return tabActivity.prepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		ITabActivity tabActivity = getCurrentTabActivity();
		
		return tabActivity.handleOptionsMenuItemSelection(item);
	}

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private void setupTab(final String tag, final String label, 
			int iconResId, Class<?> activity) {
		
		TabSpec tabSpec = tabHost.newTabSpec(tag);
		
		//---------------------------------------------------
		// TAB: Setting up tab indicator
		//---------------------------------------------------
		
		tabSpec.setIndicator(createTabIndicator(label, iconResId));
		
		//---------------------------------------------------
		// TAB: Setting up tab activity
		//---------------------------------------------------
		
		Intent tabIntent = new Intent(this, activity);
		tabSpec.setContent(tabIntent);
		
		//---------------------------------------------------
		// TAB: Adding to the tab host
		//---------------------------------------------------
		
		tabHost.addTab(tabSpec);
	}
	
	private View createTabIndicator(final String label, int iconResId) {
		
		View tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		
		ImageView ivIcon = (ImageView) tabIndicator.findViewById(R.id.iv_tab_indicator_icon);
		ivIcon.setImageResource(iconResId);
		
		TextView tvTitle = (TextView) tabIndicator.findViewById(R.id.tv_tab_indicator_title);
		tvTitle.setText(label);
		
		return tabIndicator;
	}
	
	private ITabActivity getCurrentTabActivity() {
		return (ITabActivity) getLocalActivityManager()
				.getActivity(getTabHost().getCurrentTabTag());
	}
	
	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
				NfcAdapter.EXTRA_NDEF_MESSAGES);
		Log.i("NFC Receiver", "Received an NFC message!!!");
		
		// If the cashier is ready, accept the NDEF message as a stream of bytes
		if(readyForCustomer) {
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage)rawMsgs[0];
			readyForCustomer = false;
			// record 0 contains the MIME type, record 1 is the AAR, if present
			byte[] response = msg.getRecords()[0].getPayload();
			try {
				// Deserialize the byte[] and cast it as a ShoppingList
				ShoppingList shoppingList = (ShoppingList)deserializeObject(response);
				if(shoppingList == null) {
					Toast.makeText(getApplicationContext(), "Error: I was not able to read a ShoppingList from that message.", Toast.LENGTH_LONG).show();
					readyForCustomer = true;
					return;
				}
				
				// Success. Set the text to show the shopping list
				saveNfcSLs(shoppingList);
				ApplicationState.getInstance().setCurrentSL(shoppingList);
			} catch(IOException e) {
				// Handle errors
				Toast.makeText(getApplicationContext(), "Error: I was not able to read a ShoppingList from that message. :(" +
						e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		
		readyForCustomer = true;
	}
	
	
	/**
	 * Read a byte[] and deserialize it back into an object
	 * @param byteInput
	 * @return
	 * @throws IOException
	 */
	private Object deserializeObject(byte[] byteInput) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteInput);
		ObjectInputStream in = null;
		Object deserialized = null;
		try {
			in = new ObjectInputStream(bis);
			deserialized = in.readObject(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			bis.close();
			in.close();
		}
		return deserialized;
	}
	
	private void saveNfcSLs(ShoppingList shoppingList) {
		// Set up ShoppingList with dummy data
		SLItemDAO slItemDAO = new SLItemDAO();
		List<Item> items = new ArrayList<Item>(shoppingList.getItems().size());
		
		for(Item item : shoppingList.getItems()) {
			items.add(item);
			item.setShoppingList(shoppingList);
			slItemDAO.save(item);
		}
		
		SLDAO slDAO = new SLDAO();
		slDAO.save(shoppingList);
	}

}
