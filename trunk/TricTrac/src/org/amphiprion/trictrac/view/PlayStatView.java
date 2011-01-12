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
import org.amphiprion.trictrac.entity.PlayStat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * View used to display a rule in the rule list.
 * 
 * @author amphiprion
 * 
 */
public class PlayStatView extends LinearLayout {
	/** the linked playStat. */
	private PlayStat playStat;
	/** The play stat clicked listener. */
	private OnPlayStatClickedListener playStatClickedListener;

	/** The imageview. */
	private ImageView img;
	/** The edit text. */
	private EditText txt;

	/**
	 * Construct a play stat view.
	 * 
	 * @param context
	 *            the context
	 * @param playStat
	 *            the play stat entity
	 */
	public PlayStatView(Context context, PlayStat playStat, OnPlayStatClickedListener playStatClickedListener) {
		super(context);
		this.playStat = playStat;
		this.playStatClickedListener = playStatClickedListener;

		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createCategoryLayout());
	}

	/**
	 * @return the play stat
	 */
	public PlayStat getPlayStat() {
		return playStat;
	}

	/**
	 * Create the category icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);

		if (playStat == null) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.add));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.remove));
		}
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playStatClickedListener.playStatClicked(PlayStatView.this);
			}
		});
		this.img = img;
		return img;
	}

	/**
	 * Create the category layout view
	 * 
	 * @return the view
	 */
	private View createCategoryLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		EditText t = new EditText(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		if (playStat != null) {
			t.setText(playStat.getPlayerId());
		} else {
			t.setText("");
			t.setVisibility(INVISIBLE);
		}
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		txt = t;
		// TextView desc = new TextView(getContext());
		// desc.setText("" + DateUtil.format(operation.getDate()));
		// accountLayout.addView(desc);
		return accountLayout;
	}

	public interface OnRuleClickedListener {
		void ruleClicked(PlayStatView view);
	}

	/**
	 * Set the new play stat. Call this method only if the current play stat is
	 * null.
	 * 
	 * @param rule
	 *            the new rule
	 */
	public void setPlayStat(PlayStat playStat) {
		if (this.playStat == null) {
			this.playStat = playStat;
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.remove));
			txt.setVisibility(VISIBLE);
		}
	}

	/**
	 * Update the filter of the play stat entity.
	 */
	public void updatePlayStatFilter() {
		if (playStat != null) {
			playStat.setPlayerId(txt.getText().toString());
		}
	}

	public interface OnPlayStatClickedListener {
		void playStatClicked(PlayStatView v);
	}
}
