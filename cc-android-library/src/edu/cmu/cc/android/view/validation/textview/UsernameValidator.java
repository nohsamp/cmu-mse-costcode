/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view.validation.textview;

import android.content.Context;
import edu.cmu.cc.android.R;

/**
 *  DESCRIPTION: Custom validator that validates for username.
 *  It uses RegexValidator as base.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 10, 2013
 */
public class UsernameValidator extends TextValidator {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	/** Username pattern */
	private static final String REGEX = "^[a-z0-9_-]{3,15}$";
	
	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * @param ctx - android context
	 */
	public UsernameValidator(Context ctx) {
		super(ctx, REGEX);
	}

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	public String getErrorMessage(String caption) {
		return ctx.getString(R.string.validation_username, caption);
	}
	
}
