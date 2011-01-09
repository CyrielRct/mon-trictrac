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

import org.amphiprion.trictrac.entity.Collection;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is the adapter for the collection chooser.
 * 
 * @author amphiprion
 * 
 */
public class CollectionAdapter extends ArrayAdapter<Collection> {

	/**
	 * Default constructor.
	 */
	public CollectionAdapter(Context context, List<Collection> collections) {
		super(context, android.R.layout.select_dialog_item, collections);
	}

}
