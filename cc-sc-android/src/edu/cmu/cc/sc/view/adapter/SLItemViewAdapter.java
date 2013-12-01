/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.android.util.WidgetUtils;
import edu.cmu.cc.android.view.IValidatingView;
import edu.cmu.cc.android.view.validation.spinner.OptionalSelectionSpinnerValidator;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.view.validator.textview.CommentValidator;
import edu.cmu.cc.sc.view.validator.textview.MoneyValidator;
import edu.cmu.cc.sc.view.validator.textview.NameValidator;
import edu.cmu.cc.sc.view.validator.textview.QuantityValidator;

/**
 * DESCRIPTION: Shopping list item detail view adapter.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 10, 2013
 */
public class SLItemViewAdapter extends AbstractViewAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	private static final int VALIDATING_VIEWS_COUNT = 6;

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public SLItemViewAdapter(View view) {
		initializeValidators(view);
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	public static void updateView(View view) {

		Item item = ApplicationState.getInstance().getCurrentSLItem();

		// ---------------------------------------------------
		// ITEM: Name
		// ---------------------------------------------------

		WidgetUtils.getEditText(view, R.id.et_sl_item_name).setText(
				item.getName());

		// ---------------------------------------------------
		// ITEM: Quantity
		// ---------------------------------------------------

		WidgetUtils.getEditText(view, R.id.et_sl_item_quantity).setText(
				String.valueOf(item.getQuantity()));

		// ---------------------------------------------------
		// ITEM: Price
		// ---------------------------------------------------

		if (item.getPrice() != 0) {
			WidgetUtils.getEditText(view, R.id.et_sl_item_price).setText(
					StringUtils.getMoneyFormatString(item.getPrice()));
		}

		// ---------------------------------------------------
		// ITEM: Comment
		// ---------------------------------------------------

		WidgetUtils.getEditText(view, R.id.et_sl_item_comment).setText(
				item.getDescription());

		// ---------------------------------------------------
		// ITEM: Category
		// ---------------------------------------------------

		// Spinner categoriesSpinner =
		// WidgetUtils.getSpinner(view, R.id.sp_sl_item_category);
		//
		// if (item.getCategory() != null) {
		// if (!WidgetUtils.setSpinnerSelectedItem(categoriesSpinner,
		// item.getCategory())) {
		// Logger.logErrorAndThrow(SLItemViewAdapter.class,
		// new RuntimeException("Cannot assign item Category ["
		// + item.getCategory() + "] to Spinner"));
		// }
		// }

		// ---------------------------------------------------
		// ITEM: Unit
		// ---------------------------------------------------

		Spinner unitsSpinner = WidgetUtils.getSpinner(view,
				R.id.sp_sl_item_unit);

		String unit = Item.Unit.getUnitNameByCode(item.getUnit());

		if (!StringUtils.isNullOrEmpty(unit)) {
			if (!WidgetUtils.setSpinnerSelectedItem(unitsSpinner, unit)) {
				Logger.logErrorAndThrow(SLItemViewAdapter.class,
						new RuntimeException("Cannot assign item Unit [ID="
								+ item.getUnit() + "] to Spinner"));
			}
		}
	}

	public static void updateModel(View view) {

		Item item = ApplicationState.getInstance().getCurrentSLItem();

		// ---------------------------------------------------
		// ITEM: Name
		// ---------------------------------------------------

		item.setName(WidgetUtils
				.getEditTextAsString(view, R.id.et_sl_item_name));

		// ---------------------------------------------------
		// ITEM: Quantity
		// ---------------------------------------------------

		String strQuantity = WidgetUtils.getEditTextAsString(view,
				R.id.et_sl_item_quantity);
		item.setQuantity(Integer.valueOf(strQuantity));

		// ---------------------------------------------------
		// ITEM: Price
		// ---------------------------------------------------

		String strPrice = WidgetUtils.getEditTextAsString(view,
				R.id.et_sl_item_price);
		item.setPrice(Float.parseFloat(strPrice));

		// ---------------------------------------------------
		// ITEM: Comment
		// ---------------------------------------------------

		item.setDescription(WidgetUtils.getEditTextAsString(view,
				R.id.et_sl_item_comment));

		// ---------------------------------------------------
		// ITEM: Category
		// ---------------------------------------------------

		String selectedCategory = (String) WidgetUtils.getSpinner(view,
				R.id.sp_sl_item_category).getSelectedItem();
		item.setCategory(selectedCategory);

		// if (selectedCategory instanceof ItemCategory) {
		// item.setCategory(((ItemCategory) selectedCategory).getName());
		// } else {
		// Logger.logErrorAndThrow(SLItemViewAdapter.class,
		// new RuntimeException("Cannot set item CATEGORY " +
		// "from Spinner. Unknown object type: " +
		// selectedCategory.getClass().getName()));
		// }

		// ---------------------------------------------------
		// ITEM: Unit
		// ---------------------------------------------------

		Object selectedUnit = WidgetUtils
				.getSpinner(view, R.id.sp_sl_item_unit).getSelectedItem();

		if (selectedUnit instanceof String) {
			int unitCode = Item.Unit.getUnitCodeByName((String) selectedUnit);

			if (unitCode <= 0) {
				Logger.logErrorAndThrow(SLItemViewAdapter.class,
						new RuntimeException("Cannot set item UNIT from "
								+ "Spinner. Unknown Unit: " + selectedUnit));
			}

			item.setUnit(unitCode);
		} else {
			Logger.logErrorAndThrow(SLItemViewAdapter.class,
					new RuntimeException("Cannot set item UNIT "
							+ "from Spinner. Unknown object type: "
							+ selectedUnit.getClass().getName()));
		}

		ApplicationState.getInstance().setCurrentSLItem(item);
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private void initializeValidators(View parentView) {

		Context ctx = parentView.getContext();
		validatingViews = new ArrayList<IValidatingView>(VALIDATING_VIEWS_COUNT);

		synchronized (validatingViews) {

			// assignValidatorToView(parentView, R.id.sp_sl_item_category,
			// R.string.sl_item_category,
			// new OptionalSelectionSpinnerValidator());

			assignValidatorToView(parentView, R.id.et_sl_item_name,
					R.string.sl_item_name, new NameValidator(ctx));

			assignValidatorToView(parentView, R.id.et_sl_item_quantity,
					R.string.sl_item_quantity, new QuantityValidator(ctx));

			assignValidatorToView(parentView, R.id.sp_sl_item_unit,
					R.string.sl_item_unit,
					new OptionalSelectionSpinnerValidator());

			assignValidatorToView(parentView, R.id.et_sl_item_price,
					R.string.sl_item_price, new MoneyValidator(ctx));

			assignValidatorToView(parentView, R.id.et_sl_item_comment,
					R.string.sl_item_comment, new CommentValidator(ctx));
		}
	}

}
