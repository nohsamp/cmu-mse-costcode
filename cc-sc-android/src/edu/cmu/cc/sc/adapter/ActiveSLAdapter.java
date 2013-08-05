/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.adapter;

import java.util.List;

import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 21, 2013
 */
public class ActiveSLAdapter extends AbstractSharedPrefsAdapter {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	private static String KEY_ACTIVE_SHOPPINGLIST = "active-shoppinglist";
	
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
	
	public static synchronized boolean persistActiveSL(ShoppingList sl) {
		
		return saveToSharedPrefs(ActiveSLAdapter.class, 
				ApplicationState.getContext(), KEY_ACTIVE_SHOPPINGLIST, 
				String.valueOf(sl.getId()), R.string.sl_active_error_persist);
	}
	
	public static ShoppingList retrieveActiveSL() {
		
		String strId = retrieveFromSharedPrefs(ActiveSLAdapter.class, 
				ApplicationState.getContext(), KEY_ACTIVE_SHOPPINGLIST, 
				R.string.sl_active_error_retrieve);
		
		if (!StringUtils.isNullOrEmpty(strId)) {
			return findSLById(Long.parseLong(strId));
		}
		
		return null;
	}
	

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private static ShoppingList findSLById(long id) {
		
		List<ShoppingList> list = 
				ApplicationState.getInstance().getShoppingLists();
		
		if (list != null && list.size() > 0) {
			for (ShoppingList sl : list) {
				if (sl.getId() == id) {
					return sl;
				}
			}
		}
		
		return null;
	}

}
