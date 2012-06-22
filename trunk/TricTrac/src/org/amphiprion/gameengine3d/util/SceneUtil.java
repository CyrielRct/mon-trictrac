package org.amphiprion.gameengine3d.util;

import android.opengl.GLU;

public class SceneUtil {
	public static double getDistance(float p1x, float p1y, float p1z, float p2x, float p2y, float p2z) {
		return Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y) + (p1z - p2z) * (p1z - p2z));
	}

	public static double getDistance(float[] p1, float[] p2) {
		return getDistance(p1[0], p1[1], p1[2], p2[0], p2[1], p2[2]);
	}

	public static float[] ScreenTo3D(float screenX, float screenY, float screenWidth, float screenHeight, float[] posCamera, MatrixGrabber mg, float targetZ) {
		float[] ray = getViewRay(screenX, screenY, screenWidth, screenHeight, posCamera, mg);
		float mult = (-posCamera[2] + targetZ) / ray[2];
		// Log.d("OPENGL", "3D board x=" + (posCamera[0] + ray[0] * mult) +
		// "  y=" + (posCamera[1] + ray[1] * mult) + " z=" + (posCamera[2] +
		// ray[2] * mult));
		return new float[] { (posCamera[0] + ray[0] * mult), (posCamera[1] + ray[1] * mult), (posCamera[2] + ray[2] * mult) };
	}

	public static float[] getViewRay(float screenX, float screenY, float screenWidth, float screenHeight, float[] posCamera, MatrixGrabber mg) {

		// view port
		int[] viewport = { 0, 0, (int) screenWidth, (int) screenHeight };

		// far eye point
		float[] eye = new float[4];
		GLU.gluUnProject(screenX, screenHeight - screenY, 0, mg.mModelView, 0, mg.mProjection, 0, viewport, 0, eye, 0);

		// fix
		if (eye[3] != 0) {
			eye[0] = eye[0] / eye[3];
			eye[1] = eye[1] / eye[3];
			eye[2] = eye[2] / eye[3];
		}

		// ray vector
		float[] ray = { eye[0] - posCamera[0], eye[1] - posCamera[1], eye[2] - posCamera[2], 0.0f };

		return ray;
	}
}
