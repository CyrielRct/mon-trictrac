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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.task.IProgressTask;
import org.amphiprion.trictrac.util.ImageUtil;

import android.content.Context;
import android.util.Log;

/**
 * This is the handler for 'ludotheque' page.
 * 
 * @author amphiprion
 * 
 */
public class TricTracGameHandler {
	/** The entry href pattern. */
	private static final String HREF_PATTERN = "<a href='index.php3?id=jeux&rub=detail&inf=detail&jeu=";

	/** the game name patter. */
	private static final String GAME_NAME_PATTERN = "<TD><font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'><b>";

	/** The current game. */
	private Game game;

	/** The new links between collection and games. */
	private List<Game> games;

	private IProgressTask task;
	/**
	 * Return the number of game parsed.
	 */
	private int count;

	/** true if the parser is on a game entry. */
	private boolean inGameEntry = false;

	private GameDao dao;
	private GameHandler gameHandler;

	/**
	 * Default constructor.
	 */
	public TricTracGameHandler(Context context, IProgressTask task) {
		this.task = task;
		gameHandler = new GameHandler();
		games = new ArrayList<Game>();
		dao = GameDao.getInstance(context);
	}

	/**
	 * Parse the collection of the given user id.
	 * 
	 * @param userId
	 *            the user id
	 */
	public List<Game> parse(String query, int deb, int pageSize) {
		int offset = 0;
		int total = 0;
		try {
			String uri = "http://www.trictrac.net/index.php3?id=jeux&rub=ludotheque&inf=cat&deb=" + deb + "&nb="
					+ pageSize + "&choix=" + URLEncoder.encode(query) + "&ta=";
			URL url = new URL(uri + offset);
			InputStream fis = url.openConnection().getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "ISO-8859-1"));

			String ligne;
			count = 0;
			inGameEntry = false;
			while ((ligne = br.readLine()) != null) {
				if (task.isCancelled()) {
					games = null;
					count = 0;
					break;
				}

				if (ligne.startsWith(HREF_PATTERN)) {
					total++;
					task.publishProgress(total);
					count++;
					int pos = ligne.indexOf("'", HREF_PATTERN.length());
					String id = ligne.substring(HREF_PATTERN.length(), pos);
					game = dao.getGame(id);
					if (game == null) {
						inGameEntry = true;
						game = new Game(id);
						ImageUtil.downloadImage(game);
						gameHandler.parse(game);
					}
					games.add(game);
				} else if (inGameEntry && ligne.startsWith(GAME_NAME_PATTERN)) {
					int pos = ligne.indexOf("</b>", GAME_NAME_PATTERN.length());
					game.setName(ligne.substring(GAME_NAME_PATTERN.length(), pos));
					inGameEntry = false;
					dao.createGame(game);
				}
			}
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "Error parsing tric trac collection", e);
			games = null;
		}
		return games;
	}
}
