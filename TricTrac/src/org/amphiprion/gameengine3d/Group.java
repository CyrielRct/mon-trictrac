package org.amphiprion.gameengine3d;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.util.Velocity;

public class Group implements IObject {
	private List<IObject> objects = new ArrayList<IObject>();
	private IObject parent;

	public void addObject(IObject o) {
		objects.add(o);
		o.setParent(this);
	}

	public void removeObject(IObject o) {
		objects.remove(o);
		// o.unloadTexture();
	}

	@Override
	public void draw(GL10 gl) {
		for (int i = objects.size() - 1; i >= 0; i--) {
			objects.get(i).draw(gl);
		}
	}

	@Override
	public void update(float sElapsed, List<IObject> collidableMesh) {
		for (int i = objects.size() - 1; i >= 0; i--) {
			objects.get(i).update(sElapsed, collidableMesh);
		}
	}

	@Override
	public boolean isMoving() {
		boolean r = false;
		for (int i = objects.size() - 1; i >= 0; i--) {
			r = r || objects.get(i).isMoving();
		}
		return r;
	}

	@Override
	public void setParent(IObject parent) {
		this.parent = parent;
	}

	@Override
	public IObject getParent() {
		return parent;
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public float getZ() {
		return 0;
	}

	@Override
	public Velocity getVelocity() {
		return null;
	}

	@Override
	public void setVelocity(float vx, float vy, float vz, float sElapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setX(float x) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setY(float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setZ(float z) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void unloadTexture() {
	// // TODO Auto-generated method stub
	//
	// }

	public List<IObject> getObjects() {
		return objects;
	}
}
