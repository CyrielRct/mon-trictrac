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

import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.view.PartySummaryView;

import android.app.Activity;
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
public class PartyList extends Activity {
	private Game game;
	private Party current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.party_list);

		game = (Game) getIntent().getSerializableExtra("GAME");
		setTitle(getResources().getString(R.string.my_parties, game.getName()));
		buildList();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_CREATE_PARTY, 1, R.string.add_party);
		addAccount.setIcon(android.R.drawable.ic_menu_add);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_CREATE_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			i.putExtra("GAME", game);
			// i.putExtra("PARTY", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				party.setLastUpdateDate(new Date());
				PartyDao.getInstance(this).persist(party);
				buildList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				party.setLastUpdateDate(new Date());
				PartyDao.getInstance(this).persist(party);
				buildList();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof PartySummaryView) {
			current = ((PartySummaryView) v).getParty();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_PARTY, 0, R.string.edit_party);
			menu.add(2, ApplicationConstants.MENU_ID_DELETE_PARTY, 1, R.string.delete_party);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			i.putExtra("GAME", game);
			i.putExtra("PARTY", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_PARTY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_PARTY) {
			PartyDao.getInstance(this).delete(current);
			buildList();
		}
		return true;
	}

	private void buildList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.party_list);
		ln.removeAllViews();

		List<Party> parties = PartyDao.getInstance(this).getParties(game);
		if (parties.size() > 0) {
			for (Party party : parties) {
				PartySummaryView view = new PartySummaryView(this, party);

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
			tv.setText(R.string.empty_party_list);
			ln.addView(tv);
		}
	}

}
