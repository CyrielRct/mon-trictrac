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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.task.TricTracGamesTask;
import org.amphiprion.trictrac.task.TricTracGamesTask.LoadGameListener;
import org.amphiprion.trictrac.view.GameSummaryView;
import org.amphiprion.trictrac.view.MyScrollView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author amphiprion
 * 
 */
public class TricTracGameList extends Activity implements LoadGameListener {
	private static final int PAGE_SIZE = 10;
	private MyScrollView scrollView;
	private boolean allLoaded;
	private boolean loading;
	private String query;
	private List<Game> games;
	private int loadedPage;
	private Game current;
	private TricTracGamesTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_list);

		handleIntent(getIntent());

		final Rect r = new Rect();
		scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!allLoaded && !loading) {
					LinearLayout ln = (LinearLayout) scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							loading = true;
							loadNextPage();
						}
					}
				}
			}
		});

		init();
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(query, true, null, false);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			init();
		}
	}

	private void init() {
		loadedPage = 0;
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		ln.removeAllViews();
		ln.addView(getProgressView());
		if (games == null) {
			games = new ArrayList<Game>();
		} else {
			games.clear();
		}
		loadNextPage();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("GAMES", (Serializable) games);
		outState.putInt("PAGE", loadedPage);
		outState.putBoolean("ALL_LOADED", allLoaded);
		outState.putString("FILTER", query);
		outState.putInt("SCROLL_X", scrollView.getScrollX());
		outState.putInt("SCROLL_Y", scrollView.getScrollY());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		games = (List<Game>) savedInstanceState.getSerializable("GAMES");
		loadedPage = savedInstanceState.getInt("PAGE");
		allLoaded = savedInstanceState.getBoolean("ALL_LOADED");
		query = savedInstanceState.getString("FILTER");
		buildList();
		final int x = savedInstanceState.getInt("SCROLL_X");
		final int y = savedInstanceState.getInt("SCROLL_Y");
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(x, y);
			}
		});
	}

	private void loadNextPage() {
		if (query != null && query.length() > 0) {
			task = new TricTracGamesTask(this, query, games.size(), PAGE_SIZE);
			task.execute();
		} else {
			importEnded(true, null);
		}
	}

	@Override
	protected void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importEnded(boolean succeed, List<Game> newGames) {
		if (succeed) {
			task = null;
			if (newGames != null && newGames.size() > 0) {
				if (newGames.size() == PAGE_SIZE) {
					allLoaded = false;
				} else {
					allLoaded = true;
				}
			} else {
				allLoaded = true;
			}
			if (loadedPage != 0) {
				addElementToList(newGames);
			} else {
				games = newGames;
				buildList();
			}
			loadedPage++;
		}
		loading = false;
	}

	private void buildList() {
		String title = getResources().getString(R.string.menu_search_trictrac);
		if (query != null) {
			title += " [" + query + "]";
		}
		setTitle(title);
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		ln.removeAllViews();
		if (games != null && games.size() > 0) {
			addElementToList(games);
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_trictrac_game_list);
			ln.addView(tv);
		}
	}

	private void addElementToList(List<Game> newGames) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		if (newGames != games) {
			games.addAll(newGames);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Game game : newGames) {
			GameSummaryView view = new GameSummaryView(this, game);
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

		if (!allLoaded) {
			ln.addView(getProgressView());
		}
	}

	private View getProgressView() {
		LinearLayout lnExpand = new LinearLayout(this);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lnExpand.setLayoutParams(lp);
		ImageView im = new ImageView(this);
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		im.setLayoutParams(imglp);

		im.setImageDrawable(getResources().getDrawable(R.drawable.loading));
		lnExpand.addView(im);

		LinearLayout accountLayout = new LinearLayout(this);
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setLayoutParams(aclp);

		TextView tv = new TextView(this);
		tv.setText(getResources().getText(R.string.loading));
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(tlp);
		accountLayout.addView(tv);
		lnExpand.addView(accountLayout);

		Animation a = new RotateAnimation(0, 360, 23.5f, 23.5f);
		a.setInterpolator(new LinearInterpolator());
		a.setRepeatCount(Animation.INFINITE);
		a.setDuration(2000);
		im.startAnimation(a);
		return lnExpand;
	}

	private void gotoTricTracGame(String gameId) {
		String url = "http://www.trictrac.net/index.php3?id=jeux&rub=detail&inf=detail&jeu=" + gameId;
		Home.browse(this, url);
		setResult(RESULT_CANCELED);
		finish();
	}

	private void gotoTricTracAdvice(String gameId) {
		String url = "http://www.trictrac.net/aides/aide.php?rub=jeux&aide=avis_total&ref=" + gameId;
		Home.browse(this, url);
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_SEARCH, 2, R.string.menu_seach);
		search.setIcon(android.R.drawable.ic_menu_search);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_SEARCH) {
			onSearchRequested();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof GameSummaryView) {
			current = ((GameSummaryView) v).getGame();
			menu.add(1, ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC, 0, R.string.goto_trictrac_name);
			menu.add(1, ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC, 1, R.string.goto_trictrac_advices);
			menu.add(2, ApplicationConstants.MENU_ID_CREATE_PARTY, 2, R.string.add_party);
			menu.add(2, ApplicationConstants.MENU_ID_VIEW_PARTIES, 3, R.string.view_parties);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_CREATE_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			i.putExtra("GAME", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_PARTIES) {
			viewParties(current);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC) {
			gotoTricTracGame(current.getId());
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC) {
			gotoTricTracAdvice(current.getId());
		}

		return true;
	}

	private void viewParties(Game game) {
		// Intent i = new Intent(this, PartyList.class);
		// i.putExtra("GAME", game);
		// startActivityForResult(i,
		// ApplicationConstants.ACTIVITY_RETURN_VIEW_PARTIES);
		Home.goToParties(this, game);
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				party.setLastUpdateDate(new Date());
				PartyDao.getInstance(this).persist(party);
				viewParties(current);
				setResult(RESULT_OK);
			}
		}
		if (requestCode == ApplicationConstants.ACTIVITY_RETURN_VIEW_PARTIES) {
			init();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Context getContext() {
		return this;
	}

}
