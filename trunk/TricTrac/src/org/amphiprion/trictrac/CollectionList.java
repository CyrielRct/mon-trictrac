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

import java.util.List;

import org.amphiprion.trictrac.dao.CollectionDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.task.ImportCollectionTask.ImportCollectionListener;
import org.amphiprion.trictrac.view.CollectionSummaryView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author amphiprion
 * 
 */
public class CollectionList extends Activity implements ImportCollectionListener {
	private Collection current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_list);

		buildList();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_COLLECTION, 1, R.string.add_collection);
		addAccount.setIcon(android.R.drawable.ic_menu_add);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_COLLECTION) {
			Intent i = new Intent(this, EditCollection.class);
			// i.putExtra("COLLECTION", collection);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_COLLECTION) {
				Collection collection = (Collection) data.getSerializableExtra("COLLECTION");
				CollectionDao.getInstance(this).create(collection);
				ImportCollectionTask task = new ImportCollectionTask(this);
				task.execute(collection);
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION) {
				Collection collection = (Collection) data.getSerializableExtra("COLLECTION");
				CollectionDao.getInstance(this).update(collection);
				buildList();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof CollectionSummaryView) {
			current = ((CollectionSummaryView) v).getCollection();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_COLLECTION, 0, R.string.edit_collection);
			menu.add(1, ApplicationConstants.MENU_ID_SYNC_COLLECTION, 1, R.string.synch_collection);
			menu.add(2, ApplicationConstants.MENU_ID_DELETE_COLLECTION, 2, R.string.delete_collection);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_COLLECTION) {
			Intent i = new Intent(this, EditCollection.class);
			i.putExtra("COLLECTION", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_COLLECTION);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_COLLECTION) {
			CollectionDao.getInstance(this).delete(current);
			buildList();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_SYNC_COLLECTION) {
			ImportCollectionTask task = new ImportCollectionTask(this);
			task.execute(current);
		}
		return true;
	}

	private void buildList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.collection_list);
		ln.removeAllViews();

		List<Collection> collections = CollectionDao.getInstance(this).getCollections();
		if (collections.size() > 0) {
			for (Collection collection : collections) {
				CollectionSummaryView view = new CollectionSummaryView(this, collection);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v instanceof CollectionSummaryView) {
							Collection collection = ((CollectionSummaryView) v).getCollection();
							Intent i = new Intent(CollectionList.this, GameList.class);
							i.putExtra("COLLECTION", collection);
							startActivity(i);
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
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_collection_list);
			ln.addView(tv);
		}
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
		buildList();
	}
}
