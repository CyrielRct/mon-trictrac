package org.amphiprion.gameengine3d.animation;


public abstract class GameComponentAnimation {
	protected long totalElapsed;
	protected long delay;
	protected long duration;
	protected boolean loop = false;
	protected boolean forwardAndBack = false;
	protected boolean backward;
	protected long pauseBeforeLoop = 0;
	protected long pauseBeforeBackward = 0;
	private boolean removeComponentAtEnd = false;

	public abstract void start();

	public final boolean onUpdate(long elapsed) {
		boolean running = true;
		if (duration > 0) {
			if (delay > 0) {
				delay -= elapsed;
				if (delay < 0) {
					if (!backward) {
						totalElapsed += -delay;
					} else {
						totalElapsed += delay;
					}
					delay = 0;
				}
			} else {
				if (!backward) {
					totalElapsed += elapsed;
				} else {
					totalElapsed += -elapsed;
				}
			}
			if (delay == 0) {
				if (totalElapsed > 0) {
					if (totalElapsed < duration) {
						float progress = getInterpolatedProgress((float) totalElapsed / duration);
						onUpdate(progress);
					} else {
						onUpdate(getInterpolatedProgress(1.0f));
						if (!loop && !forwardAndBack) {
							duration = 0;
							running = false;
						} else if (forwardAndBack) {
							backward = true;
							totalElapsed = duration;
							delay = pauseBeforeBackward;
						} else if (loop) {
							totalElapsed = 0;
							delay = pauseBeforeLoop;
						}
					}
				} else if (backward) {
					onUpdate(getInterpolatedProgress(0.0f));
					totalElapsed = 0;
					backward = false;
					if (!loop) {
						running = false;
					} else {
						delay = pauseBeforeLoop;
					}
				}
			}
		} else {
			running = false;
		}
		return running;
	}

	protected float getInterpolatedProgress(float progress) {
		return progress;
	}

	protected abstract void onUpdate(float progress);

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void setForwardAndBack(boolean forwardAndBack) {
		this.forwardAndBack = forwardAndBack;
	}

	public void setPauseBeforeBackward(long pauseBeforeBackward) {
		this.pauseBeforeBackward = pauseBeforeBackward;
	}

	public void setPauseBeforeLoop(long pauseBeforeLoop) {
		this.pauseBeforeLoop = pauseBeforeLoop;
	}

	public boolean isRemoveComponentAtEnd() {
		return removeComponentAtEnd;
	}

	public void setRemoveComponentAtEnd(boolean removeComponentAtEnd) {
		this.removeComponentAtEnd = removeComponentAtEnd;
	}

}
