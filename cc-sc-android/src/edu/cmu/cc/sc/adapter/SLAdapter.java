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
 * @version 1.0 Date: Jun 21, 2013
 */
public class SLAdapter extends AbstractSharedPrefsAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	private static String KEY_MEMBER_VERSION = "version-member";

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

	public static synchronized boolean persistMemberVersion(int version) {

		return saveToSharedPrefs(SLAdapter.class,
				ApplicationState.getContext(), KEY_MEMBER_VERSION,
				String.valueOf(version), R.string.sl_all_error_versionPersist);
	}

	public static int retrieveMemberVersion() {

		String strValue = retrieveFromSharedPrefs(SLAdapter.class,
				ApplicationState.getContext(), KEY_MEMBER_VERSION,
				R.string.sl_all_error_versionRetrieve);

		if (StringUtils.isNullOrEmpty(strValue)) {
			return 0;
		}

		return Integer.parseInt(strValue);
	}

}