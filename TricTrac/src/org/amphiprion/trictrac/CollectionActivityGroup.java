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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.GameListContext.ClickAction;
import org.amphiprion.trictrac.adapter.SearchAdapter;
import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.dao.SearchDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.handler.GameHandler;
import org.amphiprion.trictrac.task.ITaskListener;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.task.ImportCollectionTask.ImportCollectionListener;
import org.amphiprion.trictrac.task.LoadGamesTask;
import org.amphiprion.trictrac.task.LoadGamesTask.LoadGameListener;
import org.amphiprion.trictrac.task.SynchronizeGamesTask;
import org.amphiprion.trictrac.view.CollectionSummaryView;
import org.amphiprion.trictrac.view.GameSummaryView;
import org.amphiprion.trictrac.view.MyScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author amphiprion
 * 
 */
public class CollectionActivityGroup extends Activity {
	public static CollectionActivityGroup instance;
	private boolean isCollectionDisplayed;
	private Collection currentCollection;
	private Game currentGame;
	private GameListContext gameListContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

		// Replace the view of this ActivityGroup
		isCollectionDisplayed = true;
		setContentView(R.layout.collection_list);
		buildCollectionList();
	}

	public void buildCollectionList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.collection_list);
		ln.removeAllViews();

		List<Collection> collections = CollectionDao.getInstance(this).getCollections();
		if (collections.size() > 0) {
			Collection all = new Collection(null);
			all.setName(getResources().getString(R.string.search_all_games));
			int count = GameDao.getInstance(this).getGameCount(null, null, null);
			all.setCount(count);
			collections.add(0, all);
			for (Collection collection : collections) {
				CollectionSummaryView view = new CollectionSummaryView(this, collection);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v instanceof CollectionSummaryView) {
							Collection collection = ((CollectionSummaryView) v).getCollection();
							if (collection.getId() == null) {
								Home.gotToCollection(CollectionActivityGroup.this, null, null);
							} else {
								Home.gotToCollection(CollectionActivityGroup.this, collection, null);
							}
						}
					}
				});
				if (collection.getId() != null) {
					view.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							registerForContextMenu(v);
							openContextMenu(v);
							unregisterForContextMenu(v);
							return true;
						}
					});
				}

				ln.addView(view);
			}
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_collection_list);
			ln.addView(tv);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (isCollectionDisplayed) {
			if (v instanceof CollectionSummaryView) {
				currentCollection = ((CollectionSummaryView) v).getCollection();
				menu.add(1, ApplicationConstants.MENU_ID_EDIT_COLLECTION, 0, R.string.edit_collection);
				menu.add(1, ApplicationConstants.MENU_ID_SYNC_COLLECTION, 1, R.string.synch_collection);
				menu.add(2, ApplicationConstants.MENU_ID_DELETE_COLLECTION, 2, R.string.delete_collection);
			}
		} else {
			if (v instanceof GameSummaryView) {
				currentGame = ((GameSummaryView) v).getGame();
				menu.add(1, ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC, 0, R.string.goto_trictrac_name);
				menu.add(1, ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC, 1, R.string.goto_trictrac_advices);
				menu.add(1, ApplicationConstants.MENU_ID_SYNCHRO_GAME, 2, R.string.synch_game);
				menu.add(2, ApplicationConstants.MENU_ID_CREATE_PARTY, 3, R.string.add_party);
				menu.add(2, ApplicationConstants.MENU_ID_VIEW_PARTIES, 4, R.string.view_parties);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (isCollectionDisplayed) {
			if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_COLLECTION) {
				Intent i = new Intent(this, EditCollection.class);
				i.putExtra("COLLECTION", currentCollection);
				startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_COLLECTION) {
				CollectionDao.getInstance(this).delete(currentCollection);
				buildCollectionList();
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_SYNC_COLLECTION) {
				launchImportCollection(currentCollection);
			}
		} else {
			if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCHRO_GAME) {
				new GameHandler().parse(currentGame);
				GameDao.getInstance(this).update(currentGame);
				buildGameList();
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_CREATE_PARTY) {
				Intent i = new Intent(this, EditParty.class);
				i.putExtra("GAME", currentGame);
				startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_PARTIES) {
				viewParties(currentGame);
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_GAME_TRICTRAC) {
				gotoTricTracGame(currentGame.getId());
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_VIEW_ADVICES_TRICTRAC) {
				gotoTricTracAdvice(currentGame.getId());
			}

		}
		return true;
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
			input.setTag(gameListContext.query);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = "" + input.getText();
					value = value.replaceAll(" ", "%");
					gameListContext.query = value;
					initGameList();
				}
			});

			alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
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
		// Replace the view of this ActivityGroup
		setContentView(R.layout.collection_list);
		buildCollectionList();
		isCollectionDisplayed = true;
	}

	public void showGameList(Collection c, Search s) {
		gameListContext = new GameListContext();
		gameListContext.collection = c;
		gameListContext.search = s;
		// Replace the view of this ActivityGroup
		setContentView(R.layout.game_list);
		isCollectionDisplayed = false;
		SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
		gameListContext.clickAction = GameListContext.ClickAction.values()[pref.getInt(ClickAction.class.getName(), 0)];

		final Rect r = new Rect();
		gameListContext.scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		gameListContext.scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!gameListContext.allLoaded && !gameListContext.loading) {
					LinearLayout ln = (LinearLayout) gameListContext.scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							gameListContext.loading = true;
							loadGameNextPage();
						}
					}
				}
			}
		});
		initGameList();

	}

	private void initGameList() {
		gameListContext.loadedPage = 0;
		if (gameListContext.games == null) {
			gameListContext.games = new ArrayList<Game>();
		} else {
			gameListContext.games.clear();
		}
		loadGameNextPage();
	}

	private void loadGameNextPage() {
		if (gameListContext.loadedPage == 0) {
			int nb = GameDao.getInstance(this).getGameCount(gameListContext.collection, gameListContext.search, gameListContext.query);
			Toast.makeText(this, getResources().getString(R.string.message_nb_result, nb), Toast.LENGTH_LONG).show();
			List<Game> newGames = GameDao.getInstance(this).getGames(gameListContext.collection, gameListContext.loadedPage, GameListContext.PAGE_SIZE, gameListContext.search,
					gameListContext.query);
			importGameEnded(true, newGames);
		} else {
			LoadGameListener l = new LoadGameListener() {

				@Override
				public void importEnded(boolean succeed, List<Game> games) {
					importGameEnded(succeed, games);
				}

				@Override
				public Context getContext() {
					return CollectionActivityGroup.this;
				}
			};
			gameListContext.task = new LoadGamesTask(l, gameListContext.collection, gameListContext.loadedPage, GameListContext.PAGE_SIZE, gameListContext.search,
					gameListContext.query);
			gameListContext.task.execute();
		}
	}

	public void importGameEnded(boolean succeed, List<Game> newGames) {
		if (succeed) {
			gameListContext.task = null;
			if (newGames != null && newGames.size() > 0) {
				if (newGames.size() == GameListContext.PAGE_SIZE + 1) {
					newGames.remove(GameListContext.PAGE_SIZE);
					gameListContext.allLoaded = false;
				} else {
					gameListContext.allLoaded = true;
				}
			} else {
				gameListContext.allLoaded = true;
			}
			if (gameListContext.loadedPage != 0) {
				addGameElementToList(newGames);
			} else {
				gameListContext.games = newGames;
				buildGameList();
			}
			gameListContext.loadedPage++;
		}
		gameListContext.loading = false;

	}

	private void buildGameList() {
		String title;
		if (gameListContext.collection != null) {
			title = gameListContext.collection.getName();
		} else {
			title = getResources().getString(R.string.search_all_games);
		}
		if (gameListContext.search != null) {
			title += ": " + gameListContext.search.getName();
		}
		if (gameListContext.query != null) {
			title += " [" + gameListContext.query + "]";
		}
		Home.setTopTitle(title);
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		ln.removeAllViews();
		addGameElementToList(gameListContext.games);
	}

	private void addGameElementToList(List<Game> newGames) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.game_list);
		if (newGames != gameListContext.games) {
			gameListContext.games.addAll(newGames);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final Game game : newGames) {
			GameSummaryView view = new GameSummaryView(this, game);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (gameListContext.clickAction) {
					case TRIC_TRAC:
						gotoTricTracGame(game.getId());
						break;
					case PARTIES:
						viewParties(game);
						break;
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

		if (!gameListContext.allLoaded) {
			ln.addView(getProgressView());
		}
	}

	private View getProgressView() {
		LinearLayout lnExpand = new LinearLayout(this);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lnExpand.setLayoutParams(lp);
		lnExpand.setBackgroundColor(getResources().getColor(R.color.grey));

		ProgressBar im = new ProgressBar(this);
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		im.setLayoutParams(imglp);
		lnExpand.addView(im);

		TextView tv = new TextView(this);
		tv.setText(getResources().getText(R.string.loading));
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		tlp.gravity = Gravity.CENTER_VERTICAL;

		tv.setLayoutParams(tlp);
		lnExpand.addView(tv);

		return lnExpand;
	}

	private void gotoTricTracGame(String gameId) {
		String url = "http://www.trictrac.net/index.php3?id=jeux&rub=detail&inf=detail&jeu=" + gameId;
		Home.browse(this, url);
	}

	private void gotoTricTracAdvice(String gameId) {
		String url = "http://www.trictrac.net/aides/aide.php?rub=jeux&aide=avis_total&ref=" + gameId;
		Home.browse(this, url);
	}

	private void viewParties(Game game) {
		Home.goToParties(this, game);
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
			int index = 0;

			MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_COLLECTION, index++, R.string.add_collection);
			addAccount.setIcon(android.R.drawable.ic_menu_add);

			MenuItem searchTrictrac = menu.add(0, ApplicationConstants.MENU_ID_SEARCH_TRICTRAC_GAME, index++, R.string.menu_search_trictrac);
			searchTrictrac.setIcon(android.R.drawable.ic_menu_search);

			MenuItem synchAllGames = menu.add(0, ApplicationConstants.MENU_ID_SYNCH_ALL_GAMES, index++, R.string.menu_synch_games);
			synchAllGames.setIcon(android.R.drawable.ic_menu_share);

			MenuItem account = menu.add(1, ApplicationConstants.MENU_ID_ACCOUNT, index++, R.string.trictrac_account);
			account.setIcon(android.R.drawable.ic_menu_info_details);

			MenuItem preference = menu.add(2, ApplicationConstants.MENU_ID_PREFERENCE, index++, R.string.preference);
			preference.setIcon(android.R.drawable.ic_menu_preferences);
		} else {
			if (gameListContext.search != null || gameListContext.query != null) {
				MenuItem clearSearch = menu.add(0, ApplicationConstants.MENU_ID_CLEAR_SEARCH, 0, R.string.clear_filter);
				clearSearch.setIcon(R.drawable.search_cleared);
			}
			MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH, 1, R.string.apply_existing_filter);
			addAccount.setIcon(R.drawable.search);

			MenuItem search = menu.add(0, ApplicationConstants.MENU_ID_SEARCH, 2, R.string.menu_seach);
			search.setIcon(android.R.drawable.ic_menu_search);
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
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCH_ALL_GAMES) {
				SynchronizeGamesTask task = new SynchronizeGamesTask(new ITaskListener() {
					@Override
					public void taskEnded(boolean success) {
						buildCollectionList();
					}

					@Override
					public Context getContext() {
						return CollectionActivityGroup.this;
					}
				});
				task.execute();
			}
		} else {
			if (item.getItemId() == ApplicationConstants.MENU_ID_CHOOSE_EXISTING_SEARCH) {
				chooseSearchFilter();
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_CLEAR_SEARCH) {
				gameListContext.query = null;
				gameListContext.search = null;
				initGameList();
			} else if (item.getItemId() == ApplicationConstants.MENU_ID_SEARCH) {
				onSearchRequested();
			}
		}
		return true;
	}

	private void chooseSearchFilter() {
		final List<Search> searchs = SearchDao.getInstance(this).getSearchs();
		if (searchs.size() > 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.search_choice_title));
			builder.setAdapter(new SearchAdapter(this, searchs), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
					gameListContext.search = searchs.get(item);
					initGameList();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (searchs.size() == 1) {
			gameListContext.search = searchs.get(0);
			initGameList();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isCollectionDisplayed) {
			if (resultCode == RESULT_OK) {
				if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION) {
					Collection collection = (Collection) data.getSerializableExtra("COLLECTION");
					CollectionDao.getInstance(this).create(collection);
					launchImportCollection(collection);
				} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION) {
					Collection collection = (Collection) data.getSerializableExtra("COLLECTION");
					CollectionDao.getInstance(this).update(collection);
					buildCollectionList();
				}
			}
		} else {
			if (resultCode == RESULT_OK) {
				if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY) {
					Party party = (Party) data.getSerializableExtra("PARTY");
					party.setLastUpdateDate(new Date());
					PartyDao.getInstance(this).persist(party);
					viewParties(currentGame);
				}
			}
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_VIEW_PARTIES) {
				initGameList();
			}

		}
	}

	private void launchImportCollection(Collection collection) {
		ImportCollectionTask task = new ImportCollectionTask(new ImportCollectionListener() {

			@Override
			public void importEnded(boolean succeed, Collection collection, List<CollectionGame> links) {
				if (succeed) {
					CollectionDao.getInstance(CollectionActivityGroup.this).updateLinks(collection.getId(), links);
				}
				buildCollectionList();
			}

			@Override
			public Context getContext() {
				return CollectionActivityGroup.this;
			}
		});
		task.execute(collection);

	}
}
