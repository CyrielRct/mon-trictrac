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

import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * @author amphiprion
 * 
 */
public class PartyDao extends AbstractDao {
	/** The singleton. */
	private static PartyDao instance;

	public PartyDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static PartyDao getInstance(Context context) {
		if (instance == null) {
			instance = new PartyDao(context);
		}
		return instance;
	}

	private void create(Party party) {
		getDatabase().beginTransaction();
		try {

			String sql = "insert into PARTY (" + Party.DbField.ID + "," + Party.DbField.PLAY_DATE + ","
					+ Party.DbField.CITY + "," + Party.DbField.EVENT + "," + Party.DbField.HAPPYNESS + ","
					+ Party.DbField.DURATION + "," + Party.DbField.COMMENT + "," + Party.DbField.FK_GAME
					+ ") values (?,?,?,?,?,?,?,?)";
			Object[] params = new Object[8];
			params[0] = party.getId();
			params[1] = dateToString(party.getDate());
			params[2] = party.getCity();
			params[3] = party.getEvent();
			params[4] = party.getHappyness();
			params[5] = party.getDuration();
			params[6] = party.getComment();
			params[7] = party.getGameId();

			execSQL(sql, params);

			if (party.getStats() != null) {
				for (PlayStat playStat : party.getStats()) {
					PlayStatDao.getInstance(getContext()).persist(playStat);
				}
			}

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	private void update(Party party) {
		getDatabase().beginTransaction();
		try {
			String sql = "update PARTY set " + Party.DbField.PLAY_DATE + "=?," + Party.DbField.CITY + "=?,"
					+ Party.DbField.EVENT + "=?," + Party.DbField.HAPPYNESS + "=?," + Party.DbField.DURATION + "=?,"
					+ Party.DbField.COMMENT + "=?," + Party.DbField.FK_GAME + "=? where " + Party.DbField.ID + "=?";
			Object[] params = new Object[8];
			params[0] = dateToString(party.getDate());
			params[1] = party.getCity();
			params[2] = party.getEvent();
			params[3] = party.getHappyness();
			params[4] = party.getDuration();
			params[5] = party.getComment();
			params[6] = party.getGameId();
			params[7] = party.getId();

			execSQL(sql, params);

			sql = "delete FROM PLAY_STAT where " + PlayStat.DbField.FK_PARTY + "=?";
			execSQL(sql, new String[] { party.getId() });

			if (party.getStats() != null) {
				for (PlayStat playStat : party.getStats()) {
					playStat.setState(DbState.NEW);
					PlayStatDao.getInstance(getContext()).persist(playStat);
				}
			}

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void persist(Party party) {
		if (party.getState() == DbState.NEW) {
			create(party);
		} else if (party.getState() == DbState.LOADED) {
			update(party);
		}
	}

	public void delete(Party party) {
		getDatabase().beginTransaction();
		try {

			String sql = "DELETE FROM PARTY where " + Party.DbField.ID + "=?";
			execSQL(sql, new String[] { party.getId() });

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public List<Party> getParties(Game game) {
		String sql = "SELECT " + Party.DbField.ID + "," + Party.DbField.PLAY_DATE + "," + Party.DbField.CITY + ","
				+ Party.DbField.EVENT + "," + Party.DbField.HAPPYNESS + "," + Party.DbField.DURATION + ","
				+ Party.DbField.COMMENT + " from PARTY where " + Party.DbField.FK_GAME + "=? order by "
				+ Party.DbField.PLAY_DATE + " desc";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { game.getId() });
		ArrayList<Party> result = new ArrayList<Party>();
		if (cursor.moveToFirst()) {
			do {
				Party entity = new Party(cursor.getString(0));
				entity.setDate(stringToDate(cursor.getString(1)));
				entity.setCity(cursor.getString(2));
				entity.setEvent(cursor.getString(3));
				entity.setHappyness(cursor.getInt(4));
				entity.setDuration(cursor.getInt(5));
				entity.setComment(cursor.getString(6));
				entity.setGameId(game.getId());
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;

	}
}
