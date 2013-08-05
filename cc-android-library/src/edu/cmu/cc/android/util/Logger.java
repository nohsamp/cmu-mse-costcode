/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.util;

import edu.cmu.cc.android.R;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

/**
 *  DESCRIPTION: This class provides common logging functionality. Besides 
 *  standard logging, it enables to show Alert dialogs to the user. 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 11, 2013
 */
public class Logger {

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	public static void logInfo(Class<?> caller, String message) {
		Log.i(caller.getSimpleName(), message);
	}
	
	public static void logDebug(Class<?> caller, String message) {
		Log.d(caller.getSimpleName(), message);
	}
	
	public static void logDebug(Class<?> caller, String message, 
			Throwable t) {
		Log.d(caller.getSimpleName(), message, t);
	}
	
	public static void logError(Class<?> caller, String message) {
		Log.e(caller.getSimpleName(), message);
	}
	
	public static void logError(Class<?> caller, Throwable t) {
		Log.e(caller.getSimpleName(), t.toString(), t);
	}
	
	public static void logError(Class<?> caller, String message, 
			Throwable t) {
		Log.e(caller.getSimpleName(), message, t);
	}
	
	public static void logErrorAndThrow(Class<?> caller, Throwable t) {
		logError(caller, t);
		throw new RuntimeException(t);
	}
	
	public static void logErrorAndAlert(Context ctx, Class<?> caller, 
			String message, Throwable t) {
		
		if (t != null) {
			logError(caller, message, t);
		} else {
			logError(caller, message);
		}
		
		WidgetUtils.createOkAlertDialog(ctx, R.drawable.cancel, 
				R.string.androidUtil_error, message).show();
	}
	
	public static void logErrorAndAlert(Context ctx, Class<?> caller, 
			String message, Throwable t, OnClickListener listener) {
		
		if (t != null) {
			logError(caller, message, t);
		} else {
			logError(caller, message);
		}
		
		WidgetUtils.createOkAlertDialog(ctx, R.drawable.cancel, 
				R.string.androidUtil_error, message, listener).show();
	}
	
	public static void logErrorAndAlert(Context ctx, Class<?> caller, 
			String message) {
		logErrorAndAlert(ctx, caller, message, null);
	}
	
	

}
