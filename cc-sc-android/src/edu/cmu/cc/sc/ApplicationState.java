/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc;

import java.util.List;
import java.util.Map;

import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ItemCategory;
import edu.cmu.cc.sc.model.ShoppingList;
import android.app.Application;
import android.content.Context;
import android.util.Log;


/**
 *  DESCRIPTION: This class is used to hold application data.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 10, 2013
 */
public class ApplicationState extends Application {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private static ApplicationState instance;
	
	
	private String memberId;
	
	private Map<Long,ItemCategory> categories;
	
	private List<ShoppingList> shoppingLists;
	
	private ShoppingList currentSL;
	
	private Item currentSLItem;
	
	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public ApplicationState() {}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------
	
	public static ApplicationState getInstance() {
		return instance;
	}
	
	public static Context getContext() {
		return instance.getApplicationContext();
	}
	
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}	
	
	public Map<Long,ItemCategory> getCategories() {
		return categories;
	}
	public void setCategories(Map<Long,ItemCategory> categories) {
		this.categories = categories;
	}
	
	public List<ShoppingList> getShoppingLists() {
		return shoppingLists;
	}
	public void setShoppingLists(List<ShoppingList> shoppingLists) {
		this.shoppingLists = shoppingLists;
	}

	public ShoppingList getCurrentSL() {
		return currentSL;
	}
	public void setCurrentSL(ShoppingList currentSL) {
		this.currentSL = currentSL;
	}
	
	public Item getCurrentSLItem() {
		return currentSLItem;
	}
	public void setCurrentSLItem(Item currentSLItem) {
		this.currentSLItem = currentSLItem;
	}
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(getClass().toString(), "SLH application was created...");
		instance = this;
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------

}