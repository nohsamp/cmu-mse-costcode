/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.task;

import java.util.List;

import android.os.AsyncTask;

import edu.cmu.cc.android.activity.async.IAsyncActivity;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.dao.SLItemDAO;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 21, 2013
 */
public class FetchSLItemsTask 
extends AsyncTask<ShoppingList, Void, List<Item>>{

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private IFetchSLItemsTaskCaller caller;
	
	private SLItemDAO itemDAO;
	
	private boolean errorState;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public FetchSLItemsTask(IFetchSLItemsTaskCaller caller) {
		super();
		
		this.caller = caller;
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PROTECTED METHODS
	//-------------------------------------------------------------------------
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showProgressDialog(
				R.string.sl_item_loading);
		
		itemDAO = new SLItemDAO();
		
		errorState = false;
	}
	
	@Override
	protected List<Item> doInBackground(ShoppingList... params) {
		
		try {
			
			if (params == null || params[0] == null) {
				throw new RuntimeException("Invalid input parameter: " +
						"ShoppingList is null");
			}
			
			ShoppingList sl = params[0];
			
			return retrieveFromLocal(sl);
			
		} catch (Throwable t) {
			errorState = true;
			caller.onAsyncTaskFailed(this.getClass(), t);
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(List<Item> items) {
		super.onPostExecute(items);
		
		itemDAO.close();
		
		caller.dismissProgressDialog();
		if (!errorState) {
			caller.onFetchSLItemsTaskSucceeded(items);
		}
	}


	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private List<Item> retrieveFromLocal(final ShoppingList sl) {
		
		return itemDAO.getAll(sl);
	}
	
	//-------------------------------------------------------------------------
	// INNER INTERFACE
	//-------------------------------------------------------------------------
	
	public interface IFetchSLItemsTaskCaller extends IAsyncActivity {
		
		public void onFetchSLItemsTaskSucceeded(List<Item> items);
		
	}

}
