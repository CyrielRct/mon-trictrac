package org.amphiprion.gameengine3d.animation;

import org.amphiprion.gameengine3d.IObject2D;

import android.view.animation.Interpolator;

public class Scale2DAnimation extends GameComponentAnimation {
	private float start;
	private float end;

	private Interpolator itp;
	private IObject2D object2d;

	public Scale2DAnimation(IObject2D image2d, long duration, long delay, float start, float end) {
		this.delay = delay;
		this.start = start;
		this.end = end;
		this.duration = duration;
		object2d = image2d;
	}

	public Scale2DAnimation(IObject2D image2d, long duration, long delay, float start, float end, Interpolator interpolator) {
		this(image2d, duration, delay, start, end);
		setInterpolation(interpolator);
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
		object2d.setScale(((end - start) * progress + start));
	}

	@Override
	public void start() {
	}

	public void setInterpolation(Interpolator itp) {
		this.itp = itp;
	}
}
