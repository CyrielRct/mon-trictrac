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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.util.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class AbstractDao {
	private static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DatabaseHelper helper;
	private static SQLiteDatabase db;
	protected Context context;

	protected AbstractDao(Context context) {
		this.context = context;
	}

	protected SQLiteDatabase getDatabase() {
		if (helper == null) {
			helper = new DatabaseHelper(context);
			db = helper.getWritableDatabase();
		}
		return db;
	}

	protected Context getContext() {
		return context;
	}

	protected void execSQL(String sql) {
		execSQL(sql, null);
	}

	protected void execSQL(String sql, Object[] args) {
		boolean joinTransaction = getDatabase().inTransaction();
		if (!joinTransaction) {
			getDatabase().beginTransaction();
		}
		try {
			if (args == null) {
				getDatabase().execSQL(sql);
			} else {
				getDatabase().execSQL(sql, args);
			}
			if (!joinTransaction) {
				getDatabase().setTransactionSuccessful();
			}
		} finally {
			if (!joinTransaction) {
				getDatabase().endTransaction();
			}
		}
	}

	public String encodeString(String value) {
		if (value == null) {
			return "";
		} else {
			return value.replace("'", "''");
		}

	}

	/**
	 * Return the Date for the given iso8601 date string
	 * 
	 * @param dbDate
	 *            the string representation
	 * @return the date
	 */
	public Date stringToDate(String dbDate) {
		if (dbDate == null || "".equals(dbDate)) {
			return null;
		}
		try {
			return iso8601Format.parse(dbDate);
		} catch (ParseException e) {
			Log.e(ApplicationConstants.PACKAGE, "Can not parse date", e);
			return null;
		}
	}

	/**
	 * Return the iso8601 string representation of the given date.
	 * 
	 * @param date
	 *            the date
	 * @return the string representation
	 */
	public String dateToString(Date date) {
		if (date == null) {
			return null;
		}
		return iso8601Format.format(date);
	}
}
