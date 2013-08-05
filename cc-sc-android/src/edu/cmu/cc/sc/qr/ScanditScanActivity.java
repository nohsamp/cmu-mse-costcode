package edu.cmu.cc.sc.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

public class ScanditScanActivity extends Activity implements ScanditSDKListener {
	
	public static final String SCANDIT_APP_KEY = "zIsaZsllEeKPmus1m7SOEyi0uUSsPoXdkHEItLCFzmc";
	private ScanditSDKBarcodePicker mBarcodePicker;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scanItem();
	}

	/** 
     * Called when the user entered a bar code manually.
     * 
     * @param entry The information entered by the user.
     */
	public void didManualSearch(String entry) {
		// Stop recognition to save resources.
		mBarcodePicker.stopScanning();
		Toast.makeText(this, "User entered: " + entry, Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public void didCancel() { 
		mBarcodePicker.stopScanning();
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	@Override
	public void didScanBarcode(String barcode, String symbology) {
		Log.i("scanner", "Scan done!! barcode=\"" + barcode + "\"; symbology=\"" + symbology + "\"");
		Log.i("scanner", "scan result: " + barcode);
		
		// Stop recognition to save resources.
		mBarcodePicker.stopScanning();
		mBarcodePicker = null;
		
		Intent intent = new Intent();
		intent.putExtra("barcode", barcode);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	@Override
	protected void onPause() {
	    // When the activity is in the background immediately stop the 
	    // scanning to save resources and free the camera.
	    if (mBarcodePicker != null) {
	        mBarcodePicker.stopScanning();
	    }
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
	    // Once the activity is in the foreground again, restart scanning.
        if (mBarcodePicker != null) {
            mBarcodePicker.startScanning();
        }
	    super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		if (mBarcodePicker != null) {
	    	mBarcodePicker.stopScanning();
		}
	    finish();
	}

	/**
	 * Scan a barcode on button click with Scandit SDK
	 */
	public void scanItem() {
		// start scanning
		if (ScanditSDKBarcodePicker.canRunPortraitPicker()) {
			// the standard picker can be used
			Log.i("scanner", "Using standard scanner.");
			mBarcodePicker = new ScanditSDKBarcodePicker(this, SCANDIT_APP_KEY, 
					ScanditSDKBarcodePicker.CAMERA_FACING_BACK);
		} else {
			// the legacy picker must be used
			Log.i("scanner", "Using legacy scanner.");
		}

		// From ScanditSDKSampleBarcodeActivity initializeAndStartBarcodeScanning()
		// Add both views to activity, with the scan GUI on top.
		setContentView(mBarcodePicker);
		
		// Register listener, in order to be notified about relevant events 
		// (e.g. a successfully scanned bar code).
		mBarcodePicker.getOverlayView().addListener(this);
		
		// show search bar in scan user interface
		mBarcodePicker.getOverlayView().showSearchBar(true);
		
		// To activate recognition of 2d codes
		mBarcodePicker.setQrEnabled(true);
		mBarcodePicker.setDataMatrixEnabled(true);
		
		// Start scanning stuff
		mBarcodePicker.startScanning();
	}
}
