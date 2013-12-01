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
 * @version 1.0 Date: Jul 22, 2013
 */
public class SettingsAdapter extends AbstractSharedPrefsAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	private static String KEY_SETTING_WIFI = "setting_wifi";

	private static String KEY_SETTING_BCKG_SCANNING = "setting_scanning_bckg";

	private static String KEY_SETTING_WAREHOUSE = "setting_warehouse";

	// -------------------------------------------------------------------------
	// SETTING: WIFI
	// -------------------------------------------------------------------------

	public static synchronized boolean persistWIFIStatus(boolean status) {

		return saveToSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_WIFI,
				String.valueOf(status),
				R.string.settings_wifi_status_error_persist);
	}

	public static boolean retrieveWIFIStatus() {

		String strStatus = retrieveFromSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_WIFI,
				R.string.settings_wifi_status_error_retrieve);

		if (!StringUtils.isNullOrEmpty(strStatus)) {
			return Boolean.parseBoolean(strStatus);
		}

		return true;
	}

	// -------------------------------------------------------------------------
	// SETTING: BACKGROUND SCANNIG
	// -------------------------------------------------------------------------

	public static synchronized boolean persistBckgScanningStatus(boolean status) {

		return saveToSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_BCKG_SCANNING,
				String.valueOf(status),
				R.string.settings_bckgscan_status_error_persist);
	}

	public static boolean retrieveBckgScanningStatus() {

		String strStatus = retrieveFromSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_BCKG_SCANNING,
				R.string.settings_bckgscan_status_error_retrieve);

		if (!StringUtils.isNullOrEmpty(strStatus)) {
			return Boolean.parseBoolean(strStatus);
		}

		return false;
	}

	// -------------------------------------------------------------------------
	// SETTING: WAREHOUSE
	// -------------------------------------------------------------------------

	public static synchronized boolean persistDefaultWarehouseId(long id) {

		return saveToSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_WAREHOUSE,
				String.valueOf(id), R.string.settings_warehouse_error_persist);
	}

	public static long retrieveDefaultWarehouseId() {

		String strId = retrieveFromSharedPrefs(SettingsAdapter.class,
				ApplicationState.getContext(), KEY_SETTING_WAREHOUSE,
				R.string.settings_warehouse_error_retrieve);

		if (StringUtils.isNullOrEmpty(strId)) {
			return -1;
		}

		return Long.parseLong(strId);
	}

}
