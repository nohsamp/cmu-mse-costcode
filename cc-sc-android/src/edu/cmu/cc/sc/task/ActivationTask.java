/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.task;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import edu.cmu.cc.android.activity.async.IAsyncActivity;
import edu.cmu.cc.android.service.soap.SoapWebService;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.R;
import android.content.Context;
import android.os.AsyncTask;

/**
 * DESCRIPTION: This task activates this application is the provided Costco
 * membership id is valid.
 * 
 * @author Azamat Samiyev
 * @version 2.0 Date: Jun 13, 2013
 */
public class ActivationTask extends AsyncTask<String, Void, Boolean> {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Context ctx;

	private IActivationTaskCaller caller;

	private String memberId;

	private boolean errorState;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public ActivationTask(Context ctx, IActivationTaskCaller caller) {
		super();

		this.ctx = ctx;
		this.caller = caller;
	}

	// -------------------------------------------------------------------------
	// PROTECTED METHODS
	// -------------------------------------------------------------------------

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		caller.showProgressDialog(R.string.activation_progressDialogTitle,
				R.string.activation_progressDialogText);

		errorState = false;
		memberId = null;
	}

	/**
	 * Validate new user membership id
	 */
	@Override
	protected Boolean doInBackground(String... params) {

		try {

			if (params == null || params[0] == null) {
				throw new RuntimeException("Invalid input parameter: "
						+ "MembershipID is null");
			}

			memberId = params[0];

			SoapObject response = validateMembership(memberId);

			return parseValidity(response);

		} catch (Throwable t) {
			errorState = true;
			caller.onAsyncTaskFailed(this.getClass(), t);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		caller.dismissProgressDialog();
		if (!errorState) {
			caller.onActivationTaskSucceeded(memberId, result);
		}
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private SoapObject validateMembership(final String memberId)
			throws Throwable {

		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_activation_namespace),
				ctx.getString(R.string.ws_activation_url));

		Map<String, String> arguments = new HashMap<String, String>(1);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId),
				memberId);

		Logger.logDebug(this.getClass(), String.format(
				"Validating MembershipID[%s] on the server...", memberId));

		return service.invokeMethod(
				ctx.getString(R.string.ws_activation_method_validate),
				arguments);
	}

	private boolean parseValidity(SoapObject root) {

		String strValidity = root.getPropertyAsString(ctx
				.getString(R.string.ws_activation_property_validity));

		Logger.logDebug(this.getClass(),
				String.format("Membership validity: %s", strValidity));

		return Boolean.parseBoolean(strValidity);
	}

	// -------------------------------------------------------------------------
	// INNER INTERFACE
	// -------------------------------------------------------------------------

	/**
	 * The purpose of this Interface is to enable the task to return its result
	 * to the caller.
	 */
	public interface IActivationTaskCaller extends IAsyncActivity {

		/**
		 * Activation request was successfully handled
		 * 
		 * @param activated
		 *            - activation result
		 */
		public void onActivationTaskSucceeded(String memberId, boolean activated);

	}

}
