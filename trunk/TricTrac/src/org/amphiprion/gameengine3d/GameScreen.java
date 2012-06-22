package org.amphiprion.gameengine3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.animation.GameComponentAnimation;
import org.amphiprion.gameengine3d.util.TextureUtil;

import android.view.MotionEvent;

public class GameScreen {
	protected List<IObject> objects3d;
	protected List<IObject2D> objects2d;
	private List<GameComponentAnimation> animations;
	private List<DelayedRun> delayedRuns;
	protected GameView view;
	private boolean layered;
	private Map<Enum, IHMIComponent> hmis;
	public float maskColorRed = 1;
	public float maskColorGreen = 1;
	public float maskColorBlue = 1;
	public float maskColorAlpha = 1;

	public GameScreen() {
		this(false);
	}

	public GameScreen(boolean layered) {
		this.layered = layered;

		objects2d = new ArrayList<IObject2D>();
		objects3d = new ArrayList<IObject>();
		animations = new ArrayList<GameComponentAnimation>();
		delayedRuns = new ArrayList<GameScreen.DelayedRun>();
		hmis = new HashMap<Enum, IHMIComponent>();
	}

	public void addAnimation(GameComponentAnimation anim) {
		anim.start();
		animations.add(anim);
	}

	protected void removeAnimation(GameComponentAnimation anim) {
		animations.remove(anim);
	}

	protected void clearAnimation() {
		animations.clear();
	}

	protected void registerHMIComponent(Enum key, IHMIComponent cmp) {
		hmis.put(key, cmp);
	}

	protected IHMIComponent getHMIComponent(Enum key) {
		return hmis.get(key);
	}

	/**
	 * Number of seconds since last call.
	 * 
	 * @param state
	 *            the controler state
	 * @param sElapse
	 *            elapsed seconds
	 */
	public void onUpdate(float sElapsed) {

		long msElapsed = (long) (sElapsed * 1000);
		for (int i = animations.size() - 1; i >= 0; i--) {
			GameComponentAnimation anim = animations.get(i);

			if (!anim.onUpdate(msElapsed)) {
				// if (anim.isRemoveComponentAtEnd()) {
				// mustBeRemoved = true;// on doit remover le composant associé
				// }
				animations.remove(i);
			}
		}

		// delayed
		for (int i = delayedRuns.size() - 1; i >= 0; i--) {
			DelayedRun dr = delayedRuns.get(i);
			dr.delayed -= msElapsed;
			if (dr.delayed <= 0) {
				delayedRuns.remove(i);
				dr.runnable.run();
			}
		}
	}

	public void onDraw3D(GL10 gl) {
		// gl.glColor4f(1, 1, 1, 1);
		for (IObject o : objects3d) {
			o.draw(gl);
		}
	}

	public void onDraw2D(GL10 gl, ScreenProperty screenProperty) {
		// gl.glColor4f(maskColorRed, maskColorGreen, maskColorBlue,
		// maskColorAlpha);
		List<IObject2D> clones = new ArrayList<IObject2D>(objects2d);
		for (IObject2D o : clones) {
			o.draw(gl, screenProperty.screenScaleX, screenProperty.screenScaleY, (int) screenProperty.realWidth, (int) screenProperty.realHeight);
		}
		clones.clear();
		clones = null;
	}

	public void setView(GameView view) {
		this.view = view;
	}

	public GameView getView() {
		return view;
	}

	public void desactivate() {

	}

	public void activate() {
	}

	public boolean isLayered() {
		return layered;
	}

	public void onTouch(MotionEvent event) {

	}

	public void postDelayed(Runnable r, long delayed) {
		DelayedRun d = new DelayedRun();
		d.delayed = delayed;
		d.runnable = r;
		delayedRuns.add(d);
	}

	private class DelayedRun {
		private long delayed;
		private Runnable runnable;
	}

	public boolean backRequested() {
		return true;
	}

	public void activityPaused() {
		TextureUtil.unloadAll();
	}

	public void activityResumed() {
		TextureUtil.unloadAll();
	}

}
