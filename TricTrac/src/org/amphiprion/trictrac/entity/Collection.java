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
 * @author amphiprion
 * 
 */
public class Collection extends Entity {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Date lastSynchro;
	private int count;
	private int tricTracId;

	public enum DbField {
		ID, NAME, LAST_SYNCHRO, COUNT, TRICTRAC_ID
	}

	public Collection() {
		super();
	}

	public Collection(String id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastSynchro() {
		return lastSynchro;
	}

	public void setLastSynchro(Date lastSynchro) {
		this.lastSynchro = lastSynchro;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTricTracId() {
		return tricTracId;
	}

	public void setTricTracId(int tricTracId) {
		this.tricTracId = tricTracId;
	}

	@Override
	public String toString() {
		return name + " (" + count + ")";
	}
}
