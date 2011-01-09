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
import org.amphiprion.trictrac.dao.SearchDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.view.GameSummaryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @author amphiprion
 * 
 */
public class GameList extends Activity {

	private static final int PAGE_SIZE = 20;

	private Collection collection;
	private Search search;
	private int loadedPage;
	private List<Game> games;
	private Button next;
	private String query;
	private ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_list);
		handleIntent(getIntent());

		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		next = (Button) findViewById(R.id.btNextPage);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNextPage();
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
		buildList();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("GAMES", (Serializable) games);
		outState.putInt("PAGE", loadedPage);
		outState.putInt("NEXT", next.getVisibility());
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
		next.setVisibility(savedInstanceState.getInt("NEXT"));
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
		}
		List<Game> newGames = GameDao.getInstance(this).getGames(collection, loadedPage, PAGE_SIZE, search, query);
		if (newGames != null && newGames.size() > 0) {
			games.addAll(newGames);
			loadedPage++;
			if (newGames.size() == PAGE_SIZE) {
				next.setVisibility(View.VISIBLE);
			} else {
				next.setVisibility(View.GONE);
			}
			buildList();
		} else {
			next.setVisibility(View.GONE);
		}
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

		for (final Game game : games) {
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
			ln.addView(view);
		}
	}
}
