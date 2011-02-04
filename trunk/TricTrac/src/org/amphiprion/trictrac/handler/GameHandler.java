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
package org.amphiprion.trictrac.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.entity.Game;

import android.util.Log;

/**
 * This is the handler for 'game' page.
 * 
 * @author amphiprion
 * 
 */
public class GameHandler {
	/** The entry href pattern. */
	private static final String TYPE_PATTERN = "        <TD><font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'><b><A HREF=\"index.php3?id=jeux&rub=ludotheque&inf=cat&choix=";
	private static final String FAMILY_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\">Famille(s) :";
	private static final String MECHANISM_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\"> M&eacute;canisme(s) ";
	private static final String THEME_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\">Th&egrave;me(s) :";
	private static final String PLAYER_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\">Joueurs :";
	private static final String AGE_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\">Age :";
	private static final String DURATION_PATTERN = "            <font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'>            <FONT COLOR=\"#999999\">Dur&eacute;e :";
	private static final String LEVELS_PATTERN = "<img src='/jeux/centre/imagerie/barre_";
	private static final String NAME_PATTERN = "    <td colspan=\"2\"><font style='COLOR: #004B56; FONT-FAMILY: arial; FONT-SIZE: 20px; font-weight: BOLD'><i>";
	private static final String NB_RATING_PATTERN = "<font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'> nombre d'avis : <b>";
	private static final String ADV_RATING_PATTERN = "<font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'> Moyenne  : ";

	private enum Step {
		NAME(NAME_PATTERN), TYPE(TYPE_PATTERN), FAMILY(FAMILY_PATTERN), MECHANISM(MECHANISM_PATTERN), THEME(
				THEME_PATTERN), PLAYER(PLAYER_PATTERN), AGE(AGE_PATTERN), DURATION(DURATION_PATTERN), LEVEL_DIFFICULTY(
				LEVELS_PATTERN), LEVEL_LUCK(LEVELS_PATTERN), LEVEL_STRATEGY(LEVELS_PATTERN), LEVEL_DIPLOMATY(
				LEVELS_PATTERN), NB_RATING(NB_RATING_PATTERN), ADV_RATING(ADV_RATING_PATTERN);
		private String pattern;

		private Step(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}

		public Step next() {
			return Step.values()[ordinal() + 1];
		}
	}

	private Step step;

	/**
	 * Default constructor.
	 */
	public GameHandler() {
	}

	/**
	 * Parse the collection of the given user id.
	 * 
	 * @param userId
	 *            the user id
	 */
	public void parse(Game game) {
		step = Step.NAME;
		try {
			String uri = "http://www.trictrac.net/index.php3?id=jeux&rub=detail&inf=detail&jeu=" + game.getId();
			URL url = new URL(uri);
			InputStream fis = url.openConnection().getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "ISO-8859-1"));

