package cmu.costcode.WIFIScanner;

public class AccessPoint {
	/**
	 * @uml.property  name="bssid"
	 */
	protected String bssid;
	
	/**
	 * @uml.property  name="ssid"
	 */
	protected String ssid;
	
	/**
	 * @uml.property  name="capabilities"
	 */
	protected String capabilities;
	
	/**
	 * @uml.property  name="frequency"
	 */
	protected int frequency;
	
	/**
	 * @uml.property  name="rssi"
	 */
	protected float rssi;
	
	protected float posX;
	protected float posY;
	
	protected double distance;

	
	public AccessPoint(String bssid, String ssid, String capabilities, int frequency, float rssi, float posX, float posY) {
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
	 * @uml.property  name="ssid"
	 */
	public String getSsid() {
		return this.ssid;
	}
	
	/**
	 * @return
	 * @uml.property  name="bssid"
	 */
	public String getBssid() {
		return this.bssid;
	}

	/**
	 * @return  the capabilities
	 * @uml.property  name="capabilities"
	 */
	public String getCapabilities() {
		return capabilities;
	}
	
	/**
	 * @return  the rssi
	 * @uml.property  name="rssi"
	 */
	public float getRssi() {
		return rssi;
	}
	
	/**
	 * @return  the posX
	 * @uml.property  name="posX"
	 */
	public float getPosX() {
		return posX;
	}
	
	/**
	 * @return  the posY
	 * @uml.property  name="posY"
	 */
	public float getPosY() {
		return posY;
	}

	/**
	 * @param capabilities  the capabilities to set
	 * @uml.property  name="capabilities"
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return  the frequency
	 * @uml.property  name="frequency"
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * @return  the distance
	 * @uml.property  name="distance"
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param frequency  the frequency to set
	 * @uml.property  name="frequency"
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

		/**
	 * @param bssid  the bssid to set
	 * @uml.property  name="bssid"
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/**
	 * @param ssid  the ssid to set
	 * @uml.property  name="ssid"
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
	/**
	 * @param ssid  the rssi to set
	 * @uml.property  name="rssi"
	 */
	public void setRssi(float rssi) {
		this.rssi = rssi;
	}
	
	/**
	 * @param posX  the posX to set
	 * @uml.property  name="posX"
	 */
	public void setPosX(float posX) {
		this.posX = posX;
	}
	
	/**
	 * @param posY  the posY to set
	 * @uml.property  name="posY"
	 */
	public void setPosY(float posY) {
		this.posY = posY;
	}
	
	/**
	 * @param posY  the posY to set
	 * @uml.property  name="posY"
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
