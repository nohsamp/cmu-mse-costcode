package cmu.costcode.ShoppingList.objects;

import edu.cmu.cc.sc.model.Item;

/**
 * @author kevin
 *
 */
public class ShoppingListItem {

	private int itemId;
	private boolean checked;
	private int position;
	private Item item;
	
	public ShoppingListItem(int itemId, boolean checked, int position, Item item) {
		this.itemId = itemId;
		this.checked = checked;
		this.position = position;
		this.item = item;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public String getCategory() {
		return item.getCategory();
	}
	
	@Override
	public String toString() {
		return item.toString();
	}

}
