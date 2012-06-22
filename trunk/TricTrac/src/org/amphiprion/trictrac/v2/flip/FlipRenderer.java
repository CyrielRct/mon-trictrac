package org.amphiprion.trictrac.v2.flip;

import static javax.microedition.khronos.opengles.GL10.GL_AMBIENT;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_LEQUAL;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHT0;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_NICEST;
import static javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.microedition.khronos.opengles.GL10.GL_POSITION;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;
import static javax.microedition.khronos.opengles.GL10.GL_SMOOTH;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.View;

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
public class FlipRenderer implements GLSurfaceView.Renderer {

	private FlipViewGroup flipViewGroup;

	private FlipCards cards;

	private float angle;

	public FlipRenderer(FlipViewGroup flipViewGroup) {
		this.flipViewGroup = flipViewGroup;

		cards = new FlipCards();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}

	public static float[] light0Position = { 0, 0, 100f, 0f };

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		float fovy = 20f;
		float eyeZ = height / 2f / (float) Math.tan(Utils.d2r(fovy / 2));

		GLU.gluPerspective(gl, fovy, (float) width / (float) height, 0.5f, Math.max(1500.0f, eyeZ));

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, width / 2.0f, height / 2f, eyeZ, width / 2.0f, height / 2.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);

		float lightAmbient[] = new float[] { 3.5f, 3.5f, 3.5f, 1f };
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);

		light0Position = new float[] { 0, 0, eyeZ, 0f };
		gl.glLightfv(GL_LIGHT0, GL_POSITION, light0Position, 0);

		// Logger.i(String.format("onSurfaceChanged: %d, %d", width, height));
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// if (cards.angle < 180) {
		// cards.angle += 1;
		// }
		if (animatedDirection != 0) {
			setAngle(getAngle() + 3 * animatedDirection);
		}
		cards.draw(gl);
		// Logger.i("COUCOU:" + System.currentTimeMillis());
	}

	public void updateTexture(View firstView, View secondView) {
		cards.reloadTexture(firstView, secondView);
		// flipViewGroup.getSurfaceView().requestRender();
		// Logger.i("texture:" + System.currentTimeMillis());
	}

	public static void checkError(GL10 gl) {
		int error = gl.glGetError();
		if (error != 0) {
			throw new RuntimeException(GLU.gluErrorString(error));
		}
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float f) {
		angle = Math.min(180, Math.max(0, f));
		cards.angle = angle;
		if (animatedDirection != 0 && callbackOnAngleUpdate != null) {
			callbackOnAngleUpdate.run();
		}
	}

	public int animatedDirection;
	public Runnable callbackOnAngleUpdate;

	public void destroyTexture() {
		cards.destroyTexture();
	}
}