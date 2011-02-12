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

import java.util.Date;

/**
 * This class is used to be displayed in the party list.
 * 
 * @author amphiprion
 * 
 */
public class PartyForList extends Entity {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private String gameName;
	private String city;
	private String event;
	private int happyness;
	private Date date;
	private String gameId;
	private String trictracId;
	private Date lastSyncDate;
	private Date lastUpdateDate;
	private boolean winner;

	public PartyForList(String id) {
		super(id);
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the happyness
	 */
	public int getHappyness() {
		return happyness;
	}

	/**
	 * @param happyness
	 *            the happyness to set
	 */
	public void setHappyness(int happyness) {
		this.happyness = happyness;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the gameId
	 */
	public String getGameId() {
		return gameId;
	}

	/**
	 * @param gameId
	 *            the gameId to set
	 */
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	/**
	 * @return the trictracId
	 */
	public String getTrictracId() {
		return trictracId;
	}

	/**
	 * @param trictracId
	 *            the trictracId to set
	 */
	public void setTrictracId(String trictracId) {
		this.trictracId = trictracId;
	}

	/**
	 * @return the lastSyncDate
	 */
	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	/**
	 * @param lastSyncDate
	 *            the lastSyncDate to set
	 */
	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	/**
	 * @return the lastUpdateDate
	 */
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * @param lastUpdateDate
	 *            the lastUpdateDate to set
	 */
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	/**
	 * @return the winner
	 */
	public boolean isWinner() {
		return winner;
	}

	/**
	 * @param winner
	 *            the winner to set
	 */
	public void setWinner(boolean winner) {
		this.winner = winner;
	}

}
