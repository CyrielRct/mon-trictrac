package org.amphiprion.gameengine3d;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Group2D implements IObject2D {
	private List<IObject2D> objects = new ArrayList<IObject2D>();
	private IObject2D parent;
	private int x;
	private int y;

	public void addObject(IObject2D o) {
		objects.add(o);
		o.setParent(this);
	}

	public void removeObject(IObject2D o) {
		objects.remove(o);
	}

	@Override
	public void draw(GL10 gl, float screenScaleX, float screenScaleY, int screenWidth, int screenHeight) {
		// for (int i = objects.size() - 1; i >= 0; i--) {
		List<IObject2D> clones = new ArrayList<IObject2D>(objects);
		for (IObject2D o : clones) {
			o.draw(gl, screenScaleX, screenScaleY, screenWidth, screenHeight);
		}
		clones.clear();
		clones = null;
	}

	@Override
	public void setParent(IObject2D parent) {
		this.parent = parent;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		int diff = x - this.x;
		for (IObject2D o : objects) {
			o.setX(o.getX() + diff);
		}
		this.x = x;
	}

	@Override
	public void setY(int y) {
		int diff = y - this.y;
		for (IObject2D o : objects) {
			o.setY(o.getY() + diff);
		}
		this.y = y;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setScale(float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getScale() {
		// TODO Auto-generated method stub
		return 0;
	}

}
