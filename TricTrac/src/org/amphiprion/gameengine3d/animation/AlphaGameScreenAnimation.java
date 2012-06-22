package org.amphiprion.gameengine3d.animation;

import org.amphiprion.gameengine3d.GameScreen;

import android.view.animation.Interpolator;

public class AlphaGameScreenAnimation extends GameComponentAnimation {
	private float[] startRGBA;
	private float[] endRGBA;

	private Interpolator itp;
	private GameScreen screen;

	public AlphaGameScreenAnimation(GameScreen screen, long duration, long delay, float[] startRGBA, float[] endRGBA) {
		this.delay = delay;
		this.startRGBA = startRGBA;
		this.endRGBA = endRGBA;
		this.duration = duration;
		this.screen = screen;
	}

	public AlphaGameScreenAnimation(GameScreen screen, long duration, long delay, float[] startRGBA, float[] endRGBA, Interpolator interpolator) {
		this(screen, duration, delay, startRGBA, endRGBA);
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
		screen.maskColorRed = (endRGBA[0] - startRGBA[0]) * progress + startRGBA[0];
		screen.maskColorGreen = (endRGBA[1] - startRGBA[1]) * progress + startRGBA[1];
		screen.maskColorBlue = (endRGBA[2] - startRGBA[2]) * progress + startRGBA[2];
		screen.maskColorAlpha = (endRGBA[3] - startRGBA[3]) * progress + startRGBA[3];
	}

	@Override
	public void start() {
	}

	public void setInterpolation(Interpolator itp) {
		this.itp = itp;
	}
}
