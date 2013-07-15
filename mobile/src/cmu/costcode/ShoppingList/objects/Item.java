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
	
	public Item(int itemId, String description, String category) {
		this.itemId = itemId;
		this.description = description;
		this.category = category;
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
	
	@Override
	public String toString() {
		return "Item Id: " + itemId + ", Description: " + description + ", Category: " + category;
	}

}
