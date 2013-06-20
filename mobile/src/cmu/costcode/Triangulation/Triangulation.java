package cmu.costcode.Triangulation;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiManager;

import cmu.costcode.WIFIScanner.AccessPoint;
import cmu.costcode.WIFIScanner.WiFiScanner;

public abstract class Triangulation {
	protected List<AccessPoint> apList;
	protected WiFiScanner wifiScanner; 
	
	public static String TRIANG_METHOD = "TRIANG_METHOD";
	public static String SCAN_NUMBER = "SCAN_NUMBER"; 
	public static String NOISE_FILTER = "NOISE_FILTER";
	
	// Parameters for WiFi scanning
	protected String triangMethod = "WCL";	// triangulation method: WCL, AWCL
	protected int scanNumber = 5;				// number of wifi scan results
	protected boolean noiseFilterFlag = true;	// Filter for dropping AP with noise data
	
	public Triangulation(WifiManager wm, Map<String, Object> initParams, Context context) {
		triangMethod = (String) initParams.get(TRIANG_METHOD);
		
		wifiScanner = new WiFiScanner(wm, initParams, context);
	}
	
	public AccessPoint calculateAccessPointPosition() {
		wifiScanner.scanStart();
		apList = wifiScanner.getApList();
		return apList.get(0);
	}
	
	public void clearApList() {
		apList.clear();
	}

}
