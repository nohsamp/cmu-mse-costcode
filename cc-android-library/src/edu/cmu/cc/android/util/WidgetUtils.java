/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.util;

import edu.cmu.cc.android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * DESCRIPTION: Helper class for managing Android UI widgets
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 10, 2013
 */
public class WidgetUtils {

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	// VIEWS

	/**
	 * Retrieve EditText widget from the parent view
	 * 
	 * @param parentView
	 *            - parent view
	 * @param id
	 *            - id of the widget
	 * @return Requested EditText widget
	 */
	public static EditText getEditText(View parentView, int id) {
		return (EditText) parentView.findViewById(id);
	}

	/**
	 * Get the text of the EditText by parent view and id
	 * 
	 * @param parentView
	 *            - parent view
	 * @param id
	 *            - id of the widget
	 * @return text of the requested EditText
	 */
	public static String getEditTextAsString(View parentView, int id) {
		return getEditText(parentView, id).getText().toString();
	}

	/**
	 * Retrieve TextView widget from the parent view
	 * 
	 * @param parentView
	 *            - parent view
	 * @param id
	 *            - id of the widget
	 * @return Requested TextView widget
	 */
	public static TextView getTextView(View parentView, int id) {
		return (TextView) parentView.findViewById(id);
	}

	/**
	 * Get the text of the TextView by parent view and id
	 * 
	 * @param parentView
	 *            - parent view
	 * @param id
	 *            - id of the widget
	 * @return text of the requested TextView
	 */
	public static String getTextViewAsString(View parentView, int id) {
		return getTextView(parentView, id).getText().toString();
	}

	public static Spinner getSpinner(View parentView, int id) {
		return (Spinner) parentView.findViewById(id);
	}

	public static <T> boolean setSpinnerSelectedItem(Spinner spinner, T item) {

		int count = spinner.getCount();
		for (int i = 0; i < count; i++) {
			@SuppressWarnings("unchecked")
			T spinnerItem = (T) spinner.getItemAtPosition(i);
			if (spinnerItem.equals(item)) {
				spinner.setSelection(i);
				return true;
			}
		}

		return false;
	}

	// DIALOGS

	public static AlertDialog createOkAlertDialog(Context ctx, int iconResID,
			int titleResID, String message) {

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		return createOkAlertDialog(ctx, iconResID, titleResID, message,
				listener);

	}

	public static AlertDialog createOkAlertDialog(Context ctx, int iconResID,
			int titleResID, String message, OnClickListener listener) {

		final AlertDialog dialog = createOkAlertDialog(ctx, iconResID,
				titleResID, message, R.string.androidUtil_ok, listener);

		return dialog;
	}

	public static AlertDialog createYesNoAlertDialog(Context ctx,
			int iconResID, int titleResID, String message,
			OnClickListener listener) {

		return createOkCancelOrYesNoAlertDialog(ctx, iconResID, titleResID,
				message, R.string.androidUtil_yes, R.string.androidUtil_no,
				listener);
	}

	public static AlertDialog createOkCancelAlertDialog(Context ctx,
			int iconResID, int titleResID, String message,
			OnClickListener listener) {

		return createOkCancelOrYesNoAlertDialog(ctx, iconResID, titleResID,
				message, R.string.androidUtil_ok, R.string.androidUtil_cancel,
				listener);
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private static AlertDialog createOkAlertDialog(Context ctx, int iconResID,
			int titleResID, String message, int okButtonTextResID,
			OnClickListener listener) {

		final AlertDialog dialog = new AlertDialog.Builder(ctx).create();

		dialog.setIcon(iconResID);
		dialog.setTitle(titleResID);
		dialog.setMessage(message);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE,
				ctx.getString(okButtonTextResID), listener);

		return dialog;
	}

	public static AlertDialog createOkCancelOrYesNoAlertDialog(Context ctx,
			int iconResID, int titleResID, String message,
			int okOrYesTextResID, int cancelOrNoTextResID,
			OnClickListener listener) {

		final AlertDialog dialog = createOkAlertDialog(ctx, iconResID,
				titleResID, message, okOrYesTextResID, listener);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				ctx.getString(cancelOrNoTextResID), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		return dialog;
	}

}
