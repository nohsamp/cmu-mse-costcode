/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 *  DESCRIPTION: Custom spinner class. It assigns a custom adapter 
 *  to the spinner. 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 10, 2013
 */
public class OptionalSelectionSpinner<T> extends Spinner {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;
	
	private T emptyItem;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public OptionalSelectionSpinner(Context ctx, T emptyItem) {
		super(ctx);
		
		this.ctx = ctx;
		this.emptyItem = emptyItem;
	}
	
	public OptionalSelectionSpinner(Context ctx, AttributeSet attrs, 
			T emptyItem) {
		super(ctx, attrs);
		
		this.ctx = ctx;
		this.emptyItem = emptyItem;
	}
	
	public OptionalSelectionSpinner(Context ctx, AttributeSet attrs, 
			int defStyle, T emptyItem) {
		super(ctx, attrs, defStyle);
		
		this.ctx = ctx;
		this.emptyItem = emptyItem;
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		
		if (adapter instanceof OptionalSelectionSpinnerAdapter) {
			super.setAdapter(adapter);
		} else {
			super.setAdapter(new OptionalSelectionSpinnerAdapter<T>(
					ctx, adapter, emptyItem));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSelectedItem() {
		return (T) super.getSelectedItem();
	}
	
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------

}
