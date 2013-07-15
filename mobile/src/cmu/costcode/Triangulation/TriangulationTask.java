package cmu.costcode.Triangulation;

import java.util.HashMap;
import java.util.Map;

import cmu.costcode.ProximityAlert.ProximityIntentReceiver;
import cmu.costcode.WIFIScanner.AccessPoint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class TriangulationTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "TriangulationTask";
	private WifiManager wifiManager;
	private WCL triangulation;
	private Context context;
	private Map<String, Object> initParams = new HashMap<String, Object>();
	
	private boolean bgrunFlag = true;
	
	
	public TriangulationTask(Context context) throws Exception {
		this.context = context;
		wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		Log.d(TAG, "Setup WIfiManager getSystemService");

		// Get preference and set the flags for triangulation
		if(!setPreference())
			throw new Exception("WiFi is not enabled");
		
		// if WIFIEnabled
		if (wifiManager.isWifiEnabled() == false)
			wifiManager.setWifiEnabled(true);
		
		// Create Triangulation
		triangulation = new WCL(wifiManager, initParams, context);
	}
	
	
	private boolean setPreference() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!prefs.getBoolean("wifi_checkbox", true)) {
			Toast.makeText(context, "Make WiFi Triangulation enable" , Toast.LENGTH_SHORT).show();
			return false;
		}
		else {
			bgrunFlag = prefs.getBoolean("bgrun_checkbox", true);
			initParams.put(WCL.TRIANG_METHOD, prefs.getString("triangulation_list", "WCL"));
			initParams.put(WCL.SCAN_NUMBER, Integer.valueOf(prefs.getString("wifi_scannum", "5")));
			initParams.put(WCL.NOISE_FILTER, prefs.getBoolean("noise_filter_checkbox", true));
			initParams.put("DUMMY", prefs.getBoolean("dummy", false)); //TODO: test purpose, remove it later
		}
		return true;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if(triangulation == null) {
			return null;
		}
		
		while(true) {
			if(isCancelled())
				break;
			AccessPoint ap = triangulation.calculateAccessPointPosition();
			String category = null;
			
			if(ap != null && (category = triangulation.getNearestCategory(ap.getPosX(), ap.getPosY())) != null) {
				Intent intent = new Intent(ProximityIntentReceiver.PROXIMITY_ALERT);
				// Add category information with AP's SSID as Extra data
				intent.putExtra("category", category); 
				context.sendBroadcast(intent);
			}
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		triangulation.clearList();
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		triangulation.clearList();
	}


	public boolean isBgrunFlag() {
		return bgrunFlag;
	}

}

