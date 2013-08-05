/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dialog;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.activity.listener.ISLStateListener;
import edu.cmu.cc.sc.view.adapter.SLViewAdapter;
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

/**
 *  DESCRIPTION: Shopping list detail dialog. This dialog enables a user
 *  to edit an existing shopping list or save a new one.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 14, 2013
 */
public class SLDialog extends DialogFragment {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private SLViewAdapter viewAdapter;
	
	private ISLStateListener slStateListener;
	
	private View view;
	
	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------
	
	private void setCaller(ISLStateListener slStateListener) {
		this.slStateListener = slStateListener;
	}

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	public static SLDialog newInstance(ISLStateListener slStateListener) {
		
		SLDialog dialog = new SLDialog();
		dialog.setCaller(slStateListener);
		
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) 
				getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		view = inflater.inflate(R.layout.sl_detail, null);
		
		SLViewAdapter.updateView(view);
		
		viewAdapter = new SLViewAdapter(view);
		
		AlertDialog.Builder dialogBuilder = 
				new AlertDialog.Builder(getActivity());
		
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
					
					// Positive button
					
					Button btnPositive = dialog.getButton(Dialog.BUTTON_POSITIVE);
					btnPositive.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							viewAdapter.validateAllViews();
							view.invalidate();
							
							if (!viewAdapter.areAllViewsValid()) {
								
								Logger.logDebug(SLDialog.class, "Fields are not valid!!!");
								
								Toast.makeText(getActivity(), 
										R.string.sl_save_invalidFields, 
										Toast.LENGTH_LONG).show();
							} else {
								Logger.logDebug(SLDialog.class, "Fields are valid!!!");
								
								SLViewAdapter.updateModel(view);
								slStateListener.onSLUpdated();
								
								Toast.makeText(getActivity(), 
										R.string.sl_save_success, 
										Toast.LENGTH_LONG).show();
								
								dlg.dismiss();
							}
						}
					});
					
					// Negative button
					
					Button btnNegative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
					btnNegative.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Logger.logDebug(SLDialog.class, "CANCEL BUTTON WAS PRESSED!!!!!!");
							
							ApplicationState.getInstance().setCurrentSL(null);
							dlg.dismiss();
						}
					});
				}
				
			}
		});
		
		return dialog;
	}
	
}
