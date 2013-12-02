/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package cmu.costcode.ShoppingList.objects;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.cc.sc.model.BaseEntity;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION: This class specifies a product item in the shopping list.
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: May 31, 2013
 */
public class Item extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 2L;

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	/** Shopping list to which this item belongs */
	private ShoppingList shoppingList;

	/** Product category of this item */
	private String category;

	/** Name of the shopping list item */
	private String name;

	/** Quantity of this item */
	private int quantity;

	/** Price of the item */
	private int price;

	/** Unit id */
	private int unit;

	/** Description of this item */
	private String description;

	/** upc of the item */
	private String upc;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public Item() {
	}

	public Item(long id, String category, String name, int quantity, int price,
			int unit, String description, String upc) {
		this.id = id;
		this.category = category;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.unit = unit;
		this.description = description;
		this.upc = upc;
	}

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	public ShoppingList getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(id);
		builder.append(", ");
		builder.append((shoppingList != null) ? shoppingList.getName() : "null");
		builder.append(", ");
		builder.append((category != null) ? category : "null");
		builder.append(", ");
		builder.append(name);
		builder.append(", ");
		builder.append(quantity);
		builder.append(", ");
		builder.append(price);
		builder.append(", ");
		builder.append(upc);

		return builder.toString();
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@SuppressLint("UseSparseArrays")
	public static class Unit {

		public static final int CODE_PC = 1;
		public static final int CODE_LB = 2;
		public static final int CODE_GAL = 3;

		private static final String STR_PC = "pc.";
		private static final String STR_LB = "lb.";
		private static final String STR_GAL = "gal.";

		private static Map<Integer, String> units;

		static {
			units = new HashMap<Integer, String>(3);
			units.put(CODE_PC, STR_PC);
			units.put(CODE_LB, STR_LB);
			units.put(CODE_GAL, STR_GAL);
		}

		/**
		 * Returns short string name of the unit measure by unit code
		 * 
		 * @param unitCode
		 *            - code of the unit measure
		 * @return short name of the unit measure
		 * @throws IllegalArgumentException
		 *             - if the given unit code if not valid
		 */
		public static synchronized String getUnitNameByCode(final int unitCode) {
			return units.get(unitCode);
		}

		public static synchronized int getUnitCodeByName(final String unitName) {

			for (int key : units.keySet()) {
				if (units.get(key).equals(unitName)) {
					return key;
				}
			}

			return -1;
		}

		public static Map<Integer, String> getUnitsMap() {
			return units;
		}

	}

}
