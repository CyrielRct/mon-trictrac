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
package org.amphiprion.trictrac.task;

import java.util.List;

import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.handler.CollectionHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class ImportCollectionTask extends AsyncTask<Collection, Integer, List<CollectionGame>> {
	private ProgressDialog progress;
	private ImportCollectionListener caller;
	private Collection collection;

	/**
	 * Default constructor.
	 */
	public ImportCollectionTask(ImportCollectionListener caller) {
		this.caller = caller;
	}

	@Override
	protected List<CollectionGame> doInBackground(Collection... collections) {
		CollectionHandler handler = new CollectionHandler(caller.getContext(), this);
		collection = collections[0];
		handler.parse(collection);
		return handler.getCollectionGames();
	}

	public void publishProgress(int gameNumber) {
		publishProgress(R.string.import_game, gameNumber);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(caller.getContext(), "", caller.getContext().getString(R.string.import_game, 0),
				true);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progress.setMessage(caller.getContext().getResources().getString(values[0], values[1]));
	}

	@Override
	protected void onPostExecute(List<CollectionGame> links) {
		progress.cancel();

		if (!isCancelled()) {
			caller.importEnded(links != null, collection, links);
		}
	}

	public interface ImportCollectionListener {
		void importEnded(boolean succeed, Collection collection, List<CollectionGame> links);

		Context getContext();
	}
}
