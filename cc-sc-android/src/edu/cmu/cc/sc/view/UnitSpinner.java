/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view;

import java.util.Map;

import edu.cmu.cc.android.view.ValidatingSpinner;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.model.Item;
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
public class UnitSpinner extends ValidatingSpinner<String> {

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
	
	public UnitSpinner(Context ctx) {
		super(ctx, getEmptyItem());
		
		this.ctx = ctx;
		
		initializeAdapter();
	}
	
	public UnitSpinner(Context ctx, AttributeSet attrs) {
		super(ctx, attrs, getEmptyItem());
		
		this.ctx = ctx;
		
		initializeAdapter();
	}
	
	public UnitSpinner(Context ctx, AttributeSet attrs, int defStyle) {
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
	
	private static String getEmptyItem() {
		Context ctx = ApplicationState.getContext();
		
		String emptyItemCaption = ctx.getResources()
				.getString(R.string.sl_item_unit_empty);
		
		return emptyItemCaption;
	}
	
	private void initializeAdapter() {
		
		Map<Integer, String> unitsMap = Item.Unit.getUnitsMap();
		
		String[] units = new String[0];
		
		units = unitsMap.values().toArray(units);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ctx, R.layout.sl_item_unit_textview, units);
		
		adapter.setDropDownViewResource(R.layout.sl_item_unit_dropdownview);
		
		setAdapter(adapter);
	}

}
