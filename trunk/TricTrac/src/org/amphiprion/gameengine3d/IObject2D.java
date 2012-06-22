package org.amphiprion.gameengine3d;

import javax.microedition.khronos.opengles.GL10;

public interface IObject2D {
	void draw(GL10 gl, float screenScaleX, float screenScaleY, int screenWidth, int screenHeight);

	void setParent(IObject2D parent);

	void setX(int x);

	int getX();

	void setY(int y);

	int getY();

	void setScale(float scale);

	float getScale();
}
