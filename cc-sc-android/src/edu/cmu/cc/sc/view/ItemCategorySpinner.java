/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view;

import java.util.Map;

import edu.cmu.cc.android.view.ValidatingSpinner;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.model.ItemCategory;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 12, 2013
 */
public class ItemCategorySpinner extends ValidatingSpinner<ItemCategory> {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public ItemCategorySpinner(Context ctx) {
		super(ctx, getEmptyItem());
		
		this.ctx = ctx;
		
		initializeAdapter();
	}
	
	public ItemCategorySpinner(Context ctx, AttributeSet attrs) {
		super(ctx, attrs, getEmptyItem());
		
		this.ctx = ctx;
		
		initializeAdapter();
	}
	
	public ItemCategorySpinner(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle, getEmptyItem());
		
		this.ctx = ctx;
		
		initializeAdapter();
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private static ItemCategory getEmptyItem() {
		Context ctx = ApplicationState.getContext();
		
		String emptyItemCaption = ctx.getResources()
				.getString(R.string.sl_item_category_empty);
		
		ItemCategory emptyCategory = new ItemCategory();
		emptyCategory.setName(emptyItemCaption);
		
		return emptyCategory;
	}
	
	private void initializeAdapter() {
		
		Map<Long,ItemCategory> categoriesMap = 
				ApplicationState.getInstance().getCategories();
		
		ItemCategory[] categories = new ItemCategory[0];
		
		categories = categoriesMap.values().toArray(categories);
		
		ArrayAdapter<ItemCategory> adapter = new ArrayAdapter<ItemCategory>(
				ctx, R.layout.sl_item_category_textview, categories);
		
		adapter.setDropDownViewResource(R.layout.sl_item_category_dropdownview);
		
		setAdapter(adapter);
	}

}
