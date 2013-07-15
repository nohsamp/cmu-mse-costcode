package cmu.costcode.WIFIScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cmu.costcode.FloorPlan.FloorPlan;
import cmu.costcode.ShoppingList.objects.Category;
import cmu.costcode.Triangulation.Triangulation;

import android.content.Context;
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
	private boolean noiseFilterFlag = true;

	// WifiManager variable
	WifiManager wifimanager;

	String text = "";
	String result = "";
	String fileName = "";
	int normCount = 0;
	int avgCount = 0;
	int dropCount = 0;
	
	private List<ScanResult> mScanResult = null; // ScanResult List
	private List<AccessPoint> apList;		// Access Point List
	
	private boolean dummyFlag = false;		// TODO: Test purpose
	
	private FloorPlan fp = null;
	
	public WiFiScanner(WifiManager wm, Map<String, Object> initParams, Context context) {
		wifimanager = wm;
		
		scanNumber = (Integer) initParams.get(Triangulation.SCAN_NUMBER);
		noiseFilterFlag = (Boolean) initParams.get(Triangulation.NOISE_FILTER);
		
		dummyFlag = (Boolean)initParams.get("DUMMY");
		
		// Get the AP information
		// Create thread
		fp = new FloorPlan(context);
		fp.start();
	}
	
	public void scanStart() {
		Log.d(TAG, "WIFI Scan start");
		
		// TODO: DUMMY THINGS FOR TEST
		if(dummyFlag) {
			apList = DummyResults.parseDummyResult(scanNumber);
			return;
		}
		
		List<AccessPoint> tempList = fp.getAccessPoints(); // get Floor plan information
		// if there is no floor plan, return null
		if(tempList == null || tempList.size() == 0) {
			return;
		}
		// otherwise copy floor plan to the new access point list
		apList = new ArrayList<AccessPoint>(tempList.size());
		apList.addAll(tempList);

		wifimanager.startScan();
		mScanResult = wifimanager.getScanResults();
		for(int i=0; i<scanNumber-1; i++) {
			wifimanager.startScan();
			mScanResult.addAll(wifimanager.getScanResults());
		}
		
		parseWIFIScanResult(); // get WIFISCanResult
		mScanResult = null;
	}
	
	public void scanStop() {
		Log.d(TAG, "WIFI Scan stop");
		wifimanager = null;
	}
	
	public List<AccessPoint> getApList() {
		return apList;
	}

	// Return Category position List (use the same object with AccessPoint)
	public List<Category> getCategoryList() {
		return fp.getCategories();
	}

	private void parseWIFIScanResult() {
		Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
			@Override
			public int compare(ScanResult s1, ScanResult s2) {
				return (s1.BSSID.compareTo(s2.BSSID));
			}
		};
		Collections.sort(mScanResult, comparator);

		int rssi = 0;
		int tCount = 0;

		ScanResult oldResult = null, newResult = null;
		for (int i = 0; i < mScanResult.size(); i++) {
			newResult = mScanResult.get(i);
			if (i > 0 && oldResult.BSSID.compareTo(newResult.BSSID) != 0) {
				// if noiseFilterFlag is true and tCount is less than scanNumber, 
   				// drop info from AP
   				if(!noiseFilterFlag || tCount == scanNumber) {  
   					addAP(oldResult, i, rssi / tCount);
   				}
				rssi = newResult.level;
				tCount = 1;
			} else {
				rssi += newResult.level;
				tCount++;
			}
			oldResult = newResult;
			if (i == mScanResult.size() - 1) {
				// if noiseFilterFlag is true and tCount is less than scanNumber, 
   				// drop info from AP
   				if(!noiseFilterFlag || tCount == scanNumber) {  
   					addAP(oldResult, i, rssi / tCount);
   				}
			}
		}
		
		// Remove AP which doesn't have rssi
		for(int i=apList.size()-1; i>=0;i--) {
			if(apList.get(i).getRssi() == 0) {
				apList.remove(i);
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
	}
}