/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view.validator.textview;

import android.content.Context;
import edu.cmu.cc.android.view.validation.textview.TextValidator;
import edu.cmu.cc.sc.R;

/**
 * DESCRIPTION: Custom validator that validates comment text views
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 10, 2013
 */
public class CommentValidator extends TextValidator {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/** Validation pattern: Length should be within 0 and 100 characters */
	private static final String REGEX = ".{0,100}";

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	/**
	 * Constructor.
	 * 
	 * @param ctx
	 *            - android context
	 */
	public CommentValidator(Context ctx) {
		super(ctx, REGEX);
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public String getErrorMessage(String caption) {
		return ctx.getString(R.string.validation_comment, caption);
	}

}
