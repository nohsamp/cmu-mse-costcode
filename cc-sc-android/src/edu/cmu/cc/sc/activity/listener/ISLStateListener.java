/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.activity.listener;

import edu.cmu.cc.sc.model.ShoppingList;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jul 11, 2013
 */
public interface ISLStateListener {

	public void onSLEditItems(ShoppingList selectedSL);

	public void onSLDeleted();

	public void onSLUpdated();

}
