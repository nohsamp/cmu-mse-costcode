package cmu.costcode.Triangulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiManager;

import cmu.costcode.ShoppingList.objects.Category;
import cmu.costcode.ShoppingList.objects.Category.Location;
import cmu.costcode.WIFIScanner.AccessPoint;

public class WCL extends Triangulation {

	private static float g = 0.6f;
	boolean dummyFlag;

	public static float getG() {
		return g;
	}

	public static void setG(float g) {
		WCL.g = g;
	}

	public WCL(WifiManager wm, Map<String, Object> initParams, Context context) {
		super(wm, initParams, context);
		dummyFlag = (Boolean) initParams.get("DUMMY");
	}

	private void doAWCL() {
		Comparator<AccessPoint> comparator = new Comparator<AccessPoint>() {
			@Override
			public int compare(AccessPoint s1, AccessPoint s2) {
				return (s1.getRssi() >= s2.getRssi()) ? -1 : 1;
			}
		};
		Collections.sort(apList, comparator);

		float reduction = 0;
		float q = 0.55f;
		for (int i = 0; i < apList.size(); i++) {
			AccessPoint result = apList.get(i);
			if (i == 0) {
				reduction = result.getRssi() * q;
			}
			result.setRssi(result.getRssi() - reduction);
		}
	}

	@Override
	// WCL triangulation algorithm
	// calculate the distance from APs
	// Calculate the distance from AP using RSSI and the position of AP (posX,
	// posY)
	// The position of APs is known already.
	public AccessPoint calculateAccessPointPosition() {
		wifiScanner.scanStart();
		apList = wifiScanner.getApList();

		if (apList == null || apList.size() < 3) {
			return null;
		}

		if (triangMethod.equals("AWCL")) {
			doAWCL();
		}

		AccessPoint nearestAP = null;
		float sumRssi = 0; // sum of Rssi for g = 0.6f

		for (Iterator<AccessPoint> it = apList.iterator(); it.hasNext();) {
			AccessPoint dataSet = it.next();

			float newRssi = (float) Math.pow(
					Math.pow(10, dataSet.getRssi() / 20), g); // RSSI: level
																// of AP
			sumRssi += newRssi;
			dataSet.setRssi(newRssi); // calculate weight and set Rssi to weight

			newRssi = (float) Math.pow(Math.pow(10, dataSet.getRssi() / 20),
					2.7); // RSSI: level
		}

		float x = 0;
		float y = 0;

		for (Iterator<AccessPoint> it = apList.iterator(); it.hasNext();) {
			AccessPoint dataSet = it.next();

			float weight = dataSet.getRssi() / sumRssi;
			x += dataSet.getPosX() * weight;
			y += dataSet.getPosY() * weight;
		}

		double oldDist = Double.MAX_VALUE;
		double newDist = Double.MAX_VALUE;
		for (Iterator<AccessPoint> itd = apList.iterator(); itd.hasNext();) {
			AccessPoint dataSet = itd.next();

			newDist = Math.sqrt((dataSet.getPosX() - x)
					* (dataSet.getPosX() - x) + (dataSet.getPosY() - y)
					* (dataSet.getPosY() - y));
			if (oldDist > newDist) { // If new distance between device (x,
										// y) and AP is shorter than old
										// one,
				oldDist = newDist; // Keep the shortest distance, i.e., new
									// distance
				nearestAP = dataSet;
				nearestAP.setPosX(x);
				nearestAP.setPosY(y);
				nearestAP.setDistance(newDist);
			}
		}

		return nearestAP;
	}

	@Override
	public String getNearestCategory(double x, double y) {
		double thresholdDistance = 30.0; // Distance threshold value for finding
											// category (meter)

		if (dummyFlag) {
			categoryList = new ArrayList<Category>(4);
			categoryList.add(new Category("Food", new Location(5.3, -3.5)));
			categoryList
					.add(new Category("Electronics", new Location(13, -7.3)));
			categoryList.add(new Category("Appliances", new Location(10, 7.3)));
			categoryList.add(new Category("Clothing", new Location(18, -15)));
		} else
			categoryList = wifiScanner.getCategoryList();

		if (categoryList == null)
			return null;

		String categoryName = null; // Result category

		double oldDist = Double.MAX_VALUE;
		double newDist = Double.MAX_VALUE;

		for (Iterator<Category> itd = categoryList.iterator(); itd.hasNext();) {
			Category category = itd.next();

			newDist = Math.sqrt((category.getLocation().getLat() - x)
					* (category.getLocation().getLat() - x)
					+ (category.getLocation().getLon() - y)
					* (category.getLocation().getLon() - y));
			// If distance is greater than threshold, ignore it
			if (newDist >= thresholdDistance)
				continue;

			if (oldDist > newDist) { // If new distance between device (x,
										// y) and AP is shorter than old
										// one,
				oldDist = newDist; // Keep the shortest distance, i.e., new
									// distance
				categoryName = category.getName();
			}
		}

		return categoryName;
	}
}
