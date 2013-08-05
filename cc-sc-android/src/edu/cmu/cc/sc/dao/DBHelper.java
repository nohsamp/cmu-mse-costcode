/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.dao;

import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.ApplicationState;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 4, 2013
 */
final class DBHelper extends SQLiteOpenHelper {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	/** Database name */
	public static final String DATABASE_NAME = "cc.slh.db";
	
	/** Database version number. Used to check for updates */
	public static final int DATABASE_VERSION = 1;
	

	
	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------

	/**
	 * Constructor. 
	 * @param ctx - android context
	 * @param dbAdaptor - database adaptor object.
	 */
	public DBHelper() {
		super(ApplicationState.getContext(), 
				DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	/**
	 * Create all DB tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(ItemCategoryDAO.SQL_CREATE_TABLE);
		db.execSQL(SLDAO.SQL_CREATE_TABLE);
		db.execSQL(SLItemDAO.SQL_CREATE_TABLE);
		
		Logger.logDebug(getClass(), DATABASE_NAME + 
				" tables have been created...");
	}

	/**
	 * If the DB version is updated, delete all tables.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Logger.logDebug(getClass(), 
				String.format("%s is upgrading from version %d to version %d", 
						DATABASE_NAME, oldVersion, newVersion));
		
		db.execSQL("PRAGMA foreign_keys=OFF;");
		db.execSQL(SLItemDAO.SQL_DROP_TABLE);
		db.execSQL(SLDAO.SQL_DROP_TABLE);
		db.execSQL(ItemCategoryDAO.SQL_DROP_TABLE);
		
		onCreate(db);
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
}
