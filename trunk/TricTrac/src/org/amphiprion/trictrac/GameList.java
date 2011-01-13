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
import java.util.List;

import org.amphiprion.trictrac.adapter.SearchAdapter;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.dao.SearchDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.handler.GameHandler;
import org.amphiprion.trictrac.task.LoadGamesTask;
import org.amphiprion.trictrac.task.LoadGamesTask.LoadGameListener;
import org.amphiprion.trictrac.view.GameSummaryView;
import org.amphiprion.trictrac.view.MyScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author amphiprion
 * 
 */
public class GameList extends Activity implements LoadGameListener {

	private static final int PAGE_SIZE = 20;

	private Collection collection;
	private Search search;
	private int loadedPage;
	private List<Game> games;
	private String query;
	private MyScrollView scrollView;
	private Game current;
	private boolean allLoaded;
	private boolean loading;
	private LoadGamesTask task;

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
					LinearLayout ln = ((LinearLayout) scrollView.getChildAt(0));
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (search != null || query != null) {
			MenuItem clearSearch = menu.add(0, ApplicationConstants.MENU_ID_CLEAR_SEARCH, 0, R.string.clear_filter);
			clearSearch.setIcon(R.drawable.search_cleared);
		}
		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH, 1,
				R.string.apply_existing_filter);
		addAccount.setIcon(R.drawable.search);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH) {
			chooseSearchFilter();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_CLEAR_SEARCH) {
			query = null;
			search = null;
			init();
		}
		return true;
	}

	private void chooseSearchFilter() {
		final List<Search> searchs = SearchDao.getInstance(this).getSearchs();
		if (searchs.size() > 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.search_choice_title));
			builder.setAdapter(new SearchAdapter(GameList.this, searchs), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
					Intent i = new Intent(GameList.this, GameList.class);
					i.putExtra("COLLECTION", collection);
					i.putExtra("SEARCH", searchs.get(item));
					setIntent(i);
					handleIntent(i);
					init();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (searchs.size() == 1) {
			Intent i = new Intent(GameList.this, GameList.class);
			i.putExtra("COLLECTION", collection);
			i.putExtra("SEARCH", searchs.get(0));
			setIntent(i);
			handleIntent(i);
			init();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			query.replaceAll(" ", "%");
			init();
		} else {
			collection = (Collection) getIntent().getSerializableExtra("COLLECTION");
			search = (Search) getIntent().getSerializableExtra("SEARCH");
		}
	}

	private void init() {
		loadedPage = 0;
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
		outState.putSerializable("SEARCH", search);
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
		search = (Search) savedInstanceState.getSerializable("SEARCH");
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
		if (loadedPage == 0) {
			int nb = GameDao.getInstance(this).getGameCount(collection, search, query);
			Toast.makeText(this, getResources().getString(R.string.message_nb_result, nb), Toast.LENGTH_LONG).show();
			List<Game> newGames = GameDao.getInstance(this).getGames(collection, loadedPage, PAGE_SIZE, search, query);
			importEnded(true, newGames);
		} else {
			task = new LoadGamesTask(this, collection, loadedPage, PAGE_SIZE, search, query);
			task.execute();
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
				if (newGames.size() == PAGE_SIZE + 1) {
					newGames.remove(PAGE_SIZE);
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
		String title = collection.getName();
		if (search != null) {
			title += ": " + search.getName();
		}
		if (query != null) {
			title += " [" + query + "]";
		}
		setTitle(title);
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		ln.removeAllViews();
		addElementToList(games);
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
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = "http://www.trictrac.net/index.php3?id=jeux&rub=detail&inf=detail&jeu=" + game.getId();
					Intent i = new Intent(Intent.ACTION_VIEW);

					i.setData(Uri.parse(url));

					startActivity(i);
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

		if (!allLoaded) {
			LinearLayout lnExpand = new LinearLayout(this);
			LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lnExpand.setLayoutParams(lp);
			ImageView im = new ImageView(this);
			LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			imglp.gravity = Gravity.CENTER_VERTICAL;
			imglp.rightMargin = 5;
			im.setLayoutParams(imglp);

			im.setImageDrawable(getResources().getDrawable(R.drawable.loading));
			lnExpand.addView(im);

			LinearLayout accountLayout = new LinearLayout(this);
			LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
			accountLayout.setLayoutParams(aclp);

			TextView tv = new TextView(this);
			tv.setText(getResources().getText(R.string.loading));
			LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(tlp);
			accountLayout.addView(tv);
			lnExpand.addView(accountLayout);

			ln.addView(lnExpand);
			Animation a = new RotateAnimation(0, 360, 23.5f, 23.5f);
			a.setInterpolator(new LinearInterpolator());
			a.setRepeatCount(Animation.INFINITE);
			a.setDuration(2000);
			im.startAnimation(a);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof GameSummaryView) {
			current = ((GameSummaryView) v).getGame();
			menu.add(1, ApplicationConstants.MENU_ID_SYNCHRO_GAME, 0, R.string.synch_game);
			menu.add(2, ApplicationConstants.MENU_ID_CREATE_PARTY, 1, R.string.add_party);
			menu.add(2, ApplicationConstants.MENU_ID_VIEW_PARTIES, 2, R.string.view_parties);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCHRO_GAME) {
			new GameHandler().parse(current);
			GameDao.getInstance(this).update(current);
			buildList();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_CREATE_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			i.putExtra("GAME", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_PARTIES) {
			viewParties();
		}
		return true;
	}

	private void viewParties() {
		Intent i = new Intent(this, PartyList.class);
		i.putExtra("GAME", current);
		// startActivityForResult(i,
		// ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		startActivity(i);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				PartyDao.getInstance(this).persist(party);
				viewParties();
			}
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
