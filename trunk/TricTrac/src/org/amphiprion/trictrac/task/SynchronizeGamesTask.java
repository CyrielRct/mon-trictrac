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
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.handler.GameHandler;
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
public class SynchronizeGamesTask extends AsyncTask<Void, Integer, Void> {
	private ProgressDialog progress;
	private Context context;
	private ITaskListener listener;
	private String title;
	private PrintWriter pw;

	/**
	 * Default constructor.
	 */
	public SynchronizeGamesTask(ITaskListener listener) {
		this.listener = listener;
		context = listener.getContext();
		try {
			if (LogUtil.traceEnabled) {
				File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY
						+ "/logs/syncGames_" + System.currentTimeMillis() + ".txt");
				pw = new PrintWriter(f);
			}
		} catch (Exception e) {
		}

	}

	@Override
	protected Void doInBackground(Void... voids) {

		try {
			publishProgress(R.string.download_games, 0, 0);
			title = "" + context.getText(R.string.synch_games);
			GameHandler handler = new GameHandler();
			List<Game> games = GameDao.getInstance(context).getGames();
			int nb = 0;
			int total = games.size();
			for (Game game : games) {
				nb++;
				publishProgress(R.string.download_games, nb, total);
				handler.parse(game);
				GameDao.getInstance(context).update(game);
			}

		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "synch games failed", e);
			cancel(true);
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(context, "...", context.getString(R.string.download_players, 0), true, true,
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
		progress.setMessage(context.getResources().getString(values[0], values[1], values[2]));
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
