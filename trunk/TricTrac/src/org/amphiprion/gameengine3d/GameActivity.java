package org.amphiprion.gameengine3d;

import javax.microedition.khronos.opengles.GL;

import org.amphiprion.gameengine3d.util.MatrixGrabber;
import org.amphiprion.gameengine3d.util.MatrixTrackingGL;
import org.amphiprion.gameengine3d.util.TextureUtil;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	private MatrixGrabber mg = new MatrixGrabber();
	private OpenGLRenderer renderer;
	protected GameView view;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)

		view = new GameView(this, 768, 1280);
		renderer = new OpenGLRenderer(this, mg, 768, 1280, view);

		view.setGLWrapper(new GLSurfaceView.GLWrapper() {
			@Override
			public GL wrap(GL gl) {
				return new MatrixTrackingGL(gl);
			}
		});
		view.setRenderer(renderer);

		setContentView(view);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		view.onTouch(event);
		return true;
	}

	@Override
	protected void onDestroy() {
		TextureUtil.unloadAll();
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {
		if (view.backRequested()) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}
}