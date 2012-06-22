package org.amphiprion.trictrac.v2.screen;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.GameScreen;
import org.amphiprion.gameengine3d.animation.Translation3DAnimation;
import org.amphiprion.gameengine3d.mesh.Plane;
import org.amphiprion.gameengine3d.util.TextureUtil;
import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.entity.Game;

import android.opengl.GLU;
import android.os.Environment;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

public class GameMenuScreen extends GameScreen {
	private float[] posCamera = new float[] { 0, -9f, 8f };
	private float[] posLook = new float[] { 0, 0, 0f };

	public GameMenuScreen() {
		startAll(null);
	}

	public void startAll(Game game) {
		objects3d.clear();
		TextureUtil.unloadAll();
		Plane b;
		if (game != null) {
			b = new Plane("file:" + Environment.getExternalStorageDirectory()
					+ "/" + ApplicationConstants.DIRECTORY + "/"
					+ game.getImageName(), 2, 2, 0, 0, 0, 124, 124);
		} else {
			b = new Plane("/images/default/neon100.png", 2, 2, 0, 0, 0, 124,
					124);
		}
		b.z = 10;
		objects3d.add(b);

		Translation3DAnimation t = new Translation3DAnimation(b, 500, 0, 0, 0,
				-b.z);
		t.setInterpolation(itpDec);
		addAnimation(t);

		n1 = new Plane("/images/default/menu-game.png", 2, 2, 0, 0, 0, 124, 124);
		n1.ry = -180;
		objects3d.add(n1);

		n2 = new Plane("/images/default/menu-game.png", 2, 2, 0, 124, 0, 124,
				124);
		n2.ry = -180;
		objects3d.add(n2);

		n3 = new Plane("/images/default/menu-game.png", 2, 2, 0, 0, 124, 124,
				124);
		n3.rx = -180;
		objects3d.add(n3);

		n4 = new Plane("/images/default/menu-game.png", 2, 2, 0, 124, 124, 124,
				124);
		n4.rx = -180;
		objects3d.add(n4);

		postDelayed(new Runnable() {

			@Override
			public void run() {
				rotate = true;
			}
		}, 500);
	}

	private Plane n1;
	private Plane n2;
	private Plane n3;
	private Plane n4;
	private boolean rotate;
	private float rotatePct;
	private BounceInterpolator itp = new BounceInterpolator();
	private DecelerateInterpolator itpDec = new DecelerateInterpolator();

	@Override
	public void onUpdate(float sElapsed) {

		super.onUpdate(sElapsed);

		if (rotate) {
			rotatePct = Math.min(1, rotatePct + sElapsed * 3f);
			float v = itpDec.getInterpolation(rotatePct);
			n1.ry = -180 - 180 * v;
			n2.ry = -180 + 180 * v;
			n3.rx = -180 - 180 * v;
			n4.rx = -180 + 180 * v;
			n1.mRGBA[3] = v;
			n2.mRGBA[3] = v;
			n3.mRGBA[3] = v;
			n4.mRGBA[3] = v;
			if (rotatePct == 1) {
				rotatePct = 0;
				rotate = false;
			}
			float angle = (float) (n1.ry * Math.PI / 180);
			n1.x = 1 + (float) Math.cos(angle);
			n1.z = -(float) Math.sin(angle);
			n2.x = -1 - (float) Math.cos(angle);
			n2.z = -(float) Math.sin(angle);
			n3.y = -1 - (float) Math.cos(angle);
			n3.z = -(float) Math.sin(angle);
			n4.y = 1 + (float) Math.cos(angle);
			n4.z = -(float) Math.sin(angle);
		}
	}

	@Override
	public void onDraw3D(GL10 gl) {
		gl.glClearColor(0, 0, 0, 0);
		GLU.gluLookAt(gl, posCamera[0], posCamera[1], posCamera[2], posLook[0],
				posLook[1], posLook[2], 0, 0, 1);

		super.onDraw3D(gl);
	}

}