			String ligne;
			while ((ligne = br.readLine()) != null) {

				if (step == Step.NAME && ligne.startsWith(step.getPattern())) {
					int start = step.getPattern().length();
					int end = ligne.indexOf("<", step.getPattern().length());
					game.setName(ligne.substring(start, end));
					step = step.next();
				} else if (step == Step.TYPE && ligne.startsWith(step.getPattern())) {
					int start = ligne.indexOf(">", step.getPattern().length());
					int end = ligne.indexOf("<", step.getPattern().length());
					game.setType(ligne.substring(start + 1, end));
					// Log. d(ApplicationConstants.PACKAGE, "type=" +
					// game.getType());
					step = step.next();
				} else if (step == Step.FAMILY && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					String pat = "<A HREF=\"?id=jeux&rub=ludotheque&inf=cat&choix1=";
					String s = "|";
					while (true) {
						int pos = ligne.indexOf(pat);
						if (pos == -1) {
							break;
						}
						int end = ligne.indexOf("\"", pos + pat.length());
						s += ligne.substring(pos + pat.length(), end) + "|";
						ligne = br.readLine();
					}
					game.setFamilies(s);
					// Log. d(ApplicationConstants.PACKAGE, "families=" +
					// game.getFamilies());
					step = step.next();
				} else if (step == Step.MECHANISM && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					String pat = "<A HREF=\"?id=jeux&rub=ludotheque&inf=cat&choix1=";
					String s = "|";
					while (true) {
						int pos = ligne.indexOf(pat);
						if (pos == -1) {
							break;
						}
						int end = ligne.indexOf("\"", pos + pat.length());
						s += ligne.substring(pos + pat.length(), end) + "|";
						ligne = br.readLine();
					}
					game.setMechanisms(s);
					// Log. d(ApplicationConstants.PACKAGE, "mechanisms=" +
					// game.getMechanisms());
					step = step.next();
				} else if (step == Step.THEME && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					String pat = "<A HREF=\"?id=jeux&rub=ludotheque&inf=cat&choix1=";
					String s = "|";
					while (true) {
						int pos = ligne.indexOf(pat);
						if (pos == -1) {
							break;
						}
						int end = ligne.indexOf("\"", pos + pat.length());
						s += ligne.substring(pos + pat.length(), end) + "|";
						ligne = br.readLine();
					}
					game.setThemes(s);
					// Log. d(ApplicationConstants.PACKAGE, "themes=" +
					// game.getThemes());
					step = step.next();
				} else if (step == Step.PLAYER && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					ligne = br.readLine();
					int start = ligne.indexOf(">");
					int end = ligne.indexOf("<", start);
					String s = ligne.substring(start + 1, end).trim();
					int p1 = s.indexOf(" ");
					int p2 = s.lastIndexOf(" ");
					int min = 0;
					int max = 0;
					if (p1 == -1) {
						if (s.length() > 0) {
							min = Integer.parseInt(s);
						}
						max = min;
					} else {
						min = Integer.parseInt(s.substring(0, p1));
						String sMax = s.substring(p2 + 1);
						try {
							max = Integer.parseInt(sMax);
						} catch (NumberFormatException e) {
							Log.d(ApplicationConstants.PACKAGE, "", e);
							max = 0;
						}
					}
					game.setMinPlayer(min);
					game.setMaxPlayer(max);
					// Log. d(ApplicationConstants.PACKAGE, "minPlayer=" +
					// game.getMinPlayer());
					// Log. d(ApplicationConstants.PACKAGE, "maxPlayer=" +
					// game.getMaxPlayer());
					step = step.next();
				} else if (step == Step.AGE && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					ligne = br.readLine();
					int start = ligne.indexOf(">");
					int end = ligne.indexOf("<", start);
					String s = ligne.substring(start + 1, end).trim();
					int p1 = s.indexOf(" ");
					int p2 = s.indexOf(" ", p1 + 1);
					int p3 = s.indexOf(" ", p2 + 1);

					int min = 0;
					int max = 0;
					try {
						min = Integer.parseInt(s.substring(0, p1));
					} catch (NumberFormatException e) {
						p3 = p2;
						p2 = p1;
					}
					try {
						max = Integer.parseInt(s.substring(p2 + 1, p3));
					} catch (NumberFormatException e) {
					}
					game.setMinAge(min);
					game.setMaxAge(max);
					// Log. d(ApplicationConstants.PACKAGE, "minAge=" +
					// game.getMinAge());
					// Log. d(ApplicationConstants.PACKAGE, "maxAge=" +
					// game.getMaxAge());
					step = step.next();
				} else if (step == Step.DURATION && ligne.startsWith(step.getPattern())) {
					ligne = br.readLine();
					ligne = br.readLine();
					int start = ligne.indexOf(">");
					int end = ligne.indexOf("<", start);
					String s = ligne.substring(start + 1, end).trim();
					int p1 = s.indexOf(" ");
					int delay = 0;
					if (p1 == -1) {
						delay = 0;
					} else {
						try {
							delay = Integer.parseInt(s.substring(0, p1));
						} catch (NumberFormatException e) {
							Log.d(ApplicationConstants.PACKAGE, "", e);
							delay = 0;
						}
					}
					game.setDuration(delay);
					// Log. d(ApplicationConstants.PACKAGE, "maxDuration=" +
					// game.getDuration());
					step = step.next();
				} else if (step == Step.LEVEL_DIFFICULTY) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf(".", start);

						int level = Integer.parseInt(ligne.substring(start + step.getPattern().length(), p1));
						game.setDifficulty(level);
						// Log. d(ApplicationConstants.PACKAGE, "difficulty=" +
						// game.getDifficulty());
						step = step.next();
					}
				} else if (step == Step.LEVEL_LUCK) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf(".", start);

						int level = Integer.parseInt(ligne.substring(start + step.getPattern().length(), p1));
						game.setLuck(level);
						// Log. d(ApplicationConstants.PACKAGE, "luck=" +
						// game.getLuck());
						step = step.next();
					}
				} else if (step == Step.LEVEL_STRATEGY) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf(".", start);

						int level = Integer.parseInt(ligne.substring(start + step.getPattern().length(), p1));
						game.setStrategy(level);
						// Log. d(ApplicationConstants.PACKAGE, "strategy=" +
						// game.getStrategy());
						step = step.next();
					}
				} else if (step == Step.LEVEL_DIPLOMATY) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf(".", start);

						int level = Integer.parseInt(ligne.substring(start + step.getPattern().length(), p1));
						game.setDiplomaty(level);
						// Log. d(ApplicationConstants.PACKAGE, "diplomatie=" +
						// game.getDiplomaty());

						step = step.next();
					}
				} else if (step == Step.NB_RATING) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf("</b>", start);
						int nb = Integer.parseInt(ligne.substring(step.getPattern().length(), p1));
						game.setNumberOfRatings(nb);
						step = step.next();
					}
				} else if (step == Step.ADV_RATING) {
					int start = ligne.indexOf(step.getPattern());
					if (start != -1) {
						int p1 = ligne.indexOf("<font", step.getPattern().length());
						String s = ligne.substring(step.getPattern().length(), p1);
						if (!"-".equals(s)) {
							double adv = Double.parseDouble(s);
							game.setAdverageRating(adv);
						} else {
							game.setAdverageRating(0);
						}
						break;
					}
				}
			}

		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "Error parsing tric trac collection", e);
		}
	}
}
