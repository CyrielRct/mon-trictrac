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
package org.amphiprion.gameengine3d.control;

import android.graphics.PointF;

/**
 * @author Amphiprion
 * 
 */
public class ControlerState {
	public enum ClickMode {
		NONE, DOWN, CLICK, DOUBLE_CLICK, LONG_CLICK, UP
	}

	public ClickMode click;
	public PointF position;
	public PointF point1;
	public PointF point2;
	public PointF translate;
	public boolean touched;
	public double ratio;
	public float rotation;

	public ControlerState() {
		click = ClickMode.NONE;
		position = new PointF();
		point1 = new PointF();
		point2 = new PointF();
		translate = new PointF();
		ratio = 1;
	}

	public ControlerState(ControlerState state) {
		replaceContentBy(state);
	}

	public void replaceContentBy(ControlerState state) {
		click = state.click;
		position = new PointF(state.position.x, state.position.y);
		translate = new PointF(state.translate.x, state.translate.y);
		point1 = new PointF(state.point1.x, state.point1.y);
		point2 = new PointF(state.point2.x, state.point2.y);
		ratio = state.ratio;
		touched = state.touched;
		rotation = state.rotation;
	}
}
