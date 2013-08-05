/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.activity.async;

import android.app.ProgressDialog;
import android.content.Context;

/**
 *  DESCRIPTION: This class provides progress dialog methods
 *  for Asynchronous activities.  
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 7, 2013
 */
public class AsyncActivityHelper {

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;
	
	private ProgressDialog dialog;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------

	public AsyncActivityHelper(Context ctx) {
		this.ctx = ctx;
	}
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	/**
	 * Display progress dialog without title
	 */
	public void showProgressDialog(int textResID) {
		dialog = ProgressDialog
				.show(ctx, null, ctx.getString(textResID), true);
	}
	
	/**
	 * Display progress dialog with title
	 */
	public void showProgressDialog(int titleResID, int textResID) {
		dialog = ProgressDialog.show(ctx, 
				ctx.getString(titleResID), 
				ctx.getString(textResID), true);
	}
	
	/**
	 * Dismiss progress dialog
	 */
	public void dismissProgressDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	
}
