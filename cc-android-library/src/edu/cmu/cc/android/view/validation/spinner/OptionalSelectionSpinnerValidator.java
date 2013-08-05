/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view.validation.spinner;

import android.view.View;
import android.widget.Spinner;
import edu.cmu.cc.android.view.validation.IViewValidator;

/**
 *  DESCRIPTION: Validator for OptionalSelectionSpinner. It ensures that the
 *  user has to select the item from the spinner.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 10, 2013
 */
public class OptionalSelectionSpinnerValidator implements IViewValidator {


	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	public boolean validate(View view) {
		
		Spinner spinner = (Spinner) view;
		
		return spinner.getSelectedItemPosition() > 0;
	}

	@Override
	public String getErrorMessage(String caption) {
		throw new UnsupportedOperationException("Error message for " +
				"the Spinner is not supported");
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------

}
