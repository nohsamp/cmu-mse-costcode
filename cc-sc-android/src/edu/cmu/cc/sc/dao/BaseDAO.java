/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dao;

import edu.cmu.cc.sc.model.BaseEntity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *  DESCRIPTION: Base class for all DAO classes
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 15, 2013
 */
public abstract class BaseDAO {

	/** PRIMARY KEY: Shopping list id */
	static final String COLUMN_ID = "id";
	
	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	protected SQLiteDatabase db;

	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------
	
	public void close() {
		
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}
	}

	//-------------------------------------------------------------------------
	// PROTECTED METHODS
	//-------------------------------------------------------------------------
	
	protected void openConnectionIfClosed() {
		
		if (db == null || !db.isOpen()) {
			db = new DBHelper().getWritableDatabase();
		}
	}
	
	protected boolean isValid(BaseEntity entity) {
		
		if (entity == null || entity.getId() <= 0) {
			return false;
		}
		
		return true;
	}
	
	protected boolean alreadyExists(final String tableName, 
			BaseEntity entity, Cursor cursor) {
		
		if (!isValid(entity)) {
			return false;
		}
		
		if (cursor == null || cursor.isClosed()) {
			cursor = db.query(tableName, new String[]{COLUMN_ID}, 
					String.format("%s=%d", COLUMN_ID, entity.getId()), 
					null, null, null, null);
		}
		
		return (cursor.getCount() > 0);
	}

}
