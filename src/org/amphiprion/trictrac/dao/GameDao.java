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

import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database game access.
 * 
 * @author amphiprion
 * 
 */
public class GameDao extends AbstractDao {
	/** The singleton. */
	private static GameDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private GameDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static GameDao getInstance(Context context) {
		if (instance == null) {
			instance = new GameDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing games of a given collection.
	 * 
	 * @param collection
	 *            the collection
	 * @return the game list
	 */
	public int getGameCount(Collection collection, Search search, String filter) {
		String sql = "SELECT count(*) from COLLECTION_GAME join GAME on " + CollectionGame.DbField.FK_GAME + "="
				+ Game.DbField.ID + " where " + CollectionGame.DbField.FK_COLLECTION + "=?";

		sql += buildWhere(search, filter);
		Cursor cursor = getDatabase().rawQuery(sql, new String[] { collection.getId() });
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	/**
	 * Return true if the given game exists in android database.
	 * 
	 * @param id
	 *            the game id
	 * @return true if exists
	 */
	public boolean exists(String id) {
		String sql = "SELECT 1 from GAME where " + Game.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		if (cursor.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return all existing games of a given collection.
	 * 
	 * @param collection
	 *            the collection
	 * @return the game list
	 */
	public List<Game> getGames(Collection collection, int pageIndex, int pageSize, Search search, String filter) {
		String sql = "SELECT " + Game.DbField.ID + "," + Game.DbField.NAME + "," + Game.DbField.IMAGE_NAME + ","
				+ Game.DbField.TYPE + "," + Game.DbField.FAMILIES + "," + Game.DbField.MECHANISMS + ","
				+ Game.DbField.THEMES + "," + Game.DbField.MIN_PLAYER + "," + Game.DbField.MAX_PLAYER + ","
				+ Game.DbField.MIN_AGE + "," + Game.DbField.MAX_AGE + "," + Game.DbField.DURATION + ","
				+ Game.DbField.DIFFICULTY + "," + Game.DbField.LUCK + "," + Game.DbField.STRATEGY + ","
				+ Game.DbField.DIPLOMATY + ",(select count(*) from PARTY p where p." + Party.DbField.FK_GAME + "=g."
				+ Game.DbField.ID + "),(select sum(" + Party.DbField.HAPPYNESS + ") from PARTY p where p."
				+ Party.DbField.FK_GAME + "=g." + Game.DbField.ID + ") from COLLECTION_GAME join GAME g on "
				+ CollectionGame.DbField.FK_GAME + "=" + Game.DbField.ID + " where "
				+ CollectionGame.DbField.FK_COLLECTION + "=?";

		sql += buildWhere(search, filter);

		sql += " order by " + Game.DbField.NAME + " asc limit " + (pageSize + 1) + " offset " + (pageIndex * pageSize);

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { collection.getId() });
		ArrayList<Game> result = new ArrayList<Game>();
		if (cursor.moveToFirst()) {
			do {
				Game a = new Game(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setImageName(cursor.getString(2));
				a.setType(cursor.getString(3));
				a.setFamilies(cursor.getString(4));
				a.setMechanisms(cursor.getString(5));
				a.setThemes(cursor.getString(6));
				a.setMinPlayer(cursor.getInt(7));
				a.setMaxPlayer(cursor.getInt(8));
				a.setMinAge(cursor.getInt(9));
				a.setMaxAge(cursor.getInt(10));
				a.setDuration(cursor.getInt(11));
				a.setDifficulty(cursor.getInt(12));
				a.setLuck(cursor.getInt(13));
				a.setStrategy(cursor.getInt(14));
				a.setDiplomaty(cursor.getInt(15));
				a.setNbParty(cursor.getInt(16));
				a.setHappyness(cursor.getInt(17));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Return the given game or null if not exists.
	 * 
	 * @param id
	 *            the id
	 * @return the game
	 */
	public Game getGame(String id) {
		String sql = "SELECT " + Game.DbField.ID + "," + Game.DbField.NAME + "," + Game.DbField.IMAGE_NAME + ","
				+ Game.DbField.TYPE + "," + Game.DbField.FAMILIES + "," + Game.DbField.MECHANISMS + ","
				+ Game.DbField.THEMES + "," + Game.DbField.MIN_PLAYER + "," + Game.DbField.MAX_PLAYER + ","
				+ Game.DbField.MIN_AGE + "," + Game.DbField.MAX_AGE + "," + Game.DbField.DURATION + ","
				+ Game.DbField.DIFFICULTY + "," + Game.DbField.LUCK + "," + Game.DbField.STRATEGY + ","
				+ Game.DbField.DIPLOMATY + ",(select count(*) from PARTY p where p." + Party.DbField.FK_GAME + "=g."
				+ Game.DbField.ID + "),(select sum(" + Party.DbField.HAPPYNESS + ") from PARTY p where p."
				+ Party.DbField.FK_GAME + "=g." + Game.DbField.ID + ") from GAME g where " + Game.DbField.ID + "=?";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		Game result = null;
		if (cursor.moveToFirst()) {
			Game a = new Game(cursor.getString(0));
			a.setName(cursor.getString(1));
			a.setImageName(cursor.getString(2));
			a.setType(cursor.getString(3));
			a.setFamilies(cursor.getString(4));
			a.setMechanisms(cursor.getString(5));
			a.setThemes(cursor.getString(6));
			a.setMinPlayer(cursor.getInt(7));
			a.setMaxPlayer(cursor.getInt(8));
			a.setMinAge(cursor.getInt(9));
			a.setMaxAge(cursor.getInt(10));
			a.setDuration(cursor.getInt(11));
			a.setDifficulty(cursor.getInt(12));
			a.setLuck(cursor.getInt(13));
			a.setStrategy(cursor.getInt(14));
			a.setDiplomaty(cursor.getInt(15));
			a.setNbParty(cursor.getInt(16));
			a.setHappyness(cursor.getInt(17));
			result = a;
		}
		cursor.close();
		return result;
	}

	private String buildWhere(Search search, String filter) {
		String sql = "";
		if (filter != null) {
			sql += " and " + Game.DbField.NAME + " like '%" + encodeString(filter) + "%'";
		}
		if (search != null) {
			if (search.isExactly()) {
				if (search.getMinPlayer() != 0) {
					sql += " and " + Game.DbField.MIN_PLAYER + "=" + search.getMinPlayer();
				}
				if (search.getMaxPlayer() != 0) {
					sql += " and " + Game.DbField.MAX_PLAYER + "=" + search.getMaxPlayer();
				}
			} else {
				if (search.getMinPlayer() != 0) {
					sql += " and " + Game.DbField.MIN_PLAYER + "<=" + search.getMinPlayer();
					sql += " and " + Game.DbField.MAX_PLAYER + ">=" + search.getMinPlayer();
				}
				if (search.getMaxPlayer() != 0) {
					sql += " and " + Game.DbField.MIN_PLAYER + "<=" + search.getMaxPlayer();
					sql += " and " + Game.DbField.MAX_PLAYER + ">=" + search.getMaxPlayer();
				}
			}

			if (search.getMinDifficulty() != 0) {
				sql += " and (" + Game.DbField.DIFFICULTY + "=-1 or " + Game.DbField.DIFFICULTY + ">="
						+ search.getMinDifficulty() + ")";
			}
			if (search.getMaxDifficulty() != 0) {
				sql += " and (" + Game.DbField.DIFFICULTY + "=-1 or " + Game.DbField.DIFFICULTY + "<="
						+ search.getMaxDifficulty() + ")";
			}
			if (search.getMinLuck() != 0) {
				sql += " and (" + Game.DbField.LUCK + "=-1 or " + Game.DbField.LUCK + ">=" + search.getMinLuck() + ")";
			}
			if (search.getMaxLuck() != 0) {
				sql += " and (" + Game.DbField.LUCK + "=-1 or " + Game.DbField.LUCK + "<=" + search.getMaxLuck() + ")";
			}
			if (search.getMinStrategy() != 0) {
				sql += " and (" + Game.DbField.STRATEGY + "=-1 or " + Game.DbField.STRATEGY + ">="
						+ search.getMinStrategy() + ")";
			}
			if (search.getMaxStrategy() != 0) {
				sql += " and (" + Game.DbField.STRATEGY + "=-1 or " + Game.DbField.STRATEGY + "<="
						+ search.getMaxStrategy() + ")";
			}
			if (search.getMinDiplomacy() != 0) {
				sql += " and (" + Game.DbField.DIPLOMATY + "=-1 or " + Game.DbField.DIPLOMATY + ">="
						+ search.getMinDiplomacy() + ")";
			}
			if (search.getMaxDiplomacy() != 0) {
				sql += " and (" + Game.DbField.DIPLOMATY + "=-1 or " + Game.DbField.DIPLOMATY + "<="
						+ search.getMaxDiplomacy() + ")";
			}
			if (search.getMinDuration() != 0) {
				sql += " and (" + Game.DbField.DURATION + "=0 or " + Game.DbField.DURATION + ">="
						+ search.getMinDuration() + ")";
			}
			if (search.getMaxDuration() != 0) {
				sql += " and (" + Game.DbField.DURATION + "=0 or " + Game.DbField.DURATION + "<="
						+ search.getMaxDuration() + ")";
			}
		}
		return sql;
	}

	/**
	 * Persist a new game.
	 * 
	 * @param game
	 *            the new game
	 */
	public void createGame(Game game) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into GAME (" + Game.DbField.ID + "," + Game.DbField.NAME + ","
					+ Game.DbField.IMAGE_NAME + "," + Game.DbField.TYPE + "," + Game.DbField.FAMILIES + ","
					+ Game.DbField.MECHANISMS + "," + Game.DbField.THEMES + "," + Game.DbField.MIN_PLAYER + ","
					+ Game.DbField.MAX_PLAYER + "," + Game.DbField.MIN_AGE + "," + Game.DbField.MAX_AGE + ","
					+ Game.DbField.DURATION + "," + Game.DbField.DIFFICULTY + "," + Game.DbField.LUCK + ","
					+ Game.DbField.STRATEGY + "," + Game.DbField.DIPLOMATY
					+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] params = new Object[16];
			params[0] = game.getId();
			params[1] = game.getName();
			params[2] = game.getImageName();
			params[3] = game.getType();
			params[4] = game.getFamilies();
			params[5] = game.getMechanisms();
			params[6] = game.getThemes();
			params[7] = game.getMinPlayer();
			params[8] = game.getMaxPlayer();
			params[9] = game.getMinAge();
			params[10] = game.getMaxAge();
			params[11] = game.getDuration();
			params[12] = game.getDifficulty();
			params[13] = game.getLuck();
			params[14] = game.getStrategy();
			params[15] = game.getDiplomaty();

			execSQL(sql, params);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	public void update(Game game) {
		String sql = "update GAME set " + Game.DbField.NAME + "=?," + Game.DbField.IMAGE_NAME + "=?,"
				+ Game.DbField.TYPE + "=?," + Game.DbField.FAMILIES + "=?," + Game.DbField.MECHANISMS + "=?,"
				+ Game.DbField.THEMES + "=?," + Game.DbField.MIN_PLAYER + "=?," + Game.DbField.MAX_PLAYER + "=?,"
				+ Game.DbField.MIN_AGE + "=?," + Game.DbField.MAX_AGE + "=?," + Game.DbField.DURATION + "=?,"
				+ Game.DbField.DIFFICULTY + "=?," + Game.DbField.LUCK + "=?," + Game.DbField.STRATEGY + "=?,"
				+ Game.DbField.DIPLOMATY + "=? WHERE " + Game.DbField.ID + "=?";
		Object[] params = new Object[16];
		params[0] = game.getName();
		params[1] = game.getImageName();
		params[2] = game.getType();
		params[3] = game.getFamilies();
		params[4] = game.getMechanisms();
		params[5] = game.getThemes();
		params[6] = game.getMinPlayer();
		params[7] = game.getMaxPlayer();
		params[8] = game.getMinAge();
		params[9] = game.getMaxAge();
		params[10] = game.getDuration();
		params[11] = game.getDifficulty();
		params[12] = game.getLuck();
		params[13] = game.getStrategy();
		params[14] = game.getDiplomaty();
		params[15] = game.getId();

		execSQL(sql, params);

	}

	public void persist(Game game) {
		if (game.getState() == DbState.NEW) {
			createGame(game);
		} else if (game.getState() == DbState.LOADED) {
			update(game);
		}
	}

	/**
	 * Return true if the game id already exists in the database.
	 * 
	 * @param id
	 *            the game id
	 * @return true if already exists.
	 */
	public boolean isExists(String id) {
		String sql = "SELECT 1 from GAME where " + Game.DbField.ID + "=?";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] { id });
		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}
}
