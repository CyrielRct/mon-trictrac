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

import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Search;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * @author amphiprion
 * 
 */
public class CollectionActivityGroup extends ActivityGroup {
	public static CollectionActivityGroup instance;
	private boolean isCollectionDisplayed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		CollectionList.group = this;
		GameList.group = this;

		// Start the root activity withing the group and get its view
		View view = getLocalActivityManager().startActivity("CollectionList",
				new Intent(this, CollectionList.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();

		// Replace the view of this ActivityGroup
		setContentView(view);
		isCollectionDisplayed = true;
	}

	@Override
	public boolean onSearchRequested() {
		if (!isCollectionDisplayed) {
			// startSearch(GameList.instance.getQuery(), true, null, false);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			// alert.setTitle(R.string.search_hint);
			alert.setMessage(R.string.search_hint);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			input.setTag(GameList.instance.getQuery());
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = "" + input.getText();
					value = value.replaceAll(" ", "%");
					GameList.instance.setQuery(value);
				}
			});

			alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();

			return true;
		} else {
			return true;
		}
	}

	public void showCollectionList() {
		// Start the root activity withing the group and get its view
		View view = getLocalActivityManager().startActivity("CollectionList",
				new Intent(this, CollectionList.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();

		// Replace the view of this ActivityGroup
		setContentView(view);
		isCollectionDisplayed = true;
	}

	public void showGameList(Collection c, Search s) {
		Intent intent = new Intent(this, GameList.class);
		intent.putExtra("COLLECTION", c);
		intent.putExtra("SEARCH", s);
		View view = getLocalActivityManager()
				.startActivity("GameList", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();

		// Replace the view of this ActivityGroup
		setContentView(view);
		isCollectionDisplayed = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !isCollectionDisplayed) {

			Home.gotToCollection(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (isCollectionDisplayed) {
			MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_COLLECTION, 0, R.string.add_collection);
			addAccount.setIcon(android.R.drawable.ic_menu_add);

			MenuItem searchTrictrac = menu.add(0, ApplicationConstants.MENU_ID_SEARCH_TRICTRAC_GAME, 1,
					R.string.menu_search_trictrac);
			searchTrictrac.setIcon(android.R.drawable.ic_menu_search);

			MenuItem account = menu.add(1, ApplicationConstants.MENU_ID_ACCOUNT, 2, R.string.trictrac_account);
			account.setIcon(android.R.drawable.ic_menu_info_details);

			MenuItem preference = menu.add(2, ApplicationConstants.MENU_ID_PREFERENCE, 3, R.string.preference);
			preference.setIcon(android.R.drawable.ic_menu_preferences);
		} else {
			if (GameList.instance.getSearch() != null || GameList.instance.getQuery() != null) {
				MenuItem clearSearch = menu.add(0, ApplicationConstants.MENU_ID_CLEAR_SEARCH, 0, R.string.clear_filter);
				clearSearch.setIcon(R.drawable.search_cleared);
			}
			MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH, 1,
					R.string.apply_existing_filter);
			addAccount.setIcon(R.drawable.search);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (isCollectionDisplayed) {
			if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_COLLECTION) {
				Intent i = new Intent(this, EditCollection.class);
				// i.putExtra("COLLECTION", collection);
				startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_SEARCH_TRICTRAC_GAME) {
				Intent i = new Intent(this, TricTracGameList.class);
				startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_SEARCH_TRICTRAC_GAME);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_PREFERENCE) {
				Home.openPreference(this);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_ACCOUNT) {
				Home.openAccount(this);
			}
		} else {
			return GameList.instance.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isCollectionDisplayed) {
			CollectionList.instance.onActivityResult(this, requestCode, resultCode, data);
		}
	}
}
