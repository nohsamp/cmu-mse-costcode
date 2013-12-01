package cmu.costcode.ShoppingList.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cmu.costcode.ShoppingList.objects.Category;
import cmu.costcode.ShoppingList.objects.Customer;
import cmu.costcode.ShoppingList.objects.ShoppingListItem;
import cmu.costcode.WIFIScanner.AccessPoint;
import cmu.costcode.ShoppingList.objects.Category.Location;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

public class DatabaseAdaptor {
	
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "ShoppingList.db";
	
	private static final String TAG = "DatabaseAdaptor";
	private final Context ctx;
	private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

	
    /**
     * Create tables for ShoppingList database
     * @author kevin
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String TEXT_TYPE = " TEXT";
		private static final String INT_TYPE = " INTEGER";
		private static final String DOUBLE_TYPE = " DOUBLE";
		private static final String COMMA_SEP = ",";
		private static final String SQL_CREATE_CUSTOMER =
		    "CREATE TABLE " + DbContract.CustomerEntry.TABLE_NAME + " (" +
		    DbContract.CustomerEntry.MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    DbContract.CustomerEntry.NAME_FIRST + TEXT_TYPE + COMMA_SEP +
		    DbContract.CustomerEntry.NAME_LAST + TEXT_TYPE + COMMA_SEP +
		    DbContract.CustomerEntry.ADDRESS + TEXT_TYPE + 
		    " )";
		
		private static final String SQL_CREATE_LISTITEM =
			    "CREATE TABLE " + DbContract.ListItemEntry.TABLE_NAME + " (" +
			    DbContract.ListItemEntry.ROW_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			    DbContract.ListItemEntry.MEMBER_ID + INT_TYPE + COMMA_SEP +
			    DbContract.ListItemEntry.ITEM_ID + INT_TYPE + COMMA_SEP +
			    DbContract.ListItemEntry.CHECKED + " BOOLEAN" + COMMA_SEP +
			    DbContract.ListItemEntry.POSITION + " INT_TYPE" +
			    " )";
		
		private static final String SQL_CREATE_ITEM =
			    "CREATE TABLE " + DbContract.ItemEntry.TABLE_NAME + " (" +
			    DbContract.ItemEntry.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			    DbContract.ItemEntry.ITEM_NAME + TEXT_TYPE + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_PRICE + " FLOAT" + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_QUANTITY + INT_TYPE + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_UNIT+ TEXT_TYPE + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_CATEGORY + TEXT_TYPE + COMMA_SEP +
			    DbContract.ItemEntry.ITEM_UPC + TEXT_TYPE + 
			    " )";
		
		private static final String SQL_CREATE_CATEGORY =
			    "CREATE TABLE " + DbContract.CategoryEntry.TABLE_NAME + " (" +
			    DbContract.CategoryEntry.CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			    DbContract.CategoryEntry.CAT_NAME + TEXT_TYPE + COMMA_SEP +
			    DbContract.CategoryEntry.POSX + " FLOAT, " +
			    DbContract.CategoryEntry.POSY + " FLOAT, " +
			    DbContract.CategoryEntry.DESC + " TEXT" +
			    " )";
		
		private static final String SQL_CREATE_AP =
			    "CREATE TABLE IF NOT EXISTS " + DbContract.AccessPointEntry.TABLE_NAME + " (" +
			    DbContract.AccessPointEntry.AP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			    DbContract.AccessPointEntry.BSSID + " TEXT, " +
			    DbContract.AccessPointEntry.SSID + " TEXT, " +
			    DbContract.AccessPointEntry.POSX + " FLOAT, " +
			    DbContract.AccessPointEntry.POSY + " FLOAT, " +
			    DbContract.AccessPointEntry.DESC + " TEXT" +
			    " )";
		
		private static final String SQL_CREATE_VERSION =
			    "CREATE TABLE IF NOT EXISTS " + DbContract.InfoVersionEntry.TABLE_NAME + " (" +
			    DbContract.InfoVersionEntry.INFOVERSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			    DbContract.InfoVersionEntry.INFO_NAME + " TEXT, " +
			    DbContract.InfoVersionEntry.INFO_VERSION + " TEXT, " +
			    DbContract.InfoVersionEntry.INFO_DESC + " TEXT" +
			    " )";
		
		private static final String SQL_CREATE_ALERTS =
				"CREATE TABLE " + DbContract.AlertsEntry.TABLE_NAME + " (" +
						DbContract.AlertsEntry.ALERT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						DbContract.AlertsEntry.CATEGORY_NAME + " TEXT"+ COMMA_SEP +
						DbContract.AlertsEntry.LATITUDE + DOUBLE_TYPE + COMMA_SEP +
						DbContract.AlertsEntry.LONGITUDE + DOUBLE_TYPE + " )";


	
		//TODO: doesn't work
		private static final String SQL_DELETE_ENTRIES =
				"DROP TABLE IF EXISTS " +
				DbContract.CustomerEntry.TABLE_NAME + COMMA_SEP +
				DbContract.ListItemEntry.TABLE_NAME + COMMA_SEP +
				DbContract.ItemEntry.TABLE_NAME + COMMA_SEP +
				DbContract.CategoryEntry.TABLE_NAME + COMMA_SEP +
				DbContract.AccessPointEntry.TABLE_NAME + COMMA_SEP +
				DbContract.InfoVersionEntry.TABLE_NAME;
		
		/** Constructor*/
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		/**
		 * On creation of DB, generate all 4 ShoppingList tables
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_CUSTOMER);
			db.execSQL(SQL_CREATE_LISTITEM);
			db.execSQL(SQL_CREATE_ITEM);
			db.execSQL(SQL_CREATE_CATEGORY);
			db.execSQL(SQL_CREATE_AP);
			db.execSQL(SQL_CREATE_VERSION);
			db.execSQL(SQL_CREATE_ALERTS);
		}
	
		/**
		 * On upgrade of DB, discard all previous results, recreate original tables
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL(SQL_DELETE_ENTRIES);
			onCreate(db);
		}
    }


// ##### DATABASE ADAPTOR #####
    
	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public DatabaseAdaptor(Context ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DatabaseAdaptor open() throws SQLException {
		dbHelper = new DatabaseHelper(ctx);
		if(db == null || !db.isOpen()) {
			db = dbHelper.getWritableDatabase();
		}
		return this;
	}
	
	/**
	 * Close DatabaseHelper, freeing its resources
	 */
	public void close() {
		dbHelper.close();
	}
	
	
// ##### CLASS METHODS #####
	
// 		##### DB GET METHODS #####
	
