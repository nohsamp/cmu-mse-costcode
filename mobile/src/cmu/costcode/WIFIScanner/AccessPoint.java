package cmu.costcode.WIFIScanner;

public class AccessPoint {
	/**
	 * @uml.property name="bssid"
	 */
	private String bssid;

	/**
	 * @uml.property name="ssid"
	 */
	private String ssid;

	/**
	 * @uml.property name="capabilities"
	 */
	private String capabilities;

	/**
	 * @uml.property name="frequency"
	 */
	private int frequency;

	/**
	 * @uml.property name="rssi"
	 */
	private float rssi;

	private float posX;
	private float posY;

	private double distance;

	/**
	 * @uml.property name = "Description" Categroy description in which the AP
	 *               is located such as "Food", "Clothes"
	 */
	private String description;

	public AccessPoint(String bssid, String ssid, String capabilities,
			int frequency, float rssi, float posX, float posY) {
		this.bssid = bssid;
		this.ssid = ssid;
		this.capabilities = capabilities;
		this.frequency = frequency;
		this.rssi = rssi;
		this.posX = posX;
		this.posY = posY;
		this.distance = 0.0F;
	}

	public AccessPoint() {

	}

	/**
	 * @return
	 * @uml.property name="ssid"
	 */
	public String getSsid() {
		return this.ssid;
	}

	/**
	 * @return
	 * @uml.property name="bssid"
	 */
	public String getBssid() {
		return this.bssid;
	}

	/**
	 * @return the capabilities
	 * @uml.property name="capabilities"
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @return the rssi
	 * @uml.property name="rssi"
	 */
	public float getRssi() {
		return rssi;
	}

	/**
	 * @return the posX
	 * @uml.property name="posX"
	 */
	public float getPosX() {
		return posX;
	}

	/**
	 * @return the posY
	 * @uml.property name="posY"
	 */
	public float getPosY() {
		return posY;
	}

	/**
	 * @return description
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 * @uml.property name="capabilities"
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return the frequency
	 * @uml.property name="frequency"
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @return the distance
	 * @uml.property name="distance"
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 * @uml.property name="frequency"
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @param bssid
	 *            the bssid to set
	 * @uml.property name="bssid"
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @param ssid
	 *            the ssid to set
	 * @uml.property name="ssid"
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * @param ssid
	 *            the rssi to set
	 * @uml.property name="rssi"
	 */
	public void setRssi(float rssi) {
		this.rssi = rssi;
	}

	/**
	 * @param posX
	 *            the posX to set
	 * @uml.property name="posX"
	 */
	public void setPosX(float posX) {
		this.posX = posX;
	}

	/**
	 * @param posY
	 *            the posY to set
	 * @uml.property name="posY"
	 */
	public void setPosY(float posY) {
		this.posY = posY;
	}

	/**
	 * @param posY
	 *            the posY to set
	 * @uml.property name="posY"
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * @param description
	 *            category name to set
	 * @uml.property name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
