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

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.handler.PartyHandler;
import org.amphiprion.trictrac.util.LogUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
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
	private PrintWriter pw;

	/**
	 * Default constructor.
	 */
	public SynchronizePartiesTask(ITaskListener listener) {
		this.listener = listener;

		context = listener.getContext();
		try {
			if (LogUtil.traceEnabled) {
				File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY
						+ "/logs/syncParties_" + System.currentTimeMillis() + ".txt");
				pw = new PrintWriter(f);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected Void doInBackground(Date... dates) {
		try {
			messageId = R.string.download_players;
			title = "" + context.getText(R.string.synch_players);
			publishProgress(0);
			LogUtil.trace(pw, "Creation partyHandler");
			PartyHandler handler = new PartyHandler(context, dates[0], pw);
			// syncronize playsers
			LogUtil.trace(pw, "Creation partyHandler created");
			LogUtil.trace(pw, "call handler.synchronizePlayers");
			handler.synchronizePlayers(this);
			LogUtil.trace(pw, "success handler.synchronizePlayers");

			title = "" + context.getText(R.string.synch_parties);
			// download parties
			messageId = R.string.download_parties;
			LogUtil.trace(pw, "call handler.synchronizeParties");
			handler.synchronizeParties(this);
			LogUtil.trace(pw, "success handler.synchronizeParties");

			// upload new parties
			messageId = R.string.upload_parties;
			int nb = 0;
			LogUtil.trace(pw, "retrieve local parties");
			List<Party> parties = PartyDao.getInstance(context).getLocalParties();
			LogUtil.trace(pw, "local parties nb=" + parties.size());
			for (Party party : parties) {
				publishProgress(++nb);
				LogUtil.trace(pw, "call handler.uploadParty gameid=" + party.getGameId());
				handler.uploadParty(party, false);
				LogUtil.trace(pw, "success handler.uploadParty gameid=" + party.getGameId());
			}
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "synch player failed", e);
			LogUtil.trace(pw, e);
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
		if (pw != null) {
			pw.close();
		}
		progress.cancel();
		listener.taskEnded(!isCancelled());
	}
}
