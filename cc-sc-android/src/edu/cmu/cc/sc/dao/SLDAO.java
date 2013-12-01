/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 21, 2013
 */
public class SLDAO extends BaseDAO {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	/** TABLE NAME */
	static final String TABLE_NAME = "shoppinglist";

	/** COLUMN: Shopping list name */
	static final String COLUMN_NAME = "name";

	/** COLUMN: Shopping list creation date */
	static final String COLUMN_DATE = "date";

	/** COLUMN: Shopping list description */
	static final String COLUMN_DESC = "description";

	/** COLUMN: Shopping list version number */
	static final String COLUMN_VERSION = "version";

	/** Create ShoppingList table SQL script */
	static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT, " + COLUMN_DATE + " INTEGER, "
			+ COLUMN_DESC + " TEXT, " + COLUMN_VERSION + " INTEGER)";

	static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public SLDAO() {
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	/**
	 * Retrieves all the shopping list items from the database
	 * 
	 * @return - shopping list items
	 */
	public List<ShoppingList> getAll() {

		Cursor cursor = null;
		List<ShoppingList> list = null;

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			cursor = db.query(TABLE_NAME, null, null, null, null, null,
					COLUMN_NAME);

			int rowCount = cursor.getCount();
			Logger.logDebug(getClass(),
					String.format("Retrieved %d shopping lists...", rowCount));

			list = new ArrayList<ShoppingList>(rowCount);

			while (cursor.moveToNext()) {
				ShoppingList sl = new ShoppingList();

				sl.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
				sl.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				sl.setDate(new Date(cursor.getLong(cursor
						.getColumnIndex(COLUMN_DATE))));
				sl.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC)));
				sl.setVersion(cursor.getInt(cursor
						.getColumnIndex(COLUMN_VERSION)));

				list.add(sl);
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
	 * Saves given shopping list object into the database.
	 * 
	 * @param sl
	 *            - shopping list object to be saved
	 */
	public void save(ShoppingList sl) {

		Logger.logDebug(getClass(),
				String.format("Trying to save ShoppingList [%s]", sl));

		if (!isValid(sl)) {
			Logger.logErrorAndThrow(getClass(),
					new RuntimeException(String.format("ShoppingList[%s]"
							+ " has wrong value", sl)));
		}

		Cursor cursor = null;

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME, sl.getName());
			values.put(COLUMN_DATE, sl.getDate().getTime());
			values.put(COLUMN_DESC, sl.getDescription());
			values.put(COLUMN_VERSION, sl.getVersion());

			if (alreadyExists(TABLE_NAME, sl, cursor)) {
				db.update(TABLE_NAME, values, COLUMN_ID + "=" + sl.getId(),
						null);
			} else {
				values.put(COLUMN_ID, sl.getId());
				db.insert(TABLE_NAME, null, values);
			}

			Logger.logDebug(getClass(), String.format("ShoppingList[%s] "
					+ "was saved into the local DB", sl));

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

	}

	public void delete(ShoppingList sl) {

		Logger.logDebug(getClass(),
				String.format("Trying to delete ShoppingList [%s]", sl));

		if (!isValid(sl)) {
			Logger.logErrorAndThrow(getClass(),
					new RuntimeException(String.format("ShoppingList[%s]"
							+ " has wrong value", sl)));
		}

		try {

			if (db == null || !db.isOpen()) {
				db = new DBHelper().getWritableDatabase();
			}

			int deleted = db.delete(TABLE_NAME, COLUMN_ID + "=" + sl.getId(),
					null);

			Logger.logDebug(getClass(), String.format("[%d] "
					+ "ShoppingList records were deleted from the DB", deleted));

		} catch (Throwable t) {
			if (db != null) {
				db.close();
			}
			Logger.logErrorAndThrow(getClass(), t);
		}

	}

	/**
	 * Removes all the shopping lists from the database
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