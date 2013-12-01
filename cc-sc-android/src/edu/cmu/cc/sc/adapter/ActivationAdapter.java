/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.adapter;

import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 19, 2013
 */
public class ActivationAdapter extends AbstractSharedPrefsAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	private static String KEY_ACTIVATION_STATUS = "activation-status";

	private static String KEY_ACTIVATION_MEMBERID = "activation-memberid";

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	public static synchronized boolean persistMemberId(final String memberId) {

		return saveToSharedPrefs(ActivationAdapter.class,
				ApplicationState.getContext(), KEY_ACTIVATION_MEMBERID,
				memberId, R.string.activation_error_memberid_persist);
	}

	public static String retrieveMemberId() {

		return retrieveFromSharedPrefs(ActivationAdapter.class,
				ApplicationState.getContext(), KEY_ACTIVATION_MEMBERID,
				R.string.activation_error_memberid_retrieve);
	}

	public static synchronized boolean persistActivationStatus(boolean status) {

		return saveToSharedPrefs(ActivationAdapter.class,
				ApplicationState.getContext(), KEY_ACTIVATION_STATUS,
				String.valueOf(status),
				R.string.activation_error_status_persist);
	}

	public static boolean retrieveActivationStatus() {

		String strStatus = retrieveFromSharedPrefs(ActivationAdapter.class,
				ApplicationState.getContext(), KEY_ACTIVATION_STATUS,
				R.string.activation_error_status_retrieve);

		if (!StringUtils.isNullOrEmpty(strStatus)) {
			return Boolean.parseBoolean(strStatus);
		}

		return false;
	}

}
