package org.amphiprion.gameengine3d.util;

import org.amphiprion.gameengine3d.IPosition;

public class Velocity {
	private float[] v0 = new float[3];
	private float[] v = new float[3];
	private float v0abs = 0;
	private float t;
	private float[] a = new float[3];
	public float friction = 1.5f;

	public void setVelocity(float v0x, float v0y, float v0z, float sElapsed) {
		if (sElapsed != 0) {
			a[0] = (v0x - v0[0]) / sElapsed;
			a[1] = (v0y - v0[1]) / sElapsed;
			a[2] = (v0z - v0[2]) / sElapsed;
		}
		v0[0] = v0x;
		v0[1] = v0y;
		v0[2] = v0z;
		v0abs = (float) Math.sqrt(v0x * v0x + v0y * v0y + v0z * v0z);
		v[0] = v0[0];
		v[1] = v0[1];
		v[2] = v0[2];
		t = 0;
	}

	public void setVelocity(float[] v0, float sElapsed) {
		setVelocity(v0[0], v0[1], v0[2], sElapsed);
	}

	public boolean isMoving() {
		return v0abs != 0;
	}

	public void updateMesh(IPosition mesh, float sElapsed) {
		if (v0abs == 0) {
			return;
		}
		t += sElapsed;
		float vRatio = (float) Math.exp(-friction * t);
		float vx = v0[0] * vRatio;
		float vy = v0[1] * vRatio;
		float vz = v0[2] * vRatio;
		if (sElapsed != 0) {
			a[0] = (vx - v[0]) / sElapsed;
			a[1] = (vy - v[1]) / sElapsed;
			a[2] = (vz - v[2]) / sElapsed;
		}
		v[0] = vx;
		v[1] = vy;
		v[2] = vz;
		mesh.setX(mesh.getX() + v[0] * sElapsed);
		mesh.setY(mesh.getY() + v[1] * sElapsed);
		mesh.setZ(mesh.getZ() + v[2] * sElapsed);

		if (v0abs * vRatio < 0.0453f) {
			setVelocity(0, 0, 0, 0);
		}

	}

	public float getX() {
		return v[0];
	}

	public float getY() {
		return v[1];
	}

	public float getZ() {
		return v[2];
	}

	public float getV0() {
		return v0abs;
	}

	public float[] getAcceleration() {
		return a;
	}

	public void copyInto(Velocity clone) {
		clone.v0[0] = v0[0];
		clone.v0[1] = v0[1];
		clone.v0[2] = v0[2];

		clone.v[0] = v[0];
		clone.v[1] = v[1];
		clone.v[2] = v[2];

		clone.v0abs = v0abs;
		clone.t = t;

		clone.a[0] = a[0];
		clone.a[1] = a[1];
		clone.a[2] = a[2];

	}

	public void copyInto(Velocity clone, float factor) {
		clone.v0[0] = v0[0] * factor;
		clone.v0[1] = v0[1] * factor;
		clone.v0[2] = v0[2] * factor;

		clone.v[0] = v[0] * factor;
		clone.v[1] = v[1] * factor;
		clone.v[2] = v[2] * factor;

		clone.v0abs = v0abs * factor;
		clone.t = t;

		clone.a[0] = a[0] * factor;
		clone.a[1] = a[1] * factor;
		clone.a[2] = a[2] * factor;

	}

}
