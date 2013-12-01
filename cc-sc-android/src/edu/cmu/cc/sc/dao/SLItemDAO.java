/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 21, 2013
 */
@SuppressLint("DefaultLocale")
public class SLItemDAO extends BaseDAO {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/** TABLE NAME */
	static final String TABLE_NAME = "shoppinglist_item";

	/** PRIMARY KEY: Shopping list item id */
	static final String COLUMN_ID = "id";

	/** COLUMN: Parent shopping list id */
	static final String COLUMN_SHOPPINGLIST = "shoppinglistId";

	/** COLUMN: Shopping list item category id */
	static final String COLUMN_CATEGORY = "category";

	/** COLUMN: Shopping list item name */
	static final String COLUMN_NAME = "name";

	/** COLUMN: Shopping list item quantity */
	static final String COLUMN_QUANTITY = "quantity";

	/** COLUMN: Shopping list item price */
	static final String COLUMN_PRICE = "price";

	/** COLUMN: Shopping list item unit */
	static final String COLUMN_UNIT = "unit";

	/** COLUMN: Shopping list item description */
	static final String COLUMN_DESC = "description";

	/** Create ShoppingListItem table SQL script */
	static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_SHOPPINGLIST + " INTEGER, " + COLUMN_CATEGORY + " TEXT, "
			+ COLUMN_NAME + " TEXT, " + COLUMN_QUANTITY + " INTEGER, "
			+ COLUMN_PRICE + " FLOAT, " + COLUMN_UNIT + " INTEGER, "
			+ COLUMN_DESC + " TEXT, " + "FOREIGN KEY (" + COLUMN_SHOPPINGLIST
			+ ") REFERENCES " + SLDAO.TABLE_NAME + "(" + SLDAO.COLUMN_ID
			+ ") ON DELETE CASCADE)";

	static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public SLItemDAO() {
	}

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	/**
	 * Retrieves all the items of the given shopping list
	 * 
	 * @param shoppingListID
	 *            - id of the shopping list
	 * @return list of items
	 */
	public List<Item> getAll(ShoppingList sl) {

		if (!isValid(sl)) {
			Logger.logErrorAndThrow(getClass(),
					new RuntimeException(String.format("ShoppingList[%s]"
							+ " has wrong value", sl)));
		}

		Cursor cursor = null;
		List<Item> list = null;

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			final String sqlWhere = String.format("%s=%d", COLUMN_SHOPPINGLIST,
					sl.getId());

			cursor = db.query(TABLE_NAME, null, sqlWhere, null, null, null,
					COLUMN_NAME);

			int rowCount = cursor.getCount();
			Logger.logDebug(getClass(), String.format(
					"Retrieved %d shopping list items...", rowCount));

			list = new ArrayList<Item>(rowCount);

			while (cursor.moveToNext()) {
				Item slItem = new Item();

				slItem.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));

				slItem.setShoppingList(sl);

				slItem.setName(cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME)));

				slItem.setQuantity(cursor.getInt(cursor
						.getColumnIndex(COLUMN_QUANTITY)));

				slItem.setPrice(cursor.getFloat(cursor
						.getColumnIndex(COLUMN_PRICE)));

				slItem.setUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_UNIT)));

				slItem.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC)));

				slItem.setCategory(cursor.getString(cursor
						.getColumnIndex(COLUMN_CATEGORY)));

				list.add(slItem);
			}
		} catch (Throwable t) {
			if (db != null) {
				db.close();
			}
			Logger.logErrorAndThrow(getClass(), t);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	/**
	 * Saves the given Shopping list item into the local DB
	 * 
	 * @param slItem
	 *            - shopping list item to be saved
	 * @return saved shopping list item with attached id number
	 */
	public Item save(Item slItem) {

		Logger.logDebug(getClass(),
				String.format("Trying to save ShoppingListItem [%s]", slItem));

		if (!isValid(slItem)) {
			Logger.logErrorAndThrow(getClass(),
					new RuntimeException(String.format("ShoppingListItem[%s]"
							+ " has wrong value", slItem)));
		}

		Cursor cursor = null;

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			ContentValues values = new ContentValues();
			values.put(COLUMN_SHOPPINGLIST, slItem.getShoppingList().getId());
			values.put(COLUMN_CATEGORY, slItem.getCategory());
			values.put(COLUMN_NAME, slItem.getName());
			values.put(COLUMN_QUANTITY, slItem.getQuantity());
			values.put(COLUMN_PRICE, slItem.getPrice());
			values.put(COLUMN_UNIT, slItem.getUnit());
			values.put(COLUMN_DESC, slItem.getDescription());

			if (alreadyExists(cursor, slItem)) {
				db.update(TABLE_NAME, values, COLUMN_ID + "=" + slItem.getId(),
						null);
			} else {
				values.put(COLUMN_ID, slItem.getId());
				db.insert(TABLE_NAME, null, values);
			}

			Logger.logDebug(getClass(), String.format("ShoppingListItem[%s] "
					+ "was saved into the local DB", slItem));

		} catch (Throwable t) {
			if (db != null) {
				db.close();
			}
			Logger.logErrorAndThrow(getClass(), t);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return slItem;
	}

	public void delete(Item item) {

		Logger.logDebug(getClass(),
				String.format("Trying to delete ShoppingListItem [%s]", item));

		if (!isValid(item)) {
			Logger.logErrorAndThrow(getClass(),
					new RuntimeException(String.format("ShoppingListItem[%s]"
							+ " has wrong value", item)));
		}

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			int deleted = db.delete(TABLE_NAME, COLUMN_ID + "=" + item.getId(),
					null);

			Logger.logDebug(getClass(), String.format("[%d] "
					+ "ShoppingListItem were deleted from the DB", deleted));

		} catch (Throwable t) {
			if (db != null) {
				db.close();
			}
			Logger.logErrorAndThrow(getClass(), t);
		}
	}

	/**
	 * Removes all the shopping list items from the local DB
	 */
	public void deleteAll() {

		if (db == null || !db.isOpen()) {
			db = new DBHelper().getWritableDatabase();
		}

		db.delete(TABLE_NAME, null, null);
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private boolean isValid(Item item) {

		if (item == null || item.getId() <= 0 || item.getShoppingList() == null
				|| item.getCategory() == null) {

			return false;
		}

		return true;
	}

	private boolean alreadyExists(Cursor cursor, Item slItem) {

		if (slItem == null) {
			return false;
		}

		if (cursor == null || cursor.isClosed()) {
			cursor = db.query(TABLE_NAME, new String[] { COLUMN_ID },
					String.format("%s=%d", COLUMN_ID, slItem.getId()), null,
					null, null, null);
		}

		return (cursor.getCount() > 0);
	}

}