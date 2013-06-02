package cmu.costcode.WIFIScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private List<AccessPoint> apList = new ArrayList<AccessPoint>(); // ScanResult
	
	public WiFiScanner(WifiManager wm, int scanNumber) {
		wifimanager = wm;
		this.scanNumber = scanNumber;
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
	
	public void parseWIFIScanResult() {
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
				// if(tCount > 0) {
				addAP(oldResult, i, rssi / tCount);
				// }
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
		
	public void addAP(ScanResult mResult, int i, float rssi) {
		// mock the AP position data
		PointF mockAP = null;
		//
		// points[0] = new PointF(0, 0); // siruiptime
		// points[1] = new PointF(11.3, -2.5); // siru=softap
		// points[2] = new PointF(15, -8.8); // siru-mac
		// points[3]
		if (mResult.SSID.compareTo("siruiptime") == 0) {
			mockAP = new PointF(0, 0);
		} else if (mResult.SSID.compareTo("siru-softap") == 0) {
			mockAP = new PointF((float) 11.3, (float) -2.5);
		} else if (mResult.SSID.compareTo("siru-mac") == 0) {
			mockAP = new PointF((float) 13, (float) -10); // inside the room
															// -10.5, outside
															// the room -10
		} else if (mResult.SSID.compareTo("siru-sony") == 0) {
			mockAP = new PointF((float) 2.3, (float) -10);
		} else {
			mockAP = new PointF((float) i, (float) i); // 다음 라인이랑
														// alternate comment and
														// uncomment
//			return;
		}
		AccessPoint ap = new AccessPoint(mResult.BSSID, mResult.SSID,
				mResult.capabilities, mResult.frequency, rssi, mockAP.x,
				mockAP.y);
		apList.add(ap);
	}

	public void getWIFIScanResult() {

		// mScanResult = wifimanager.getScanResults(); // ScanResult
		Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
			@Override
			public int compare(ScanResult s1, ScanResult s2) {
				return (s1.level >= s2.level) ? -1 : 1;
			}
		};
		Collections.sort(mScanResult, comparator);

		// Scan count
		// textStatus.setText("Scan count is \t" + ++scanCount + " times \n");

//		textStatus.setText("=======================================\n");
//		for (int i = 0; i < mScanResult.size(); i++) {
//			ScanResult result = mScanResult.get(i);
//			textStatus.append((i + 1) + ". SSID : " + result.SSID.toString()
//					+ "\t\t RSSI : " + result.level + " dBm\n");
//		}
//		textStatus.append("=======================================\n");
	}
}