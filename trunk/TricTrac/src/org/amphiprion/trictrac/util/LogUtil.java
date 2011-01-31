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
package org.amphiprion.trictrac.util;

import java.io.PrintWriter;

/**
 * @author amphiprion
 * 
 */
public class LogUtil {
	public static boolean traceEnabled = false;

	public static void trace(PrintWriter pw, String message) {
		if (traceEnabled) {
			pw.println(message);
		}
	}

	public static void trace(PrintWriter pw, Throwable e) {
		if (traceEnabled) {
			e.printStackTrace(pw);
		}
	}
}
