package cmu.costcode.simplifiedcheckout.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cmu.costcode.ShoppingList.ListQRDisplayActivity;
import cmu.costcode.ShoppingList.objects.ShoppingListItem;

public class CustomerWeb {

	private final static String TAG = "CustomerWeb";
	private ArrayList<ShoppingListItem> shoppingList;
	private String customerName;
	final static String SERVER_IP = "cmu-mse-costco.herokuapp.com";
	private final String API_POST_LOCATION = "/costco/api/order";
	
	private Context context;
	
	// Constructor
	public CustomerWeb(Context context, int customerId, ArrayList<ShoppingListItem> shoppingList) {
		this.shoppingList = shoppingList;
		this.context = context;
		this.customerName = "Customer" + customerId;
	}
	
	/**
	 * Called when Send to Cashier button is pressed;
	 * @param view
	 */
	public void broadcastShoppingList(View view) {
		Map<String, Object> orderMap = new HashMap<String, Object>();
		orderMap.put("customer", customerName);
		orderMap.put("order", shoppingListToArray(shoppingList));
		Log.d(TAG, "Created JSON Object: " + orderMap.toString());
		
		// Try to make an HTTP JSON Post
		String apiPostAddress = "http://" + SERVER_IP + API_POST_LOCATION; 
		try {
			JSONObject jsonObjSend = getJsonObjectFromMap(orderMap);
			Log.i(TAG, "Sending shopping list to server: " + jsonObjSend.toString());
			sendAsyncShoppingListRequest(apiPostAddress, jsonObjSend, this.context);
		} catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Something broked. :( \nCheck the looogs.", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Convert the shopping list to a JSONArray
	 * @param list
	 * @return
	 */
	private JSONArray shoppingListToArray(ArrayList<ShoppingListItem> list) {
		JSONArray jsonListArray = new JSONArray();
		for(int i=0; i<list.size(); i++) {
			JSONObject jsonItemMap = new JSONObject();
			try {
				jsonItemMap.put("upc", list.get(i).getItem().getUpc());
				jsonItemMap.put("quantity", 1);
				//TODO: Account for item quantity? In ShoppingListItem perhaps. 
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        jsonListArray.put(jsonItemMap);
	    }
	    return jsonListArray;
	}
	
	
	/**
	 * Call an asynchronous POST request to the server to save a shopping list.
	 * @param requestUrl
	 * @param jsonObjSend
	 * @param ctx
	 */
	private void sendAsyncShoppingListRequest(String requestUrl, final JSONObject jsonObjSend, final Context ctx) {
		new AsyncTask<String, Void, JSONObject>() {
			@Override
			protected void onPreExecute() {
				Toast.makeText(ctx, "Sending shopping list to cashier", Toast.LENGTH_SHORT).show();
			}
			
		    @Override
		    protected JSONObject doInBackground(String... urls) {
				JSONObject jsonObjRecv = HttpJsonClient.SendHttpPost(urls[0], jsonObjSend);
				return jsonObjRecv;
		    }
		    
		    @Override
		    protected void onPostExecute(JSONObject jsonObjRecv) {
		    	if(jsonObjRecv == null) {
		    		Toast.makeText(ctx, "Something broked. :( \nCheck the looogs.", Toast.LENGTH_SHORT).show();
		    	} else {
		    		//TODO: check to see if successful post; response code 201 in HttpJsonClient
			    	Log.i(TAG, "Flask Server Response!: " + jsonObjRecv.toString());
					Toast.makeText(ctx, "Successfully sent shopping list to server!", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(ctx, ListQRDisplayActivity.class);
					
					// Get new customer ID
					try {
						int customerId = jsonObjRecv.getInt("customer_id");
						intent.putExtra("CustomerID", customerId);
						ctx.startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(ctx, "Server sent back an invalid response. :(", Toast.LENGTH_SHORT).show();
					}
		    	}
		    }
		}.execute(requestUrl);
	}
	
	
	private static JSONObject getJsonObjectFromMap(Map<String, Object> params) throws JSONException {
	    Iterator<Entry<String, Object>> iter = params.entrySet().iterator();

	    //Stores JSON
	    JSONObject holder = new JSONObject();

	    while (iter.hasNext()) {
	        Entry<String, Object> pair = iter.next();
			holder.put((String)pair.getKey(), pair.getValue());
	    }
	    return holder;
	}

}