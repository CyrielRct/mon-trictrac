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

import java.io.File;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.entity.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a game in the game list.
 * 
 * @author amphiprion
 * 
 */
public class GameSummaryView extends LinearLayout {
	/** the linked game. */
	private Game game;

	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param game
	 *            the game entity
	 */
	public GameSummaryView(Context context, Game game) {
		super(context);
		this.game = game;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createAccountLayout(game, getContext()));

	}

	/**
	 * @return the collection
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);

		File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/" + game.getImageName());
		Bitmap bitmap = null;
		if (f.exists()) {
			bitmap = BitmapFactory.decodeFile(f.toString());
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);
		}

		img.setImageBitmap(bitmap);
		return img;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	public static View createAccountLayout(Game game, Context context) {
		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(game.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(context.getResources().getColor(R.color.black));
		accountLayout.addView(t);

		t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(game.getType());
		accountLayout.addView(t);

		accountLayout.addView(createMainStatsLayout(game, context, true, false));
		accountLayout.addView(createLevelsLayout(game, context));
		accountLayout.addView(createRatings(game, context));
		accountLayout.addView(createParties(game, context, false));
		if (game.getOwnerCollectionNames() != null) {
			accountLayout.addView(createOwnerCollections(game, context));
		}
		return accountLayout;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	public static View createMainStatsLayout(Game game, Context context, boolean includeAge, boolean center) {
		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp;
		if (center) {
			aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			aclp.gravity = Gravity.CENTER_HORIZONTAL;
		} else {
			aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		accountLayout.setLayoutParams(aclp);
		// //// Players
		ImageView im = new ImageView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		im.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_player));
		accountLayout.addView(im);
		TextView t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		String nbPlayers = "";
		if (game.getMinPlayer() != 0) {
			nbPlayers += "" + game.getMinPlayer();
		}
		if (game.getMaxPlayer() != 0) {
			nbPlayers += "-" + game.getMaxPlayer();
		}
		nbPlayers += "  ";
		t.setLayoutParams(tlp);
		t.setText(nbPlayers);
		// t.setTextSize(16);
		// t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		// ////// Duration
		im = new ImageView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		im.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_duration));
		accountLayout.addView(im);
		t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		String duration = "";
		if (game.getDuration() != 0) {
			duration += "" + game.getDuration() + " mn";
		}
		duration += "  ";
		t.setLayoutParams(tlp);
		t.setText(duration);
		// t.setTextSize(16);
		// t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		if (includeAge) {
			// //// Age
			im = new ImageView(context);
			tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			im.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_age));
			accountLayout.addView(im);
			t = new TextView(context);
			tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			String ages = "";
			if (game.getMinAge() != 0) {
				ages += "" + game.getMinAge();
			}
			if (game.getMaxPlayer() != 0) {
				ages += "-" + game.getMaxAge();
			} else if (game.getMinAge() != 0) {
				ages += "+";
			}
			ages += "  ";
			t.setLayoutParams(tlp);
			t.setText(ages);
			// t.setTextSize(16);
			// t.setTextColor(getContext().getResources().getColor(R.color.black));
			accountLayout.addView(t);
		}

		return accountLayout;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	private static View createLevelsLayout(Game game, Context context) {
		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		accountLayout.setLayoutParams(aclp);
		// //// Difficulté
		LinearLayout vl = new LinearLayout(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl.setOrientation(VERTICAL);
		vl.setLayoutParams(tlp);
		TextView t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		tlp.gravity = Gravity.CENTER;
		t.setLayoutParams(tlp);
		t.setText(R.string.difficulty);
		t.setTextSize(10);
		vl.addView(t);
		vl.addView(createLevel(game.getDifficulty(), game, context));
		accountLayout.addView(vl);

		// //// Chance
		vl = new LinearLayout(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl.setOrientation(VERTICAL);
		vl.setLayoutParams(tlp);
		t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		tlp.gravity = Gravity.CENTER;
		t.setLayoutParams(tlp);
		t.setText(R.string.luck);
		t.setTextSize(10);
		vl.addView(t);
		vl.addView(createLevel(game.getLuck(), game, context));
		accountLayout.addView(vl);

		// //// Strategy
		vl = new LinearLayout(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl.setOrientation(VERTICAL);
		vl.setLayoutParams(tlp);
		t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		tlp.gravity = Gravity.CENTER;
		t.setLayoutParams(tlp);
		t.setText(R.string.strategy);
		t.setTextSize(10);
		vl.addView(t);
		vl.addView(createLevel(game.getStrategy(), game, context));
		accountLayout.addView(vl);

		// //// Diplomatie
		vl = new LinearLayout(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		vl.setOrientation(VERTICAL);
		vl.setLayoutParams(tlp);

		t = new TextView(context);
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tlp.gravity = Gravity.CENTER;
		t.setLayoutParams(tlp);
		t.setText(R.string.diplomacy);
		t.setTextSize(10);
		vl.addView(t);
		vl.addView(createLevel(game.getDiplomaty(), game, context));
		accountLayout.addView(vl);

		return accountLayout;
	}

	private static ImageView createLevel(int level, Game game, Context context) {
		ImageView im = new ImageView(context);
		if (level > -1) {
			// LayoutParams tlp = new
			// LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
			// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			// im.setLayoutParams(tlp);
			int id = context.getResources().getIdentifier("level_" + level, "drawable", ApplicationConstants.PACKAGE);
			im.setImageDrawable(context.getResources().getDrawable(id));
		}
		return im;
	}

	public static View createParties(Game game, Context context, boolean center) {
		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp;
		if (center) {
			aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			aclp.gravity = Gravity.CENTER_HORIZONTAL;
		} else {
			aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(tlp);
		t.setText(context.getResources().getString(R.string.game_parties_nb, game.getNbParty()));
		t.setTextSize(10);
		accountLayout.addView(t);

		if (game.getNbParty() > 0) {
			t = new TextView(context);
			tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(tlp);
			t.setText(" " + context.getResources().getString(R.string.game_parties_happyness, game.getHappyness() / game.getNbParty()));
			t.setTextSize(10);
			accountLayout.addView(t);
		}
		return accountLayout;
	}

	private static View createOwnerCollections(Game game, Context context) {
		TextView t = new TextView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(tlp);
		t.setText(context.getResources().getString(R.string.game_owner_collection_names, game.getOwnerCollectionNames()));
		t.setTextSize(10);
		return t;
	}

	private static View createRatings(Game game, Context context) {
		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(tlp);
		t.setText(context.getResources().getString(R.string.game_ratings_nb, game.getNumberOfRatings()));
		t.setTextSize(10);
		accountLayout.addView(t);

		if (game.getNumberOfRatings() > 0) {
			t = new TextView(context);
			tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(tlp);
			t.setText(" " + context.getResources().getString(R.string.game_ratings_adv, game.getAdverageRating()));
			t.setTextSize(10);
			accountLayout.addView(t);
		}
		return accountLayout;
	}
}
