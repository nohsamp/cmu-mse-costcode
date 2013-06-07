package cmu.costcode.ProximityAlert;

import java.util.ArrayList;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import cmu.costcode.ShoppingList.R;
import cmu.costcode.ShoppingList.LoginActivity;
import cmu.costcode.ShoppingList.ViewListActivity;
import cmu.costcode.ShoppingList.db.DatabaseAdaptor;
import cmu.costcode.ShoppingList.objects.Item;
import cmu.costcode.ShoppingList.objects.ShoppingListItem;

public class ProximityIntentReceiver extends BroadcastReceiver {

	private static final int NOTIFICATION_ID = 1000;
	public static final String PROXIMITY_ALERT = "PROXIMITY_ALERT";
	
	private DatabaseAdaptor db;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		String alert = intent.getAction();
		Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
		String category = null;

		// Alert from WiFi triangulation
		if(alert.equals(PROXIMITY_ALERT)){
			// Get the section name (category)
			category = intent.getStringExtra("category");
			entering = true;
		}
		else {// Alert from addProximityAlert method, alert name is the category name
			category = alert;
		}
		
		// Get the message from the intent
		int memberId = intent.getIntExtra(LoginActivity.MEMBERID, 1);

		// Open database
		db = new DatabaseAdaptor(context);
		db.open();

		Map<String, ArrayList<ShoppingListItem>> shoppingList = db.dbGetShoppingListItems(memberId);
		String uncheckedItems = " ";

		if (shoppingList != null && shoppingList.containsKey(category)) {

			for (ShoppingListItem listItem : shoppingList.get(category)) {

				if (!listItem.isChecked()) {
					Item item = listItem.getItem();
					uncheckedItems = uncheckedItems + item.getDescription();
				}

			}
		}

		if(entering) {
			Log.d(getClass().getSimpleName(), "entering");
			Toast.makeText(context, category + " section entering. Unchecked items: " + uncheckedItems,
					Toast.LENGTH_LONG).show();

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = createNotification();

			Intent viewIntent = new Intent(context, ViewListActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(context, category
					+ " section entering. Unchecked items: " + uncheckedItems,
					"You are entering your point of interest.", pendingIntent);
			notificationManager.notify(NOTIFICATION_ID, notification);	
		} 
		else {
			Log.d(getClass().getSimpleName(), "exiting");
			Toast.makeText(context, category + " section exiting.",	Toast.LENGTH_LONG).show();
		}
	}
	
	private Notification createNotification() {
		Notification notification = new Notification();

		notification.icon = R.drawable.icon;;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE; //TODO: User choose this??

//		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;

		return notification;
	}

}
