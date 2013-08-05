package cmu.costcode.FloorPlan;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.ShoppingList.objects.Category;
import cmu.costcode.ShoppingList.objects.Category.Location;
import cmu.costcode.WIFIScanner.AccessPoint;

/**
 * @author NohSam
 * Call web service for the access point locations
 */
public class FloorPlan extends Thread {

	private static final String TAG = "FloorPlan";

	private List<AccessPoint> apList = null;
	private List<Category> categoryList = null;

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
	public List<Category> getCategories() {
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
		for(Iterator<Category> itr = categoryList.iterator(); itr.hasNext(); ) {
			Category category = itr.next();
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

		categoryList = new ArrayList<Category>();

		AccessPoint ap = null;
		boolean ssidFlag = true;

		String categoryName = null;
		double posx=0, posy=0;
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

				categoryName = splits[++index];
				index += 2; // skip next xml tag: <category>food</category>
			}
			else if(splits[index].toLowerCase().equals("posx")) {
				if(ssidFlag) { // if data is AP
					ap.setPosX(Float.parseFloat(splits[++index]));
				}
				else { // if data is category
					posx = Double.parseDouble(splits[++index]);
				}
				index += 2; // skip next xml tag

			}
			else if(splits[index].toLowerCase().equals("posy")) {
				if(ssidFlag) { // if data is AP
					ap.setPosY(Float.parseFloat(splits[++index]));
					apList.add(ap);
				}
				else { // if data is category
					posy = Double.parseDouble(splits[++index]);
					categoryList.add(new Category(categoryName, new Location(posx, posy)));
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
			if(!checkFloorplanVersion(webService.invokeMethod("checkVersion", wsArguments))) {
				getAPsFromSoap(webService.invokeMethod("APsLocation", wsArguments));
				getCategoriesFromSoap(webService.invokeMethod("SectionsLocation", wsArguments));
			}
		} catch (ConnectException e) {
			Log.d(TAG, e.getMessage());
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
}
