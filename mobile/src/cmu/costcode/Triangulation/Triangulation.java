package cmu.costcode.Triangulation;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiManager;

import cmu.costcode.ShoppingList.objects.Category;
import cmu.costcode.WIFIScanner.AccessPoint;
import cmu.costcode.WIFIScanner.WiFiScanner;

public abstract class Triangulation {
	protected List<AccessPoint> apList;
	protected List<Category> categoryList;
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
	
	/**
	 * Find the nearest Access Point and set the current position
	 * @return The nearest Access Point with the current position
	 */
	public AccessPoint calculateAccessPointPosition() {
		wifiScanner.scanStart();
		apList = wifiScanner.getApList();
		return apList.get(0);
	}
	
	public void clearList() {
		wifiScanner.scanStop();
		if(apList != null)
			apList.clear();
		if(categoryList != null)
			categoryList.clear();
	}
	
	/**
	 * Find the nearest category using the current position
	 * @param x X coordinate of the current position
	 * @param y Y coordinate of the current position 
	 * @return Category name for notification
	 */
	public String getNearestCategory(double x, double y) {
		return null;
	}

}
