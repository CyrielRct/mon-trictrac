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

import org.amphiprion.trictrac.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
public class SynchronizePartiesTask extends AsyncTask<Date, Integer, Void> {
	private ProgressDialog progress;
	private Context context;

	/**
	 * Default constructor.
	 */
	public SynchronizePartiesTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Date... dates) {
		// CollectionHandler handler = new
		// CollectionHandler(caller.getContext(), this);
		// collection = collections[0];
		// progress.setTitle("" + collection.getName());
		// handler.parse(collection);
		return null;
	}

	public void publishProgress(int gameNumber) {
		publishProgress(R.string.import_game, gameNumber);
	}

	@Override
	protected void onPreExecute() {
		progress = ProgressDialog.show(context, "plop", context.getString(R.string.download_parties, 0), true, true,
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progress.setMessage(context.getResources().getString(values[0], values[1]));
	}

	@Override
	protected void onPostExecute(Void result) {
		progress.cancel();

		if (!isCancelled()) {
		}
	}
}
