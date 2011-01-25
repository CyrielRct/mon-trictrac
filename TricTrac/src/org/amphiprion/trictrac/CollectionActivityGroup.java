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
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

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
		// Start the root activity withing the group and get its view
		View view = getLocalActivityManager().startActivity("CollectionList",
				new Intent(this, CollectionList.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();

		// Replace the view of this ActivityGroup
		setContentView(view);
		isCollectionDisplayed = true;
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
}
