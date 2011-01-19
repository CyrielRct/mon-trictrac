/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of MyTricTrac.
 *
 * MyTricTrac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyTricTrac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.trictrac;

import java.util.List;

import org.amphiprion.trictrac.dao.PlayerDao;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.task.ITaskListener;
import org.amphiprion.trictrac.task.SynchronizePlayersTask;
import org.amphiprion.trictrac.view.PlayerSummaryView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author amphiprion
 * 
 */
public class PlayerList extends Activity implements ITaskListener {
	private Player current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_list);
		buildList();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_PLAYER, 0, R.string.add_player);
		addAccount.setIcon(android.R.drawable.ic_menu_add);

		MenuItem synchPlayer = menu.add(0, ApplicationConstants.MENU_ID_SYNCH_PLAYER, 1, R.string.synch_players);
		synchPlayer.setIcon(android.R.drawable.ic_menu_share);

		MenuItem account = menu.add(1, ApplicationConstants.MENU_ID_ACCOUNT, 2, R.string.trictrac_account);
		account.setIcon(android.R.drawable.ic_menu_info_details);

		MenuItem preference = menu.add(2, ApplicationConstants.MENU_ID_PREFERENCE, 3, R.string.preference);
		preference.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_PLAYER) {
			Intent i = new Intent(this, EditPlayer.class);
			// i.putExtra("PLAYER", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PLAYER);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_PREFERENCE) {
			Home.openPreference(this);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_ACCOUNT) {
			Home.openAccount(this);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCH_PLAYER) {
			SynchronizePlayersTask task = new SynchronizePlayersTask(this);
			task.execute();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PLAYER) {
				Player player = (Player) data.getSerializableExtra("PLAYER");
				PlayerDao.getInstance(this).persist(player);
				buildList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_PLAYER) {
				Player player = (Player) data.getSerializableExtra("PLAYER");
				PlayerDao.getInstance(this).persist(player);
				buildList();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof PlayerSummaryView) {
			current = ((PlayerSummaryView) v).getPlayer();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_PLAYER, 0, R.string.edit_player);
			menu.add(2, ApplicationConstants.MENU_ID_DELETE_PLAYER, 1, R.string.delete_player);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_PLAYER) {
			Intent i = new Intent(this, EditPlayer.class);
			i.putExtra("PLAYER", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_PLAYER);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_PLAYER) {
			PlayerDao.getInstance(this).delete(current);
			buildList();
		}
		return true;
	}

	private void buildList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.player_list);
		ln.removeAllViews();

		List<Player> players = PlayerDao.getInstance(this).getPlayers();
		if (players.size() > 0) {
			for (Player player : players) {
				PlayerSummaryView view = new PlayerSummaryView(this, player);

				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						registerForContextMenu(v);
						openContextMenu(v);
						unregisterForContextMenu(v);
						return true;
					}
				});

				ln.addView(view);
			}
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_player_list);
			ln.addView(tv);
		}
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void taskEnded(boolean success) {
		buildList();
	}

}
