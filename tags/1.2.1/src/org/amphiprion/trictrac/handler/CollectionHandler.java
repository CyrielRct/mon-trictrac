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
import java.util.ArrayList;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.CollectionGame;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.task.ImportCollectionTask;
import org.amphiprion.trictrac.util.ImageUtil;

import android.content.Context;
import android.util.Log;

/**
 * This is the handler for 'ludotheque' page.
 * 
 * @author amphiprion
 * 
 */
public class CollectionHandler {
	/** The entry href pattern. */
	private static final String HREF_PATTERN = "<a href='index.php3?id=jeux&rub=detail&inf=detail&jeu=";

	/** the game name patter. */
	private static final String GAME_NAME_PATTERN = "<TD><font style='COLOR: #000000; FONT-FAMILY: arial; FONT-SIZE: 12px;'><b>";

	/** The builder. */
	// private StringBuilder builder;

	/** The current game. */
	private Game game;

	/** The new links between collection and games. */
	private List<CollectionGame> links;

	/** The game DAO. */
	private GameDao dao;
	private ImportCollectionTask task;
	private GameHandler gameHandler;
	/**
	 * Return the number of game parsed.
	 */
	private int count;

	/** true if the parser is on a game entry. */
	private boolean inGameEntry = false;

	/**
	 * Default constructor.
	 */
	public CollectionHandler(Context context, ImportCollectionTask task) {
		links = new ArrayList<CollectionGame>();
		dao = GameDao.getInstance(context);
		gameHandler = new GameHandler();
		this.task = task;
	}

	/**
	 * Parse the collection of the given user id.
	 * 
	 * @param userId
	 *            the user id
	 */
	public void parse(Collection collection) {
		int offset = 0;
		int total = 0;
		try {
			String uri = "http://www.trictrac.net/index.php3?id=jeux&rub=ludoperso&inf=liste&choix="
					+ collection.getTricTracId() + "&choix1=1&date=1&deb=";
			while (true) {
				if (task.isCancelled()) {
					links = null;
					break;
				}
				URL url = new URL(uri + offset);
				InputStream fis = url.openConnection().getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(fis, "ISO-8859-1"));

				String ligne;
				count = 0;
				inGameEntry = false;
				while ((ligne = br.readLine()) != null) {
					if (task.isCancelled()) {
						links = null;
						count = 0;
						break;
					}

					if (ligne.startsWith(HREF_PATTERN)) {
						total++;
						task.publishProgress(total);
						count++;
						int pos = ligne.indexOf("'", HREF_PATTERN.length());
						String id = ligne.substring(HREF_PATTERN.length(), pos);
						if (!dao.isExists(id)) {
							inGameEntry = true;
							game = new Game(id);
							// int start = ligne.indexOf(TAG_IMAGE);
							// int end = ligne.indexOf("'", TAG_IMAGE.length());
							// href += ligne.substring(start +
							// TAG_IMAGE.length(), end);
							ImageUtil.downloadImage(game);
							gameHandler.parse(game);
						}
						CollectionGame link = new CollectionGame();
						link.setCollectionId(collection.getId());
						link.setGameId(id);
						links.add(link);
					} else if (inGameEntry && ligne.startsWith(GAME_NAME_PATTERN)) {
						int pos = ligne.indexOf("</b>", GAME_NAME_PATTERN.length());
						game.setName(ligne.substring(GAME_NAME_PATTERN.length(), pos));
						inGameEntry = false;
						dao.createGame(game);
					}
				}

				if (count == 0) {
					break;
				}
				offset += 20;
			}
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "Error parsing tric trac collection", e);
			links = null;
		}
	}

	/**
	 * Return the list of links
	 * 
	 * @return the links
	 */
	public List<CollectionGame> getCollectionGames() {
		return links;
	}

}
