/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.model;

import java.util.List;

/**
 *  DESCRIPTION: This class specifies Costco products category.
 *	
 *  @author Azamat Samiyev
 *	@version 2.0
 *  Date: Jul 15, 2013
 */
public class ItemCategory extends BaseEntity {

	
	//-------------------------------------------------------------------------
	// FIELDS
	//-------------------------------------------------------------------------
	
	
	/** Name of the product category */
	private String name;
	
	/** Description of this product category */
	private String description;
	
	/** Shopping List items belonging to this category */
	private List<Item> items;
	
	
	//-------------------------------------------------------------------------
	// CONSTRUCTORS
	//-------------------------------------------------------------------------
	
	
	public ItemCategory() {}


	//-------------------------------------------------------------------------
	// GETTERS - SETTERS
	//-------------------------------------------------------------------------
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	//-------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		
		return name;
	}
	
}
