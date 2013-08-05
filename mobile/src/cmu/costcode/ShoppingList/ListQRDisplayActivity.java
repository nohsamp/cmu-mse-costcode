package cmu.costcode.ShoppingList;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cmu.costcode.ShoppingList.objects.ShoppingList;
import cmu.costcode.ShoppingList.objects.StoreItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cmu.costcode.R;


public class ListQRDisplayActivity extends Activity {

	protected static final String TAG = "ListQRDisplayActivity";
	private final String SCANDIT_API_KEY = "F_m-ymHHpvSYUimDUZCcpu7qGkQoyI4dxqe5WSPGLS6";
	private final String SHOPPING_LIST_URL = "/costco/api/order/";
	private final String SCANDIT_API_URL_HEAD = "https://api.scandit.com/barcode-generator/v1/qr/";
	private final String SCANDIT_API_URL_TAIL = "?size=600&key=";
	private String urlToCall;
	protected ImageView imgView;
	protected TextView urlTextView;
	private ShoppingList shoppingList;	//TODO: change to real ShoppingList object

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_qrdisplay);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get customer ID and build a QR code
		Intent intent = getIntent();
		int customerId = intent.getIntExtra("CustomerID", 1);
		urlToCall = ViewListActivity.SERVER_URL + SHOPPING_LIST_URL + customerId;
		String safeUrlToCall = urlToCall.replace(":", "%3A").replace("/", "%2F");
		
		// Get shopping list extra and set to display receipt
		try {
			JSONObject shoppingListJson = new JSONObject(intent.getStringExtra("ShoppingList"));
			shoppingList = createShoppingListFromJson(shoppingListJson);
		} catch (JSONException e) {
			Toast.makeText(this, "Invalid shopping list. :(", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		TextView receiptView = (TextView)findViewById(R.id.qrTextReceipt);
		if(shoppingList != null) {
    		receiptView.setText(shoppingList.toString());
    		Toast.makeText(this, "Successfully retrieved shopping list from server!", Toast.LENGTH_SHORT).show();
    		//TODO: Test that this works Kevin
    	}
		
		// Load view resources
		imgView = (ImageView)findViewById(R.id.qrImageView);
		urlTextView = (TextView)findViewById(R.id.qrTextView);

		// Request new QR code
		String apiCallString = SCANDIT_API_URL_HEAD + safeUrlToCall + SCANDIT_API_URL_TAIL + SCANDIT_API_KEY;
		sendAsyncGetRequest(apiCallString, this);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_qrdisplay, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Call an asynchronous GET request to the server to retrieve a QR code image
	 * @param requestUrl
	 * @param ctx
	 */
	private void sendAsyncGetRequest(String requestUrl, final Context ctx) {
		new AsyncTask<String, Void, Drawable>() {
			@Override
			protected Drawable doInBackground(String... urls) {
				try {
					InputStream is = (InputStream) new URL(urls[0]).getContent();
					Drawable d = Drawable.createFromStream(is, "sourceNameQuestionMark");
					return d;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Drawable drawable) {
				if(drawable == null) {
					// Fail
					Toast.makeText(ctx, "Something broked. :( \nCheck the looogs.", Toast.LENGTH_LONG).show();
				} else {
					// Success
					imgView.setImageDrawable(drawable);
					urlTextView.setText(urlToCall);
				}
			}
		}.execute(requestUrl);
	}
	
	/**
	 * Convert the values in a JSONObject to a real ShoppingList
	 * @param jsonResponse
	 * @return
	 */
	private ShoppingList createShoppingListFromJson(JSONObject jsonResponse) {
		ShoppingList shoppingList = null;
		try {
			String customerName = jsonResponse.getString("customer");
			shoppingList = new ShoppingList(customerName);
			
			JSONArray orders = jsonResponse.getJSONArray("order");
			for(int i=0; i< orders.length(); i++) {
				JSONObject order = orders.getJSONObject(i);
				String name = order.getString("name");
				String upc = order.getString("upc");
				double price = order.getDouble("price");
				int quantity = order.getInt("quantity");
				shoppingList.addItem(new StoreItem(name, upc, price, quantity));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return shoppingList;
	}
}
