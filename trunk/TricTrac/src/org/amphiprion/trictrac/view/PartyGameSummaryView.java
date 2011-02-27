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
import org.amphiprion.trictrac.entity.Game;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a party in the party list.
 * 
 * @author amphiprion
 * 
 */
public class PartyGameSummaryView extends LinearLayout {
	/** the linked game title. */
	private Game game;
	private boolean expanded;
	private ImageView img;

	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param party
	 *            the party entity
	 */
	public PartyGameSummaryView(Context context, Game game) {
		super(context);
		this.game = game;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createIcon());

		addView(createPartyGameLayout());

	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Create the search icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);
		img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.collapse));
		return img;
	}

	/**
	 * Create the party game view
	 * 
	 * @return the view
	 */
	private View createPartyGameLayout() {
		// LinearLayout accountLayout = new LinearLayout(getContext());
		// LayoutParams aclp = new
		// LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
		// android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		// accountLayout.setOrientation(VERTICAL);
		// accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);

		t.setText(game.getName() + " (" + game.getNbParty() + ")");
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		// t.setTextColor(getContext().getResources().getColor(R.color.black));
		// accountLayout.addView(t);
		// accountLayout.addView(createInformation());
		return t;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (expanded) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.expanded));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.collapse));
		}
	}

}
