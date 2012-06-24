package org.amphiprion.trictrac.v2.flip;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL;

import org.amphiprion.gameengine3d.GameScreen;
import org.amphiprion.gameengine3d.GameView;
import org.amphiprion.gameengine3d.OpenGLRenderer;
import org.amphiprion.gameengine3d.util.MatrixGrabber;
import org.amphiprion.gameengine3d.util.MatrixTrackingGL;
import org.amphiprion.trictrac.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/*
 Copyright 2012 Aphid Mobile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

public class FlipViewGroup extends ViewGroup {

	private LinkedList<View> flipViews = new LinkedList<View>();

	private GLSurfaceView surfaceView;
	private FlipRenderer renderer;
	private OpenGLRenderer rendererGame;
	private GameView gameView;
	private int width;
	private int height;

	private boolean flipping = false;
	private int currentView = 0;
	private int flipDirection = 1;
	private int nbGLViews = 2;
	public Runnable callbackWhenPageChanged;

	public FlipViewGroup(Context context) {
		super(context);
		setupSurfaceView();
	}

	public View getFlipView(int index) {
		return flipViews.get(index);
	}

	private void setupSurfaceView() {
		surfaceView = new GLSurfaceView(getContext());

		renderer = new FlipRenderer(this);
		renderer.callbackOnAngleUpdate = new Runnable() {
			@Override
			public void run() {
				angleUpdated();
			}
		};

		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		surfaceView.setZOrderOnTop(true);
		surfaceView.setRenderer(renderer);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		addView(surfaceView);
		surfaceView.setVisibility(INVISIBLE);

		// GAME RENDERER
		MatrixGrabber mg = new MatrixGrabber();
		gameView = new GameView(getContext(), 768, 1280);
		rendererGame = new OpenGLRenderer(getContext(), mg, 768, 1280, gameView);

		gameView.setGLWrapper(new GLSurfaceView.GLWrapper() {
			@Override
			public GL wrap(GL gl) {
				return new MatrixTrackingGL(gl);
			}
		});
		gameView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		gameView.setZOrderOnTop(true);
		gameView.setRenderer(rendererGame);
		gameView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		gameView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		// END GAME RENDERER
		addView(gameView);
		rendererGame.callbackOnFirstDraw = new Runnable() {
			@Override
			public void run() {
				gameView.post(new Runnable() {
					@Override
					public void run() {
						gameView.setVisibility(INVISIBLE);
					}
				});
			}
		};

	}

	public GLSurfaceView getSurfaceView() {
		return surfaceView;
	}

	public FlipRenderer getRenderer() {
		return renderer;
	}

	public void addFlipView(View v) {
		flipViews.add(v);
		addView(v, nbGLViews);
		if (flipViews.size() - 1 != currentView) {
			v.setVisibility(INVISIBLE);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Logger.i(String.format("onLayout: %d, %d, %d, %d; child %d", l, t, r,
		// b, flipViews.size()));

		for (View child : flipViews) {
			child.layout(0, 0, r - l, b - t);
		}

		if (changed) {
			int w = r - l;
			int h = b - t;
			surfaceView.layout(0, 0, w, h);
			gameView.layout(0, 0, w, h);

			if (width != w || height != h) {
				width = w;
				height = h;

				if (flipping && !flipViews.isEmpty()) {
					View firstView = flipViews.get(currentView); //
					View secondView = flipViews.get(currentView + 1); //
					renderer.updateTexture(firstView, secondView);
					// view.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Logger.i( String.format("onMeasure: %d, %d, ; child %d",
		// widthMeasureSpec, heightMeasureSpec, flipViews.size()));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (View child : flipViews) {
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!delegateTouch(event)) {
			return super.dispatchTouchEvent(event);
		} else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!delegateTouch(event)) {
			return super.onTouchEvent(event);
		} else {
			return true;
		}
	}

	private boolean delegateTouch(MotionEvent event) {
		if (locked) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// Logger.i("nbPage = " + getPageCount());
			if (!flipping && Math.abs(py - event.getY()) < 10) {
				return true;
			}

			if (py < event.getY()) {
				flipDirection = -1;
			} else if (py > event.getY()) {
				flipDirection = 1;
			} else {
				return true;
			}
			if (!flipping) {
				renderer.setAngle(0);
				if (flipDirection == 1 && currentView + 1 >= flipViews.size()) {
					return true;
				} else if (flipDirection == -1 && currentView == 0) {
					return true;
				}
				if (flipDirection == -1) {
					currentView--;
					renderer.setAngle(180);
				}
				View firstView = flipViews.get(currentView); //
				View secondView = flipViews.get(currentView + 1); //
				renderer.updateTexture(firstView, secondView);
				// if (flipDirection == -1) {
				// firstView.setVisibility(VISIBLE);
				// secondView.setVisibility(INVISIBLE);
				// } else {
				// firstView.setVisibility(INVISIBLE);
				// secondView.setVisibility(VISIBLE);
				// }
				flipping = true;
				surfaceView.setVisibility(VISIBLE);
			}
			renderer.setAngle(renderer.getAngle() + (py - event.getY()) / 3.0f);

			if (renderer.getAngle() < 90) {
				View firstView = flipViews.get(currentView);
				firstView.setVisibility(VISIBLE);
				View secondView = flipViews.get(currentView + 1);
				secondView.setVisibility(INVISIBLE);
			} else {
				View firstView = flipViews.get(currentView);
				firstView.setVisibility(INVISIBLE);
				View secondView = flipViews.get(currentView + 1);
				secondView.setVisibility(VISIBLE);
			}
			px = event.getX();
			py = event.getY();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			py = -1;
			if (flipping) {
				if (renderer.getAngle() != 0 && renderer.getAngle() != 180) {
					renderer.animatedDirection = flipDirection;
				} else {
					angleUpdated();
				}
				return true;
			} else {
				return false;
			}
		} else {
			px = event.getX();
			py = event.getY();
			return false;
		}

	}

	protected void angleUpdated() {
		final View firstView = flipViews.get(currentView);
		final View secondView = flipViews.get(currentView + 1);
		if (renderer.getAngle() == 180) {
			renderer.animatedDirection = 0;
			post(new Runnable() {
				@Override
				public void run() {
					firstView.setVisibility(INVISIBLE);
					secondView.setVisibility(VISIBLE);
					surfaceView.setVisibility(INVISIBLE);
				}
			});
			flipping = false;
			renderer.destroyTexture();

			// renderer.setAngle(0);
			currentView++;
			if (callbackWhenPageChanged != null) {
				callbackWhenPageChanged.run();
			}
		} else if (renderer.getAngle() == 0) {
			renderer.animatedDirection = 0;
			flipping = false;
			post(new Runnable() {
				@Override
				public void run() {
					firstView.setVisibility(VISIBLE);
					secondView.setVisibility(INVISIBLE);
					surfaceView.setVisibility(INVISIBLE);
				}
			});
			renderer.destroyTexture();

			// renderer.setAngle(0);
			if (callbackWhenPageChanged != null) {
				callbackWhenPageChanged.run();
			}
		} else {
			if (renderer.getAngle() < 90) {
				post(new Runnable() {
					@Override
					public void run() {
						firstView.setVisibility(VISIBLE);
						secondView.setVisibility(INVISIBLE);
					}
				});
			} else {
				post(new Runnable() {
					@Override
					public void run() {
						firstView.setVisibility(INVISIBLE);
						secondView.setVisibility(VISIBLE);
					}
				});
			}
		}
	}

	private float px;
	private float py;
	private boolean locked;

	public void clearFlipViews() {
		for (View v : flipViews) {
			removeView(v);
		}
		flipViews.clear();
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int getCurrentPage() {
		return currentView;
	}

	public int getPageCount() {
		return flipViews.size();
	}

	public boolean isFlipping() {
		return flipping;
	}

	public void openGameMenu(GameScreen screen) {
		locked = true;
		View v = flipViews.get(currentView);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.mask);
		ll.setVisibility(View.VISIBLE);

		gameView.setVisibility(VISIBLE);
		gameView.addScreen(screen);
	}

	public void closeGameMenu() {
		final View v = flipViews.get(currentView);
		v.post(new Runnable() {

			@Override
			public void run() {
				LinearLayout ll = (LinearLayout) v.findViewById(R.id.mask);
				ll.setVisibility(View.INVISIBLE);

				gameView.setVisibility(INVISIBLE);
				locked = false;

			}
		});
	}
}