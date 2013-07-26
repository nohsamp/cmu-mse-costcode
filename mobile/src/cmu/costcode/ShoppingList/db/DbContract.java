package cmu.costcode.ShoppingList.db;

import android.provider.BaseColumns;

public class DbContract {

	/** Prevent instantiation */
	private DbContract() { }
	
	/** Constants for Customer table in DB */
	public static abstract class CustomerEntry implements BaseColumns {
		public static final String TABLE_NAME = "customers";
		public static final String MEMBER_ID = "member_id";
		public static final String NAME_FIRST = "name_first";
		public static final String NAME_LAST = "name_last";
		public static final String ADDRESS = "address";
	}

	/** Constants for ListItemEntry table in DB */
	public static abstract class ListItemEntry implements BaseColumns {
		public static final String TABLE_NAME = "list_items";
		public static final String ROW_KEY = "row_key";
		public static final String MEMBER_ID = "member_id";
		public static final String ITEM_ID = "list_id";
		public static final String CHECKED = "checked";
		public static final String POSITION = "position";
	}
	
	/** Constants for Item table in DB */
	public static abstract class ItemEntry implements BaseColumns {
		public static final String TABLE_NAME = "items";
		public static final String ITEM_ID = "item_id";
		public static final String DESCRIPTION = "description";
		public static final String CATEGORY_NAME = "category_name";
		public static final String PRICE = "price";
		public static final String UPC = "upc";
	}
	
	/** Constants for Category table in DB */
	public static abstract class CategoryEntry implements BaseColumns {
		public static final String TABLE_NAME = "categories";
		public static final String CATEGORY_ID = "category_id";
		public static final String CAT_NAME = "cat_name";
		public static final String POSX = "posx";
		public static final String POSY = "posy";
		public static final String DESC = "description";
	}
	
	/** Constants for AccessPoint table in DB */
	public static abstract class AccessPointEntry implements BaseColumns {
		public static final String TABLE_NAME = "accesspoint";
		public static final String AP_ID = "ap_id";
		public static final String BSSID = "bssid";
		public static final String SSID = "ssid";
		public static final String POSX = "posx";
		public static final String POSY = "posy";
		public static final String DESC = "description";
	}
	
	/** Constants for InfoVersion table in DB */
	public static abstract class InfoVersionEntry implements BaseColumns {
		public static final String TABLE_NAME = "infoversion";
		public static final String INFOVERSION_ID = "version_id";
		public static final String INFO_NAME = "info_name";
		public static final String INFO_VERSION = "version";
		public static final String INFO_DESC = "description";
	}
	
	/** Constants for Alerts table in DB */
	public static abstract class AlertsEntry implements BaseColumns {
		public static final String TABLE_NAME = "alerts";
		public static final String ALERT_ID = "alert_id";
		public static final String CATEGORY_NAME = "category_name";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
	}

}
