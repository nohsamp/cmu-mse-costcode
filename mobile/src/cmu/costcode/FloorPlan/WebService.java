package cmu.costcode.FloorPlan;

import java.net.ConnectException;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {
	private String nameSpace, url, methodName, soapAction;
	
	/** Default constructor
	 * 
	 */
	public WebService() {
		nameSpace = "http://costcode.mse.cmu.edu/";
		url = "http://shltestweb.appspot.com/shlappeng";	
		methodName = "sayHello";
		soapAction = nameSpace + methodName;
	}
	
	/**
	 * 
	 * @param nameSpace The web method namespace
	 * @param methodName The method name for Web Service
	 * @param url The complete URL where the web service resides 
	 */
	public WebService(String nameSpace, String methodName, String url) {
		this.nameSpace = nameSpace;
		this.methodName = methodName;
		this.url = url;
		soapAction = nameSpace + methodName;
	}
	
	/** Constructor with namespace and URL
	 * @param nameSpace The web method namespace
	 * @param url The complete URL where the web service resides 
	 */
	public WebService(String nameSpace, String url) {
		this.nameSpace = nameSpace;
		this.url = url;
	}
	
	/** Call the web service with methodName
	 * and return the SOAP object as a return value of Web Service
	 * @param methodName The method name
	 * @param arguments The arguments of method
	 * @return Web Service result (SOAP object)
	 * @throws Exception 
	 */
	public SoapObject invokeMethod(String methodName, Map<String, String> arguments) throws Exception {
		this.methodName = methodName;
		soapAction = nameSpace + methodName;
		return invokeMethod(arguments);
	}
	
	/** Call the default web service method 
	 * and return the SOAP object as a return value of Web Service
	 * @param arguments The arguments of method
	 * @return Web Service result (SOAP object)
	 */
	public SoapObject invokeMethod(Map<String, String> arguments) throws Exception {
		SoapObject request = new SoapObject(nameSpace, methodName);
		
		// passing method arguments to SOAP request
		for( Map.Entry<String, String> entry : arguments.entrySet()) {
			request.addProperty(entry.getKey().toString(), entry.getValue().toString());
		}
		
		// get envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12); 
		envelope.setOutputSoapObject(request);
		envelope.dotNet = false;
		
		return makeCall(envelope);
	}

	/** 
	 * @param envelope The envelope of Web Service
     * @return Web Service result (SOAP object)
	 * @throws Exception 
	 */
	private SoapObject makeCall(SoapSerializationEnvelope envelope) throws Exception {
		SoapObject result; // Return object
		
		HttpTransportSE androidHttpTransport = new HttpTransportSE(url);

		try {
			androidHttpTransport.call(soapAction, envelope);
			SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			result = resultsRequestSOAP;
		} catch (ConnectException e) {
			throw e; 
//			result."Network error. Check Internet", methodName);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
}
