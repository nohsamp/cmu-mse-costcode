/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import edu.cmu.cc.android.R;
import edu.cmu.cc.android.view.validation.IViewValidator;

/**
 *  DESCRIPTION: This spinner provides validating capabilities and displays
 *  error icon when the selection was not performed.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 10, 2013
 */
public class ValidatingSpinner<T> extends OptionalSelectionSpinner<T> 
implements IValidatingView {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private IViewValidator validator;
	
	private boolean validationMode;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public ValidatingSpinner(Context ctx, T emptyItem) {
		super(ctx, emptyItem);
	}
	
	public ValidatingSpinner(Context ctx, AttributeSet attrs, T emptyItem) {
		super(ctx, attrs, emptyItem);
	}
	
	public ValidatingSpinner(Context ctx, AttributeSet attrs, int defStyle, 
			T emptyItem) {
		super(ctx, attrs, defStyle, emptyItem);
	}

	//-------------------------------------------------------------------------
	// GETTER - SETTERS
	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	@Override
	public void setValidator(IViewValidator validator, 
			String fieldDisplayName) {
		
		this.validator = validator;
		this.validationMode = false;
		registerListerners();
	}

	@Override
	public boolean isValid() {
		
		if (validator == null || !validationMode) {
			return true;
		}
		
		return validator.validate(this);
	}

	@Override
	public void flagOrUnflagValidationError(boolean validationMode) {
		this.validationMode = validationMode;
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if (!isValid()) {
			drawErrorIcon(canvas);
		}
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	private void drawErrorIcon(Canvas canvas) {
		
		final int ICON_RIGHT_MARGIN = 40;
		
		Bitmap icon = BitmapFactory.decodeResource(getResources(), 
				R.drawable.indicator_input_error);
		
		float left = getWidth() - ICON_RIGHT_MARGIN - icon.getWidth();
		float top = (getHeight() - icon.getHeight()) / 2;
		
		left = (left < 0) ? 0 : left;
		top = (top < 0) ? 0 : top;
		
		canvas.drawBitmap(icon, left, top, new Paint());
	}
	
	/**
	 * Setting up listeners for this view.
	 */
	protected void registerListerners() {
		registerOnFocusChangeListener();
		registerOnItemSelectedListener();
	}
	
	protected void registerOnFocusChangeListener() {
		setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					flagOrUnflagValidationError(validationMode);
				}
			}
		});
	}
	
	protected void registerOnItemSelectedListener() {
		setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				flagOrUnflagValidationError(validationMode);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

}
