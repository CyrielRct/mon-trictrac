package org.amphiprion.gameengine3d.animation;

import org.amphiprion.gameengine3d.IObject;

import android.view.animation.Interpolator;

public class Translation3DAnimation extends GameComponentAnimation {
	private float deltaX;
	private float deltaY;
	private float deltaZ;
	private float startX;
	private float endX;
	private float startY;
	private float endY;
	private float startZ;
	private float endZ;

	private Interpolator itp;
	private IObject object3d;

	public Translation3DAnimation(IObject image3d, long duration, long delay, float deltaX, float deltaY, float deltaZ) {
		this.delay = delay;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.deltaZ = deltaZ;
		this.duration = duration;
		object3d = image3d;
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
		if (deltaX != 0) {
			object3d.setX(((endX - startX) * progress + startX));
		}
		if (deltaY != 0) {
			object3d.setY(((endY - startY) * progress + startY));
		}
		if (deltaZ != 0) {
			object3d.setZ(((endZ - startZ) * progress + startZ));
		}
	}

	@Override
	public void start() {
		startX = object3d.getX();
		endX = startX + deltaX;
		startY = object3d.getY();
		endY = startY + deltaY;
		startZ = object3d.getZ();
		endZ = startZ + deltaZ;
	}

	public void setInterpolation(Interpolator itp) {
		this.itp = itp;
	}
}
