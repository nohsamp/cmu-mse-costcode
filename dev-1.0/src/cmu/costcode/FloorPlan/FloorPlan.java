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

import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.WIFIScanner.AccessPoint;

/**
 * @author NohSam
 * Call web service for the access point locations
 */
public class FloorPlan implements Runnable {
	private List<AccessPoint> apList = null;
	private WebService webService;				// Web Service Class for getting floor plan
	private Map<String, String> wsArguments = null;	// Web Service Input parameters
	private DatabaseAdaptor db;
	private final static String INFO_NAME = "floorplan";

	/** Constructor for floorplan
	 * 
	 */
	public FloorPlan(Context context) {
		String nameSpace ="namespace";
		String url = "url";
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
	
	/** Parse results and retrieve data
	 * 
	 * @param soap The returned SOAP object
	 */
	private void getAPsFromSoap(SoapObject soap) {
		if(soap == null) {
//			apList = null;
//			return;
		// TODO: delete the below part and uncomment the above
			
			// make dummy AP information
			soap = new SoapObject();
			String[] ssid = {"CMU", "CMU-SECURE", "PSC", "LINKCISCO"};
			for(int i=0;i<4;i++) {
				SoapObject sp = new SoapObject();
				sp.addProperty("BSSID", "some value");
				sp.addProperty("SSID", ssid[i]);
				sp.addProperty("POSX", String.valueOf(i).concat(".0"));
				sp.addProperty("POSY", String.valueOf(i).concat(".0"));
				
				soap.addSoapObject(sp);
			}
		}
		
		// Delete the existing AP information
		db.open();
		db.dbDeleteAccessPoint();
		db.close();
		
		apList = new ArrayList<AccessPoint>(soap.getPropertyCount());
		
		for(int i=0; i<soap.getPropertyCount(); i++) {
			SoapObject pii = (SoapObject)soap.getProperty(i);
			
			// Making new AccessPoint object
			AccessPoint ap = new AccessPoint();
			ap.setBssid(pii.getPropertyAsString(0));
			ap.setSsid(pii.getPropertyAsString(1));
			ap.setPosX(Float.parseFloat(pii.getPropertyAsString(2)));
			ap.setPosY(Float.parseFloat(pii.getPropertyAsString(3)));
			apList.add(ap);
		}
		
		saveAccessPoints();
	}

	/** Set Web Service Inputs
	 * 
	 */
	private void setWSInputs() {
		wsArguments = new HashMap<String, String>(1);
		wsArguments.put("input1", "value1");
	}
	
	private boolean checkFloorplanVersion(SoapObject soapObject) {
		if(soapObject == null) {
//			return true; // use the existing version of floor plan regardless of error
		// TODO: delete the below part and uncomment the above
			// make dummy version information
			soapObject = new SoapObject();
			soapObject.addProperty("Version", "1.0");
		}
		
		db.open();
		String oldVersion = db.dbGetVersion(INFO_NAME);
		
		// Check version
		boolean result = false;
		if(oldVersion != null) {
			result = oldVersion.equals(soapObject.getPropertyAsString("Version"));
		}
		// If not equal
		if(!result) {
			// insert or update
			db.dbSetVersion(INFO_NAME, soapObject.getPropertyAsString("Version"), "Floorplan Information Version");
		}
		db.close();
		return result;
	}
	
	@Override
	public void run() {
		setWSInputs();
		try {
//			if(!checkFloorplanVersion(webService.invokeMethod("checkVersion", wsArguments))) {
//				getAPsFromSoap(webService.invokeMethod("methodName", wsArguments));
//			}
			//TODO: change the above with the below 
			if(!checkFloorplanVersion(null)) {
				getAPsFromSoap(null);
			}
//		} catch (ConnectException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	
}
