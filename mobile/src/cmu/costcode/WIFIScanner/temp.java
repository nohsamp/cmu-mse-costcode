package cmu.costcode.WIFIScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cmu.costcode.ProximityAlert.ProximityIntentReceiver;
import cmu.costcode.Triangulation.WCL;
import cmu.costcode.ShoppingList.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

/**
 * WIFI Scanner
 * 
 * @author NohSam
 * 
 */
public class temp extends Activity implements OnClickListener {

	private static final String TAG = "WIFIScanner";
	private static final int FILTER_AVG = 1;
	public static final int FILTER_NONE = 2;

	private int filter = FILTER_NONE;

	private boolean awclFlag = false; // default is WCL algorithm

	// WifiManager variable
	WifiManager wifimanager;

	// UI variable
	TextView textStatus;
	Button btnScanStart;
	Button btnScanStop;
	Button btnAvgFilterScanStart;
	Button btnDropFilterScanStart;

	private int scanCount = 0;
	String text = "";
	String result = "";
	String fileName = "";
	int normCount = 0;
	int avgCount = 0;
	int dropCount = 0;

	private List<ScanResult> mScanResult = null; // ScanResult List
	private List<AccessPoint> apList = new ArrayList<AccessPoint>(); // ScanResult
																		// List
	
	private ProximityIntentReceiver pReceiver; // Proximity Broadcasting Receiver

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				switch (filter) {
					case FILTER_NONE:
						mScanResult = wifimanager.getScanResults();
						parseWIFIScanResult(); // get WIFISCanResult
						break;
					case FILTER_AVG:
						scanCount++;
						if (scanCount == 1) {
							mScanResult = wifimanager.getScanResults();
						} else {
							mScanResult.addAll(wifimanager.getScanResults());
						}
	
						if (scanCount == 5) {
							parseWIFIScanResult(); // get WIFISCanResult
							scanCount = 0;
							mScanResult = null;
						}
						break;
					default:
						break;
				}
				wifimanager.startScan(); // for refresh
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
			}
		}
	};

	
//	public WIFIScanner() {
//		// Setup WIFI
//		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
//		Log.d(TAG, "Setup WIfiManager getSystemService");
//	
//		// if WIFIEnabled
//		if (wifimanager.isWifiEnabled() == false)
//			wifimanager.setWifiEnabled(true);		
//	}
	
	public void scanStart(int scanOption) {
		Log.d(TAG, "WIFI Scan start");
		printToast("WIFI SCAN start");
		if (scanFlag == false) {
			initWIFIScan(); // start WIFIScan
			textStatus.setText("=======================================\n");
			scanFlag = true;
			filter = scanOption;
		}
	}
	
	public void scanStop() {
		Log.d(TAG, "WIFI Scan stop");
		printToast("WIFI SCAN stop");
		if (scanFlag == true) {
			unregisterReceiver(mReceiver);	// stop WIFISCan
			unregisterReceiver(pReceiver);	// stop ProximityAlert
			scanFlag = false;
			filter = FILTER_NONE;
		}
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
		
		WCL tr = new WCL(wifimanager, null);
		AccessPoint nAP = tr.calculateAccessPointPosition();
		if (nAP != null) {
			textStatus.append("Nearest AP" + "\tSSID: " + nAP.getSsid()
					+ "\t, Distance: " + nAP.getDistance() + "\t, PointX: "
					+ nAP.getPosX() + "\t, PointY: " + nAP.getPosY() + "\n");
		}
		apList.clear();
		
		Intent intent = new Intent(ProximityIntentReceiver.PROXIMITY_ALERT);
//		intent.putExtra("category", "food");
		
		sendBroadcast(intent);
		
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

		textStatus.setText("=======================================\n");
		for (int i = 0; i < mScanResult.size(); i++) {
			ScanResult result = mScanResult.get(i);
			textStatus.append((i + 1) + ". SSID : " + result.SSID.toString()
					+ "\t\t RSSI : " + result.level + " dBm\n");
		}
		textStatus.append("=======================================\n");
	}

	public void initWIFIScan() {
		// init WIFISCAN
		scanCount = 0;
		text = "";
		// Register Broadcaster receiver for WiFi scanning 
		final IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(mReceiver, filter);
		
		// Register Broadcaster receiver for proximity alert 
		IntentFilter proximityFilter = new IntentFilter();
		proximityFilter.addAction(ProximityIntentReceiver.PROXIMITY_ALERT);
		pReceiver = new ProximityIntentReceiver();
		registerReceiver(pReceiver, proximityFilter);
		
		wifimanager.startScan();
		Log.d(TAG, "initWIFIScan()");
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifiscan);

		// Setup UI
		textStatus = (TextView) findViewById(R.id.textStatus);
		btnScanStart = (Button) findViewById(R.id.btnScanStart);
		btnScanStop = (Button) findViewById(R.id.btnScanStop);
		btnAvgFilterScanStart = (Button) findViewById(R.id.btnAvgFilterScanStart);
		btnDropFilterScanStart = (Button) findViewById(R.id.btnDropFilterScanStart);

		// Setup OnClickListener
		btnScanStart.setOnClickListener(this);
		btnScanStop.setOnClickListener(this);
		btnAvgFilterScanStart.setOnClickListener(this);
		btnDropFilterScanStart.setOnClickListener(this);

		// Setup WIFI
		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
		Log.d(TAG, "Setup WIfiManager getSystemService");

		// if WIFIEnabled
		if (wifimanager.isWifiEnabled() == false)
			wifimanager.setWifiEnabled(true);

	}

	public void printToast(String messageToast) {
		Toast.makeText(this, messageToast, Toast.LENGTH_LONG).show();
	}

	private boolean scanFlag = false;
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnScanStart) {
			scanStart(FILTER_NONE);
		}
		else if (v.getId() == R.id.btnScanStop) {
			scanStop();
		}
		else if (v.getId() == R.id.btnAvgFilterScanStart) {
			scanStart(FILTER_AVG);
		}
	}

	// Proximity Alert menu function
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "WCL");
		menu.add(0, 2, 0, "AWCL");

		return true;
	}

	// Add an item into the shopping list
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == 1) {
			Toast.makeText(this, "WCL is selected", Toast.LENGTH_SHORT).show();
			this.awclFlag = false;
			return true;
		}
		if (item.getItemId() == 2) {
			Toast.makeText(this, "AWCL is selected", Toast.LENGTH_SHORT).show();
			this.awclFlag = true;
			return true;
		}

		return false;
	}
}