	/**
	 * Return a new Customer object, created from DB data in 'customers' table with 'memberId'
	 * @param memberId
	 * @return
	 */
	public Customer dbGetCustomer(int memberId) {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.CustomerEntry.TABLE_NAME, 
				new String[] {
					DbContract.CustomerEntry.MEMBER_ID,
					DbContract.CustomerEntry.NAME_FIRST,
					DbContract.CustomerEntry.NAME_LAST,
					DbContract.CustomerEntry.ADDRESS
				}, DbContract.CustomerEntry.MEMBER_ID + "=" + memberId, null,
				null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of Customers with memberId " + memberId 
					+ ". Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No Customer with memberId " + memberId + " found.");
			return null;
		}
		
		// Create new Customer object, return it
		String firstName = mCursor.getString(
			    mCursor.getColumnIndexOrThrow(DbContract.CustomerEntry.NAME_FIRST)
				);
		String lastName = mCursor.getString(
			    mCursor.getColumnIndexOrThrow(DbContract.CustomerEntry.NAME_LAST)
				);
		String address = mCursor.getString(
			    mCursor.getColumnIndexOrThrow(DbContract.CustomerEntry.ADDRESS)
				);
		Customer cust = new Customer(memberId, firstName, lastName, address);
		
		return cust;
	}
	
	
	public ShoppingList dbGetItemList(int memberId) {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.ListItemEntry.TABLE_NAME, 
				new String[] {
					DbContract.ListItemEntry.ITEM_ID,
				}, DbContract.ListItemEntry.MEMBER_ID + "=" + memberId, null,
				null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of ShoppingListItems for memberId " + memberId 
					+ ". Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No ListItems with memberId " + memberId + " found.");
			return null;
		}
		

		// Create Customer's list of ListItems mapped to category
		ShoppingList shoppingList = 
				new ShoppingList();
		shoppingList.setId(memberId);
		shoppingList.setName(dbGetCustomer(memberId).getName() +"'s List");
		shoppingList.setDate(Calendar.getInstance().getTime());
		
		// Iterate through all ShoppingListItems
		for(int i=0; i<mCursor.getCount(); i++) {
			// Read ShoppingListItem properties
			
			long itemId = mCursor.getLong(
				    mCursor.getColumnIndexOrThrow(DbContract.ListItemEntry.ITEM_ID)
					);
			
			// Get Item object from DB
			Item item = dbGetItemById(itemId);
			
			// Get list of items for current ListItem category
			shoppingList.addItem(item);
			mCursor.moveToNext();
		}
		
		Log.i(TAG, "Reading from DB: ShoppingList with " + shoppingList.getItems().size() + " items");
		return shoppingList;
	}
	
	
	/**
	 * Return an ShoppingListItem corresponding to MemberId
	 * @param memberId
	 * @return
	 */
	public Map<String, ShoppingList> dbGetShoppingList(int memberId) {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.ListItemEntry.TABLE_NAME,
				new String[] {
				DbContract.ListItemEntry.ITEM_ID
		}, DbContract.ListItemEntry.MEMBER_ID + "=" + memberId, null,
		null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of ShoppingListItems for memberId " + memberId
					+ ". Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No ListItems with memberId " + memberId + " found.");
			return null;
		}
		

		// Create Customer's list of ListItems mapped to category
		Map<String, ShoppingList> shoppingList = 
				new HashMap<String, ShoppingList>();
		
		// Iterate through all ShoppingListItems
		for(int i=0; i<mCursor.getCount(); i++) {
			// Read ShoppingListItem properties
			int itemId = mCursor.getInt(
				    mCursor.getColumnIndexOrThrow(DbContract.ListItemEntry.ITEM_ID)
					);
			
			// Get Item object from DB
			Item item = dbGetItemById(itemId);
			String category = item.getCategory();
			
			// Get list of items for current ListItem category
			ShoppingList list;
			if(shoppingList.containsKey(category)) {
				list = shoppingList.get(category);
			} else {
				list = new ShoppingList();
				list.setName(memberId + " member shopping list");
				
			}
			list.addItem(item);
			
			// Add this ShoppingListItem to shoppingList
			shoppingList.put(category, list);
			mCursor.moveToNext();
		}
		
		Log.i(TAG, "Reading from DB: ShoppingList with " + shoppingList.size() + " categories");
		return shoppingList;
	}
	
	
	/**
	 * Returns an Item object by its itemId
	 * @param listId
	 * @return
	 */
	public Item dbGetItemById(long itemId) {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.ItemEntry.TABLE_NAME, 
				new String[] {
					DbContract.ItemEntry.ITEM_ID,
					DbContract.ItemEntry.ITEM_NAME,
					DbContract.ItemEntry.ITEM_DESCRIPTION,
					DbContract.ItemEntry.ITEM_PRICE,
					DbContract.ItemEntry.ITEM_QUANTITY,
					DbContract.ItemEntry.ITEM_UNIT,
					DbContract.ItemEntry.ITEM_CATEGORY,
					DbContract.ItemEntry.ITEM_UPC,
				}, DbContract.ItemEntry.ITEM_ID + "=" + itemId, null,
				null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of Items with itemId " + itemId 
					+ ". Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No Items with itemId " + itemId + " found.");
			return null;
		}
		
		// Read Item data from DB
		String name = mCursor.getString(
				mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_NAME)
				);
		String description = mCursor.getString(
				mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_DESCRIPTION)
				);
		float price = mCursor.getFloat(
				mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_PRICE)
				);
		int quantity = mCursor.getInt(
				mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_QUANTITY)
				);
		int unit = mCursor.getInt(
				mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_UNIT)
				);
		String category = mCursor.getString(
			    mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_CATEGORY)
				);
		String upc = mCursor.getString(
			    mCursor.getColumnIndexOrThrow(DbContract.ItemEntry.ITEM_UPC)
				);
		
		// Create Item object and return it
		Log.i(TAG, "Reading from DB: Item (id " + itemId + ") in category '" 
				+ category + "': '" + description + "'");
		return new Item(itemId, category, name, quantity, price, unit, description, upc);
	}
	
	
	
	/**
	 * Return list of access point
	 * @return 
	 */
	public List<AccessPoint> dbGetAccessPoint() {
		db.execSQL(DatabaseHelper.SQL_CREATE_AP);
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.AccessPointEntry.TABLE_NAME, 
				new String[] {
					DbContract.AccessPointEntry.BSSID,
					DbContract.AccessPointEntry.SSID,
					DbContract.AccessPointEntry.POSX,
					DbContract.AccessPointEntry.POSY,
				}, null, null,
				null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of AccessPoint. Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No AccessPoint found.");
			return null;
		}
		

		// Create the list of AccessPoint
		List<AccessPoint> apList = 
				new ArrayList<AccessPoint>(mCursor.getCount());
		
		// Iterate through all AccessPoint list
		for(int i=0; i<mCursor.getCount(); i++) {
			// Read AccessPoint properties
			AccessPoint ap = new AccessPoint();
			ap.setBssid(mCursor.getString(
					mCursor.getColumnIndexOrThrow(DbContract.AccessPointEntry.BSSID)
					)
			);
			ap.setSsid(mCursor.getString(
					mCursor.getColumnIndexOrThrow(DbContract.AccessPointEntry.SSID)
					)
			);
			ap.setPosX(mCursor.getFloat(
					mCursor.getColumnIndexOrThrow(DbContract.AccessPointEntry.POSX)
					)
			);
			ap.setPosY(mCursor.getFloat(
					mCursor.getColumnIndexOrThrow(DbContract.AccessPointEntry.POSY)
					)
			);
			apList.add(ap);
			
			mCursor.moveToNext();
		}
		
		Log.i(TAG, "Reading from DB: APlist with " + apList.size() + " APs");
		return apList;
	}
	
	/**
	 * Return list of category
	 * @return categoryList
	 */
	public List<Category> dbGetCategory() {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.CategoryEntry.TABLE_NAME, 
				new String[] {
					DbContract.CategoryEntry.CATEGORY_ID,
					DbContract.CategoryEntry.CAT_NAME,
					DbContract.CategoryEntry.POSX,
					DbContract.CategoryEntry.POSY,
					DbContract.CategoryEntry.DESC
				}, null, null,
				null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of Category. Found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No Category found.");
			return null;
		}
		

		// Create the list of AccessPoint for categoryList
		List<Category> categoryList = 
				new ArrayList<Category>(mCursor.getCount());
		
		// Iterate through all AccessPoint list
		for(int i=0; i<mCursor.getCount(); i++) {
			// Read AccessPoint properties
			
			String name = mCursor.getString(mCursor.getColumnIndexOrThrow(DbContract.CategoryEntry.CAT_NAME));
			double x = mCursor.getFloat(mCursor.getColumnIndexOrThrow(DbContract.CategoryEntry.POSX));
			double y = mCursor.getFloat(mCursor.getColumnIndexOrThrow(DbContract.CategoryEntry.POSY));
			Category category = new Category(name, new Location(x, y));
			
			categoryList.add(category);
			
			mCursor.moveToNext();
		}
		
		Log.i(TAG, "Reading from DB: Category list with " + categoryList.size() + " categories");
		return categoryList;
	}
	
	
	
