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
package org.amphiprion.trictrac.interpolator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

/**
 * @author Amphiprion
 * 
 */
public class BounceInterpolator implements Interpolator {
	public BounceInterpolator() {
	}

	public BounceInterpolator(Context context, AttributeSet attrs) {
	}

	private static float bounce(float t) {
		return t * t * 8.0f;
	}

	@Override
	public float getInterpolation(float t) {
		t *= 1.1226f;
		if (t < 0.3535f) {
			return bounce(t);
		} else if (t < 0.7408f) {
			return bounce(t - 0.54719f) + 0.7f;
		} else if (t < 0.9644f) {
			return bounce(t - 0.8526f) + 0.9f;
		} else {
			return bounce(t - 1.0435f) + 0.95f;
		}
	}

}
