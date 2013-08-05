/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.service.soap.util;

import org.ksoap2.serialization.SoapObject;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 24, 2013
 */
public class SoapUtils {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	public static synchronized long getLongPropertyValue(SoapObject source, 
			String attributeName) {
		
		return Long.parseLong(source.getAttributeAsString(attributeName));
	}
	
	public static synchronized int getIntPropertyValue(SoapObject source, 
			String attributeName) {
		
		return Integer.parseInt(source.getAttributeAsString(attributeName));
	}
	
	public static synchronized double getDoublePropertyValue(
			SoapObject source, String attributeName) {
		
		return Double.parseDouble(source.getAttributeAsString(attributeName));
	}
	
	public static synchronized boolean getBooleanPropertyValue(
			SoapObject source, String attributeName) {
		
		return Boolean.parseBoolean(source.getAttributeAsString(attributeName));
	}

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
}
