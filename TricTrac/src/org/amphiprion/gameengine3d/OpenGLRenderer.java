package org.amphiprion.gameengine3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.mesh.LabelMaker;
import org.amphiprion.gameengine3d.util.MatrixGrabber;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class OpenGLRenderer implements Renderer {
	public Runnable callbackOnFirstDraw;

	public boolean firstDraw = false;
	private MatrixGrabber mg;
	private float[] posStartCamera = new float[] { 0, -17.5f, 10f };
	private float angleStartCamera = 42;
	// ICameraAnimation cameraAnimation = new RearCameraAnimation();

	private float[] posCamera;
	private float[] posLook = new float[3];
	private float angleCamera;

	private Group root;
	private Group2D root2D;

	private long time = System.currentTimeMillis();
	private int width;
	private int height;
	// private Interpolator itp = new DecelerateInterpolator();
	private int referenceWidth;
	private int referenceHeight;
	private float screenScale = 1.0f;
	private LabelMaker labelMarker;
	private GameView view;

	// private int labelId;

	public OpenGLRenderer(Context context, MatrixGrabber mg, int referenceWidth, int referenceHeight, GameView view) {
		this.mg = mg;
		this.referenceWidth = referenceWidth;
		this.referenceHeight = referenceHeight;
		labelMarker = new LabelMaker(true, 1024, 64);
		this.view = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
	 * microedition.khronos.opengles.GL10, javax.microedition.khronos.
	 * egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);// OpenGL docs.
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
				GL10.GL_NICEST);
		labelMarker.initialize(gl);
		labelMarker.beginAdding(gl);
		// labelId = labelMarker.add(gl, "Coucou", new Paint());
		labelMarker.endAdding(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
	 * microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {

		// /////////////////////// OLD ////////////////
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		// /////////////////////////////// On remet
		// gl.glViewport(0, 0, width, height);// OpenGL docs.
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, (float) width / height, .1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.
		gl.glClearDepthf(1.0f);// OpenGL docs.
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.

		view.onDraw3D(gl);
		mg.getCurrentState(gl);
		long e = System.currentTimeMillis();
		float sElapsed = (e - time) / 1000.0f;
		if (sElapsed > 0.100f) {
			sElapsed = 0.07f;
		}
		view.onUpdate(sElapsed);
		time = e;

		draw2d(gl);

		// labelMarker.beginDrawing(gl, width, height);
		// labelMarker.draw(gl, 100, 100, labelId);
		// labelMarker.endDrawing(gl);
	}

	private void draw2d(GL10 gl) {
		// gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		// gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		// Magic offsets to promote consistent rasterization.
		gl.glTranslatef(0.375f, 0.375f, 0.0f);

		// root2D.draw(gl, screenScale, width, height);
		view.onDraw2D(gl);

		gl.glDisable(GL10.GL_BLEND);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
	 * microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		screenScale = Math.max((float) width / referenceWidth, (float) height / referenceHeight);
		updateScreenProperties();

		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL docs.

		// TODO je pense que c'est inutile maintenant
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, (float) width / height, 0.1f, 100.0f);
		// GLU.gluOrtho2D(gl, 0, width, height, 0);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.

	}

	public void updateScreenProperties() {
		if (view != null) {
			ScreenProperty sp = view.getScreenProperty();
			sp.screenScaleX = (float) width / referenceWidth;
			sp.screenScaleY = (float) height / referenceHeight;
			sp.realWidth = width;
			sp.realHeight = height;
		}
	}

	public MatrixGrabber getMatrixGrabber() {
		return mg;
	}
}
