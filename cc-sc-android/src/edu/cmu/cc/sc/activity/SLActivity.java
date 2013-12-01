/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import edu.cmu.cc.android.activity.async.AbstractAsyncListActivity;
import edu.cmu.cc.android.util.Logger;
import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.activity.listener.ISLStateListener;
import edu.cmu.cc.sc.dialog.SLDialog;
import edu.cmu.cc.sc.model.ShoppingList;
import edu.cmu.cc.sc.task.DeleteSLTask;
import edu.cmu.cc.sc.task.FetchSLsTask;
import edu.cmu.cc.sc.task.SaveSLTask;
import edu.cmu.cc.sc.task.FetchSLsTask.IFetchSLTaskCaller;
import edu.cmu.cc.sc.view.adapter.AllSLViewListAdapter;

/**
 * DESCRIPTION:
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 21, 2013
 */
@SuppressLint("UseSparseArrays")
public class SLActivity extends AbstractAsyncListActivity implements
		IFetchSLTaskCaller, ISLStateListener, ITabActivity {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Map<Integer, MenuItem> menuItems;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// GETTERS - SETTERS
	// -------------------------------------------------------------------------

	@Override
	public AllSLViewListAdapter getListAdapter() {
		return (AllSLViewListAdapter) super.getListAdapter();
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_sl);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fetchShoppingLists();
	}

	@Override
	public void onAsyncTaskSucceeded(final Class<?> taskClass) {
		super.onAsyncTaskSucceeded(taskClass);

		addTaskToUIQueue(new Runnable() {

			@Override
			public void run() {

				if (taskClass == SaveSLTask.class) {
					Toast.makeText(SLActivity.this, R.string.sl_save_success,
							Toast.LENGTH_LONG).show();
				} else if (taskClass == DeleteSLTask.class) {
					Toast.makeText(SLActivity.this, R.string.sl_delete_success,
							Toast.LENGTH_LONG).show();
				}

				fetchShoppingLists();
			}
		});
	}

	@Override
	public void onAsyncTaskFailed(Class<?> taskClass, final Throwable t) {

		final String errorMsg = getAsyncTaskFailedMessage(taskClass, t);

		addTaskToUIQueue(new Runnable() {

			@Override
			public void run() {
				Logger.logErrorAndAlert(SLActivity.this, SLActivity.class,
						errorMsg, t);
			}
		});
	}

	@Override
	public void onFetchSLTaskSucceeded(List<ShoppingList> list) {

		ApplicationState.getInstance().setShoppingLists(list);

		refreshGUI();
	}

	@Override
	public boolean prepareOptionsMenu(Menu menu) {
		menuItems = new HashMap<Integer, MenuItem>(1);

		menuItems.put(R.string.sl_all_add, menu.add(R.string.sl_all_add)
				.setIcon(R.drawable.add));

		setMenuItemState(R.string.sl_all_add, true, true);

		return true;
	}

	@Override
	public boolean handleOptionsMenuItemSelection(final MenuItem item) {

		addTaskToUIQueue(new Runnable() {

			@Override
			public void run() {
				if (item.getTitle().equals(getString(R.string.sl_all_add))) {
					ShoppingList newSL = new ShoppingList();
					newSL.setDate(new Date(System.currentTimeMillis()));
					showShoppingListDialog(newSL);
				}
			}
		});

		return true;
	}

	@Override
	public void refresh() {
	}

	// -------------------------------------------------------------------------
	// ISLStateListener methods
	// -------------------------------------------------------------------------

	@Override
	public void onSLUpdated() {

		ShoppingList sl = ApplicationState.getInstance().getCurrentSL();

		new SaveSLTask(SLActivity.this, SLActivity.this)
				.execute(new ShoppingList[] { sl });
	}

	@Override
	public void onSLDeleted() {

		ShoppingList sl = ApplicationState.getInstance().getCurrentSL();

		new DeleteSLTask(SLActivity.this, SLActivity.this)
				.execute(new ShoppingList[] { sl });
	}

	@Override
	public void onSLEditItems(ShoppingList selectedSL) {

		ApplicationState.getInstance().setCurrentSL(selectedSL);

		Intent intent = new Intent(this, SLItemsActivity.class);
		startActivity(intent);
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private void setMenuItemState(int menuItemTitleResID, boolean visible,
			boolean enabled) {

		MenuItem menuItem = menuItems.get(menuItemTitleResID);
		menuItem.setVisible(visible);
		menuItem.setEnabled(enabled);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	/**
	 * Displays a detail shopping list dialog
	 */
	private void showShoppingListDialog(final ShoppingList sl) {

		ApplicationState.getInstance().setCurrentSL(sl);

		DialogFragment slDialog = SLDialog.newInstance(this);
		slDialog.show(getFragmentManager(), null);
	}

	private String getAsyncTaskFailedMessage(Class<?> taskClass, Throwable t) {

		int msgResID = R.string.error_unspecified;

		if (taskClass == FetchSLsTask.class) {
			msgResID = R.string.sl_all_error_fetch;
		} else {
			Logger.logErrorAndThrow(getClass(), new IllegalArgumentException(
					"Unexpected class: " + taskClass.toString()));
		}

		return StringUtils.getLimitedString(
				getString(msgResID, t.getMessage()), 200, "...");
	}

	private void fetchShoppingLists() {
		new FetchSLsTask(this, this).execute();
	}

	/**
	 * Setting the shopping lists adapter
	 */
	private void setListAdapter() {

		List<ShoppingList> list = ApplicationState.getInstance()
				.getShoppingLists();

		setListAdapter(new AllSLViewListAdapter(this, list, this));
	}

	/**
	 * Initializes list click capability
	 */
	private void prepareListClick() {

		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						ShoppingList sl = (ShoppingList) getListAdapter()
								.getItem(position);

						showShoppingListDialog(sl);
					}
				});
	}

	/*
	 * Update activity UI
	 */
	private void refreshGUI() {
		setListAdapter();
		onContentChanged();
		prepareListClick();
	}
}