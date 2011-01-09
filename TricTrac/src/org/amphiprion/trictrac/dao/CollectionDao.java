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
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database collection access.
 * 
 * @author amphiprion
 * 
 */
public class CollectionDao extends AbstractDao {
	/** The singleton. */
	private static CollectionDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CollectionDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CollectionDao getInstance(Context context) {
		if (instance == null) {
			instance = new CollectionDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing collection.
	 * 
	 * @return the collection list
	 */
	public List<Collection> getCollections() {

		String sql = "SELECT " + Collection.DbField.ID + "," + Collection.DbField.NAME + ","
				+ Collection.DbField.LAST_SYNCHRO + "," + Collection.DbField.TRICTRAC_ID + ","
				+ "(select count(*) from COLLECTION_GAME WHERE " + CollectionGame.DbField.FK_COLLECTION + "="
				+ Collection.DbField.ID + ")" + " from COLLECTION order by " + Collection.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Collection> result = new ArrayList<Collection>();
		if (cursor.moveToFirst()) {
			do {
				Collection entity = new Collection(cursor.getString(0));
				entity.setName(cursor.getString(1));
				entity.setLastSynchro(stringToDate(cursor.getString(2)));
				entity.setTricTracId(Integer.parseInt(cursor.getString(3)));
				entity.setCount(Integer.parseInt(cursor.getString(4)));
				result.add(entity);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new collection.
	 * 
	 * @param collection
	 *            the new collection
	 */
	public void create(Collection collection) {
		String sql = "insert into COLLECTION (" + Collection.DbField.ID + "," + Collection.DbField.NAME + ","
				+ Collection.DbField.LAST_SYNCHRO + "," + Collection.DbField.TRICTRAC_ID + ") values (?,?,?,?)";
		Object[] params = new Object[4];
		params[0] = collection.getId();
		params[1] = collection.getName();
		params[2] = dateToString(collection.getLastSynchro());
		params[3] = collection.getTricTracId();

		execSQL(sql, params);
	}

	/**
	 * Update an existing collection.
	 * 
	 * @param collection
	 *            the collection to update
	 */
	public void update(Collection collection) {
		String sql = "update COLLECTION set " + Collection.DbField.NAME + "=?," + Collection.DbField.LAST_SYNCHRO
				+ "=?," + Collection.DbField.TRICTRAC_ID + "=? WHERE " + Collection.DbField.ID + "=?";
		Object[] params = new Object[4];
		params[0] = collection.getName();
		params[1] = dateToString(collection.getLastSynchro());
		params[2] = collection.getTricTracId();
		params[3] = collection.getId();

		execSQL(sql, params);
	}

	public void delete(Collection collection) {
		getDatabase().beginTransaction();
		try {
			String sql = "delete from COLLECTION_GAME where " + CollectionGame.DbField.FK_COLLECTION + "=?";
			execSQL(sql, new String[] { collection.getId() });

			sql = "delete from COLLECTION where " + Collection.DbField.ID + "=?";
			execSQL(sql, new String[] { collection.getId() });

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}

	}

	public void updateLinks(String collectionId, List<CollectionGame> links) {
		if (links == null) {
			return;
		}
		getDatabase().beginTransaction();
		try {
			String sql = "delete from COLLECTION_GAME where " + CollectionGame.DbField.FK_COLLECTION + "=?";
			execSQL(sql, new String[] { collectionId });
			execSQL(sql, null);
			if (links.size() > 0) {
				String[] ids = new String[2];
				for (CollectionGame link : links) {
					sql = "insert into COLLECTION_GAME (" + CollectionGame.DbField.FK_COLLECTION + ","
							+ CollectionGame.DbField.FK_GAME + ") values (?,?)";
					ids[0] = link.getCollectionId();
					ids[1] = link.getGameId();
					execSQL(sql, ids);

				}
			}
			sql = "update COLLECTION set " + Collection.DbField.LAST_SYNCHRO + "=? where " + Collection.DbField.ID
					+ "=?";
			execSQL(sql, new Object[] { dateToString(new Date()), collectionId });
			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}

	}
}
