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
import org.amphiprion.trictrac.entity.Player;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a player in the player list.
 * 
 * @author amphiprion
 * 
 */
public class PlayerSummaryView extends LinearLayout {
	/** the linked player. */
	private Player player;
	private boolean isOwner;

	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param player
	 *            the player entity
	 * @param isOwner
	 *            true if it is the player account
	 */
	public PlayerSummaryView(Context context, Player player, boolean isOwner) {
		super(context);
		this.isOwner = isOwner;
		this.player = player;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createAccountLayout());

	}

	/**
	 * @return the search
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Create the search icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);
		if (isOwner) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.player_myself));
		} else if (player.getTrictracId() != null) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.play));
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
		t.setText(player.getPseudo());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);
		accountLayout.addView(createInformation());

		return accountLayout;
	}

	private View createInformation() {
		LinearLayout infoLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		infoLayout.setLayoutParams(aclp);

		TextView t = new TextView(getContext());
		LayoutParams txtlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		t.setLayoutParams(txtlp);
		infoLayout.addView(t);

		if (isOwner || player.getTrictracId() != null) {
			ImageView imgLink = new ImageView(getContext());
			LayoutParams imgLinkLp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			imgLinkLp.gravity = Gravity.CENTER_VERTICAL;
			imgLinkLp.leftMargin = 5;
			imgLink.setLayoutParams(imgLinkLp);
			infoLayout.addView(imgLink);
			if (!isOwner && player.getLastSyncDate() != null && player.getLastUpdateDate().after(player.getLastSyncDate())) {
				imgLink.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.linked_need_update));
			} else {
				imgLink.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.linked));
			}

		}
		return infoLayout;
	}
}
