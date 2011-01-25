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

/**
 * @author amphiprion
 * 
 */
public class Player extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, PSEUDO, TRICTRAC_PROFILE_ID, TRICTRAC_ID, SYNC_DATE, UPDATE_DATE
	}

	private String pseudo;
	private String tricTracProfileId;
	private String trictracId;
	private Date lastSyncDate;
	private Date lastUpdateDate;

	public Player() {
		super();
	}

	public Player(String id) {
		super(id);
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getTricTracProfileId() {
		return tricTracProfileId;
	}

	public void setTricTracProfileId(String tricTracProfileId) {
		this.tricTracProfileId = tricTracProfileId;
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

	@Override
	public String toString() {
		return pseudo;
	}

}
