package cmu.costcode.ShoppingList.objects;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.cc.sc.model.ShoppingList;

/**
 * @author kevin
 *
 */
public class Customer {

	private int memberId;
	private String firstName;
	private String lastName;
	private String address;
	private Map<String, ShoppingList> shoppingList;
	
	public Customer() {
		// TODO Auto-generated constructor stub
	}
	
	public Customer(int memberId, String firstName, String lastName, String address) {
		this.memberId = memberId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		shoppingList = new HashMap<String, ShoppingList>();
	}
	
	
// ##### GETTERS AND SETTERS #####
	
	public void setShoppingList(Map<String, ShoppingList> shoppingList) {
		this.shoppingList = shoppingList;
	}
	
	public Map<String, ShoppingList> getShoppingList() {
		if(shoppingList == null) {
			return new HashMap<String, ShoppingList>();
		} else {
			return shoppingList;
		}
	}
	
	/** Returns Customer's full name (first + last) */
	public String getName() {
		String name;
		name = firstName + " " + lastName;
		return name.trim();
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
