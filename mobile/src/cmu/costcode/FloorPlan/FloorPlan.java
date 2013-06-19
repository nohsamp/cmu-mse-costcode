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

import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.WIFIScanner.AccessPoint;

/**
 * @author NohSam
 * Call web service for the access point locations
 */
public class FloorPlan extends Thread {
	private List<AccessPoint> apList = null;
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
	
	/** Parse results and retrieve data
	 * 
	 * @param soap The returned SOAP object
	 */
	private void getAPsFromSoap(SoapObject soap) {
		if(soap == null) {
//			apList = null;
//			return;
		// TODO: delete the next part and uncomment the above
			
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
		
		// TODO: uncomment the next line
//		parseXML(soap.getPrimitivePropertyAsString("APsLocation"));
		
		// TODO: comment the next part before saveAccessPoints()
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

	private void parseXML(String result) {
		String[] splits = result.replaceAll("<", "").replaceAll("/", ">").split(">");
		int index = 0;
		
		apList = new ArrayList<AccessPoint>();
		
		AccessPoint ap = null;
		while(true) {
        	if(splits[index].equals("ssid")) {
        		if(ap != null) // if the ap is not the first, add AP to the list
        			apList.add(ap);
        		ap = new AccessPoint(); // then create new AP
        		ap.setSsid(splits[++index]);
        		index += 2; // skip next xml tag: <ssid>CMU</ssid>
        	}
        	if(splits[index].equals("posx")) {
        		ap.setPosX(Float.parseFloat(splits[++index]));
        		index += 2; // skip next xml tag: <ssid>CMU</ssid>

        	}
        	if(splits[index].equals("posy")) {
        		ap.setPosY(Float.parseFloat(splits[++index]));
        		index += 2; // skip next xml tag: <ssid>CMU</ssid>

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
		wsArguments.put("warehouseID", "1");
	}
	
	private boolean checkFloorplanVersion(SoapObject soapObject) {
		if(soapObject == null) {
//			return true; // use the existing version of floor plan regardless of error
		// TODO: delete the below part and uncomment the above
			// make dummy version information
			soapObject = new SoapObject();
			soapObject.addProperty("version", "1.0");
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
//			if(!checkFloorplanVersion(webService.invokeMethod("checkVersion", wsArguments))) {
//				getAPsFromSoap(webService.invokeMethod("APsLocation", wsArguments));
//			}
			//TODO: comment the next part
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
