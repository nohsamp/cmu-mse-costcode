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
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.adapter.SLAdapter;
import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 19, 2013
 */
public class SaveSLTask extends AsyncTask<ShoppingList, Void, Void> {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Context ctx;

	private IAsyncActivity caller;

	private SLDAO slDAO;

	private String memberId;

	private boolean errorState;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public SaveSLTask(Context ctx, IAsyncActivity caller) {
		super();

		this.ctx = ctx;
		this.caller = caller;
	}

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (!DeviceUtils.isNetworkConnectedElseAlert(ctx, getClass(),
				R.string.ws_error_noconnection)) {
			this.cancel(true);
		}

		caller.showProgressDialog(R.string.sl_all_saving);

		slDAO = new SLDAO();

		errorState = false;
	}

	@Override
	protected Void doInBackground(ShoppingList... params) {

		try {

			if (params == null || params[0] == null) {
				throw new RuntimeException("Invalid input parameter: "
						+ "ShoppingList is null");
			}
			ShoppingList sl = params[0];

			// ---------------------------------------------------
			// Saving ShoppingList
			// ---------------------------------------------------

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

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private SoapObject saveServerSL(ShoppingList sl) throws Throwable {

		Logger.logDebug(this.getClass(),
				String.format("Saving ShoppingList[%s] on the server...", sl));

		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_sl_namespace),
				ctx.getString(R.string.ws_sl_url));

		Map<String, String> arguments = new HashMap<String, String>(5);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId),
				memberId);
		arguments.put(ctx.getString(R.string.ws_sl_property_id),
				String.valueOf(sl.getId()));
		arguments
				.put(ctx.getString(R.string.ws_sl_property_name), sl.getName());
		arguments.put(ctx.getString(R.string.ws_sl_property_date),
				String.valueOf(sl.getDate().getTime()));
		arguments.put(ctx.getString(R.string.ws_sl_property_desc),
				sl.getDescription());

		return service.invokeMethod(
				ctx.getString(R.string.ws_sl_method_saveSL), arguments);
	}

	private int parseMemberVersion(SoapObject root) throws Throwable {

		String strMemberVersion = root.getPropertyAsString(ctx
				.getString(R.string.ws_sl_property_memberVersion));

		return Integer.parseInt(strMemberVersion);
	}

	private int retrieveLocalMemberVersion() {
		return SLAdapter.retrieveMemberVersion();
	}

	private void saveLocalMemberVersion(int version) {
		SLAdapter.persistMemberVersion(version);
	}

	private void parseAndSaveSL(SoapObject root, ShoppingList sl) {

		String strId = root.getPropertyAsString(ctx
				.getString(R.string.ws_sl_property_id));

		long id = Long.parseLong(strId);
		sl.setId(id);

		saveSL(sl);
	}

	private void saveSL(ShoppingList sl) {
		slDAO.save(sl);
	}

	// -------------------------------------------------------------------------
	// INNER INTERFACE
	// -------------------------------------------------------------------------

}
