/**
 * 
 */
package cmu.costcode.FloorPlan;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.WIFIScanner.AccessPoint;

/**
 * @author NohSam
 * Call web service for the access point locations
 */
public class FloorPlan extends Thread {
	
	private static final String TAG = "FloorPlan";
	
	private List<AccessPoint> apList = null;
	private List<AccessPoint> categoryList = null; // This is not Access point, but use the same object
	
	private WebService webService;				// Web Service Class for getting floor plan
	private Map<String, String> wsArguments = null;	// Web Service Input parameters
	private DatabaseAdaptor db;
	private final static String INFO_NAME = "floorplan";

	/** Constructor for floorplan
	 * 
	 */
	public FloorPlan(Context context) {
		String nameSpace ="http://ws.biz.slh.cc.mse.cmu.edu/";
		String url = "http://slhwsapp-costcode.rhcloud.com:80/FloorPlanWS";
		webService = new WebService(nameSpace, url);
		db = new DatabaseAdaptor(context);
	}
	
	/** Get Access Points information
	 * @return List<AccessPoint>
	 */
	public List<AccessPoint> getAccessPoints() {
		if(apList == null) {
			db.open();
			apList = db.dbGetAccessPoint();
			db.close();
		}
		return apList;
	}
	
	/** Save Access Points information to the DB
	 */
	private void saveAccessPoints() {
		// Open database
		db.open();
		
		// Insert apList into db
		for(Iterator<AccessPoint> itr = apList.iterator(); itr.hasNext(); ) {
			AccessPoint ap = itr.next();
			db.dbCreateAccessPoint(ap);
		}
		
		// Close database
		db.close();
	}
	
	/** Get Category information
	 * @return List<AccessPoint>
	 */
	public List<AccessPoint> getCategories() {
		if(categoryList == null) {
			db.open();
			categoryList = db.dbGetCategory();
			db.close();
		}
		return categoryList;
	}
	
	/** Save Categories information to the DB
	 */
	private void saveCategories() {
		// Open database
		db.open();
		
		// Insert apList into db
		for(Iterator<AccessPoint> itr = categoryList.iterator(); itr.hasNext(); ) {
			AccessPoint category = itr.next();
			db.dbCreateCategory(category);
		}
		
		// Close database
		db.close();
	}
	
	/** Parse results and retrieve Access Point data
	 * 
	 * @param soap The returned SOAP object
	 */
	private void getAPsFromSoap(SoapObject soap) {
		if(soap == null) {
			apList = null;
			return;
		}
		
//		 Delete the existing AP information
		db.open();
		db.dbDeleteAccessPoint();
		db.close();
		
		// TODO: uncomment the next line
//		if(soap == null) {
//			String testString = "<ssid>food</ssid><posx>0.0</posx><posy>0.0</posy><ssid>CMU-SECURE</ssid><posx>20.0</posx><posy>0.5</posy>" +
//					"<ssid>PSC</ssid><posx>20.5</posx><posy>-20</posy><ssid>CMU</ssid><posx>0</posx><posy>-20.4</posy>";
//			parseXML(testString);
//		}
		parseXML(soap.getPrimitivePropertyAsString("APsLocation"));
		
		// after parse, insert into DB
		if(apList != null) {
			saveAccessPoints();
		}
	}
	
