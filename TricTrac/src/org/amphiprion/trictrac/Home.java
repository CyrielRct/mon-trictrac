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
import java.util.List;

import org.amphiprion.trictrac.GameList.ClickAction;
import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.task.ImportCollectionTask.ImportCollectionListener;
import org.amphiprion.trictrac.util.DateUtil;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TabHost;

public class Home extends TabActivity implements ImportCollectionListener {
	private static boolean init = false;
	private boolean cleanOnDestroy = true;
	private List<Collection> collectionsToUpdate;

	/** Possible action on startup. */
	private enum StartupAction {
		NOTHING, SYNCH_COLLECTION
	}

	private StartupAction startupAction;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (!init) {
			DateUtil.init(this);
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY).mkdirs();
		}

		SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
		startupAction = StartupAction.values()[pref.getInt(StartupAction.class.getName(), 0)];

	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		if (!init && startupAction == StartupAction.SYNCH_COLLECTION) {
			collectionsToUpdate = CollectionDao.getInstance(this).getCollections();
			importNextCollection();
		} else {
			init();
		}

	}

	private void importNextCollection() {
		if (!collectionsToUpdate.isEmpty()) {
			ImportCollectionTask task = new ImportCollectionTask(this);
			Collection current = collectionsToUpdate.get(0);
			collectionsToUpdate.remove(0);
			task.execute(current);
		} else {
			init();
		}
	}

	private void init() {
		init = true;
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
		intent = new Intent().setClass(this, PlayerList.class);
		spec = tabHost.newTabSpec("partylist").setIndicator(res.getString(R.string.tab_player),
				res.getDrawable(R.drawable.play)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		cleanOnDestroy = false;
		return super.onRetainNonConfigurationInstance();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		cleanOnDestroy = true;
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onDestroy() {
		if (cleanOnDestroy) {
			init = false;
		}
		super.onDestroy();
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void importEnded(boolean succeed, Collection collection, List<CollectionGame> links) {
		if (succeed) {
			CollectionDao.getInstance(this).updateLinks(collection.getId(), links);
		}
		importNextCollection();
	}

	public static void openPreference(final Context context) {
		final SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View vvv = LayoutInflater.from(context).inflate(R.layout.preferences, null);
		final Spinner cbStartup = (Spinner) vvv.findViewById(R.id.cbStartup);
		cbStartup.setSelection(pref.getInt(StartupAction.class.getName(), 0));
		final Spinner cbGameClick = (Spinner) vvv.findViewById(R.id.cbGameClick);
		cbGameClick.setSelection(pref.getInt(ClickAction.class.getName(), 0));
		alert.setView(vvv);
		alert.setPositiveButton(context.getResources().getText(R.string.save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editor edit = pref.edit();
				edit.putInt(StartupAction.class.getName(), cbStartup.getSelectedItemPosition());
				edit.putInt(ClickAction.class.getName(), cbGameClick.getSelectedItemPosition());
				edit.commit();
			}
		});

		alert.setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}
}