//		##### DB CREATE METHODS #####
	
	/**
	 * Create a new row in the Customers db
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @return new memberId
	 */
	public int dbCreateCustomer(String firstName, String lastName, String address) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.CustomerEntry.NAME_FIRST, firstName);
		values.put(DbContract.CustomerEntry.NAME_LAST, lastName);
		values.put(DbContract.CustomerEntry.ADDRESS, address);
		
		// Insert the new row, returning the primary key value of the new row
		return (int)db.insert(DbContract.CustomerEntry.TABLE_NAME, null, values);
	}
	
	/**
	 * Create a new row in the ShoppingListItem db; returns memberId
	 * @param newItemId
	 * @param memberId
	 * @param checked
	 * @param position
	 * @return
	 */
	public long dbCreateShoppingListItem(long newItemId, int memberId, boolean checked, int position) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.ListItemEntry.ITEM_ID, newItemId);
		values.put(DbContract.ListItemEntry.MEMBER_ID, memberId);
		values.put(DbContract.ListItemEntry.CHECKED, checked);
		values.put(DbContract.ListItemEntry.POSITION, position);
		
		// Insert the new row, returning the primary key value of the new row (memberId)
		return db.insert(DbContract.ListItemEntry.TABLE_NAME, null, values);
	}
	
	/**
	 * Create a new row in the Item db; returns new itemId
	 * @param description
	 * @param category
	 * @return
	 */
	public long dbCreateItem(String name, String category, int quantity, float price, String upc) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.ItemEntry.ITEM_NAME, name);
		values.put(DbContract.ItemEntry.ITEM_CATEGORY, category);
		values.put(DbContract.ItemEntry.ITEM_QUANTITY, quantity);
		values.put(DbContract.ItemEntry.ITEM_PRICE, price);
		values.put(DbContract.ItemEntry.ITEM_UPC, upc);

		
		// Insert the new row, returning the primary key value of the new row (itemId)
		return db.insert(DbContract.ItemEntry.TABLE_NAME, null, values);
	}
	
	/**
	 * Create a new row in the AccessPoint db; returns BSSID of AccessPoint
	 * @param description
	 * @return
	 */
	public void dbCreateAccessPoint(AccessPoint ap) {
		db.execSQL(DatabaseHelper.SQL_CREATE_AP);
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.AccessPointEntry.BSSID, ap.getBssid());
		values.put(DbContract.AccessPointEntry.SSID, ap.getSsid());
		values.put(DbContract.AccessPointEntry.POSX, ap.getPosX());
		values.put(DbContract.AccessPointEntry.POSY, ap.getPosY());
		values.put(DbContract.AccessPointEntry.DESC, ap.getDescription());
		
		// Insert the new row, returning the primary key value of the new row (itemId)
		db.insert(DbContract.AccessPointEntry.TABLE_NAME, null, values);
	}
	
	/**
	 * Create a new row in the category db; returns category_id of category
	 * @param category
	 * @return
	 */
	public void dbCreateCategory(Category category) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.CategoryEntry.CAT_NAME, category.getName());
		values.put(DbContract.CategoryEntry.POSX, category.getLocation().getLat()); // posx
		values.put(DbContract.CategoryEntry.POSY, category.getLocation().getLon()); // posy
		
		// Insert the new row, returning the primary key value of the new row (itemId)
		db.insert(DbContract.CategoryEntry.TABLE_NAME, null, values);
	}
	
	
