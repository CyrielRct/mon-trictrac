package org.amphiprion.gameengine3d.animation;

import org.amphiprion.gameengine3d.IObject2D;

import android.view.animation.Interpolator;

public class Translation2DAnimation extends GameComponentAnimation {
	private float deltaX;
	private float deltaY;
	private float startX;
	private float endX;
	private float startY;
	private float endY;

	private Interpolator itp;
	private IObject2D object2d;

	public Translation2DAnimation(IObject2D image2d, long duration, long delay, float deltaX, float deltaY) {
		this.delay = delay;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.duration = duration;
		this.object2d = image2d;
	}

	@Override
	protected float getInterpolatedProgress(float progress) {
		if (itp != null) {
			return itp.getInterpolation(progress);
		} else {
			return super.getInterpolatedProgress(progress);
		}
	}

	@Override
	protected void onUpdate(float progress) {
		object2d.setX((int) ((endX - startX) * progress + startX));
		object2d.setY((int) ((endY - startY) * progress + startY));
	}

	@Override
	public void start() {
		startX = object2d.getX();
		endX = startX + deltaX;
		startY = object2d.getY();
		endY = startY + deltaY;
	}

	public void setInterpolation(Interpolator itp) {
		this.itp = itp;
	}
}
