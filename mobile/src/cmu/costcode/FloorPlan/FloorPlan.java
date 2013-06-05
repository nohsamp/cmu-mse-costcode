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

import cmu.costcode.WIFIScanner.AccessPoint;

/**
 * @author NohSam
 * Call web service for the access point locations
 */
public class FloorPlan implements Runnable {
	private List<AccessPoint> apList = null;
	private WebService webService;				// Web Service Class for getting floorplan
	private Map<String, String> wsArguments = null;	// Web Service Input parameters

	/** Constructor for floorplan
	 * 
	 */
	public FloorPlan() {
		String nameSpace ="namespace";
		String url = "url";
		webService = new WebService(nameSpace, url);
		
	}
	
	/** Get Access Points information
	 * @return List<AccessPoint>
	 */
	public List<AccessPoint> getAccessPoints() {
		return apList;
	}
	
	/** Parse results and retrieve data
	 * 
	 * @param soap The returned SOAP object
	 */
	public void getAPsFromSoap(SoapObject soap) {
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
	}

	/** Set Web Service Inputs
	 * 
	 */
	private void setWSInputs() {
		wsArguments = new HashMap<String, String>(1);
		wsArguments.put("input1", "value1");
	}
	
	
	
	@Override
	public void run() {
		setWSInputs();
		try {
			getAPsFromSoap(webService.invokeMethod("methodName", wsArguments));
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