	/** Parse results and retrieve category data
	 * 
	 * @param soap The returned SOAP object
	 */
	private void getCategoriesFromSoap(SoapObject soap) {
		if(soap == null) {
			categoryList =null;
			return;
		}
		
//		 Delete the existing category information
		db.open();
		db.dbDeleteCategory();
		db.close();
		
		// TODO: uncomment the next line
//		if(soap == null) {
//			String testString = "<category>food</category><posx>5.3</posx><posy>-3.5</posy>" +
//					"<category>electronics</category><posx>13</posx><posy>-7.3</posy>" +
//					"<category>furniture</category><posx>13</posx><posy>-7.3</posy>" +
//					"<category>clothes</category><posx>18</posx><posy>-15</posy>";
//			parseXML(testString);
//		}
		parseXML(soap.getPrimitivePropertyAsString("SectionsLocation"));
		
		// after parse, insert into DB
		if(categoryList != null) {
			saveCategories();
		}
	}

	
	private void parseXML(String result) {
		// Result format
		// <ssid>name</ssid><posx>x</posx><posy>y</posy><category>name</ssid><posx>x</posx><posy>y</posy>
		
		String[] splits = result.replaceAll("<", "").replaceAll("/", ">").split(">");
		int index = 0;
		
		apList = new ArrayList<AccessPoint>();
		
		categoryList = new ArrayList<AccessPoint>(); // use the same object in Category
		
		AccessPoint ap = null;
		AccessPoint category = null;
		boolean ssidFlag = true;
		
		while(true) {
        	if(splits[index].toLowerCase().equals("ssid")) {
        		ssidFlag = true; // Data is AP
        		
        		ap = new AccessPoint(); // create new AP
        		ap.setSsid(splits[++index]); // set the name of AP
        		index += 2; // skip next xml tag: <ssid>CMU</ssid>
        	}
        	else if(splits[index].toLowerCase().equals("sid")) {
//        	else if(splits[index].toLowerCase().equals("category")) {
        		ssidFlag = false; // Data is category
        		
        		category = new AccessPoint(); // create new AP
        		category.setSsid(splits[++index]); // set the name of category
        		index += 2; // skip next xml tag: <category>food</category>
        	}
        	else if(splits[index].toLowerCase().equals("posx")) {
        		if(ssidFlag) { // if data is AP
	        		ap.setPosX(Float.parseFloat(splits[++index]));
        		}
        		else { // if data is category
        			category.setPosX(Float.parseFloat(splits[++index]));
        		}
	        		index += 2; // skip next xml tag

        	}
        	else if(splits[index].toLowerCase().equals("posy")) {
        		if(ssidFlag) { // if data is AP
	        		ap.setPosY(Float.parseFloat(splits[++index]));
	        		apList.add(ap);
        		}
        		else { // if data is category
        			category.setPosY(Float.parseFloat(splits[++index]));
        			categoryList.add(category);
        		}

        		index += 2; // skip next xml tag: <ssid>CMU</ssid>
        	}
        	else {
        		index++;
        	}
        	if(index >= splits.length)
        		break;
        }
		
		
		
	}

	/** Set Web Service Inputs
	 * 
	 */
	private void setWSInputs() {
		wsArguments = new HashMap<String, String>(1);
		wsArguments.put("warehouseID", "2");
	}
	
	private boolean checkFloorplanVersion(SoapObject soapObject) {
		if(soapObject == null) {
			return true; // use the existing version of floor plan regardless of error
			// make dummy version information
//			soapObject = new SoapObject();
//			soapObject.addProperty("version", "1.0");
		}
		
		db.open();
		String oldVersion = db.dbGetVersion(INFO_NAME);
		
		// Check version
		boolean result = false;
		if(oldVersion != null) {
			result = oldVersion.equals(soapObject.getPropertyAsString("version"));
		}
		// If not equal
		if(!result) {
			// insert or update
			db.dbSetVersion(INFO_NAME, soapObject.getPropertyAsString("version"), "Floorplan Information Version");
		}
		db.close();
		return result;
	}
	
	@Override
	public void run() {
		// set warehouse ID as web service input
		setWSInputs();

		try {
			//TODO: uncomment the next
			if(!checkFloorplanVersion(webService.invokeMethod("checkVersion", wsArguments))) {
				getAPsFromSoap(webService.invokeMethod("APsLocation", wsArguments));
				getCategoriesFromSoap(webService.invokeMethod("SectionsLocation", wsArguments));
			}
			//TODO: comment the next part
//			if(!checkFloorplanVersion(null)) {
//				getAPsFromSoap(null);
//			}
		} catch (ConnectException e) {
			Log.d(TAG, e.getMessage());
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	
}
