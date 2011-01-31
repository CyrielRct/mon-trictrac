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
package org.amphiprion.trictrac.util;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.entity.Search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "amphiprion_trictrac";
	private static final int DATABASE_VERSION = 10;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("create table GAME (" + Game.DbField.ID + " text primary key, " + Game.DbField.NAME
					+ " text not null, " + Game.DbField.IMAGE_NAME + " text" + "," + Game.DbField.TYPE + " text" + ","
					+ Game.DbField.THEMES + " text" + "," + Game.DbField.MECHANISMS + " text" + ","
					+ Game.DbField.FAMILIES + " text" + "," + Game.DbField.MIN_PLAYER + " integer" + ","
					+ Game.DbField.MAX_PLAYER + " integer" + "," + Game.DbField.MIN_AGE + " integer" + ","
					+ Game.DbField.MAX_AGE + " integer" + "," + Game.DbField.DURATION + " integer" + ","
					+ Game.DbField.DIFFICULTY + " integer" + "," + Game.DbField.LUCK + " integer" + ","
					+ Game.DbField.STRATEGY + " integer" + "," + Game.DbField.DIPLOMATY + " integer" + ") ");

			db.execSQL("create table COLLECTION (" + Collection.DbField.ID + " text primary key, "
					+ Collection.DbField.NAME + " text," + Collection.DbField.LAST_SYNCHRO + " date, "
					+ Collection.DbField.TRICTRAC_ID + " text, " + Collection.DbField.COUNT + " integer)");

			db.execSQL("create table COLLECTION_GAME (" + CollectionGame.DbField.FK_COLLECTION + " text not null, "
					+ CollectionGame.DbField.FK_GAME + " text not null)");

			onUpgrade(db, 1, DATABASE_VERSION);
		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) {
			db.execSQL("create table SEARCH (" + Search.DbField.ID + " text not null, " + Search.DbField.NAME
					+ " text not null," + Search.DbField.MIN_PLAYER + " integer," + Search.DbField.MAX_PLAYER
					+ " integer)");
			oldVersion++;
		}
		if (oldVersion == 2) {
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_DIFFICULTY + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_DIFFICULTY + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_LUCK + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_LUCK + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_STRATEGY + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_STRATEGY + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_DIPLOMACY + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_DIPLOMACY + " integer default 0");
			oldVersion++;
		}
		if (oldVersion == 3) {
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MIN_DURATION + " integer default 0");
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.MAX_DURATION + " integer default 0");
			oldVersion++;
		}

		if (oldVersion == 4) {
			db.execSQL("create table PLAYER (" + Player.DbField.ID + " text not null, " + Player.DbField.PSEUDO
					+ " text ," + Player.DbField.TRICTRAC_ID + " text)");
			db.execSQL("create table PARTY (" + Party.DbField.ID + " text not null, " + Party.DbField.CITY + " text ,"
					+ Party.DbField.COMMENT + " text," + Party.DbField.DURATION + " integer," + Party.DbField.EVENT
					+ " text," + Party.DbField.HAPPYNESS + " integer," + Party.DbField.PLAY_DATE + " date)");
			db.execSQL("create table PLAY_STAT (" + PlayStat.DbField.ID + " text not null, "
					+ PlayStat.DbField.FK_PLAYER + " text ," + PlayStat.DbField.RANK + " integer,"
					+ PlayStat.DbField.SCORE + " integer)");
			oldVersion++;
		}
		if (oldVersion == 5) {
			db.execSQL("ALTER TABLE PLAY_STAT ADD " + PlayStat.DbField.FK_PARTY + " text");
			oldVersion++;
		}
		if (oldVersion == 6) {
			db.execSQL("ALTER TABLE PARTY ADD " + Party.DbField.FK_GAME + " text");
			oldVersion++;
		}
		if (oldVersion == 7) {
			db.execSQL("ALTER TABLE SEARCH ADD " + Search.DbField.EXACTLY + " integer default 0");
			oldVersion++;
		}
		if (oldVersion == 8) {
			db.execSQL("ALTER TABLE PARTY ADD " + Party.DbField.TRICTRAC_ID + " text");
			db.execSQL("ALTER TABLE PLAYER ADD " + Player.DbField.TRICTRAC_PROFILE_ID + " text");
			db.beginTransaction();
			db.execSQL("update PLAYER set " + Player.DbField.TRICTRAC_PROFILE_ID + "=" + Player.DbField.TRICTRAC_ID);
			db.execSQL("update PLAYER set " + Player.DbField.TRICTRAC_ID + "=null");
			db.setTransactionSuccessful();
			db.endTransaction();
			oldVersion++;
		}
		if (oldVersion == 9) {
			db.execSQL("ALTER TABLE PLAYER ADD " + Player.DbField.SYNC_DATE + " date");
			db.execSQL("ALTER TABLE PLAYER ADD " + Player.DbField.UPDATE_DATE + " date");
			db.execSQL("ALTER TABLE PARTY ADD " + Player.DbField.SYNC_DATE + " date");
			db.execSQL("ALTER TABLE PARTY ADD " + Player.DbField.UPDATE_DATE + " date");
			oldVersion++;
		}
	}

}
