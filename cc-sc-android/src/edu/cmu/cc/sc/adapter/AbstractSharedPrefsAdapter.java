/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.adapter;

import android.content.Context;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.android.util.SharedPrefsAdapter;
import edu.cmu.cc.android.util.StringUtils;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 21, 2013
 */
abstract class AbstractSharedPrefsAdapter {

	// -------------------------------------------------------------------------
	// PROTECTED METHODS
	// -------------------------------------------------------------------------

	protected static boolean saveToSharedPrefs(Class<?> caller, Context ctx,
			String key, String value, int errMsgResId) {

		try {

			return SharedPrefsAdapter.persist(ctx, key, value);

		} catch (Throwable t) {
			String errMsg = getErrorMessage(ctx, errMsgResId, t);
			Logger.logErrorAndAlert(ctx, caller, errMsg, t);
		}

		return false;
	}

	protected static String retrieveFromSharedPrefs(Class<?> caller,
			Context ctx, String key, int errMsgResId) {

		try {

			return SharedPrefsAdapter.retrieve(ctx, key);

		} catch (Throwable t) {
			String errMsg = getErrorMessage(ctx, errMsgResId, t);
			Logger.logErrorAndAlert(ctx, caller, errMsg, t);
		}

		return null;
	}

	protected static String getErrorMessage(Context ctx, int errMsgResID,
			Throwable t) {

		return StringUtils.getLimitedString(
				ctx.getString(errMsgResID, t.getMessage()), 200, "...");
	}

}
