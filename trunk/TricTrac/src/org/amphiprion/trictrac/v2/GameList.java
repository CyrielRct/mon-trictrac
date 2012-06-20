package org.amphiprion.trictrac.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.GameListContext;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.task.LoadGamesTask;
import org.amphiprion.trictrac.task.LoadGamesTask.LoadGameListener;
import org.amphiprion.trictrac.v2.flip.FlipViewGroup;
import org.amphiprion.trictrac.view.GameSummaryView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameList extends Activity {
	/** Called when the activity is first created. */

	private FlipViewGroup contentView;
	private int nbPerPage = 6;
	private boolean editMode;
	private Game currentGame;
	private GameListContext gameListContext;
	private int totalGames;
	private String title = "Jeux";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		contentView = new FlipViewGroup(this);
		gameListContext = new GameListContext();
		gameListContext.collection = (Collection) getIntent()
				.getSerializableExtra("COLLECTION");

		initGameList();
		setContentView(contentView);
		contentView.callbackWhenPageChanged = new Runnable() {
			@Override
			public void run() {
				contentView.post(new Runnable() {
					@Override
					public void run() {
						if (!gameListContext.allLoaded) {
							if (contentView.getCurrentPage() + 2 >= contentView
									.getPageCount()) {
								loadGameNextPage();
							}
						}
					}
				});
			}
		};
		// contentView.startFlipping(); // make the first_page view flipping

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return contentView.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (resultCode == RESULT_OK) {
		// if (requestCode ==
		// ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION) {
		// Collection collection = (Collection)
		// data.getSerializableExtra("COLLECTION");
		// CollectionDao.getInstance(this).create(collection);
		// launchImportCollection(collection);
		// } else if (requestCode ==
		// ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION) {
		// Collection collection = (Collection)
		// data.getSerializableExtra("COLLECTION");
		// CollectionDao.getInstance(this).update(collection);
		// currentCollection.setName(collection.getName());
		// currentCollection.setTricTracId(collection.getTricTracId());
		//
		// int index = collections.indexOf(currentCollection);
		// int page = index / nbPerPage;
		// int c = index % nbPerPage;
		// View v = contentView.getFlipView(page);
		// TextView txt = (TextView)
		// v.findViewById(getResources().getIdentifier("name" + c, "id",
		// ApplicationConstants.PACKAGE));
		// txt.setText(currentCollection.getName() + "\n" +
		// currentCollection.getCount());
		// if (editMode) {
		// gotoEditMode();
		// }
		// }
		// }
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
			int nb = GameDao.getInstance(this).getGameCount(
					gameListContext.collection, gameListContext.search,
					gameListContext.query);
			totalGames = nb;
			Toast.makeText(this,
					getResources().getString(R.string.message_nb_result, nb),
					Toast.LENGTH_LONG).show();
			List<Game> newGames = GameDao.getInstance(this).getGames(
					gameListContext.collection, gameListContext.loadedPage,
					GameListContext.PAGE_SIZE, gameListContext.search,
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
					return GameList.this;
				}
			};
			gameListContext.task = new LoadGamesTask(l,
					gameListContext.collection, gameListContext.loadedPage,
					GameListContext.PAGE_SIZE, gameListContext.search,
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
		contentView.clearFlipViews();
		addGameElementToList(gameListContext.games);
	}

	private void addGameElementToList(List<Game> newGames) {
		if (newGames != gameListContext.games) {
			gameListContext.games.addAll(newGames);
		}

		int nbTotalPage = (totalGames - 1) / nbPerPage + 1;
		int nbExisting = contentView.getPageCount();
		int nbPage = (newGames.size() - 1) / nbPerPage + 1;

		for (int i = 0; i < nbPage; i++) {
			View v = View.inflate(this, R.layout.v2_game_page, null);
			TextView txtTitle = (TextView) v.findViewById(R.id.title);
			txtTitle.setText(title);
			txtTitle = (TextView) v.findViewById(R.id.page);
			txtTitle.setText("Page " + (nbExisting + i + 1) + " / "
					+ nbTotalPage);
			contentView.addFlipView(v);
			for (int c = 0; c < nbPerPage; c++) {
				int index = nbPerPage * i + c;
				ImageView img = (ImageView) v.findViewById(getResources()
						.getIdentifier("img" + c, "id",
								ApplicationConstants.PACKAGE));
				if (index < newGames.size()) {
					final Game game = newGames.get(index);

					File f = new File(Environment.getExternalStorageDirectory()
							+ "/" + ApplicationConstants.DIRECTORY + "/"
							+ game.getImageName());
					Bitmap bitmap = null;
					if (f.exists()) {
						bitmap = BitmapFactory.decodeFile(f.toString());
					}
					if (bitmap == null) {
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.no_game_image);
					}

					img.setImageBitmap(bitmap);

					TextView txt = (TextView) v.findViewById(getResources()
							.getIdentifier("name" + c, "id",
									ApplicationConstants.PACKAGE));
					txt.setText(game.getName());
					img.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							gotoEditMode();
							return true;
						}
					});
					ImageView imgDel = (ImageView) v
							.findViewById(getResources().getIdentifier(
									"imgDelete" + c, "id",
									ApplicationConstants.PACKAGE));
					imgDel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// buildPages();
						}
					});
					ImageView imgEdit = (ImageView) v
							.findViewById(getResources().getIdentifier(
									"imgEdit" + c, "id",
									ApplicationConstants.PACKAGE));
					imgEdit.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							currentGame = game;
							// Intent i = new Intent(GameList.this,
							// EditCollection.class);
							// i.putExtra("COLLECTION", currentCollection);
							// startActivityForResult(i,
							// ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION);
						}
					});
					LinearLayout ll = (LinearLayout) v
							.findViewById(getResources().getIdentifier(
									"detail" + c, "id",
									ApplicationConstants.PACKAGE));
					ll.addView(GameSummaryView.createMainStatsLayout(game,
							this, false, true));
					ll.addView(GameSummaryView.createParties(game, this, true));
				} else {
					img.setImageResource(R.drawable.no_game_image); // ludo_invis);
					img.setVisibility(View.INVISIBLE);
					LinearLayout ll = (LinearLayout) v
							.findViewById(getResources().getIdentifier(
									"detail" + c, "id",
									ApplicationConstants.PACKAGE));
					ll.setVisibility(View.INVISIBLE);
				}
			}
		}
		if (editMode) {
			editMode = false;
			gotoEditMode();
		}
	}

	public void gotoEditMode() {
		if (contentView.isFlipping()) {
			return;
		}
		editMode = !editMode;
		contentView.setLocked(editMode);
		Animation anim = AnimationUtils.loadAnimation(GameList.this,
				R.anim.shake);
		View v = contentView.getFlipView(contentView.getCurrentPage());
		for (int c = 0; c < nbPerPage; c++) {
			int index = contentView.getCurrentPage() * nbPerPage + c;
			if (index < gameListContext.games.size()) {
				ImageView img = (ImageView) v.findViewById(getResources()
						.getIdentifier("img" + c, "id",
								ApplicationConstants.PACKAGE));
				ImageView imgDel = (ImageView) v.findViewById(getResources()
						.getIdentifier("imgDelete" + c, "id",
								ApplicationConstants.PACKAGE));
				ImageView imgEdit = (ImageView) v.findViewById(getResources()
						.getIdentifier("imgEdit" + c, "id",
								ApplicationConstants.PACKAGE));
				if (editMode) {
					img.startAnimation(anim);
					imgDel.setVisibility(View.VISIBLE);
					imgEdit.setVisibility(View.VISIBLE);
				} else {
					img.setAnimation(null);
					imgDel.setVisibility(View.INVISIBLE);
					imgEdit.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (editMode) {
			gotoEditMode();
		} else {
			super.onBackPressed();
		}
	}
}