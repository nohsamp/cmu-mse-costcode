/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view.validation.textview;

import android.content.Context;
import edu.cmu.cc.android.view.validation.IViewValidator;

/**
 * DESCRIPTION: Abstract class for all numeric validators
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 27, 2013
 */
public abstract class NumberValidator extends RegexValidator implements
		IViewValidator {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	protected NumberValidator(Context ctx, final String regex) {
		super(ctx, regex);
	}

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

}
