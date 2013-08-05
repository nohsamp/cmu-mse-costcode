/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.task;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.AsyncTask;
import edu.cmu.cc.android.activity.async.IAsyncActivity;
import edu.cmu.cc.android.service.soap.SoapWebService;
import edu.cmu.cc.android.util.DeviceUtils;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.adapter.ActivationAdapter;
import edu.cmu.cc.sc.adapter.SLAdapter;
import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 20, 2013
 */
public class DeleteSLTask extends AsyncTask<ShoppingList, Void, Void> {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;
	
	private IAsyncActivity caller;
	
	private SLDAO slDAO;
	
	private String memberId;
	
	private boolean errorState;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public DeleteSLTask(Context ctx, IAsyncActivity caller) {
		super();
		
		this.ctx = ctx;
		this.caller = caller;
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if (!DeviceUtils.isNetworkConnectedElseAlert(
				ctx, getClass(), R.string.ws_error_noconnection)) {
			this.cancel(true);
		}
		
		caller.showProgressDialog(R.string.sl_all_deleting);
		
		slDAO = new SLDAO();
		
		errorState = false;
	}
	
	@Override
	protected Void doInBackground(ShoppingList... params) {
		
		try {
			
			if (params == null || params[0] == null) {
				throw new RuntimeException("Invalid input parameter: " +
						"ShoppingList is null");
			}
			ShoppingList sl = params[0];
			
//			//---------------------------------------------------
//			// Getting User Membership ID
//			//---------------------------------------------------
//			
//			memberId = ActivationAdapter.retrieveMemberId();
//			if (StringUtils.isNullOrEmpty(memberId)) {
//				throw new RuntimeException("MemberID is null or empty!");
//			}
			
			//---------------------------------------------------
			// Deleting ShoppingList
			//---------------------------------------------------
			
			deleteLocalSL(sl);
		} catch (Throwable t) {
			errorState = true;
			caller.onAsyncTaskFailed(this.getClass(), t);
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		slDAO.close();
		
		caller.dismissProgressDialog();
		if (!errorState) {
			caller.onAsyncTaskSucceeded(getClass());
		}
	}

	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private SoapObject deleteServerSL(ShoppingList sl) throws Throwable {
		
		Logger.logDebug(this.getClass(), String.format("Deleting " +
				"ShoppingList[%s] from the server...", sl));
		
		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_sl_namespace),
				ctx.getString(R.string.ws_sl_url));
		
		Map<String, String> arguments = new HashMap<String, String>(2);
		arguments.put(
				ctx.getString(R.string.ws_activation_property_memberId), 
				memberId);
		arguments.put(
				ctx.getString(R.string.ws_sl_property_id), 
				String.valueOf(sl.getId()));

		return service.invokeMethod(
				ctx.getString(R.string.ws_sl_method_deleteSL), arguments);
	}
	
	private boolean isDeletionSucceeded(SoapObject root) {
		
		String strStatus = root.getPropertyAsString(
				ctx.getString(R.string.ws_method_status));
		
		return Boolean.parseBoolean(strStatus);
	}
	
	private int parseMemberVersion(SoapObject root) {
		
		String strMemberVersion = root.getPropertyAsString(
				ctx.getString(R.string.ws_sl_property_memberVersion));
		
		return Integer.parseInt(strMemberVersion);
	}
	
	private int retrieveLocalMemberVersion() {
		return SLAdapter.retrieveMemberVersion();
	}
	
	private void deleteLocalSL(ShoppingList sl) {
		slDAO.delete(sl);
	}
	
	private void saveLocalMemberVersion(int version) {
		SLAdapter.persistMemberVersion(version);
	}

}
