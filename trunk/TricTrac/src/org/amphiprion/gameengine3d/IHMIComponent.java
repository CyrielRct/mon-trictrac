package org.amphiprion.gameengine3d;

public interface IHMIComponent extends IObject2D {
	void setEnable(boolean enable);

	boolean isEnable();

	boolean contains(int px, int py);
}
