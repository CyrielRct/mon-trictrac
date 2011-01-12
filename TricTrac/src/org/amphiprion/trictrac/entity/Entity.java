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
import java.util.UUID;

/**
 * @author amphiprion
 * 
 */
public class Entity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DbState {
		NEW, LOADED, DELETE
	}

	private String id;
	/** used to define if a persist command call insert, or update. */
	private DbState state;

	public String getId() {
		return id;
	}

	Entity() {
		this(UUID.randomUUID().toString());
		state = DbState.NEW;
	}

	Entity(String id) {
		this.id = id;
		state = DbState.LOADED;
	}

	public DbState getState() {
		return state;
	}

	public void setState(DbState state) {
		this.state = state;
	}
}
