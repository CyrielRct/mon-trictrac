package org.amphiprion.gameengine3d;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.util.Velocity;

public interface IObject extends IPosition {
	void draw(GL10 gl);

	void setParent(IObject parent);

	IObject getParent();

	void update(float sElapsed, List<IObject> collidableMesh);

	boolean isMoving();

	Velocity getVelocity();

	void setVelocity(float vx, float vy, float vz, float sElapsed);

	// void unloadTexture();
}
