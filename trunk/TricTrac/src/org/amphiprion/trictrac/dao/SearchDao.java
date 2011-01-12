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

import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.entity.Entity.DbState;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database search access.
 * 
 * @author amphiprion
 * 
 */
public class SearchDao extends AbstractDao {
	/** The singleton. */
	private static SearchDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private SearchDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static SearchDao getInstance(Context context) {
		if (instance == null) {
			instance = new SearchDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing search.
	 * 
	 * @return the search list
	 */
	public List<Search> getSearchs() {

		String sql = "SELECT " + Search.DbField.ID + "," + Search.DbField.NAME + "," + Search.DbField.MIN_PLAYER + ","
				+ Search.DbField.MAX_PLAYER + "," + Search.DbField.MIN_DIFFICULTY + "," + Search.DbField.MAX_DIFFICULTY
				+ "," + Search.DbField.MIN_LUCK + "," + Search.DbField.MAX_LUCK + "," + Search.DbField.MIN_STRATEGY
				+ "," + Search.DbField.MAX_STRATEGY + "," + Search.DbField.MIN_DIPLOMACY + ","
				+ Search.DbField.MAX_DIPLOMACY + "," + Search.DbField.MIN_DURATION + "," + Search.DbField.MAX_DURATION
				+ " from SEARCH order by " + Search.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Search> result = new ArrayList<Search>();
		if (cursor.moveToFirst()) {
			do {
				Search entity = new Search(cursor.getString(0));
				entity.setName(cursor.getString(1));
				entity.setMinPlayer(cursor.getInt(2));
				entity.setMaxPlayer(cursor.getInt(3));
				entity.setMinDifficulty(cursor.getInt(4));
				entity.setMaxDifficulty(cursor.getInt(5));
				entity.setMinLuck(cursor.getInt(6));
				entity.setMaxLuck(cursor.getInt(7));
				entity.setMinStrategy(cursor.getInt(8));
				entity.setMaxStrategy(cursor.getInt(9));
				entity.setMinDiplomacy(cursor.getInt(10));
				entity.setMaxDiplomacy(cursor.getInt(11));
				entity.setMinDuration(cursor.getInt(12));
				entity.setMaxDuration(cursor.getInt(13));
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	public void persist(Search search) {
		if (search.getState() == DbState.NEW) {
			create(search);
		} else if (search.getState() == DbState.LOADED) {
			update(search);
		}
	}

	/**
	 * Persist a new search.
	 * 
	 * @param search
	 *            the new search
	 */
	public void create(Search search) {
		String sql = "insert into SEARCH (" + Search.DbField.ID + "," + Search.DbField.NAME + ","
				+ Search.DbField.MIN_PLAYER + "," + Search.DbField.MAX_PLAYER + "," + Search.DbField.MIN_DIFFICULTY
				+ "," + Search.DbField.MAX_DIFFICULTY + "," + Search.DbField.MIN_LUCK + "," + Search.DbField.MAX_LUCK
				+ "," + Search.DbField.MIN_STRATEGY + "," + Search.DbField.MAX_STRATEGY + ","
				+ Search.DbField.MIN_DIPLOMACY + "," + Search.DbField.MAX_DIPLOMACY + "," + Search.DbField.MIN_DURATION
				+ "," + Search.DbField.MAX_DURATION + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[14];
		params[0] = search.getId();
		params[1] = search.getName();
		params[2] = search.getMinPlayer();
		params[3] = search.getMaxPlayer();
		params[4] = search.getMinDifficulty();
		params[5] = search.getMaxDifficulty();
		params[6] = search.getMinLuck();
		params[7] = search.getMaxLuck();
		params[8] = search.getMinStrategy();
		params[9] = search.getMaxStrategy();
		params[10] = search.getMinDiplomacy();
		params[11] = search.getMaxDiplomacy();
		params[12] = search.getMinDuration();
		params[13] = search.getMaxDuration();

		execSQL(sql, params);
	}

	/**
	 * Update an existing search.
	 * 
	 * @param search
	 *            the search to update
	 */
	public void update(Search search) {
		String sql = "update SEARCH set " + Search.DbField.NAME + "=?," + Search.DbField.MIN_PLAYER + "=?,"
				+ Search.DbField.MAX_PLAYER + "=?," + Search.DbField.MIN_DIFFICULTY + "=?,"
				+ Search.DbField.MAX_DIFFICULTY + "=?," + Search.DbField.MIN_LUCK + "=?," + Search.DbField.MAX_LUCK
				+ "=?," + Search.DbField.MIN_STRATEGY + "=?," + Search.DbField.MAX_STRATEGY + "=?,"
				+ Search.DbField.MIN_DIPLOMACY + "=?," + Search.DbField.MAX_DIPLOMACY + "=?,"
				+ Search.DbField.MIN_DURATION + "=?," + Search.DbField.MAX_DURATION + "=? WHERE " + Search.DbField.ID
				+ "=?";
		Object[] params = new Object[14];
		params[0] = search.getName();
		params[1] = search.getMinPlayer();
		params[2] = search.getMaxPlayer();
		params[3] = search.getMinDifficulty();
		params[4] = search.getMaxDifficulty();
		params[5] = search.getMinLuck();
		params[6] = search.getMaxLuck();
		params[7] = search.getMinStrategy();
		params[8] = search.getMaxStrategy();
		params[9] = search.getMinDiplomacy();
		params[10] = search.getMaxDiplomacy();
		params[11] = search.getMinDuration();
		params[12] = search.getMaxDuration();
		params[13] = search.getId();

		execSQL(sql, params);
	}

	public void delete(Search search) {
		String sql = "delete from SEARCH where " + Search.DbField.ID + "=?";
		execSQL(sql, new String[] { search.getId() });
	}

}
