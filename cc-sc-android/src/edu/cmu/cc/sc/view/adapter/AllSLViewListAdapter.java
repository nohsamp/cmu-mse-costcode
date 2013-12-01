/**
 * Copyright (c) 2013, CostCode. All rights reserved.
 * Use is subject to license terms.
 */
package edu.cmu.cc.sc.view.adapter;

import java.util.List;

import edu.cmu.cc.android.util.StringUtils;
import edu.cmu.cc.android.util.WidgetUtils;
import edu.cmu.cc.sc.ApplicationState;
import edu.cmu.cc.sc.R;
import edu.cmu.cc.sc.activity.listener.ISLStateListener;
import edu.cmu.cc.sc.model.ShoppingList;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * DESCRIPTION: View adapter for the list of shopping lists
 * 
 * @author Azamat Samiyev
 * @version 1.0 Date: Jun 24, 2013
 */
public class AllSLViewListAdapter extends BaseAdapter {

	// -------------------------------------------------------------------------
	// CONSTANTS
	// -------------------------------------------------------------------------

	private static final String DATE_PATTERN = "yyyy-MM-dd";

	// -------------------------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------------------------

	private Context ctx;

	private LayoutInflater inflater;

	private List<ShoppingList> list;

	private ISLStateListener slStateListener;

	// -------------------------------------------------------------------------
	// CONSTRUCTORS
	// -------------------------------------------------------------------------

	public AllSLViewListAdapter(Context ctx, List<ShoppingList> list,
			ISLStateListener slStateListener) {

		super();

		this.ctx = ctx;
		this.list = list;
		this.slStateListener = slStateListener;
		this.inflater = LayoutInflater.from(ctx);
	}

	// -------------------------------------------------------------------------
	// PUBLIC METHODS
	// -------------------------------------------------------------------------

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.all_sl_row, null);
			viewHolder = getViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		customizeView(position, viewHolder);

		return convertView;
	}

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	// -------------------------------------------------------------------------

	private void customizeView(int position, ViewHolder viewHolder) {
		ShoppingList sl = list.get(position);
		customizeView(sl, viewHolder);
	}

	private void customizeView(ShoppingList sl, ViewHolder viewHolder) {

		viewHolder.getNameView().setText(sl.getName());
		viewHolder.getDateView().setText(
				StringUtils.getDateAsString(sl.getDate(), DATE_PATTERN));

		customizeEditItemsImage(viewHolder.getEditItemsView(), sl);
		customizeDeleteImage(viewHolder.getDeleteView(), sl);
	}

	private void customizeEditItemsImage(ImageView editItemsImage,
			final ShoppingList sl) {

		editItemsImage.setVisibility(View.VISIBLE);
		editItemsImage.setClickable(true);
		editItemsImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				slStateListener.onSLEditItems(sl);
			}

		});
	}

	private void customizeDeleteImage(ImageView deleteImage,
			final ShoppingList sl) {

		deleteImage.setVisibility(View.VISIBLE);
		deleteImage.setClickable(true);
		deleteImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						ApplicationState.getInstance().setCurrentSL(sl);

						slStateListener.onSLDeleted();
					}
				};

				final String deleteMessage = getDeleteMessage(
						R.string.sl_delete_message, sl);

				WidgetUtils
						.createYesNoAlertDialog(ctx,
								edu.cmu.cc.android.R.drawable.cancel,
								R.string.sl_delete_title, deleteMessage,
								deleteListener).show();
			}

		});
	}

	private ViewHolder getViewHolder(View view) {

		TextView nameView = (TextView) view
				.findViewById(R.id.tv_sl_all_row_name);

		TextView dateView = (TextView) view
				.findViewById(R.id.tv_sl_all_row_date);

		ImageView editItemsView = (ImageView) view
				.findViewById(R.id.btn_sl_all_row_items_edit);

		ImageView deleteView = (ImageView) view
				.findViewById(R.id.btn_sl_all_row_delete);

		ViewHolder viewHolder = new ViewHolder(nameView, dateView,
				editItemsView, deleteView);

		return viewHolder;
	}

	private String getDeleteMessage(int deleteMsgResID, final ShoppingList sl) {
		return ctx.getString(deleteMsgResID, sl.getName());
	}

	// -------------------------------------------------------------------------
	// INNER CLASS
	// -------------------------------------------------------------------------

	/**
	 * View Holder design pattern.
	 */
	private static class ViewHolder {

		private TextView nameView;

		private TextView dateView;

		private ImageView editItemsView;

		private ImageView deleteView;

		public ViewHolder(TextView nameView, TextView dateView,
				ImageView editItemsView, ImageView deleteView) {

			this.nameView = nameView;
			this.dateView = dateView;
			this.editItemsView = editItemsView;
			this.deleteView = deleteView;
		}

		public TextView getNameView() {
			return nameView;
		}

		public TextView getDateView() {
			return dateView;
		}

		public ImageView getEditItemsView() {
			return editItemsView;
		}

		public ImageView getDeleteView() {
			return deleteView;
		}

	}

}
