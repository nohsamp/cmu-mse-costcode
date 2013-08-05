/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.activity;

import android.view.Menu;
import android.view.MenuItem;

/**
 *  DESCRIPTION: 
 *	
 *  @author Azamat Samiyev
 *	@version 1.0
 *  Date: Jul 12, 2013
 */
public interface ITabActivity {

	public boolean prepareOptionsMenu(Menu menu);
	
	public boolean handleOptionsMenuItemSelection(MenuItem item);
	
	public void refresh();
	
}
