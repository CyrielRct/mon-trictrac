package org.amphiprion.gameengine3d;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.util.MatrixGrabber;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameView extends GLSurfaceView {
	private ScreenProperty screenProperty;
	private List<GameScreen> screens = new ArrayList<GameScreen>();
	private int startScreen = -1;
	private OpenGLRenderer renderer;

	public GameView(Context context, int referenceWidth, int referenceHeight) {
		super(context);
		screenProperty = new ScreenProperty();
		screenProperty.referenceWidth = referenceWidth;
		screenProperty.referenceHeight = referenceHeight;
	}

	public ScreenProperty getScreenProperty() {
		return screenProperty;
	}

	protected void onDraw3D(GL10 gl) {
		for (int i = startScreen; i < screens.size(); i++) {
			if (i >= 0) {
				GameScreen scr = screens.get(i);
				scr.onDraw3D(gl);
			}
		}
	}

	protected void onDraw2D(GL10 gl) {
		for (int i = startScreen; i < screens.size(); i++) {
			if (i >= 0) {
				GameScreen scr = screens.get(i);
				scr.onDraw2D(gl, screenProperty);
			}
		}
	}

	public void onUpdate(float sElapsed) {
		// only the last screen receive input
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).onUpdate(sElapsed);
		}
	}

	public void addScreen(GameScreen screen) {
		screen.setView(this);
		screens.add(screen);
		computeStartScreen();
	}

	public void removeScreen(int number) {
		for (int i = 0; i < number; i++) {
			GameScreen scr = screens.remove(screens.size() - 1);
			scr.desactivate();
		}
		computeStartScreen();
	}

	private void computeStartScreen() {
		int newStart = screens.size() - 1;
		for (int i = screens.size() - 1; i >= 0; i--) {
			newStart = i;
			if (!screens.get(i).isLayered()) {
				break;
			}
		}
		for (int i = startScreen; i < newStart; i++) {
			if (i >= 0) {
				screens.get(i).desactivate();
			}
		}
		screens.get(newStart).activate();
		startScreen = newStart;
	}

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		this.renderer = (OpenGLRenderer) renderer;
		this.renderer.updateScreenProperties();

	}

	public MatrixGrabber getMatrixGrabber() {
		return renderer.getMatrixGrabber();
	}

	public void onTouch(MotionEvent event) {
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).onTouch(event);
		}
	}

	@Override
	public boolean postDelayed(Runnable action, long delayMillis) {
		throw new UnsupportedOperationException("use gameScreen.postDelayed instead");
	}

	public boolean backRequested() {
		if (screens.size() > 0) {
			return screens.get(screens.size() - 1).backRequested();
		} else {
			return true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).activityPaused();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).activityResumed();
		}
	}
}
