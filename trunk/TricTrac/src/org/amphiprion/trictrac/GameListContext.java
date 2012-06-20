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

import java.util.List;

import org.amphiprion.trictrac.entity.Collection;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Search;
import org.amphiprion.trictrac.task.LoadGamesTask;
import org.amphiprion.trictrac.view.MyScrollView;

/**
 * This class is the context of the game list view.
 * 
 * @author amphiprion
 * 
 */
public class GameListContext {
	public static final int PAGE_SIZE = 18;

	public Collection collection;
	public Search search;
	public int loadedPage;
	public List<Game> games;
	public String query;
	public MyScrollView scrollView;
	public Game current;
	public boolean allLoaded;
	public boolean loading;
	public LoadGamesTask task;

	/** Possible action on game simple click. */
	public enum ClickAction {
		TRIC_TRAC, PARTIES, NOTHING
	}

	public ClickAction clickAction;

}