//		##### UPDATE DB METHODS #####
	
	/**
	 * Sets checked status of a ListItem to 'checked'
	 * @param memberId
	 * @param itemId
	 * @param checked
	 * @return
	 */
	public int dbSetItemChecked(int memberId, int itemId, boolean checked) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.ListItemEntry.CHECKED, checked);

		// Which row to update, based on the ID
		String selection = DbContract.ListItemEntry.MEMBER_ID + "=" + memberId
				+ " AND " + DbContract.ListItemEntry.ITEM_ID + "=" + itemId;
		
		return db.update(DbContract.ListItemEntry.TABLE_NAME, values, selection, null);
	}
	
	/**
	 * Delete a row from ListItem db for selected member, 
	 * @param memberId
	 * @param itemId
	 * @return
	 */
	public int dbDeleteItemRow(int memberId, int itemId) {
		// Which row to update, based on the ID
		String selection = DbContract.ListItemEntry.MEMBER_ID + "=" + memberId
				+ " AND " + DbContract.ListItemEntry.ITEM_ID + "=" + itemId;
		return db.delete(DbContract.ListItemEntry.TABLE_NAME, selection, null);
	}
	
	/**
	 * Update a row in the Item db with new category/description
	 * @param itemId
	 * @param category
	 * @param description
	 * @return
	 */
	public int dbUpdateItemName(long itemId, String category, String name) { //, int quantity, float price, String upc) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.ItemEntry.ITEM_NAME, name);
		values.put(DbContract.ItemEntry.ITEM_CATEGORY, category);
//		values.put(DbContract.ItemEntry.ITEM_QUANTITY, quantity);
//		values.put(DbContract.ItemEntry.ITEM_PRICE, price);
//		values.put(DbContract.ItemEntry.ITEM_UPC, upc);

		// Which row to update, based on the ID
		String selection = DbContract.ItemEntry.ITEM_ID + "=" + itemId;
		
		return db.update(DbContract.ItemEntry.TABLE_NAME, values, selection, null);
	}
	
	/**
	 * Delete all of data in AccessPoint Table
	 * @return The number of deleted rows
	 */
	public int dbDeleteAccessPoint() {
		return db.delete(DbContract.AccessPointEntry.TABLE_NAME, null, null);
	}
	
	/**
	 * Delete all of data in Category Table
	 * @return The number of deleted rows
	 */
	public int dbDeleteCategory() {
		return db.delete(DbContract.CategoryEntry.TABLE_NAME, null, null);
	}

	/**
	 * Get Version information
	 * @param The information name
	 * @return The version of information
	 */
	public String dbGetVersion(String infoName) {
		db.execSQL(DatabaseHelper.SQL_CREATE_VERSION);
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(DbContract.InfoVersionEntry.TABLE_NAME, 
				new String[] {
					DbContract.InfoVersionEntry.INFO_VERSION,
				}, DbContract.InfoVersionEntry.INFO_NAME + "= '" + infoName + "'", null,
				null, null, null, null);
		
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read floorplan version. Found " + mCursor.getCount() + " matches.");
			// Create new Customer object, return it
			return mCursor.getString(mCursor.getColumnIndexOrThrow(DbContract.InfoVersionEntry.INFO_VERSION));
		} else {
			// No matches found
			Log.d(TAG, "No floorplan version found.");
			return null;
		}
		
		
	}

	/**
	 * Insert or update version information
	 * @param infoName The name of information
	 * @param version The version of information
	 * @param description Description about the information
	 * @return The number of affected rows
	 */
	public int dbSetVersion(String infoName, String version, String description) {
		ContentValues values = new ContentValues(3);
		values.put(DbContract.InfoVersionEntry.INFO_NAME, infoName);
		values.put(DbContract.InfoVersionEntry.INFO_VERSION, version);
		values.put(DbContract.InfoVersionEntry.INFO_DESC, description);
		
		// Insert if no data
		if(dbGetVersion(infoName) == null) {
			// insert
			return (int)db.insert(DbContract.InfoVersionEntry.TABLE_NAME, null, values);
		}
		// else update data
		else {
			// Which row to update, based on the infoName
			String selection = DbContract.InfoVersionEntry.INFO_NAME + "= '" + infoName + "'";
			
			return db.update(DbContract.InfoVersionEntry.TABLE_NAME, values, selection, null);
		}
	}
	
	/**
	 * Return a map of all proximity alerts, tying category name to GPS coordinates
	 * @param memberId
	 * @return
	 */
	public Map<String, Location> dbGetProxAlerts() {
		// Return cursor location of row with matching memberId
		Cursor mCursor = db.query(true, DbContract.AlertsEntry.TABLE_NAME,
				new String[] {
				DbContract.AlertsEntry.CATEGORY_NAME,
				DbContract.AlertsEntry.LATITUDE,
				DbContract.AlertsEntry.LONGITUDE
		}, null, null,
		null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read list of Proximity Alerts, found " + mCursor.getCount() + " matches.");
		} else {
			// No matches found
			Log.d(TAG, "No Proximity Alerts found.");
			return new HashMap<String, Location>();
		}


		// Create the map object of proximity alerts
		Map<String, Location> proximityAlerts = new HashMap<String, Location>();

		// Iterate through all Proximity Alerts found
		for(int i=0; i<mCursor.getCount(); i++) {
			String category = mCursor.getString(
					mCursor.getColumnIndexOrThrow(DbContract.AlertsEntry.CATEGORY_NAME)
					);
			Double latitude = mCursor.getDouble(
					mCursor.getColumnIndexOrThrow(DbContract.AlertsEntry.LATITUDE));
			Double longitude = mCursor.getDouble(
					mCursor.getColumnIndexOrThrow(DbContract.AlertsEntry.LONGITUDE));

			// Add new proximity alert and move on to the next
			proximityAlerts.put(category, new Location(latitude, longitude));
			mCursor.moveToNext();
		}

		return proximityAlerts;
	}
	
	/**
	 * Create a new proximity alert with name 'category' at location (latitude, longitude)
	 * @param category
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public int dbCreateAlert(String category, double latitude, double longitude) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DbContract.AlertsEntry.CATEGORY_NAME, category);
		values.put(DbContract.AlertsEntry.LATITUDE, latitude);
		values.put(DbContract.AlertsEntry.LONGITUDE, longitude);

		// Insert the new row, returning the primary key value of the new row (itemId)
		return (int)db.insert(DbContract.AlertsEntry.TABLE_NAME, null, values);
	}
	
	/**
	 * Delete a proximity alert of a given category from the DB
	 * @param category
	 */
	public int dbDeleteProxAlert(String category, double latitude, double longitude) {
		// Which row to delete, based on 'category'
		String selection = DbContract.AlertsEntry.CATEGORY_NAME + "='" + category + "'" +
				" AND " + DbContract.AlertsEntry.LATITUDE + "=" + latitude +
				" AND " + DbContract.AlertsEntry.LONGITUDE + "=" + longitude;
		return db.delete(DbContract.AlertsEntry.TABLE_NAME, selection, null);
	}

	public ShoppingListItem dbGetShoppingListItem(Item item) {
		int itemId = (int)item.getId();
		Cursor mCursor = db.query(true, DbContract.ListItemEntry.TABLE_NAME,
				new String[] {
				DbContract.ListItemEntry.CHECKED,
				DbContract.ListItemEntry.POSITION
		}, DbContract.ListItemEntry.ITEM_ID + "=" + itemId, null,
		null, null, null, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			Log.d(TAG, "Read ShoppingListItem for itemId (UPC)" + itemId
					+ ". Found");
		} else {
			// No matches found
			Log.d(TAG, "No ShoppingListItem with itemId (UPC) " + itemId + " found.");
			return null;
		}
		
		boolean checked = mCursor.getInt(mCursor.getColumnIndexOrThrow(DbContract.ListItemEntry.CHECKED)) > 0;
		int position = mCursor.getInt(mCursor.getColumnIndexOrThrow(DbContract.ListItemEntry.POSITION));

		return new ShoppingListItem(itemId, checked, position, item);
	}
}
