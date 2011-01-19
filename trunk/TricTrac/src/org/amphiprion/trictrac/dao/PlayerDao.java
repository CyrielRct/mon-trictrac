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

import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * @author amphiprion
 * 
 */
public class PlayerDao extends AbstractDao {
	/** The singleton. */
	private static PlayerDao instance;

	public PlayerDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static PlayerDao getInstance(Context context) {
		if (instance == null) {
			instance = new PlayerDao(context);
		}
		return instance;
	}

	private void create(Player player) {
		String sql = "insert into PLAYER (" + Player.DbField.ID + "," + Player.DbField.PSEUDO + ","
				+ Player.DbField.TRICTRAC_PROFILE_ID + "," + Player.DbField.TRICTRAC_ID + ") values (?,?,?,?)";
		Object[] params = new Object[4];
		params[0] = player.getId();
		params[1] = player.getPseudo();
		params[2] = player.getTricTracProfileId();
		params[3] = player.getTrictracId();

		execSQL(sql, params);
	}

	private void update(Player player) {
		String sql = "update PLAYER set " + Player.DbField.PSEUDO + "=?," + Player.DbField.TRICTRAC_PROFILE_ID + "=?,"
				+ Player.DbField.TRICTRAC_ID + "=? where " + Player.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = player.getPseudo();
		params[1] = player.getTricTracProfileId();
		params[2] = player.getTrictracId();
		params[3] = player.getId();

		execSQL(sql, params);
	}

	public void persist(Player player) {
		if (player.getState() == DbState.NEW) {
			create(player);
		} else if (player.getState() == DbState.LOADED) {
			update(player);

		}
	}

	public void delete(Player player) {
		getDatabase().beginTransaction();
		try {
			String sql = "UPDATE PLAY_STAT set " + PlayStat.DbField.FK_PLAYER + "=null where "
					+ PlayStat.DbField.FK_PLAYER + "=?";
			execSQL(sql, new String[] { player.getId() });

			sql = "DELETE FROM PLAYER where " + Player.DbField.ID + "=?";
			execSQL(sql, new String[] { player.getId() });

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public List<Player> getPlayers() {
		String sql = "SELECT " + Player.DbField.ID + "," + Player.DbField.PSEUDO + ","
				+ Player.DbField.TRICTRAC_PROFILE_ID + "," + Player.DbField.TRICTRAC_ID + " from PLAYER order by "
				+ Player.DbField.PSEUDO;

		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Player> result = new ArrayList<Player>();
		if (cursor.moveToFirst()) {
			do {
				Player entity = new Player(cursor.getString(0));
				entity.setPseudo(cursor.getString(1));
				entity.setTricTracProfileId(cursor.getString(2));
				entity.setTrictracId(cursor.getString(3));
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;

	}

	public List<Player> getLocalPlayers() {
		String sql = "SELECT " + Player.DbField.ID + "," + Player.DbField.PSEUDO + ","
				+ Player.DbField.TRICTRAC_PROFILE_ID + "," + Player.DbField.TRICTRAC_ID + " from PLAYER where "
				+ Player.DbField.TRICTRAC_ID + " is null";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Player> result = new ArrayList<Player>();
		if (cursor.moveToFirst()) {
			do {
				Player entity = new Player(cursor.getString(0));
				entity.setPseudo(cursor.getString(1));
				entity.setTricTracProfileId(cursor.getString(2));
				entity.setTrictracId(cursor.getString(3));
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	public Player getPlayer(String trictracId) {
		String sql = "SELECT " + Player.DbField.ID + "," + Player.DbField.PSEUDO + ","
				+ Player.DbField.TRICTRAC_PROFILE_ID + "," + Player.DbField.TRICTRAC_ID + " from PLAYER where "
				+ Player.DbField.TRICTRAC_ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { trictracId });
		Player entity = null;
		if (cursor.moveToFirst()) {
			entity = new Player(cursor.getString(0));
			entity.setPseudo(cursor.getString(1));
			entity.setTricTracProfileId(cursor.getString(2));
			entity.setTrictracId(cursor.getString(3));
		}
		cursor.close();
		return entity;
	}
}
