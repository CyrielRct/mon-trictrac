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

import java.util.List;

import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.entity.Player;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This is the adapter for the player chooser.
 * 
 * @author amphiprion
 * 
 */
public class PlayerAdapter extends ArrayAdapter<Player> {
	TextView v;

	/**
	 * Default constructor.
	 */
	public PlayerAdapter(Context context, List<Player> players) {
		super(context, android.R.layout.select_dialog_item, players);
		v = new TextView(context);
		v.setTextColor(context.getResources().getColor(R.color.black));
		v.setTextSize(14.0f);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		v.setText("" + getItem(position));
		return v;
	}
}
