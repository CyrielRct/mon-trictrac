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
package org.amphiprion.trictrac.dao;

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * @author amphiprion
 * 
 */
public class PlayStatDao extends AbstractDao {
	/** The singleton. */
	private static PlayStatDao instance;

	public PlayStatDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static PlayStatDao getInstance(Context context) {
		if (instance == null) {
			instance = new PlayStatDao(context);
		}
		return instance;
	}

	private void create(PlayStat playStat) {
		String sql = "insert into PLAY_STAT (" + PlayStat.DbField.ID + "," + PlayStat.DbField.FK_PLAYER + ","
				+ PlayStat.DbField.RANK + "," + PlayStat.DbField.SCORE + "," + PlayStat.DbField.FK_PARTY
				+ ") values (?,?,?,?,?)";
		Object[] params = new Object[5];
		params[0] = playStat.getId();
		params[1] = playStat.getPlayerId();
		params[2] = playStat.getRank();
		params[3] = playStat.getScore();
		params[4] = playStat.getPartyId();

		execSQL(sql, params);
	}

	private void update(PlayStat playStat) {
		String sql = "update PLAY_STAT set " + PlayStat.DbField.FK_PLAYER + "=?," + PlayStat.DbField.RANK + "=?,"
				+ PlayStat.DbField.SCORE + "=? where " + PlayStat.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = playStat.getPlayerId();
		params[1] = playStat.getRank();
		params[2] = playStat.getScore();
		params[3] = playStat.getId();

		execSQL(sql, params);
	}

	public void persist(PlayStat playStat) {
		if (playStat.getState() == DbState.NEW) {
			create(playStat);
		} else if (playStat.getState() == DbState.LOADED) {
			update(playStat);

		}
	}

	public void delete(PlayStat playStat) {
		getDatabase().beginTransaction();
		try {

			String sql = "DELETE FROM PLAYER where " + PlayStat.DbField.ID + "=?";
			execSQL(sql, new String[] { playStat.getId() });

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public List<PlayStat> getPlayStat(Party party) {
		String sql = "SELECT " + PlayStat.DbField.ID + "," + PlayStat.DbField.FK_PLAYER + "," + PlayStat.DbField.RANK
				+ "," + PlayStat.DbField.SCORE + "," + PlayStat.DbField.FK_PARTY + " from PLAY_STAT order by "
				+ PlayStat.DbField.RANK;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<PlayStat> result = new ArrayList<PlayStat>();
		if (cursor.moveToFirst()) {
			do {
				PlayStat entity = new PlayStat(cursor.getString(0));
				entity.setPlayerId(cursor.getString(1));
				entity.setRank(cursor.getInt(2));
				entity.setScore(cursor.getInt(3));
				entity.setPartyId(cursor.getString(4));
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;

	}
}
