/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.activity.listener.ISLItemStateListener;
import edu.cmu.cc.sc.view.adapter.SLItemViewAdapter;

/**
 * DESCRIPTION: Shopping list item dialog. This dialog enables the user to edit
 * the existing item or create a new one.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 9, 2013
 */
public class SLItemDialog extends DialogFragment {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private SLItemViewAdapter viewAdapter;

	private ISLItemStateListener caller;

	private View view;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	private void setCaller(ISLItemStateListener caller) {
		this.caller = caller;
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	public static SLItemDialog newInstance(ISLItemStateListener caller) {

		SLItemDialog dialog = new SLItemDialog();
		dialog.setCaller(caller);

		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.sl_item_detail, null);

		SLItemViewAdapter.updateView(view);

		viewAdapter = new SLItemViewAdapter(view);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity());

		dialogBuilder.setView(view);

		dialogBuilder.setPositiveButton(R.string.sl_save,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Logger.logDebug(SLDialog.class, "POSITIVE BUTTON!!!!");
					}
				});

		dialogBuilder.setNegativeButton(R.string.sl_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Logger.logDebug(SLDialog.class, "NEGATIVE BUTTON!!!!");
					}
				});

		final AlertDialog dialog = dialogBuilder.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(final DialogInterface dlg) {

				if (dialog != null) {

					// ---------------------------------------------------
					// Button: Save
					// ---------------------------------------------------

					Button btnPositive = dialog
							.getButton(DialogInterface.BUTTON_POSITIVE);
					btnPositive.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							viewAdapter.validateAllViews();
							view.invalidate();

							if (!viewAdapter.areAllViewsValid()) {
								Logger.logDebug(SLItemDialog.class,
										"Fields are not valid!!!");

								Toast.makeText(getActivity(),
										R.string.sl_item_save_invalidFields,
										Toast.LENGTH_LONG).show();
							} else {
								Logger.logDebug(SLItemDialog.class,
										"Fields are valid!!!");

								SLItemViewAdapter.updateModel(view);
								caller.onSLItemUpdated();

								Toast.makeText(getActivity(),
										R.string.sl_item_save_success,
										Toast.LENGTH_LONG).show();

								dlg.dismiss();
							}
						}
					});

					// ---------------------------------------------------
					// Button: Cancel
					// ---------------------------------------------------

					Button btnNegative = dialog
							.getButton(DialogInterface.BUTTON_NEGATIVE);
					btnNegative.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							Logger.logDebug(SLItemDialog.class,
									"CANCEL BUTTON WAS PRESSED!!!!!!");

							ApplicationState.getInstance().setCurrentSLItem(
									null);
							dlg.dismiss();
						}
					});

				}

			}
		});

		return dialog;
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

}
