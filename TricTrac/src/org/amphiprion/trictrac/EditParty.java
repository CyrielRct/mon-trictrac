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
package org.amphiprion.trictrac;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.adapter.DateAdapter;
import org.amphiprion.trictrac.dao.PlayStatDao;
import org.amphiprion.trictrac.dao.PlayerDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.view.DatePickerSpinner;
import org.amphiprion.trictrac.view.PlayStatView;
import org.amphiprion.trictrac.view.PlayStatView.OnPlayStatClickedListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The activity used to edit/create a party.
 * 
 * @author amphiprion
 * 
 */
public class EditParty extends Activity implements OnPlayStatClickedListener {
	private Party party;

	private List<PlayStat> playStats;
	private List<Player> players;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_party);

		players = PlayerDao.getInstance(this).getPlayers();
		Player unknowPlayer = new Player(null);
		unknowPlayer.setPseudo("" + getResources().getText(R.string.default_player));

		players.add(0, unknowPlayer);
		Game game = (Game) getIntent().getSerializableExtra("GAME");
		ImageView imgGame = (ImageView) findViewById(R.id.img_game);
		File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/"
				+ game.getImageName());
		Bitmap bitmap = null;
		if (f.exists()) {
			bitmap = BitmapFactory.decodeFile(f.toString());
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);
		}

		imgGame.setImageBitmap(bitmap);

		final DatePickerSpinner cbDate = (DatePickerSpinner) findViewById(R.id.cbDate);
		DateAdapter dateAdapter = new DateAdapter(this);
		cbDate.setAdapter(dateAdapter);

		final TextView txtCity = (TextView) findViewById(R.id.txtCity);
		final TextView txtEvent = (TextView) findViewById(R.id.txtEvent);
		final TextView txtRating = (TextView) findViewById(R.id.stRating);
		final TextView txtDuration = (TextView) findViewById(R.id.txtDuration);
		final TextView txtComment = (TextView) findViewById(R.id.txtComment);

		if (party == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				party = (Party) intent.getExtras().getSerializable("PARTY");
				if (party != null) {
					txtCity.setText(party.getCity());
					if (party.getDate() == null) {
						dateAdapter.add(new Date());
					} else {
						dateAdapter.add(party.getDate());
					}
					txtEvent.setText("" + party.getEvent());
					txtRating.setText("" + party.getHappyness());
					txtDuration.setText("" + party.getDuration());
					txtComment.setText("" + party.getComment());
				}
			}
		}
		if (party == null) {
			// its a creation
			party = new Party();
			party.setGameId(game.getId());
			setTitle(R.string.add_party);
			dateAdapter.add(new Date());
		} else {
			setTitle(R.string.edit_party);
		}

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				party.setDate((Date) cbDate.getSelectedItem());
				party.setCity("" + txtCity.getText());
				party.setEvent("" + txtEvent.getText());
				if ("".equals("" + txtRating.getText())) {
					party.setHappyness(0);
				} else {
					party.setHappyness(Integer.parseInt("" + txtRating.getText()));
				}
				if ("".equals("" + txtDuration.getText())) {
					party.setDuration(0);
				} else {
					party.setDuration(Integer.parseInt("" + txtDuration.getText()));
				}
				party.setComment("" + txtComment.getText());
				party.setStats(playStats);

				Intent i = new Intent();
				i.putExtra("PARTY", party);
				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		buildPlayStatList();
	}

	private void buildPlayStatList() {
		playStats = PlayStatDao.getInstance(this).getPlayStat(party);
		LinearLayout ln = (LinearLayout) findViewById(R.id.play_stats_list);
		ln.removeAllViews();
		for (PlayStat playStat : playStats) {
			ln.addView(new PlayStatView(this, playStat, this, players));
		}
		ln.addView(new PlayStatView(this, null, this, players));
	}

	@Override
	public void playStatClicked(PlayStatView view) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.play_stats_list);
		if (view.getPlayStat() == null) {
			ln.addView(new PlayStatView(this, null, this, players));
			PlayStat playStat = new PlayStat();
			playStat.setPartyId(party.getId());
			view.setPlayStat(playStat);
			playStats.add(playStat);
		} else if (view.getPlayStat().getState() == PlayStat.DbState.NEW) {
			ln.removeView(view);
			playStats.remove(view.getPlayStat());
		} else {
			view.getPlayStat().setState(PlayStat.DbState.DELETE);
			ln.removeView(view);
		}
	}

}
