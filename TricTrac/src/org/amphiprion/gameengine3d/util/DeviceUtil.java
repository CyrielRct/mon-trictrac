/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of Descent Assistant.
 *
 * Descent Assistant is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Descent Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Descent Assistant.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.gameengine3d.util;
import android.content.Context;
import android.os.Vibrator;


public class DeviceUtil {
	private static boolean disableVibration = false;
	private static Vibrator vibrator;

	public static void init(Context context) {
		try {
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		} catch (Throwable e) {
		}
	}

	public static void disableVibration(boolean disable) {
		DeviceUtil.disableVibration = disable;
	}

	public static void vibrate(long milliseconds) {
		try {
			if (vibrator != null && !disableVibration) {
				vibrator.vibrate(milliseconds);
			}
		} catch (Throwable e) {
		}
	}
}
