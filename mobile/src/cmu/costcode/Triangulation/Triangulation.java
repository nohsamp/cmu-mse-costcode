package cmu.costcode.Triangulation;

import java.util.List;
import java.util.Map;

import android.net.wifi.WifiManager;

import cmu.costcode.WIFIScanner.AccessPoint;
import cmu.costcode.WIFIScanner.WiFiScanner;

public abstract class Triangulation {
	protected List<AccessPoint> apList;
	protected WiFiScanner wifiScanner; 
	
	public static String TRIANG_METHOD = "TRIANG_METHOD";
	public static String SCAN_NUMBER = "SCAN_NUMBER"; 
	
	// Parameters for WiFi scanning
	protected String triangMethod = "WCL";	// triangulation method: WCL, AWCL
	protected int scanNumber = 5;				// number of wifi scan results
	
	public Triangulation(WifiManager wm, Map<String, Object> initParams) {
		triangMethod = (String) initParams.get(TRIANG_METHOD);
		scanNumber = (Integer) initParams.get(SCAN_NUMBER);
		wifiScanner = new WiFiScanner(wm, scanNumber);
	}
	
	public AccessPoint calculateAccessPointPosition() {
		apList = wifiScanner.scanStart();
		return apList.get(0);
	}
	
	public void clearApList() {
		apList.clear();
	}

}
