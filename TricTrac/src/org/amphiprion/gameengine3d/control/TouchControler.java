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

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.gameengine3d.GameView;
import org.amphiprion.gameengine3d.control.ControlerState.ClickMode;
import org.amphiprion.gameengine3d.util.DrawUtil;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Amphiprion
 * 
 */
public class TouchControler {
	private Object mutex = new Object();
	private ControlerState controlState = new ControlerState();
	private boolean moving;
	private volatile long lastDownTime = 0;
	private List<ControlerState> waitingStates = new ArrayList<ControlerState>();
	private ControlerState lastState;

	public TouchControler(final GameView view) {
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Log.d("com.amphiprion.westeros", "mode=" + mode + "  index="
				// + event.getActionIndex() + "  down=" + event.getAction());
				// int index = event.getActionIndex();
				synchronized (mutex) {

					int actionCode = event.getAction();
					final float x = DrawUtil.convertX(event.getX(), event.getY(), view.getScreenProperty());
					final float y = DrawUtil.convertY(event.getX(), event.getY(), view.getScreenProperty());

					switch (actionCode) {
					case MotionEvent.ACTION_DOWN:
						long time = System.currentTimeMillis();
						if (time - lastDownTime < 250) {
							controlState.click = ClickMode.DOUBLE_CLICK;
							controlState.touched = false;
						} else {
							controlState.click = ClickMode.DOWN;
							lastDownTime = time;
							controlState.touched = true;
						}
						controlState.position.x = x;
						controlState.position.y = y;
						controlState.translate.x = 0;
						controlState.translate.y = 0;
						controlState.ratio = 1;

						break;

					case MotionEvent.ACTION_UP:
						if (!moving && controlState.touched && (controlState.click == ClickMode.NONE || controlState.click == ClickMode.DOWN)) {
							controlState.click = ClickMode.CLICK;
						} else if (moving && controlState.touched && (controlState.click == ClickMode.NONE || controlState.click == ClickMode.DOWN)) {
							controlState.click = ClickMode.UP;
						}

						controlState.position.x = x;
						controlState.position.y = y;
						controlState.translate.x = 0;
						controlState.translate.y = 0;
						controlState.ratio = 1;
						controlState.touched = false;
						moving = false;
						break;
					case MotionEvent.ACTION_MOVE:
						if (controlState.touched) {
							float dx = x - controlState.position.x;
							float dy = y - controlState.position.y;

							if (!moving && (Math.abs(dx) > 8 || Math.abs(dy) > 8)) {
								moving = true;
							}
							if (moving) {
								controlState.translate.x += dx;
								controlState.translate.y += dy;
								controlState.position.x = x;
								controlState.position.y = y;
							}

						}
						break;
					}
					if (lastState != null) {
						if (lastState.click == controlState.click) {
							lastState.replaceContentBy(controlState);
						} else {
							lastState = new ControlerState(controlState);
							waitingStates.add(lastState);
						}
					} else {
						lastState = new ControlerState(controlState);
						waitingStates.add(lastState);
					}
					controlState.click = ClickMode.NONE;
					controlState.translate.x = 0;
					controlState.translate.y = 0;
					controlState.rotation = 0;
				}

				return true;
			}
		});
	}

	public ControlerState getControlerState() {
		ControlerState state = null;
		synchronized (mutex) {
			if (waitingStates.size() == 0) {
				state = new ControlerState(controlState);
			} else {
				state = waitingStates.get(0);
				waitingStates.remove(0);
			}
			if (waitingStates.size() == 0) {
				lastState = null;
			}
			if (!moving && controlState.touched && System.currentTimeMillis() - lastDownTime > 500) {
				controlState.click = ClickMode.LONG_CLICK;
				controlState.touched = false;
				state.touched = false;
				state.click = ClickMode.LONG_CLICK;
			}

			controlState.click = ClickMode.NONE;
			controlState.translate.x = 0;
			controlState.translate.y = 0;
			controlState.rotation = 0;
		}

		return state;
	}
}
