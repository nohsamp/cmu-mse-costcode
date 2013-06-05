package cmu.costcode.WIFIScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cmu.costcode.FloorPlan.FloorPlan;

import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * WIFI Scanner
 * 
 * @author NohSam
 * 
 */
public class WiFiScanner {

	private static final String TAG = "WIFIScanner";
	public static final int FILTER_AVG = 1;
	public static final int FILTER_NONE = 2;

	private int scanNumber = 5;
	private boolean scanFlag = false;

	// WifiManager variable
	WifiManager wifimanager;

	String text = "";
	String result = "";
	String fileName = "";
	int normCount = 0;
	int avgCount = 0;
	int dropCount = 0;

	private List<ScanResult> mScanResult = null; // ScanResult List
	private List<AccessPoint> apList;
	private FloorPlan fp = null;
	
	public WiFiScanner(WifiManager wm, int scanNumber) {
		wifimanager = wm;
		this.scanNumber = scanNumber;
		
		// Get the AP information
		// Create thread
		fp = new FloorPlan();
		//fp.run(); // TODO Uncomment this line for the actual execution of web service
		apList = fp.getAccessPoints();
		
		// TODO Remove the below part for the actual execution of web service
		// mock the AP position data
		if(apList == null)
			apList = new ArrayList<AccessPoint>(10);
	}
	
	public List<AccessPoint> scanStart() {
		Log.d(TAG, "WIFI Scan start");
		if (scanFlag == false) {
			scanFlag = true;
		}
		
		wifimanager.startScan();
		mScanResult = wifimanager.getScanResults();
		for(int i=0; i<scanNumber-2; i++) {
			wifimanager.startScan();
			mScanResult.addAll(wifimanager.getScanResults());
		}
		
		parseWIFIScanResult(); // get WIFISCanResult
		mScanResult = null;
		
		return apList;
	}
	
	public void scanStop() {
		scanFlag = false;
	}
	
	private void parseWIFIScanResult() {
//		Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
//			@Override
//			public int compare(ScanResult s1, ScanResult s2) {
//				return (s1.BSSID.compareTo(s2.BSSID));
//			}
//		};
//		Collections.sort(mScanResult, comparator);

		int rssi = 0;
		int tCount = 0;

		ScanResult oldResult = null, newResult = null;
		for (int i = 0; i < mScanResult.size(); i++) {
			newResult = mScanResult.get(i);
			if (i > 0 && oldResult.BSSID.compareTo(newResult.BSSID) != 0) {
				addAP(oldResult, i, rssi / tCount);
				rssi = newResult.level;
				tCount = 1;
			} else {
				rssi += newResult.level;
				tCount++;
			}
			oldResult = newResult;
			if (i == mScanResult.size() - 1) {
				addAP(oldResult, i, rssi / tCount);
			}
		}
	}
		
	private void addAP(ScanResult mResult, int i, float rssi) {
		for (Iterator<AccessPoint> itd = apList.iterator(); itd.hasNext();) {
			AccessPoint ap = itd.next();
			if(ap.getSsid().equals(mResult.SSID)) {
				ap.setBssid(mResult.BSSID);
				ap.setCapabilities(mResult.capabilities);
				ap.setFrequency(mResult.frequency);
				ap.setRssi(rssi);
			}
			
		}
		
		// TODO: delete the below part for the real WS call
		PointF mockAP = null;
		mockAP = new PointF((float) i, (float) i);
		AccessPoint ap = new AccessPoint(mResult.BSSID, mResult.SSID,
				mResult.capabilities, mResult.frequency, rssi, mockAP.x,
				mockAP.y);
		apList.add(ap);
	}

//	private void getWIFIScanResult() {
//		Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
//			@Override
//			public int compare(ScanResult s1, ScanResult s2) {
//				return (s1.level >= s2.level) ? -1 : 1;
//			}
//		};
//		Collections.sort(mScanResult, comparator);
//	}
}