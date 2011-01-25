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

import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.handler.PartyHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author amphiprion
 * 
 */
public class SynchronizePartiesTask extends AsyncTask<Date, Integer, Void> implements IProgressTask {
	private ProgressDialog progress;
	private Context context;
	private ITaskListener listener;
	private int messageId;
	private String title;

	/**
	 * Default constructor.
	 */
	public SynchronizePartiesTask(ITaskListener listener) {
		this.listener = listener;
		context = listener.getContext();
	}

	@Override
	protected Void doInBackground(Date... dates) {
		try {
			messageId = R.string.download_players;
			title = "" + context.getText(R.string.synch_players);
			publishProgress(0);
			PartyHandler handler = new PartyHandler(context, dates[0]);
			// syncronize playsers
			handler.synchronizePlayers(this);

			title = "" + context.getText(R.string.synch_parties);
			// download parties
			messageId = R.string.download_parties;
			handler.synchronizeParties(this);

			// upload new parties
			messageId = R.string.upload_parties;
			int nb = 0;
			List<Party> parties = PartyDao.getInstance(context).getLocalParties();
			for (Party party : parties) {
				publishProgress(++nb);
				handler.uploadParty(party, false);
			}
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "synch player failed", e);
			cancel(true);
		}
		return null;
	}

	public void publishProgress(int nb) {
		publishProgress(messageId, nb);
	}

	public void publishProgress(int messageId, int nb) {
		super.publishProgress(messageId, nb);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(context, "...", context.getString(R.string.download_parties, 0), true, true,
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progress.setTitle(title);
		progress.setMessage(context.getResources().getString(values[0], values[1]));
	}

	@Override
	protected void onPostExecute(Void result) {
		progress.cancel();
		listener.taskEnded(!isCancelled());
	}
}
