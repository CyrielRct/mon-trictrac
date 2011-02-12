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

import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.PartyForList;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class LoadPartiesTask extends AsyncTask<Void, Integer, List<PartyForList>> {
	private LoadPartyListener caller;
	private int pageIndex;
	private int pageSize;
	private Game game;

	/**
	 * Default constructor.
	 */
	public LoadPartiesTask(LoadPartyListener caller, Game game, int pageIndex, int pageSize) {
		this.caller = caller;
		this.game = game;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}

	@Override
	protected List<PartyForList> doInBackground(Void... v) {
		try {
			List<PartyForList> parties = PartyDao.getInstance(caller.getContext()).getParties(game, pageIndex, pageSize, null);
			return parties;

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
	protected void onPostExecute(List<PartyForList> parties) {
		caller.importEnded(!isCancelled() && parties != null, parties);
	}

	public interface LoadPartyListener {
		void importEnded(boolean succeed, List<PartyForList> parties);

		Context getContext();
	}
}
