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
package org.amphiprion.trictrac;

import org.amphiprion.trictrac.entity.Search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class EditSearch extends Activity {
	private Search search;
	private CheckBox chkPlayer;
	private CheckBox chkDifficulty;
	private CheckBox chkLuck;
	private CheckBox chkStrategy;
	private CheckBox chkDiplomacy;
	private CheckBox chkDuration;
	private TextView txtName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_search);

		txtName = (TextView) findViewById(R.id.txtName);

		if (search == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				search = (Search) intent.getExtras().getSerializable("SEARCH");
			}
		}

		chkPlayer = (CheckBox) findViewById(R.id.chkPlayers);
		chkPlayer.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pPlayers)));
		chkDifficulty = (CheckBox) findViewById(R.id.chkDifficulty);
		chkDifficulty.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pDifficulty)));
		chkLuck = (CheckBox) findViewById(R.id.chkLuck);
		chkLuck.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pLuck)));
		chkStrategy = (CheckBox) findViewById(R.id.chkStrategy);
		chkStrategy.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pStrategy)));
		chkDiplomacy = (CheckBox) findViewById(R.id.chkDiplomacy);
		chkDiplomacy.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pDiplomacy)));
		chkDuration = (CheckBox) findViewById(R.id.chkDuration);
		chkDuration.setOnCheckedChangeListener(new CheckExpander(findViewById(R.id.pDuration)));
		if (search == null) {
			// its a creation
			search = new Search();
			setTitle(R.string.add_search);
		} else {
			setTitle(R.string.edit_search);
			updateHMI();
		}

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				fillSearch();
				Intent i = new Intent();
				i.putExtra("SEARCH", search);
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

	private void updateHMI() {
		txtName.setText(search.getName());

		// Player
		boolean checked = false;
		if (search.getMinPlayer() != 0) {
			TextView txtMinPlayer = (TextView) findViewById(R.id.txtMinPlayer);
			txtMinPlayer.setText("" + search.getMinPlayer());
			checked = true;
		}
		if (search.getMaxPlayer() != 0) {
			TextView txtMaxPlayer = (TextView) findViewById(R.id.txtMaxPlayer);
			txtMaxPlayer.setText("" + search.getMaxPlayer());
			checked = true;
		}
		CheckBox chkExactly = (CheckBox) findViewById(R.id.chkExclusive);
		chkExactly.setChecked(search.isExactly());
		chkPlayer.setChecked(checked);

		// Difficulty
		checked = false;
		if (search.getMinDifficulty() != 0) {
			TextView txtMinDifficulty = (TextView) findViewById(R.id.txtMinDifficulty);
			txtMinDifficulty.setText("" + search.getMinDifficulty());
			checked = true;
		}
		if (search.getMaxDifficulty() != 0) {
			TextView txtMaxDifficulty = (TextView) findViewById(R.id.txtMaxDifficulty);
			txtMaxDifficulty.setText("" + search.getMaxDifficulty());
			checked = true;
		}
		chkDifficulty.setChecked(checked);

		// Luck
		checked = false;
		if (search.getMinLuck() != 0) {
			TextView txtMinLuck = (TextView) findViewById(R.id.txtMinLuck);
			txtMinLuck.setText("" + search.getMinLuck());
			checked = true;
		}
		if (search.getMaxLuck() != 0) {
			TextView txtMaxLuck = (TextView) findViewById(R.id.txtMaxLuck);
			txtMaxLuck.setText("" + search.getMaxLuck());
			checked = true;
		}
		chkLuck.setChecked(checked);

		// Strategy
		checked = false;
		if (search.getMinStrategy() != 0) {
			TextView txtMinStrategy = (TextView) findViewById(R.id.txtMinStrategy);
			txtMinStrategy.setText("" + search.getMinStrategy());
			checked = true;
		}
		if (search.getMaxStrategy() != 0) {
			TextView txtMaxStrategy = (TextView) findViewById(R.id.txtMaxStrategy);
			txtMaxStrategy.setText("" + search.getMaxStrategy());
			checked = true;
		}
		chkStrategy.setChecked(checked);

		// Diplomacy
		checked = false;
		if (search.getMinDiplomacy() != 0) {
			TextView txtMinDiplomaty = (TextView) findViewById(R.id.txtMinDiplomacy);
			txtMinDiplomaty.setText("" + search.getMinDiplomacy());
			checked = true;
		}
		if (search.getMaxDiplomacy() != 0) {
			TextView txtMaxDiplomaty = (TextView) findViewById(R.id.txtMaxDiplomacy);
			txtMaxDiplomaty.setText("" + search.getMaxDiplomacy());
			checked = true;
		}
		chkDiplomacy.setChecked(checked);

		// Duration
		checked = false;
		if (search.getMinDuration() != 0) {
			TextView txtMinDuration = (TextView) findViewById(R.id.txtMinDuration);
			txtMinDuration.setText("" + search.getMinDuration());
			checked = true;
		}
		if (search.getMaxDuration() != 0) {
			TextView txtMaxDuration = (TextView) findViewById(R.id.txtMaxDuration);
			txtMaxDuration.setText("" + search.getMaxDuration());
			checked = true;
		}
		chkDuration.setChecked(checked);
	}

	private void fillSearch() {
		search.setName("" + txtName.getText());

		// players
		search.setMinPlayer(0);
		search.setMaxPlayer(0);
		if (chkPlayer.isChecked()) {
			TextView txtMinPlayer = (TextView) findViewById(R.id.txtMinPlayer);
			TextView txtMaxPlayer = (TextView) findViewById(R.id.txtMaxPlayer);
			if (!"".equals("" + txtMinPlayer.getText())) {
				search.setMinPlayer(Integer.parseInt("" + txtMinPlayer.getText()));
			}
			if (!"".equals("" + txtMaxPlayer.getText())) {
				search.setMaxPlayer(Integer.parseInt("" + txtMaxPlayer.getText()));
			}
			CheckBox chkExactly = (CheckBox) findViewById(R.id.chkExclusive);
			search.setExactly(chkExactly.isChecked());
		}

		// Difficulty
		search.setMinDifficulty(0);
		search.setMaxDifficulty(0);
		if (chkDifficulty.isChecked()) {
			TextView txtMin = (TextView) findViewById(R.id.txtMinDifficulty);
			TextView txtMax = (TextView) findViewById(R.id.txtMaxDifficulty);
			if (!"".equals("" + txtMin.getText())) {
				search.setMinDifficulty(Integer.parseInt("" + txtMin.getText()));
			}
			if (!"".equals("" + txtMax.getText())) {
				search.setMaxDifficulty(Integer.parseInt("" + txtMax.getText()));
			}
		}

		// Luck
		search.setMinLuck(0);
		search.setMaxLuck(0);
		if (chkLuck.isChecked()) {
			TextView txtMin = (TextView) findViewById(R.id.txtMinLuck);
			TextView txtMax = (TextView) findViewById(R.id.txtMaxLuck);
			if (!"".equals("" + txtMin.getText())) {
				search.setMinLuck(Integer.parseInt("" + txtMin.getText()));
			}
			if (!"".equals("" + txtMax.getText())) {
				search.setMaxLuck(Integer.parseInt("" + txtMax.getText()));
			}
		}
		// Strategy
		search.setMinStrategy(0);
		search.setMaxStrategy(0);
		if (chkStrategy.isChecked()) {
			TextView txtMin = (TextView) findViewById(R.id.txtMinStrategy);
			TextView txtMax = (TextView) findViewById(R.id.txtMaxStrategy);
			if (!"".equals("" + txtMin.getText())) {
				search.setMinStrategy(Integer.parseInt("" + txtMin.getText()));
			}
			if (!"".equals("" + txtMax.getText())) {
				search.setMaxStrategy(Integer.parseInt("" + txtMax.getText()));
			}
		}

		// Diplomacy
		search.setMinDiplomacy(0);
		search.setMaxDiplomacy(0);
		if (chkDiplomacy.isChecked()) {
			TextView txtMin = (TextView) findViewById(R.id.txtMinDiplomacy);
			TextView txtMax = (TextView) findViewById(R.id.txtMaxDiplomacy);
			if (!"".equals("" + txtMin.getText())) {
				search.setMinDiplomacy(Integer.parseInt("" + txtMin.getText()));
			}
			if (!"".equals("" + txtMax.getText())) {
				search.setMaxDiplomacy(Integer.parseInt("" + txtMax.getText()));
			}
		}

		// Duration
		search.setMinDuration(0);
		search.setMaxDuration(0);
		if (chkDuration.isChecked()) {
			TextView txtMin = (TextView) findViewById(R.id.txtMinDuration);
			TextView txtMax = (TextView) findViewById(R.id.txtMaxDuration);
			if (!"".equals("" + txtMin.getText())) {
				search.setMinDuration(Integer.parseInt("" + txtMin.getText()));
			}
			if (!"".equals("" + txtMax.getText())) {
				search.setMaxDuration(Integer.parseInt("" + txtMax.getText()));
			}
		}

	}

	private class CheckExpander implements CompoundButton.OnCheckedChangeListener {
		private View panel;

		public CheckExpander(View panel) {
			this.panel = panel;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				panel.setVisibility(View.VISIBLE);
			} else {
				panel.setVisibility(View.GONE);
			}
		}
	}

}