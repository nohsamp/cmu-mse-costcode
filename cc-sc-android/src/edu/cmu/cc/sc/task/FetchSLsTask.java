/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.os.AsyncTask;

import edu.cmu.cc.android.activity.async.IAsyncActivity;
import edu.cmu.cc.android.service.soap.SoapWebService;
import edu.cmu.cc.android.util.DeviceUtils;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.adapter.ItemCategoryAdapter;
import edu.cmu.cc.sc.adapter.SLAdapter;
import edu.cmu.cc.sc.dao.ItemCategoryDAO;
import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.dao.SLItemDAO;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ItemCategory;
import edu.cmu.cc.sc.model.ShoppingList;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jun 21, 2013
 */
public class FetchSLsTask 
extends AsyncTask<Void, Void, List<ShoppingList>> {

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	private Context ctx;
	
	private IFetchSLTaskCaller caller;
	
	private ItemCategoryDAO categoryDAO;
	
	private SLDAO slDAO;
	
	private SLItemDAO slItemDAO;
	
	private String memberId;
	
	private boolean errorState;

	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	public FetchSLsTask(Context ctx, IFetchSLTaskCaller caller) {
		super();
		
		this.ctx = ctx;
		this.caller = caller;
	}

	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------

	//-------------------------------------------------------------------------
	// PROTECTED METHODS
	//-------------------------------------------------------------------------
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if (!DeviceUtils.isNetworkConnectedElseAlert(
				ctx, getClass(), R.string.ws_error_noconnection)) {
			this.cancel(true);
		}
		
		caller.showProgressDialog(R.string.sl_all_loading);
		
		categoryDAO = new ItemCategoryDAO();
		slDAO = new SLDAO();
		slItemDAO = new SLItemDAO();
		
		errorState = false;
	}
	
	@Override
	protected List<ShoppingList> doInBackground(Void... params) {
		
		try {
			
			//---------------------------------------------------
			// Item Categories Version checking
			//---------------------------------------------------
			loadLocalCategories();
			
			return retrieveLocalSLs();
			
		} catch (Throwable t) {
			errorState = true;
			caller.onAsyncTaskFailed(this.getClass(), t);
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(List<ShoppingList> list) {
		super.onPostExecute(list);
		
		slItemDAO.close();
		slDAO.close();
		categoryDAO.close();
		
		caller.dismissProgressDialog();
		if (!errorState) {
			caller.onFetchSLTaskSucceeded(list);
		}
	}


	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------
	
	// Item Category version info
	
	private int retrieveLocalCategoryVersion() {
		return ItemCategoryAdapter.retrieveVersion();
	}
	
	private int retrieveServerCategoryVersion() throws Throwable {
		
		Logger.logDebug(this.getClass(), 
				"Retrieving Categories Version number from the server...");
		
		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_category_namespace), 
				ctx.getString(R.string.ws_category_url));
		
		Map<String, String> arguments = new HashMap<String, String>(1);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId), 
				memberId);
		
		SoapObject response = service.invokeMethod(
				ctx.getString(R.string.ws_category_method_retrieveVersion), 
				arguments);
		
		String strVersion = response.getPropertyAsString(
				ctx.getString(R.string.ws_category_property_version));
		
		Logger.logDebug(this.getClass(), 
				String.format("Server Categories Version: [%s]", strVersion));
		
		return Integer.parseInt(strVersion);
	}
	
	private void deleteLocalCategories() {
		categoryDAO.deleteAll();
	}
	
	private void retrieveAndSaveServerCategories() throws Throwable {
		
		Logger.logDebug(this.getClass(), 
				"Retrieving Categories from the server...");
		
		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_category_namespace), 
				ctx.getString(R.string.ws_category_url));
		
		Map<String, String> arguments = new HashMap<String, String>(1);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId), 
				memberId);
		
		SoapObject response = service.invokeMethod(
				ctx.getString(R.string.ws_category_method_retrieveCategories), 
				arguments);
		
		parseAndSaveCategoriesXML(response);
	}
	
	private void parseAndSaveCategoriesXML(SoapObject root) {
		
		if (root == null) {
			throw new RuntimeException("Soap source is null");
		}
		
		Object categoriesProperty = root.getProperty(
				ctx.getString(R.string.ws_category_property_categories));
		
		if (categoriesProperty instanceof SoapObject) {
			SoapObject categoriesXml = (SoapObject) categoriesProperty;
			
			for (int i = 0; i < categoriesXml.getPropertyCount(); i++) {
				Object categoryProperty = categoriesXml.getProperty(i);
				if (categoryProperty instanceof SoapObject) {
					SoapObject categoryXml = (SoapObject) categoryProperty;
					
					ItemCategory category = new ItemCategory();
					category.setId((Long) categoryXml
							.getProperty(ctx.getString(R.string.ws_category_property_id)));
					category.setName(categoryXml
							.getProperty(ctx.getString(R.string.ws_category_property_name))
							.toString());
					category.setDescription(categoryXml
							.getProperty(ctx.getString(R.string.ws_category_property_desc))
							.toString());
					
					saveCategory(category);
				}
			}
		}
		
	}
	
	private void saveCategory(ItemCategory category) {
		categoryDAO.save(category);
	}
	
	private void loadLocalCategories() {
		
		List<ItemCategory> categories = categoryDAO.getAll();
		
		Map<Long, ItemCategory> categoriesMap = 
				new HashMap<Long, ItemCategory>(categories.size());
		
		for (ItemCategory category : categories) {
			categoriesMap.put(category.getId(), category);
		}
		
		ApplicationState.getInstance().setCategories(categoriesMap);
	}
	
	private void saveLocalCategoriesVersion(int version) {
		ItemCategoryAdapter.persistVersion(version);
	}
	
	// Member version info
	
	private int retrieveLocalMemberVersion() {
		return SLAdapter.retrieveMemberVersion();
	}
	
	private int retrieveServerMemberVersion() throws Throwable {
		
		Logger.logDebug(this.getClass(), 
				"Retrieving MemberVersion from the server...");
		
		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_sl_namespace),
				ctx.getString(R.string.ws_sl_url));
		
		Map<String, String> arguments = new HashMap<String, String>(1);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId), 
				memberId);
		
		SoapObject response = service.invokeMethod(
				ctx.getString(R.string.ws_sl_method_retrieveMemberVersion), 
				arguments);
		
		String strMemberVersion = response.getPropertyAsString(
				ctx.getString(R.string.ws_sl_property_memberVersion));
		
		Logger.logDebug(this.getClass(),
				String.format("Server MemberVersion: [%s]", strMemberVersion));
		
		return Integer.parseInt(strMemberVersion);
	}
	
	private void deleteLocalSLs() {
		slItemDAO.deleteAll();
		slDAO.deleteAll();
	}
	
	private void retrieveAndSaveServerSLs() throws Throwable {
		
		Logger.logDebug(this.getClass(), 
				"Retrieving ShoppingLists from the server...");
		
		SoapWebService service = new SoapWebService(
				ctx.getString(R.string.ws_sl_namespace),
				ctx.getString(R.string.ws_sl_url));
		
		Map<String, String> arguments = new HashMap<String, String>(1);
		arguments.put(ctx.getString(R.string.ws_activation_property_memberId), 
				memberId);
		
		SoapObject response = service.invokeMethod(
				ctx.getString(R.string.ws_sl_method_retrieveSLs), arguments);
		
		parseSLsXML(response);
	}
	
	private void parseSLsXML(SoapObject root) {
		
		if (root == null) {
			throw new RuntimeException("Soap source is null");
		}
		
		for (int i = 0; i < root.getPropertyCount(); i++) {
			Object slProperty = root.getProperty(i);
			if (slProperty instanceof SoapObject) {
				SoapObject slXml = (SoapObject) slProperty;
				
				ShoppingList sl = new ShoppingList();
				sl.setId((Long) 
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_id)));
				sl.setName(
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_name))
						.toString());
				sl.setDate(new Date((Long)
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_date))));
				sl.setDescription(
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_desc))
						.toString());
				sl.setVersion((Integer)
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_version)));
				
				Object itemsXML = 
						slXml.getProperty(ctx.getString(R.string.ws_sl_property_items));
				
				if (itemsXML instanceof SoapObject) {
					parseSLItemsXML((SoapObject)itemsXML, sl);
				}
				
				saveSL(sl);
			}
		}
		
	}
	
	private void parseSLItemsXML(SoapObject root, ShoppingList sl) {
		
		if (root == null) {
			return;
		}
		
		for (int i = 0; i < root.getPropertyCount(); i++) {
			Object slItemProperty = root.getProperty(i);
			if (slItemProperty instanceof SoapObject) {
				SoapObject itemXml = (SoapObject) slItemProperty;
				
				Item slItem = new Item();
				
				//---------------------------------------------------
				// SL Item: ID
				//---------------------------------------------------
				slItem.setId((Long) 
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_id)));
				
				//---------------------------------------------------
				// SL Item: ShoppingList
				//---------------------------------------------------
				slItem.setShoppingList(sl);
				
				//---------------------------------------------------
				// SL Item: ItemCategory
				//---------------------------------------------------
				slItem.setCategory(getCategoryById((Long)
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_category))).getName());
				
				//---------------------------------------------------
				// SL Item: Name
				//---------------------------------------------------
				slItem.setName(
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_name))
								.toString());
				
				//---------------------------------------------------
				// SL Item: Quantity
				//---------------------------------------------------
				slItem.setQuantity((Integer) 
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_quantity)));
				
				//---------------------------------------------------
				// SL Item: Price
				//---------------------------------------------------
				
				int price = (Integer) itemXml
						.getProperty(ctx.getString(R.string.ws_sl_item_property_price));
				
				slItem.setPrice(price);
				
				//---------------------------------------------------
				// SL Item: Unit
				//---------------------------------------------------
				slItem.setUnit((Integer) 
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_unit)));
				
				//---------------------------------------------------
				// SL Item: Description
				//---------------------------------------------------
				slItem.setDescription(
						itemXml.getProperty(
								ctx.getString(R.string.ws_sl_item_property_desc))
								.toString());
				
				sl.addItem(slItem);
			}
		}
		
	}
	
	private ItemCategory getCategoryById(long id) {
		return ApplicationState.getInstance().getCategories().get(id);
	}
	
	private void saveSL(ShoppingList sl) {
		slDAO.save(sl);
		for (Item item : sl.getItems()) {
			slItemDAO.save(item);
		}
	}
	
	private void saveLocalMemberVersion(int version) {
		SLAdapter.persistMemberVersion(version);
	}
	
	private List<ShoppingList> retrieveLocalSLs() {
		
		return slDAO.getAll();
	}
	
	private void saveSampleSLs() {
		// Set up ShoppingList with dummy data
		ShoppingList shoppingList = new ShoppingList();
		shoppingList.setName("Sample SL");
		shoppingList.setId(1);
		shoppingList.setDate(Calendar.getInstance().getTime());

		slItemDAO = new SLItemDAO();
		List<Item> items = new ArrayList<Item>(3);

		Item item = new Item(1, "Furniture", "Couch cushions", 1, 37, 1, null, "184930574960");
		items.add(item);
		item.setShoppingList(shoppingList);
		slItemDAO.save(item);
		item = new Item(2, "Food", "Pre-made sandwichs", 1, 5, 1, null, "185038672860");
		items.add(item);
		item.setShoppingList(shoppingList);
		slItemDAO.save(item);
		item = new Item(3, "Clothing", "Woolen jackets aplenty", 1, 68, 1, null, "123456789048");
		items.add(item);
		item.setShoppingList(shoppingList);
		slItemDAO.save(item);
		shoppingList.setItems(items);

		saveSL(shoppingList);
	}
	
	//-------------------------------------------------------------------------
	// INNER INTERFACE
	//-------------------------------------------------------------------------
	
	public interface IFetchSLTaskCaller extends IAsyncActivity {
		
		public void onFetchSLTaskSucceeded(List<ShoppingList> list);
		
	}

}
