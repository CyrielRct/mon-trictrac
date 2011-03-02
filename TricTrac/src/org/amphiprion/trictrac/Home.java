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
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.adapter.PlayerAdapter;
import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.dao.PlayerDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.entity.Entity.DbState;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.task.ImportCollectionTask.ImportCollectionListener;
import org.amphiprion.trictrac.util.DateUtil;
import org.amphiprion.trictrac.util.LogUtil;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Home extends TabActivity implements ImportCollectionListener {
	private static boolean init = false;
	private boolean cleanOnDestroy = true;
	private List<Collection> collectionsToUpdate;
	private static TabHost tabHost;
	private static TabSpec browserSpec;
	private static TabActivity instance;
	private static String[] titles;

	/** Possible action on startup. */
	private enum StartupAction {
		NOTHING, SYNCH_COLLECTION
	}

	private StartupAction startupAction;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.main);

		if (!init) {
			DateUtil.init(this);
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/logs").mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/stats").mkdirs();
		}

		SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
		startupAction = StartupAction.values()[pref.getInt(StartupAction.class.getName(), 0)];
		LogUtil.traceEnabled = pref.getBoolean("ACTIVE_TRACE", false);
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
		if (!init) {
			init = true;
			Resources res = getResources(); // Resource object to get Drawables
			titles = new String[5];

			tabHost = getTabHost(); // The activity TabHost
			tabHost.clearAllTabs();
			TabHost.TabSpec spec; // Resusable TabSpec for each tab
			Intent intent; // Reusable Intent for each tab

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(this, CollectionActivityGroup.class);
			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec("collectionlist").setIndicator(res.getString(R.string.tab_collection), res.getDrawable(R.drawable.collection)).setContent(intent);
			tabHost.addTab(spec);

			//
			intent = new Intent().setClass(this, SearchList.class);
			spec = tabHost.newTabSpec("searchlist").setIndicator(res.getString(R.string.tab_search), res.getDrawable(R.drawable.search)).setContent(intent);
			tabHost.addTab(spec);

			//
			intent = new Intent().setClass(this, PlayerList.class);
			spec = tabHost.newTabSpec("playerlist").setIndicator(res.getString(R.string.tab_player), res.getDrawable(R.drawable.play)).setContent(intent);
			tabHost.addTab(spec);

			//
			intent = new Intent().setClass(this, PartyList.class);
			spec = tabHost.newTabSpec("partylist").setIndicator(res.getString(R.string.tab_party), res.getDrawable(R.drawable.party)).setContent(intent);
			tabHost.addTab(spec);

			//
			intent = new Intent().setClass(this, Browser.class);
			intent.setData(Uri.parse("http://www.trictrac.net/"));
			spec = tabHost.newTabSpec("browser").setIndicator(res.getString(R.string.tab_browser), res.getDrawable(R.drawable.internet)).setContent(intent);
			tabHost.addTab(spec);
			browserSpec = spec;

			tabHost.setOnTabChangedListener(new OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					if (Browser.instance != null) {
						if (tabHost.getCurrentTab() != 4) {
							Browser.instance.emptyPage();
						} else {
							Browser.instance.restorePage();
						}
					}
					if (titles[tabHost.getCurrentTab()] == null) {
						titles[tabHost.getCurrentTab()] = "" + getResources().getText(R.string.app_name);
					}
					setTitle(titles[tabHost.getCurrentTab()]);
				}
			});

			tabHost.setCurrentTab(0);
		}
	}

	public static void goToParties(Context context, Game game) {
		PartyList.dontCallHandleIntentOnCreate = true;
		tabHost.setCurrentTab(3);
		Intent intent = new Intent().setClass(context, PartyList.class);
		intent.putExtra("GAME", game);
		PartyList.instance.handleIntent(intent);
	}

	public static void setTopProgress(int progress) {
		instance.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress);
	}

	public static void setTopTitle(String title) {
		Home.instance.setTitle(title);
		titles[tabHost.getCurrentTab()] = title;
	}

	public static void browse(Context context, String url) {
		Intent intent = new Intent().setClass(context, Browser.class);
		intent.setData(Uri.parse(url));
		browserSpec.setContent(intent);
		tabHost.setCurrentTab(4);
	}

	public static void gotToCollection(Context context) {
		titles[0] = "" + context.getResources().getText(R.string.app_name);
		setTopTitle(titles[0]);
		tabHost.setCurrentTab(0);
		CollectionActivityGroup.instance.showCollectionList();
	}

	public static void gotToCollection(Context context, Collection collection, Search search) {
		tabHost.setCurrentTab(0);
		CollectionActivityGroup.instance.showGameList(collection, search);
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
		cbGameClick.setSelection(pref.getInt(GameListContext.ClickAction.class.getName(), 0));
		final CheckBox chkTrace = (CheckBox) vvv.findViewById(R.id.chkTrace);
		chkTrace.setChecked(pref.getBoolean("ACTIVE_TRACE", false));
		alert.setView(vvv);
		alert.setPositiveButton(context.getResources().getText(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Editor edit = pref.edit();
				edit.putInt(StartupAction.class.getName(), cbStartup.getSelectedItemPosition());
				edit.putInt(GameListContext.ClickAction.class.getName(), cbGameClick.getSelectedItemPosition());
				edit.putBoolean("ACTIVE_TRACE", chkTrace.isChecked());
				LogUtil.traceEnabled = chkTrace.isChecked();
				edit.commit();
			}
		});

		alert.setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}

	public static void openAccount(final Context context) {
		final SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View vvv = LayoutInflater.from(context).inflate(R.layout.trictrac_account, null);

		final TextView txtLogin = (TextView) vvv.findViewById(R.id.txtLogin);
		final String originalLogin = pref.getString("LOGIN", "");
		txtLogin.setText(originalLogin);
		final TextView txtPwd = (TextView) vvv.findViewById(R.id.txtPwd);
		txtPwd.setText(pref.getString("PWD", ""));

		final Player newPlayer = new Player();
		newPlayer.setPseudo(context.getString(R.string.create_player, "" + txtLogin.getText()));
		final List<Player> players = PlayerDao.getInstance(context).getLocalPlayers();
		players.add(0, newPlayer);

		Player existingPlayer = new Player(pref.getString("ACCOUNT_PLAYER_ID", null));

		PlayerAdapter adapter = new PlayerAdapter(context, players);
		final Spinner cbPlayers = (Spinner) vvv.findViewById(R.id.cbAccountPlayer);
		cbPlayers.setAdapter(adapter);
		if (existingPlayer.getId() != null) {
			cbPlayers.setSelection(players.indexOf(existingPlayer));
			cbPlayers.setEnabled(false);
		}
		cbPlayers.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// be sure txtLogin lost focus before opening combo
				txtPwd.requestFocus();
				return false;
			}
		});

		txtLogin.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					boolean found = false;
					for (int i = 1; i < players.size(); i++) {
						if (players.get(i).getPseudo().equalsIgnoreCase("" + txtLogin.getText())) {
							cbPlayers.setEnabled(false);
							cbPlayers.setSelection(i);
							found = true;
							break;
						}
					}
					if (!found) {
						cbPlayers.setEnabled(true);
						newPlayer.setPseudo(context.getString(R.string.create_player, "" + txtLogin.getText()));
					}
				}
			}
		});

		alert.setView(vvv);
		alert.setPositiveButton(context.getResources().getText(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Editor edit = pref.edit();
				edit.putString("LOGIN", "" + txtLogin.getText());
				edit.putString("PWD", "" + txtPwd.getText());
				Player selectedPlayer = (Player) cbPlayers.getSelectedItem();
				if (selectedPlayer.getState() == DbState.NEW) {
					selectedPlayer.setPseudo("" + txtLogin.getText());
					selectedPlayer.setLastUpdateDate(new Date());
					PlayerDao.getInstance(context).persist(selectedPlayer);
				}
				edit.putString("ACCOUNT_PLAYER_ID", selectedPlayer.getId());
				edit.commit();
			}
		});

		alert.setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();

	}
}