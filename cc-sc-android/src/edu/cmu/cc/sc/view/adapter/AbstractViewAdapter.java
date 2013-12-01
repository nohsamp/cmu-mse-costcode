/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view.adapter;

import java.util.List;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import edu.cmu.cc.android.view.IValidatingView;
import edu.cmu.cc.android.view.validation.IViewValidator;

/**
 * DESCRIPTION: Abstract parent class for model view adapters
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 10, 2013
 */
public abstract class AbstractViewAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	protected List<IValidatingView> validatingViews;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	/**
	 * Performs validation on all views
	 */
	public void validateAllViews() {

		synchronized (validatingViews) {
			for (IValidatingView view : validatingViews) {
				view.flagOrUnflagValidationError(true);
			}
		}
	}

	/**
	 * Checks whether all the views are valid or not
	 * 
	 * @return
	 */
	public boolean areAllViewsValid() {

		synchronized (validatingViews) {
			for (IValidatingView view : validatingViews) {
				if (!view.isValid()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Resets the values of the views and removes validation errors
	 */
	public void resetAllViewsValues() {

		synchronized (validatingViews) {
			for (IValidatingView view : validatingViews) {
				if (view instanceof EditText) {
					((EditText) view).setText("");
				} else if (view instanceof Spinner) {
					((Spinner) view).setSelection(0);
				}
				view.flagOrUnflagValidationError(false);
			}
		}
	}

	// -------------------------------------------------------------------------
	// PROTECTED METHODS
	// -------------------------------------------------------------------------

	protected void assignValidatorToView(View parentView, int viewResID,
			int viewDisplayNameResID, IViewValidator validator) {

		IValidatingView validatingView = (IValidatingView) parentView
				.findViewById(viewResID);

		String viewDisplayName = parentView.getContext().getResources()
				.getString(viewDisplayNameResID);

		validatingView.setValidator(validator, viewDisplayName);

		validatingViews.add(validatingView);
	}

}
