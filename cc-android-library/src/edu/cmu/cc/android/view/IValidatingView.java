/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view;

import edu.cmu.cc.android.view.validation.IViewValidator;

/**
 * DESCRIPTION: Interface that all views which require validation should
 * implement. It provides self-validation methods.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 10, 2013
 */
public interface IValidatingView {

	/**
	 * Sets the validator to the view
	 * 
	 * @param validator
	 *            - validator to be set
	 * @param fieldDisplayName
	 *            - a name of the field to be displayed in the error message
	 */
	public void setValidator(IViewValidator validator, String fieldDisplayName);

	/**
	 * Checks whether the value of the view is valid or not
	 * 
	 * @return <b>true</b> - valid, <b>false</b> - not valid
	 */
	public boolean isValid();

	/**
	 * Shows a validation error sign if the view is not valid
	 */
	public void flagOrUnflagValidationError(boolean validationMode);

}
