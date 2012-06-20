package org.amphiprion.trictrac.v2;

import java.io.File;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.EditCollection;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.task.ImportCollectionTask.ImportCollectionListener;
import org.amphiprion.trictrac.util.DateUtil;
import org.amphiprion.trictrac.util.LogUtil;
import org.amphiprion.trictrac.v2.flip.FlipViewGroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends Activity {
	/** Called when the activity is first created. */
	private static boolean init = false;

	private FlipViewGroup contentView;
	private List<Collection> collections;
	private int nbPerPage = 12;
	private boolean editMode;
	private Collection currentCollection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (!init) {
			DateUtil.init(this);
			new File(Environment.getExternalStorageDirectory() + "/"
					+ ApplicationConstants.DIRECTORY).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/"
					+ ApplicationConstants.DIRECTORY + "/logs").mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/"
					+ ApplicationConstants.DIRECTORY + "/stats").mkdirs();
		}

		SharedPreferences pref = getSharedPreferences(
				ApplicationConstants.GLOBAL_PREFERENCE, 0);
		// startupAction =
		// StartupAction.values()[pref.getInt(StartupAction.class.getName(),
		// 0)];
		LogUtil.traceEnabled = pref.getBoolean("ACTIVE_TRACE", false);

		contentView = new FlipViewGroup(this);
		buildPages();
		setContentView(contentView);

		// contentView.startFlipping(); // make the first_page view flipping

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return contentView.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION) {
				Collection collection = (Collection) data
						.getSerializableExtra("COLLECTION");
				CollectionDao.getInstance(this).create(collection);
				launchImportCollection(collection);
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION) {
				Collection collection = (Collection) data
						.getSerializableExtra("COLLECTION");
				CollectionDao.getInstance(this).update(collection);
				currentCollection.setName(collection.getName());
				currentCollection.setTricTracId(collection.getTricTracId());

				int index = collections.indexOf(currentCollection);
				int page = index / nbPerPage;
				int c = index % nbPerPage;
				View v = contentView.getFlipView(page);
				TextView txt = (TextView) v.findViewById(getResources()
						.getIdentifier("name" + c, "id",
								ApplicationConstants.PACKAGE));
				txt.setText(currentCollection.getName() + "\n"
						+ currentCollection.getCount());
				if (editMode) {
					gotoEditMode();
				}
			}
		}
	}

	private void launchImportCollection(Collection collection) {
		ImportCollectionTask task = new ImportCollectionTask(
				new ImportCollectionListener() {

					@Override
					public void importEnded(boolean succeed,
							Collection collection, List<CollectionGame> links) {
						if (succeed) {
							CollectionDao.getInstance(Home.this).updateLinks(
									collection.getId(), links);
							buildPages();
							// int nbPage = (collections.size() + 1 - 1) /
							// nbPerPage +
							// 1;
							// int indexInView = collections.size() % nbPerPage;
							//
							// collections.add(collection);
							// View v = contentView.getFlipView(nbPage - 1);
							// ImageView img = (ImageView)
							// v.findViewById(getResources().getIdentifier("img"
							// +
							// indexInView, "id",
							// ApplicationConstants.PACKAGE));
							// img.setBackgroundResource(R.drawable.ludo);
							// img.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public Context getContext() {
						return Home.this;
					}
				});
		task.execute(collection);

	}

	private void buildPages() {
		contentView.clearFlipViews();
		collections = CollectionDao.getInstance(this).getCollections();
		if (collections.size() > 0) {
			Collection all = new Collection(null);
			all.setName(getResources().getString(R.string.search_all_games));
			int count = GameDao.getInstance(this)
					.getGameCount(null, null, null);
			all.setCount(count);
			collections.add(0, all);
		}

		int nbPage = (collections.size() + 1 - 1) / nbPerPage + 1;

		for (int i = 0; i < nbPage; i++) {
			View v = View.inflate(this, R.layout.v2_collection_page, null);
			TextView title = (TextView) v.findViewById(R.id.page);
			title.setText("Page " + (i + 1) + " / " + nbPage);
			contentView.addFlipView(v);
			for (int c = 0; c < nbPerPage; c++) {
				int index = nbPerPage * i + c;
				ImageView img = (ImageView) v.findViewById(getResources()
						.getIdentifier("img" + c, "id",
								ApplicationConstants.PACKAGE));
				if (index < collections.size()) {
					final Collection collection = collections.get(index);
					img.setImageResource(R.drawable.ludo);
					TextView txt = (TextView) v.findViewById(getResources()
							.getIdentifier("name" + c, "id",
									ApplicationConstants.PACKAGE));
					txt.setText(collection.getName() + "\n"
							+ collection.getCount());
					img.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							gotoEditMode();
							return true;
						}
					});
					img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (!editMode) {
								Intent intent = new Intent(Home.this,
										GameList.class);
								if (collection.getId() != null) {
									intent.putExtra("COLLECTION", collection);
								}
								startActivity(intent);
							}
						}
					});

					if (collection.getId() != null) {
						ImageView imgDel = (ImageView) v
								.findViewById(getResources().getIdentifier(
										"imgDelete" + c, "id",
										ApplicationConstants.PACKAGE));
						imgDel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								CollectionDao.getInstance(Home.this).delete(
										collection);
								buildPages();
							}
						});
						ImageView imgEdit = (ImageView) v
								.findViewById(getResources().getIdentifier(
										"imgEdit" + c, "id",
										ApplicationConstants.PACKAGE));
						imgEdit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								currentCollection = collection;
								Intent i = new Intent(Home.this,
										EditCollection.class);
								i.putExtra("COLLECTION", currentCollection);
								startActivityForResult(
										i,
										ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION);
							}
						});
					}
				} else if (index == collections.size()) {
					img.setImageResource(R.drawable.ludo_add);
					img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent i = new Intent(Home.this,
									EditCollection.class);
							// i.putExtra("COLLECTION", collection);
							startActivityForResult(
									i,
									ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION);
						}
					});
				} else {
					img.setImageResource(R.drawable.ludo_invis);
					// img.setVisibility(View.INVISIBLE);
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
		Animation anim = AnimationUtils.loadAnimation(Home.this, R.anim.shake);
		View v = contentView.getFlipView(contentView.getCurrentPage());
		for (int c = 0; c < nbPerPage; c++) {
			int index = contentView.getCurrentPage() * nbPerPage + c;
			if (index < collections.size()) {
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
					Collection collection = collections.get(index);
					if (collection.getId() != null) {
						img.startAnimation(anim);
						imgDel.setVisibility(View.VISIBLE);
						imgEdit.setVisibility(View.VISIBLE);
					}
				} else {
					img.setAnimation(null);
					imgDel.setVisibility(View.INVISIBLE);
					imgEdit.setVisibility(View.INVISIBLE);
				}
			} else if (index == collections.size()) {
				ImageView img = (ImageView) v.findViewById(getResources()
						.getIdentifier("img" + c, "id",
								ApplicationConstants.PACKAGE));
				if (editMode) {
					img.setVisibility(View.INVISIBLE);
				} else {
					img.setVisibility(View.VISIBLE);
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