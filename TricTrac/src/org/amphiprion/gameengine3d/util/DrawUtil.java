package org.amphiprion.gameengine3d.util;

import org.amphiprion.gameengine3d.ScreenProperty;

import android.graphics.PointF;

public class DrawUtil {

	public static double getDistance(PointF p1, PointF p2) {
		return getDistance(p1.x, p1.y, p2.x, p2.y);
	}

	public static double getDistance(float p1x, float p1y, float p2x, float p2y) {
		return Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
	}

	/**
	 * return angle between -180 and 180
	 * 
	 * @param p
	 * @param center
	 * @return
	 */
	public static float getAngle(PointF p, PointF center) {
		return getAngle(p.x, p.y, center.x, center.y);
	}

	public static float getAngle(float x, float y, float ox, float oy) {
		return (float) (Math.atan2(y - oy, x - ox) * 180 / Math.PI);
	}

	public static float getDifferenceAngle(float angle1, float angle2) {
		float dif = (angle2 - angle1) % 360; // in range
		if (angle1 > angle2) {
			dif += 360;
		}
		if (dif >= 180) {
			dif = -(dif - 360);
		}
		return dif;
	}

	public static float convertX(float x, float y, ScreenProperty screenProperty) {
		float newX = x;
		if (screenProperty.screenRotation == 90) {
			newX = y;
		}
		newX /= screenProperty.screenScaleX;
		newX += screenProperty.screenOffsetX;
		return newX;
	}

	public static float convertY(float x, float y, ScreenProperty screenProperty) {
		float newY = y;
		if (screenProperty.screenRotation == 90) {
			newY = screenProperty.realWidth - x;
		}
		newY /= screenProperty.screenScaleY;
		newY += screenProperty.screenOffsetY;
		return newY;
	}
}
