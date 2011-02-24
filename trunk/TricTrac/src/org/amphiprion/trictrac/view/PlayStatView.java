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

import java.util.List;

import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.adapter.PlayerAdapter;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

	private LinearLayout playStatLayout;
	private Button bt;

	private TextView txtPlayerName;
	private TextView txtRank;
	private TextView txtScore;
	private List<Player> players;

	/**
	 * Construct a play stat view.
	 * 
	 * @param context
	 *            the context
	 * @param playStat
	 *            the play stat entity
	 */
	public PlayStatView(Context context, PlayStat playStat, OnPlayStatClickedListener playStatClickedListener, List<Player> players) {
		super(context);
		this.players = players;
		this.playStat = playStat;
		this.playStatClickedListener = playStatClickedListener;

		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PlayStatView.this.playStat == null) {
					PlayStatView.this.playStatClickedListener.playStatClicked(PlayStatView.this);
				}
			}
		});
		addView(createIcon());

		addView(createCategoryLayout());

		addView(createEditButton());
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
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
		playStatLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		playStatLayout.setOrientation(VERTICAL);
		playStatLayout.setLayoutParams(aclp);
		txtPlayerName = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		txtPlayerName.setLayoutParams(tlp);
		if (playStat != null) {
			if (playStat.getPlayer() == null) {
				txtPlayerName.setText(getResources().getText(R.string.default_player));
			} else {
				txtPlayerName.setText(playStat.getPlayer().getPseudo());
			}
		} else {
			txtPlayerName.setText("");
			playStatLayout.setVisibility(INVISIBLE);
		}
		txtPlayerName.setTextSize(16);
		txtPlayerName.setTypeface(Typeface.DEFAULT_BOLD);
		txtPlayerName.setTextColor(getContext().getResources().getColor(R.color.black));
		playStatLayout.addView(txtPlayerName);

		LinearLayout hl = new LinearLayout(getContext());
		LayoutParams hlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		hl.setLayoutParams(hlp);

		LinearLayout vl1 = new LinearLayout(getContext());
		LayoutParams vl1p = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl1.setLayoutParams(vl1p);
		vl1.setOrientation(VERTICAL);
		txtRank = new TextView(getContext());
		if (playStat == null) {
			txtRank.setText(getResources().getText(R.string.rank));
		} else {
			txtRank.setText(getResources().getText(R.string.rank) + ": " + playStat.getRank());
		}
		vl1.addView(txtRank);

		hl.addView(vl1);

		LinearLayout vl2 = new LinearLayout(getContext());
		LayoutParams vl2p = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl2.setLayoutParams(vl2p);
		vl2.setOrientation(VERTICAL);
		txtScore = new TextView(getContext());
		if (playStat == null) {
			txtScore.setText(getResources().getText(R.string.score));
		} else {
			if (playStat.getScore() == (int) playStat.getScore()) {
				txtScore.setText(getResources().getText(R.string.score) + ": " + (int) playStat.getScore());
			} else {
				txtScore.setText(getResources().getText(R.string.score) + ": " + playStat.getScore());
			}
		}
		vl2.addView(txtScore);

		hl.addView(vl2);
		playStatLayout.addView(hl);

		return playStatLayout;
	}

	private Button createEditButton() {
		bt = new Button(getContext());
		bt.setText("...");
		if (playStat == null) {
			bt.setVisibility(INVISIBLE);
		}
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				editStat();
			}
		});
		return bt;
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
			playStatLayout.setVisibility(VISIBLE);
			bt.setVisibility(VISIBLE);
			editStat();
		}
	}

	private void editStat() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		final View vvv = LayoutInflater.from(getContext()).inflate(R.layout.edit_play_stat, null);
		final EditText txtRank1 = (EditText) vvv.findViewById(R.id.txtRank);
		final EditText txtScore1 = (EditText) vvv.findViewById(R.id.txtScore);
		final Spinner cbPlayer1 = (Spinner) vvv.findViewById(R.id.cbPlayer);
		cbPlayer1.setAdapter(new PlayerAdapter(getContext(), players));
		txtRank1.setText("" + playStat.getRank());
		if (playStat.getScore() == (int) playStat.getScore()) {
			txtScore1.setText("" + (int) playStat.getScore());
		} else {
			txtScore1.setText("" + playStat.getScore());
		}
		if (playStat.getPlayer() != null) {
			cbPlayer1.setSelection(players.indexOf(new Player(playStat.getPlayer().getId())));
		}
		alert.setView(vvv);
		alert.setPositiveButton(getResources().getText(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				playStat.setRank(Integer.parseInt("" + txtRank1.getText()));
				playStat.setScore(Double.parseDouble("" + txtScore1.getText()));
				playStat.setPlayer((Player) cbPlayer1.getSelectedItem());
				txtRank.setText(getResources().getText(R.string.rank) + ": " + playStat.getRank());
				txtScore.setText(getResources().getText(R.string.score) + ": " + playStat.getScore());
				txtPlayerName.setText(playStat.getPlayer().getPseudo());
			}
		});

		alert.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				if (playStat.getPlayer() == null) {
					playStatClickedListener.playStatClicked(PlayStatView.this);
				}
			}
		});
		alert.show();

	}

	public interface OnPlayStatClickedListener {
		void playStatClicked(PlayStatView v);
	}
}
