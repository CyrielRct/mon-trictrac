package org.amphiprion.gameengine3d.mesh;

import javax.microedition.khronos.opengles.GL10;

public class Plane extends Mesh {
	private float[] vertices;
	private float textureCoordinates[];
	// The order we like to connect them.
	private short[] indices = { 0, 1, 2, 0, 2, 3, 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0 };

	public Plane(String uri, float xLenght, float yLength, float zLength, float textureOffX, float textureOffY) {
		super(uri);
		vertices = new float[] { -xLenght / 2.0f, yLength / 2.0f, zLength / 2.0f, // 0,
																					// Top
																					// Left
				-xLenght / 2.0f, -yLength / 2.0f, zLength / 2.0f, // 1, Bottom
																	// Left
				xLenght / 2.0f, -yLength / 2.0f, zLength / 2.0f, // 2, Bottom
																	// Right
				xLenght / 2.0f, yLength / 2.0f, zLength / 2.0f, // 3, Top Right

				-xLenght / 2.0f, yLength / 2.0f, -zLength / 2.0f, // 0,
				// Top
				// Left
				-xLenght / 2.0f, -yLength / 2.0f, -zLength / 2.0f, // 1, Bottom
				// Left
				xLenght / 2.0f, -yLength / 2.0f, -zLength / 2.0f, // 2, Bottom
				// Right
				xLenght / 2.0f, yLength / 2.0f, -zLength / 2.0f, // 3, Top Right

		};

		setIndices(indices);
		setVertices(vertices);
		// setTextureCoordinates(textureCoordinates);
	}

	@Override
	protected void loadGLTexture(GL10 gl) {
		super.loadGLTexture(gl);
		textureCoordinates = new float[] { 0, 0, //
				0, (float) texture.originalHeight / texture.height, //
				(float) texture.originalWidth / texture.width, (float) texture.originalHeight / texture.height, //
				(float) texture.originalWidth / texture.width, 0.0f, //
				0.0f, 0.0f, //
				0.0f, 1.0f, //
				1.0f, 1.0f, //
				1.0f, 0.0f, };
		setTextureCoordinates(textureCoordinates);
	}
}
