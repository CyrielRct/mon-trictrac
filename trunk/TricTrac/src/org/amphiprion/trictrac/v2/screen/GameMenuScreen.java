package org.amphiprion.trictrac.v2.screen;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.GameScreen;
import org.amphiprion.gameengine3d.ScreenProperty;
import org.amphiprion.gameengine3d.animation.Translation3DAnimation;
import org.amphiprion.gameengine3d.mesh.Plane;
import org.amphiprion.gameengine3d.util.SceneUtil;
import org.amphiprion.gameengine3d.util.TextureUtil;
import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.entity.Game;

import android.opengl.GLU;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

public class GameMenuScreen extends GameScreen {
	private float[] posCamera = new float[] { 0, -9f, 8f };
	private float[] posLook = new float[] { 0, 0, 0f };
	private int rotateDirection = 1;
	private int selectedButton = -1;
	private SateliteAnimation anim = new SateliteAnimation();
	private Plane[] bt;
	private Plane centerBt;

	private boolean rotate;
	private float rotatePct;

	private boolean inTransition;

	private DecelerateInterpolator itpDec = new DecelerateInterpolator();

	public enum Item {
		NONE, SYNCHRO, DETAIL, TRICTRAC, ADD_PARTIE
	}

	public interface GameScreenExited {
		void exited(Item item);
	}

	private GameScreenExited callbackExited;

	public GameMenuScreen(Game game, GameScreenExited callbackExited) {
		this.callbackExited = callbackExited;
		bt = new Plane[4];
		startAll(game);
	}

	public void startAll(Game game) {
		objects3d.clear();
		TextureUtil.unloadAll();
		rotatePct = 0;
		rotateDirection = 1;
		selectedButton = -1;
		rotate = false;
		inTransition = false;

		if (game != null) {
			centerBt = new Plane("file:"
					+ Environment.getExternalStorageDirectory() + "/"
					+ ApplicationConstants.DIRECTORY + "/"
					+ game.getImageName(), 2, 2, 0, 0, 0, 124, 124);
		} else {
			centerBt = new Plane("/images/default/neobt[0]00.png", 2, 2, 0, 0,
					0, 124, 124);
		}
		centerBt.z = 10;
		objects3d.add(centerBt);

		Translation3DAnimation t = new Translation3DAnimation(centerBt, 500, 0,
				0, 0, -centerBt.z);
		t.setInterpolation(itpDec);
		addAnimation(t);

		bt[0] = new Plane("/images/default/menu-game.png", 2, 2, 0, 0, 0, 124,
				124);
		bt[0].ry = -180;
		objects3d.add(bt[0]);

		bt[1] = new Plane("/images/default/menu-game.png", 2, 2, 0, 124, 0,
				124, 124);
		bt[1].ry = -180;
		objects3d.add(bt[1]);

		bt[2] = new Plane("/images/default/menu-game.png", 2, 2, 0, 0, 124,
				124, 124);
		bt[2].rx = -180;
		objects3d.add(bt[2]);

		bt[3] = new Plane("/images/default/menu-game.png", 2, 2, 0, 124, 124,
				124, 124);
		bt[3].rx = -180;
		objects3d.add(bt[3]);

		postDelayed(new Runnable() {

			@Override
			public void run() {
				inTransition = true;
				rotate = true;
			}
		}, 500);
	}

	@Override
	public void onUpdate(float sElapsed) {

		super.onUpdate(sElapsed);

		if (rotate) {
			rotatePct = Math.max(0,
					Math.min(1, rotatePct + rotateDirection * sElapsed * 4f));
			float v = itpDec.getInterpolation(rotatePct);
			if (rotatePct == 1 && rotateDirection == 1) {
				rotatePct = 1;
				rotate = false;
				inTransition = false;
			} else if (rotatePct == 0 && rotateDirection == -1) {
				rotatePct = 0;
				rotate = false;
				postDelayed(new Runnable() {

					@Override
					public void run() {
						if (callbackExited != null) {
							callbackExited.exited(Item.values()[selectedButton + 1]);
						}
						view.removeScreen(1);
					}
				}, 200);
			}
			if (rotateDirection == -1 && selectedButton != -1) {
				anim.updateCamera(1 - rotatePct, posCamera, posLook,
						bt[selectedButton]);
			}
			float angle = (float) ((-180 - 180 * v) * Math.PI / 180);
			if (selectedButton != 0) {
				bt[0].x = 1 + (float) Math.cos(angle);
				bt[0].z = -(float) Math.sin(angle);
				bt[0].ry = -180 - 180 * v;
			}
			if (selectedButton != 1) {
				bt[1].ry = -180 + 180 * v;
				bt[1].x = -1 - (float) Math.cos(angle);
				bt[1].z = -(float) Math.sin(angle);
			}
			if (selectedButton != 2) {
				bt[2].rx = -180 - 180 * v;
				bt[2].y = -1 - (float) Math.cos(angle);
				bt[2].z = -(float) Math.sin(angle);
			}
			if (selectedButton != 3) {
				bt[3].rx = -180 + 180 * v;
				bt[3].y = 1 + (float) Math.cos(angle);
				bt[3].z = -(float) Math.sin(angle);
			}
		}
	}

	@Override
	public void onDraw3D(GL10 gl) {
		gl.glClearColor(0, 0, 0, 0);
		GLU.gluLookAt(gl, posCamera[0], posCamera[1], posCamera[2], posLook[0],
				posLook[1], posLook[2], 0, 0, 1);

		super.onDraw3D(gl);
	}

	@Override
	public void onTouch(MotionEvent event) {
		if (!inTransition && event.getAction() == MotionEvent.ACTION_UP) {
			ScreenProperty sp = view.getScreenProperty();
			float[] onBoard = SceneUtil.ScreenTo3D(event.getX(), event.getY(),
					sp.realWidth, sp.realHeight, posCamera,
					view.getMatrixGrabber(), 0);

			if (SceneUtil.getDistance(onBoard[0], onBoard[1], 0, centerBt.x,
					centerBt.y, 0) <= 1) {
				selectedButton = -1;
				rotateDirection = -1;
				rotate = true;
				inTransition = true;

				return;
			} else {
				for (int i = 0; i < bt.length; i++) {
					if (SceneUtil.getDistance(onBoard[0], onBoard[1], 0,
							bt[i].x, bt[i].y, 0) <= 1) {
						selectedButton = i;
						rotateDirection = -1;
						rotate = true;
						inTransition = true;

						return;
					}
				}
			}
			// Logger.i("x=" + onBoard[0] + "  y=" + onBoard[1]);
		}
	}

}
