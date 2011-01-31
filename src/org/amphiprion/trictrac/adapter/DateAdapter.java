/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.trictrac.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This is the adapter for the File driver date format chooser.
 * 
 * @author amphiprion
 * 
 */
public class DateAdapter extends ArrayAdapter<Date> {
	private static SimpleDateFormat defaultFormat = new SimpleDateFormat("dd MMM yyyy");

	/**
	 * Default constructor.
	 */
	public DateAdapter(Context context) {
		super(context, android.R.layout.simple_spinner_item);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		((TextView) view).setText(defaultFormat.format(getItem(position)));
		return view;
	}
}
