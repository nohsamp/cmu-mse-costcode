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
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.dao.SLItemDAO;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 22, 2013
 */
public class DeleteSLItemTask extends AsyncTask<Item, Void, Void> {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Context ctx;

	private IAsyncActivity caller;

	private SLDAO slDAO;

	private SLItemDAO itemDAO;

	private String memberId;

	private boolean errorState;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public DeleteSLItemTask(Context ctx, IAsyncActivity caller) {
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

		caller.showProgressDialog(R.string.sl_item_deleting);

		slDAO = new SLDAO();
		itemDAO = new SLItemDAO();

		errorState = false;
	}

	@Override
	protected Void doInBackground(Item... params) {

		try {

			if (params == null || params[0] == null) {
				throw new RuntimeException("Invalid input parameter: "
						+ "ShoppingListItem is null");
			}
			Item item = params[0];

			// ---------------------------------------------------
			// Deleting ShoppingListItem
			// ---------------------------------------------------

			deleteLocalSLItem(item);
		} catch (Throwable t) {
			errorState = true;
			caller.onAsyncTaskFailed(this.getClass(), t);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		itemDAO.close();
		slDAO.close();

		caller.dismissProgressDialog();
		if (!errorState) {
			caller.onAsyncTaskSucceeded(getClass());
		}
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private SoapObject deleteServerSLItem(Item item) throws Throwable {

		Logger.logDebug(this.getClass(), String.format("Deleting "
				+ "ShoppingListItem[%s] from the server...", item));

		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_sl_namespace),
				ctx.getString(R.string.ws_sl_url));

		Map<String, String> arguments = new HashMap<String, String>(2);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId),
				memberId);
		arguments.put(ctx.getString(R.string.ws_sl_item_property_id),
				String.valueOf(item.getId()));

		return service.invokeMethod(
				ctx.getString(R.string.ws_sl_item_method_deleteSLItem),
				arguments);
	}

	private boolean isDeletionSucceeded(SoapObject root) {

		String strStatus = root.getPropertyAsString(ctx
				.getString(R.string.ws_method_status));

		return Boolean.parseBoolean(strStatus);
	}

	private int parseSLVersion(SoapObject root) {

		String strSLVersion = root.getPropertyAsString(ctx
				.getString(R.string.ws_sl_property_version));

		return Integer.parseInt(strSLVersion);
	}

	private int retrieveLocalSLVersion(ShoppingList sl) {
		return sl.getVersion();
	}

	private void deleteLocalSLItem(Item item) {
		itemDAO.delete(item);
	}

	private void saveLocalSLVersion(int version, ShoppingList parentSL) {
		parentSL.setVersion(version);
		slDAO.save(parentSL);
		ApplicationState.getInstance().setCurrentSL(parentSL);
	}

}
