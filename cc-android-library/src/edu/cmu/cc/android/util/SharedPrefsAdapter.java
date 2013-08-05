/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


/**
 *  DESCRIPTION: This class provides easy mechanism to store and retrieve
 *  data from the shared preferences file.
 *	
 *  @author Azamat Samiyev
 *	@version 2.0
 *  Date: Jun 14, 2013
 */
public class SharedPrefsAdapter {

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	/**
	 * Storing the given value by given key
	 * @param ctx - android context
	 * @param key - key of the value to be stored
	 * @param value - data to be stored
	 * @return <b>true</b> - data was successfully stored, <b>false</b> - if not
	 */
	public static boolean persist(Context ctx, String key, String value) {
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		
		Editor editor = preferences.edit();
		
		editor.putString(key, value);
		
		return editor.commit();
	}
	
	/**
	 * Retrieving a stored value by the given key
	 * @param ctx - android context
	 * @param key - key by which the data to be retrieved
	 * @return data value
	 */
	public static String retrieve(Context ctx, String key) {
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		
		return preferences.getString(key, null);
	} 

	
}
