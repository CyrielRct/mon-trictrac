/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.trictrac.view;

import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.util.DateUtil;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a collection in the collection list.
 * 
 * @author amphiprion
 * 
 */
public class CollectionSummaryView extends LinearLayout {
	/** the linked collection. */
	private Collection collection;

	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param collection
	 *            the collection entity
	 */
	public CollectionSummaryView(Context context, Collection collection) {
		super(context);
		this.collection = collection;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createAccountLayout());

		addView(createBalance());
	}

	/**
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);
		if (collection.getId() == null) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.all_games));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.collection));
		}
		return img;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	private View createAccountLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(collection.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		TextView desc = new TextView(getContext());
		if (collection.getLastSynchro() != null) {
			desc.setText("" + DateUtil.defaultDateFormat.format(collection.getLastSynchro()));
		}
		accountLayout.addView(desc);
		return accountLayout;
	}

	/**
	 * Create the balance view.
	 * 
	 * @return the view
	 */
	private View createBalance() {
		TextView balance = new TextView(getContext());
		LayoutParams blp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		balance.setGravity(Gravity.RIGHT);
		balance.setLayoutParams(blp);
		balance.setText("" + collection.getCount());
		balance.setTextSize(16);
		balance.setTypeface(Typeface.DEFAULT_BOLD);
		return balance;
	}

}
