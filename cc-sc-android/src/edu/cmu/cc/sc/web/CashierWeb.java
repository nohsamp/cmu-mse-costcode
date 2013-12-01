package edu.cmu.cc.sc.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cc.sc.dao.SLDAO;
import edu.cmu.cc.sc.dao.SLItemDAO;
import edu.cmu.cc.sc.model.Item;
import edu.cmu.cc.sc.model.ShoppingList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CashierWeb {
	private final static String TAG = "CashierActivity";
	final static String SERVER_IP = "cmu-mse-costco.herokuapp.com";
	private final String API_GET_LOCATION = "/costco/api/order/";

	private Context context;
	private long customerId;
	private ShoppingList shoppingList;
	private String url;

	public ShoppingList getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}

	public CashierWeb(String url, Context context) {
		this.url = url;
		this.context = context;
		this.customerId = parseUrl(url);
	}

	private long parseUrl(String url) {
		try {
			return Long.parseLong(url.split(API_GET_LOCATION)[1]);
		} catch (NullPointerException e) {
			return 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public CashierWeb(long customerId, Context context) {
		this.customerId = customerId;
		this.context = context;
		this.url = "http://" + SERVER_IP + API_GET_LOCATION + customerId;
	}

	/**
	 * Called on Retrieve List button; sends async GET request to retrieve a
	 * customer's shopping list
	 * 
	 * @param view
	 */
	public void retrieveShoppingList() {

		try {
			sendAsyncGetRequest(url, context);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Something broked. :( \nCheck the looogs.",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Call an asynchronous GET request to the server to save a shopping list.
	 * 
	 * @param requestUrl
	 * @param ctx
	 */
	private void sendAsyncGetRequest(String requestUrl, final Context ctx) {
		new AsyncTask<String, Void, JSONObject>() {
			@Override
			protected JSONObject doInBackground(String... urls) {
				JSONObject jsonObjRecv = HttpJsonClient.SendHttpGet(urls[0]);
				return jsonObjRecv;
			}

			@Override
			protected void onPostExecute(JSONObject jsonObjRecv) {
				if (jsonObjRecv == null) {
					// Fail
					Toast.makeText(ctx,
							"Something broked. :( \nCheck the looogs.",
							Toast.LENGTH_LONG).show();
				} else {
					// Success
					// TODO: check to see if successful get; response code 201
					// in HttpJsonClient
					Log.i(TAG,
							"Flask Server Response!: " + jsonObjRecv.toString());
					shoppingList = createShoppingListFromJson(jsonObjRecv);
					if (shoppingList == null) {
						Toast.makeText(ctx, "Not found for the customer ID!",
								Toast.LENGTH_LONG).show();
					} else {
						saveSLs();
						Toast.makeText(
								ctx,
								"Successfully retrieved shopping list from server!",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}.execute(requestUrl);
	}

	/**
	 * Convert the values in a JSONObject to a real ShoppingList
	 * 
	 * @param jsonResponse
	 * @return
	 */
	private ShoppingList createShoppingListFromJson(JSONObject jsonResponse) {
		ShoppingList shoppingList = null;
		try {
			String customerName = jsonResponse.getString("customer");
			shoppingList = new ShoppingList();
			shoppingList.setId(customerId);
			shoppingList.setName(customerName + "'s List");
			shoppingList.setDate(new Date(System.currentTimeMillis()));

			JSONArray orders = jsonResponse.getJSONArray("order");
			for (int i = 0; i < orders.length(); i++) {
				JSONObject order = orders.getJSONObject(i);
				long itemId = order.getLong("id");
				String category = "Item List"; // order.getString("category");
												// // TODO: category check on
												// mobile app
				String name = order.getString("name");
				String upc = order.getString("upc");
				float price = (float) order.getDouble("price");
				int quantity = order.getInt("quantity");
				shoppingList.addItem(new Item(itemId, category, name, quantity,
						price, 1, null, upc));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shoppingList;
	}

	private void saveSLs() {
		if (shoppingList == null) {
			return;
		}
		// Set up ShoppingList with dummy data
		SLItemDAO slItemDAO = new SLItemDAO();
		List<Item> items = new ArrayList<Item>(shoppingList.getItems().size());

		for (Item item : shoppingList.getItems()) {
			items.add(item);
			item.setShoppingList(shoppingList);
			slItemDAO.save(item);
		}

		SLDAO slDAO = new SLDAO();
		slDAO.save(shoppingList);
	}

}
