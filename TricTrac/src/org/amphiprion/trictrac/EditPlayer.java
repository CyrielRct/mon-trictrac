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

import org.amphiprion.trictrac.entity.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * The activity used to edit/create a player.
 * 
 * @author amphiprion
 * 
 */
public class EditPlayer extends Activity {
	private Player player;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_player);

		final TextView txtName = (TextView) findViewById(R.id.txtPseudoName);
		final TextView txtTricTracId = (TextView) findViewById(R.id.txtTricTracId);

		if (player == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				player = (Player) intent.getExtras().getSerializable("PLAYER");
				txtName.setText(player.getPseudo());
				txtTricTracId.setText("" + player.getTricTracId());
			}
		}
		if (player == null) {
			// its a creation
			player = new Player();
			setTitle(R.string.add_player);
		} else {
			setTitle(R.string.edit_player);
		}

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				player.setPseudo("" + txtName.getText());
				player.setTricTracId("" + txtTricTracId.getText());

				Intent i = new Intent();
				i.putExtra("PLAYER", player);
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
