/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.adapter;

import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 18, 2013
 */
public class ItemCategoryAdapter extends AbstractSharedPrefsAdapter {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	private static String KEY_ITEMCATEGORIES_VERSION = "version-itemcategories";

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
	
	public static synchronized boolean persistVersion(int version) {
		
		return saveToSharedPrefs(ItemCategoryAdapter.class, 
				ApplicationState.getContext(), KEY_ITEMCATEGORIES_VERSION, 
				String.valueOf(version), 
				R.string.itemcategory_error_versionPersist);
	}
	
	public static int retrieveVersion() {
		
		String strValue = retrieveFromSharedPrefs(ItemCategoryAdapter.class, 
				ApplicationState.getContext(), KEY_ITEMCATEGORIES_VERSION, 
				R.string.itemcategory_error_versionRetrieve);
		
		if (StringUtils.isNullOrEmpty(strValue)) {
			return -1;
		}
		
		return Integer.parseInt(strValue);
	}

}
