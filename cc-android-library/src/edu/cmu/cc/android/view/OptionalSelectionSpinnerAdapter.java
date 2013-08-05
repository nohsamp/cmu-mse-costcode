/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 *  DESCRIPTION: Spinner adapter class.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 10, 2013
 */
public class OptionalSelectionSpinnerAdapter<T> extends BaseAdapter 
implements SpinnerAdapter {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;
	
	private SpinnerAdapter adapter;
	
	private T emptyItem;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public OptionalSelectionSpinnerAdapter(Context ctx, 
			SpinnerAdapter adapter, T emptyItem) {
		
		this.ctx = ctx;
		this.adapter = adapter;
		this.emptyItem = emptyItem;
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	public int getCount() {
		return adapter.getCount() + 1;
	}

	@Override
	public Object getItem(int position) {
		
		if (position == 0) {
			return emptyItem;
		} else if (position > 0) {
			return adapter.getItem(position - 1);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if (position == 0) {
			TextView tvEmptyItem = new TextView(ctx);
			tvEmptyItem.setText(emptyItem.toString());
			
			return tvEmptyItem;
		} else if (position > 0) {
			return adapter.getView(position-1, null, parent);
		}
		
		return null;
	}

	@Override
	public View getDropDownView(int position, View convertView, 
			ViewGroup parent) {
		
		if (position == 0) {
			return new TextView(ctx);
		} else if (position > 0) {
			return adapter.getDropDownView(position-1, null, parent);
		}
		
		return null;
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------

}