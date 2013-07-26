package cmu.costcode.ShoppingList.objects;

import java.io.Serializable;

/**
 * @author kevin
 *
 */
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	private int itemId;
	private String description;
	private String category;
	private float price;
	private String upc;
	
	public Item(int itemId, String description, String category, float price, String upc) {
		this.itemId = itemId;
		this.description = description;
		this.category = category;
		this.price = price;
		this.upc = upc;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public String getUpc() {
		return upc;
	}
	
	public void setUpc(String upc) {
		this.upc = upc;
	}
	
	@Override
	public String toString() {
		return "Item Id: " + itemId + ", Description: " + description + ", Category: " + category;
	}

}
