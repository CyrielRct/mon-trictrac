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

import java.io.File;

import org.amphiprion.trictrac.util.DateUtil;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;

public class Home extends TabActivity {
	private static boolean init = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (!init) {
			DateUtil.init(this);
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY).mkdirs();
		}

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, CollectionList.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("collectionlist").setIndicator(res.getString(R.string.tab_collection),
				res.getDrawable(R.drawable.collection)).setContent(intent);

		tabHost.addTab(spec);

		// 
		intent = new Intent().setClass(this, SearchList.class);
		spec = tabHost.newTabSpec("searchlist").setIndicator(res.getString(R.string.tab_search),
				res.getDrawable(R.drawable.search)).setContent(intent);
		tabHost.addTab(spec);

		// 
		intent = new Intent().setClass(this, PartyList.class);
		spec = tabHost.newTabSpec("partylist").setIndicator(res.getString(R.string.tab_party),
				res.getDrawable(R.drawable.play)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

		// ImportCollectionTask task = new ImportCollectionTask(this);
		// String userId = "15938";
		// task.execute(userId);
		// handler.parse("15938");
		// handler.parse("7684");
	}

	// @Override
	// public void importEnded(boolean succeed, List<Game> games) {
	// // ((TextView) findViewById(R.id.toto)).setText(succeed + "--- " +
	// // games.size());
	// // Toast.makeText(this, "loaded=" + games.size(), Toast.LENGTH_LONG);
	// }

}