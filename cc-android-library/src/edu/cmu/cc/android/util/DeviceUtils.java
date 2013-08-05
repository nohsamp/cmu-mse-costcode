/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.util;

import edu.cmu.cc.android.R;
import android.content.Context;
import android.net.ConnectivityManager;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 13, 2013
 */
public class DeviceUtils {

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
	
	/**
	 * Checks whether the device is connected to the Network. If not, it will
	 * log error and display error dialog to the user
	 * @param ctx - android context
	 * @param caller - calling class
	 * @return <b>true</b> - if connected, <b>false</b> - if not connected
	 */
	public static boolean isNetworkConnectedElseAlert(Context ctx, 
			Class<?> caller) {
		
		boolean connected = isNetworkConnected(ctx);
		if (!connected) {
			Logger.logErrorAndAlert(ctx, caller, 
					ctx.getString(R.string.androidUtil_error_notConnected));
		}
		
		return connected;
	}
	
	public static boolean isNetworkConnectedElseAlert(Context ctx, 
			Class<?> caller, int msgResID) {
		
		boolean connected = isNetworkConnected(ctx);
		if (!connected) {
			Logger.logErrorAndAlert(ctx, caller, ctx.getString(msgResID));
		}
		
		return connected;
	}
	
	/**
	 * Checks whether the device is connected to the Network.
	 * @param ctx - android context
	 * @return <b>true</b> - if connected, <b>false</b> - if not connected
	 */
	public static boolean isNetworkConnected(Context ctx) {
		
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return manager.getActiveNetworkInfo().isConnected();
	}

	//-------------------------------------------------------------------------
	// HELPER METHODS
	//-------------------------------------------------------------------------

}
