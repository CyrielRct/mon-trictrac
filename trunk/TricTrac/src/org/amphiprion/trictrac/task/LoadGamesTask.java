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

import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Search;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class LoadGamesTask extends AsyncTask<Void, Integer, List<Game>> {
	private LoadGameListener caller;
	private Collection collection;
	private int pageIndex;
	private int pageSize;
	private Search search;
	private String filter;

	/**
	 * Default constructor.
	 */
	public LoadGamesTask(LoadGameListener caller, Collection collection, int pageIndex, int pageSize, Search search,
			String filter) {
		this.caller = caller;
		this.collection = collection;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.search = search;
		this.filter = filter;

	}

	@Override
	protected List<Game> doInBackground(Void... v) {
		try {
			return GameDao.getInstance(caller.getContext()).getGames(collection, pageIndex, pageSize, search, filter);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(List<Game> games) {
		caller.importEnded(!isCancelled() && games != null, games);
	}

	public interface LoadGameListener {
		void importEnded(boolean succeed, List<Game> games);

		Context getContext();
	}
}
