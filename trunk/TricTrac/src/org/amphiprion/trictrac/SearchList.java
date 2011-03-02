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

import org.amphiprion.trictrac.adapter.CollectionAdapter;
import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.dao.SearchDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.view.SearchSummaryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author amphiprion
 * 
 */
public class SearchList extends Activity {
	private Search current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list);
		buildList();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_SEARCH, 1, R.string.add_search);
		addAccount.setIcon(android.R.drawable.ic_menu_add);

		MenuItem account = menu.add(1, ApplicationConstants.MENU_ID_ACCOUNT, 1, R.string.trictrac_account);
		account.setIcon(android.R.drawable.ic_menu_info_details);

		MenuItem preference = menu.add(2, ApplicationConstants.MENU_ID_PREFERENCE, 2, R.string.preference);
		preference.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_SEARCH) {
			Intent i = new Intent(this, EditSearch.class);
			// i.putExtra("SEARCH", search);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_SEARCH);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_PREFERENCE) {
			Home.openPreference(this);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_ACCOUNT) {
			Home.openAccount(this);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_SEARCH) {
				Search search = (Search) data.getSerializableExtra("SEARCH");
				SearchDao.getInstance(this).create(search);
				buildList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_SEARCH) {
				Search search = (Search) data.getSerializableExtra("SEARCH");
				SearchDao.getInstance(this).update(search);
				buildList();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof SearchSummaryView) {
			current = ((SearchSummaryView) v).getSearch();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_SEARCH, 0, R.string.edit_search);
			menu.add(2, ApplicationConstants.MENU_ID_DELETE_SEARCH, 1, R.string.delete_search);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_SEARCH) {
			Intent i = new Intent(this, EditSearch.class);
			i.putExtra("SEARCH", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_SEARCH);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_SEARCH) {
			SearchDao.getInstance(this).delete(current);
			buildList();
		}
		return true;
	}

	private void buildList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.search_list);
		ln.removeAllViews();

		List<Search> searchs = SearchDao.getInstance(this).getSearchs();
		if (searchs.size() > 0) {
			for (Search search : searchs) {
				SearchSummaryView view = new SearchSummaryView(this, search);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v instanceof SearchSummaryView) {
							Search search = ((SearchSummaryView) v).getSearch();
							launchSearch(search);
							// Search search = ((SearchSummaryView)
							// v).getSearch();
							// Intent i = new Intent(SearchList.this,
							// GameList.class);
							// i.putExtra("SEARCH", search);
							// startActivity(i);
						}
					}
				});

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
			tv.setText(R.string.empty_search_list);
			ln.addView(tv);
		}
	}

	private void launchSearch(final Search search) {
		final List<Collection> collections = CollectionDao.getInstance(this).getCollections();
		if (collections.size() > 1) {
			Collection all = new Collection(null);
			all.setName(getResources().getString(R.string.search_all_games));
			int count = GameDao.getInstance(this).getGameCount(null, null, null);
			all.setCount(count);
			collections.add(0, all);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.collection_choice_title));
			builder.setAdapter(new CollectionAdapter(SearchList.this, collections), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
					Collection c = collections.get(item);
					if (c.getId() == null) {
						Home.gotToCollection(SearchList.this, null, search);
					} else {
						Home.gotToCollection(SearchList.this, c, search);
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (collections.size() == 1) {
			// Intent i = new Intent(SearchList.this, GameList.class);
			// i.putExtra("COLLECTION", collections.get(0));
			// i.putExtra("SEARCH", search);
			// startActivity(i);
			Home.gotToCollection(SearchList.this, collections.get(0), search);
		}
	}
}
