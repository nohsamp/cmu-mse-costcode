package cmu.costcode.simplifiedcheckout.nfc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import cmu.costcode.ShoppingList.objects.ShoppingListItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CustomerNFC implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private final static String TAG = "CustomerNFC";
	NfcAdapter nfcAdapter;
	private static final int MESSAGE_SENT = 1;
	private ArrayList<ShoppingListItem> shoppingList;
	Context context;
	
	// Constructor
	// The caller should pass Activity using ActivityName.this
	public CustomerNFC(Activity sourceActivity, Context context, ArrayList<ShoppingListItem> shoppingList) {
		this.shoppingList = shoppingList;
		this.context = context;
		
		// Show the Up button in the action bar.
//		setupActionBar();
		
		// Set up NFC Adapter
		nfcAdapter = NfcAdapter.getDefaultAdapter(context);
		if (nfcAdapter == null) {
			Toast.makeText(context, "NFC is not available on this device. :(", Toast.LENGTH_LONG).show();
			return;  // NFC not available on this device
		}
		
		nfcAdapter.setNdefPushMessageCallback(this, sourceActivity);
		nfcAdapter.setOnNdefPushCompleteCallback(this, sourceActivity);
	}
	
	/**
	 * Convert a serializable object 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	private byte[] serializeObject(Object object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] serialized;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(object);
			serialized = bos.toByteArray();
		} finally {
			out.close();
			bos.close();
		}
		
		return serialized;
	}
	
// NDEF Stuff
	
	/**
	 * Generate new Ndef Message to send to whoever wants to listen
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
        byte[] byteMessage;
		try {
			byteMessage = serializeObject(shoppingList);
		} catch (IOException e) {
			Log.e(TAG, "Error: could not serialize ShoppingList properly; " + e.getMessage());
			byteMessage = new byte[1];
		}
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
        		"application/cmu.costco.simplifiedcheckout.nfc", byteMessage)
//        		,NdefRecord.createApplicationRecord("cmu.costco.simplifiedcheckout.nfc")
        );
        Log.i(TAG, msg.getRecords()[0].toString());
        return msg;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		// A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}
	
	/** This handler receives a message from onNdefPushComplete */
    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(context, "Message sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
    
}