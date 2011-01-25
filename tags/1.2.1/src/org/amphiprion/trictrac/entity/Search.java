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
package org.amphiprion.trictrac.entity;

/**
 * @author amphiprion
 * 
 */
public class Search extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, NAME, MIN_PLAYER, MAX_PLAYER, MIN_DIFFICULTY, MAX_DIFFICULTY, MIN_LUCK, MAX_LUCK, MIN_STRATEGY, MAX_STRATEGY, MIN_DIPLOMACY, MAX_DIPLOMACY, MIN_DURATION, MAX_DURATION, EXACTLY
	}

	private String name;
	private int minPlayer;
	private int maxPlayer;
	private boolean exactly;
	private int minDifficulty;
	private int maxDifficulty;
	private int minLuck;
	private int maxLuck;
	private int minStrategy;
	private int maxStrategy;
	private int minDiplomacy;
	private int maxDiplomacy;
	private int minDuration;
	private int maxDuration;

	public Search() {
		super();
	}

	public Search(String id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public void setMinPlayer(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	public int getMaxDifficulty() {
		return maxDifficulty;
	}

	public void setMaxDifficulty(int maxDifficulty) {
		this.maxDifficulty = maxDifficulty;
	}

	public int getMaxDiplomacy() {
		return maxDiplomacy;
	}

	public void setMaxDiplomacy(int maxDiplomacy) {
		this.maxDiplomacy = maxDiplomacy;
	}

	public int getMaxLuck() {
		return maxLuck;
	}

	public void setMaxLuck(int maxLuck) {
		this.maxLuck = maxLuck;
	}

	public int getMaxStrategy() {
		return maxStrategy;
	}

	public void setMaxStrategy(int maxStrategy) {
		this.maxStrategy = maxStrategy;
	}

	public int getMinDifficulty() {
		return minDifficulty;
	}

	public void setMinDifficulty(int minDifficulty) {
		this.minDifficulty = minDifficulty;
	}

	public int getMinDiplomacy() {
		return minDiplomacy;
	}

	public void setMinDiplomacy(int minDiplomacy) {
		this.minDiplomacy = minDiplomacy;
	}

	public int getMinLuck() {
		return minLuck;
	}

	public void setMinLuck(int minLuck) {
		this.minLuck = minLuck;
	}

	public int getMinStrategy() {
		return minStrategy;
	}

	public void setMinStrategy(int minStrategy) {
		this.minStrategy = minStrategy;
	}

	public int getMinDuration() {
		return minDuration;
	}

	public void setMinDuration(int minDuration) {
		this.minDuration = minDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}

	public boolean isExactly() {
		return exactly;
	}

	public void setExactly(boolean exactly) {
		this.exactly = exactly;
	}

	@Override
	public String toString() {
		return name;
	}
}
