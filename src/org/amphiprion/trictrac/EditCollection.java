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
package org.amphiprion.trictrac;

import org.amphiprion.trictrac.entity.Collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * The activity used to edit/create a collection.
 * 
 * @author amphiprion
 * 
 */
public class EditCollection extends Activity {
	private Collection collection;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_collection);

		final TextView txtName = (TextView) findViewById(R.id.txtCollectionName);
		final TextView txtTricTracId = (TextView) findViewById(R.id.txtTricTracId);

		if (collection == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				collection = (Collection) intent.getExtras().getSerializable("COLLECTION");
				txtName.setText(collection.getName());
				txtTricTracId.setText("" + collection.getTricTracId());
			}
		}
		if (collection == null) {
			// its a creation
			collection = new Collection();
			setTitle(R.string.add_collection);
		} else {
			setTitle(R.string.edit_collection);
		}

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				collection.setName("" + txtName.getText());
				collection.setTricTracId(Integer.parseInt("" + txtTricTracId.getText()));

				Intent i = new Intent();
				i.putExtra("COLLECTION", collection);
				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
}
