/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *  DESCRIPTION: ShoppingList class specifies a user's shopping list.
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: May 31, 2013
 */
public class ShoppingList extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------
	// CONSTANTS
	//-------------------------------------------------------------------------
	
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
	
	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	
	/** Shopping list name */
	private String name;
	
	/** Shopping list creation date */
	private Date date;
	
	/** Description information */
	private String description;
	
	/** Version number */
	private int version;
	
	/** Items of this shopping list */
	private List<Item> items;
	
	
	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	
	public ShoppingList() {}
	
	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	public void addItem(Item item) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		items.add(item);
	}
	

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append(id);
		builder.append(", ");
		builder.append(name);
		builder.append(", ");
		builder.append(getDateAsString(date, DATE_PATTERN));
		builder.append(", ");
		builder.append(version);
		
		return builder.toString();
	}
	
	/**
	 * Checks whether the given string is empty or null
	 * @param value - a string to be validated
	 * @return <b>true</b> - if the given string is null or empty, 
	 * <b>false</b> - if not
	 */
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty();
	}
	
	/**
	 * Converts date into formatted string
	 * @param date - date to be converted
	 * @param pattern - pattern of the string representation
	 * @return string representation of the date
	 */
	public static String getDateAsString(Date date, final String pattern) {
		
		if (date == null || isNullOrEmpty(pattern)) {
			return null;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		sdf.setTimeZone(TimeZone.getDefault());
		
		return sdf.format(date);
	}
	
}
