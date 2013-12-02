/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.activity.async;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

/**
 * DESCRIPTION: This abstract activity provides Progress dialog functionality.
 * Any activity using Asynchronous task should extends this class.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 7, 2013
 */
public abstract class AbstractAsyncActivity extends Activity implements
		IAsyncActivity {

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private AsyncActivityHelper helper;

	private Handler asyncTaskHandler;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public AbstractAsyncActivity() {
		super();
		helper = new AsyncActivityHelper(this);
	}

	// -------------------------------------------------------------------------
	// PROTECTED METHODS
	// -------------------------------------------------------------------------

	@Override
	protected void onResume() {
		super.onResume();
		asyncTaskHandler = new Handler();
	}

	protected void addTaskToUIQueue(Runnable callback) {
		Message osMessage = Message.obtain(this.asyncTaskHandler, callback);
		osMessage.sendToTarget();
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public void showProgressDialog(int titleResID, int textResID) {
		helper.showProgressDialog(titleResID, textResID);
	}

	@Override
	public void showProgressDialog(int textResID) {
		helper.showProgressDialog(textResID);
	}

	@Override
	public void dismissProgressDialog() {
		helper.dismissProgressDialog();
	}

	@Override
	public void onAsyncTaskSucceeded(Class<?> taskClass) {
	}

	@Override
	public void onAsyncTaskFailed(Class<?> taskClass, Throwable t) {
	}

}
