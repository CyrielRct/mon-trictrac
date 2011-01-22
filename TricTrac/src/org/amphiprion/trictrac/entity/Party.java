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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author amphiprion
 * 
 */
public class Party extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, CITY, EVENT, HAPPYNESS, DURATION, COMMENT, PLAY_DATE, FK_GAME, TRICTRAC_ID, SYNC_DATE, UPDATE_DATE
	}

	private String city;
	private String event;
	private List<PlayStat> stats;
	private int happyness;
	private int duration;
	private String comment;
	private Date date;
	private String gameId;
	private String trictracId;
	private Date lastSyncDate;
	private Date lastUpdateDate;

	public Party() {
		super();
	}

	public Party(String id) {
		super(id);
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
	 * @return the stats
	 */
	public List<PlayStat> getStats() {
		return stats;
	}

	/**
	 * @param stats
	 *            the stats to set
	 */
	public void setStats(List<PlayStat> stats) {
		this.stats = stats;
		for (PlayStat ps : this.stats) {
			ps.setPartyId(getId());
		}
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
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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

	public String getGameId() {
		return gameId;
	}

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

}
