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
package org.amphiprion.trictrac.view;

import java.util.Date;

import org.amphiprion.trictrac.adapter.DateAdapter;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.Spinner;

/**
 * @author amphiprion
 * 
 */
public class DatePickerSpinner extends Spinner {

	/**
	 * @param context
	 */
	public DatePickerSpinner(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param set
	 */
	public DatePickerSpinner(Context context, AttributeSet set) {
		super(context, set);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.widget.Spinner#performClick()
	 */
	@Override
	public boolean performClick() {
		OnDateSetListener l = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
				if (getAdapter() instanceof DateAdapter) {
					((DateAdapter) getAdapter()).clear();
					((DateAdapter) getAdapter()).add(date);
				}
			}

		};
		Date date = (Date) getAdapter().getItem(0);
		DatePickerDialog dlg = new DatePickerDialog(getContext(), l, date.getYear() + 1900, date.getMonth(), date
				.getDate());
		dlg.show();
		return true;
	}
}
