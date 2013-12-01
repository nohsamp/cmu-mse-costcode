/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view.validator.textview;

import android.content.Context;
import edu.cmu.cc.android.view.validation.textview.NumberValidator;
import edu.cmu.cc.sc.R;

/**
 * DESCRIPTION: Custom validator which validates money values.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 10, 2013
 */
public class MoneyValidator extends NumberValidator {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/** Money format: ### or ###.## */
	private static final String REGEX = "^[0-9]+|[0-9]+(\\.[0-9]{2})$";

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	/**
	 * Constructor.
	 * 
	 * @param ctx
	 *            - android context
	 */
	public MoneyValidator(Context ctx) {
		super(ctx, REGEX);
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public String getErrorMessage(String caption) {
		return ctx.getString(R.string.validation_money, caption);
	}

}
