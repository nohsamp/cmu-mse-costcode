/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view.validation;

import android.view.View;

/**
 *  DESCRIPTION: Interface that all custom validators should implement.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 10, 2013
 */
public interface IViewValidator {

	/**
	 * Validate the given view
	 * @param view - a view to be validated
	 * @return <b>true</b> - if the view is correct, <b>false</b> - if not
	 */
	public boolean validate(View view);
	
	/**
	 * Return the validation error message
	 * @param caption - caption to be inserted into error message
	 * @return error message
	 */
	public String getErrorMessage(String caption);
	
}
