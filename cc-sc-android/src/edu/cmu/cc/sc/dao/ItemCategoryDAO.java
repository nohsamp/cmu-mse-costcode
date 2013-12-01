/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.model.ItemCategory;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 21, 2013
 */
public class ItemCategoryDAO extends BaseDAO {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/** TABLE NAME */
	static final String TABLE_NAME = "itemcategory";

	/** PRIMARY KEY: Item category id */
	static final String COLUMN_ID = "id";

	/** COLUMN: Item category name */
	static final String COLUMN_NAME = "name";

	/** COLUMN: Item category description */
	static final String COLUMN_DESC = "description";

	/** Create ItemCategory table SQL script */
	static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT, " + COLUMN_DESC + " TEXT)";

	static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public ItemCategoryDAO() {
	}

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	/**
	 * Retrieves all item categories from the database
	 * 
	 * @return - item categories list
	 */
	public List<ItemCategory> getAll() {

		Cursor cursor = null;
		List<ItemCategory> list = null;

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			cursor = db.query(TABLE_NAME, null, null, null, null, null,
					COLUMN_NAME);

			int rowCount = cursor.getCount();
			Logger.logDebug(getClass(),
					String.format("Retrieved %d Item Categories...", rowCount));

			list = new ArrayList<ItemCategory>(rowCount);

			while (cursor.moveToNext()) {
				ItemCategory category = new ItemCategory();

				category.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
				category.setName(cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME)));
				category.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC)));

				list.add(category);
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
	 * Saves given item category object into the database.
	 * 
	 * @param category
	 *            - item category object to be saved
	 */
	public void save(ItemCategory category) {

		Logger.logDebug(getClass(),
				String.format("Trying to save ItemCategory [%s]", category));

		if (category == null) {
			Logger.logErrorAndThrow(getClass(), new RuntimeException(
					"Saving null " + "ItemCategory object is not allowed"));
		}

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			ContentValues values = new ContentValues();
			values.put(COLUMN_ID, category.getId());
			values.put(COLUMN_NAME, category.getName());
			values.put(COLUMN_DESC, category.getDescription());

			db.insert(TABLE_NAME, null, values);

			Logger.logDebug(getClass(),
					String.format("ItemCategory was saved in the local DB. "
							+ "[%s]", category));

		} catch (Throwable t) {
			if (db != null) {
				db.close();
			}
			Logger.logErrorAndThrow(getClass(), t);
		}

	}

	/**
	 * Removes all the item categories from the database
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

}
