package org.amphiprion.gameengine3d.mesh;

public class Cylinder extends Mesh {
	public Cylinder(String uri, int slices, float halfLength, float radius) {
		super(uri);
		// slices = 4;
		int nbVertices = 2 + slices; // up
		nbVertices += slices; // side

		float[] vertices = new float[nbVertices * 3];
		float[] textureCoordinates = new float[nbVertices * 2];

		int nbIndices = slices; // up
		nbIndices += slices * 2; // side
		short[] indices = new short[nbIndices * 3]; // indice of each
													// triangles

		short indexIndice = 0;
		short indexVertex = 0;

		vertices[indexVertex] = 0.0f;
		vertices[indexVertex + 1] = 0.0f;
		vertices[indexVertex + 2] = halfLength;
		textureCoordinates[indexVertex] = 0.5f;
		textureCoordinates[indexVertex + 1] = 0.5f;
		indices[0] = 0;

		indexVertex++;
		indexIndice++;

		vertices[indexVertex * 3] = radius;
		vertices[indexVertex * 3 + 1] = 0.0f;
		vertices[indexVertex * 3 + 2] = halfLength;
		textureCoordinates[indexVertex * 2] = 1.0f;
		textureCoordinates[indexVertex * 2 + 1] = 0.5f;
		indices[indexIndice] = indexIndice;

		indexVertex++;
		indexIndice++;

		// up
		double angle = 2 * Math.PI / slices;
		for (int i = 0; i < slices; i++) {
			float nextTheta = (float) angle * (i + 1);

			vertices[indexVertex * 3] = (float) (radius * Math.cos(nextTheta));
			vertices[indexVertex * 3 + 1] = (float) (radius * Math.sin(nextTheta));
			vertices[indexVertex * 3 + 2] = halfLength;
			textureCoordinates[indexVertex * 2] = (radius + vertices[indexVertex * 3]) / (2 * radius);
			textureCoordinates[indexVertex * 2 + 1] = -((radius + vertices[indexVertex * 3 + 1]) / (2 * radius) - 0.5f) + 0.5f;

			indices[indexIndice] = indexVertex;
			indexIndice++;
			if (i + 1 < slices) {
				indices[indexIndice] = 0;
				indexIndice++;
				indices[indexIndice] = indexVertex;
				indexIndice++;
			}
			indexVertex++;

		}

		// side
		for (int i = 0; i < slices; i++) {
			float nextTheta = (float) (angle * i);

			vertices[indexVertex * 3] = (float) (radius * Math.cos(nextTheta));
			vertices[indexVertex * 3 + 1] = (float) (radius * Math.sin(nextTheta));
			vertices[indexVertex * 3 + 2] = -halfLength;
			textureCoordinates[indexVertex * 2] = (radius + vertices[indexVertex * 3]) / (2 * radius);
			textureCoordinates[indexVertex * 2 + 1] = -((radius + vertices[indexVertex * 3 + 1]) / (2 * radius) - 0.5f) + 0.5f;

			indices[indexIndice] = (short) (i + 1);
			indices[indexIndice + 1] = (short) (slices + 2 + i);
			if (i + 1 < slices) {
				indices[indexIndice + 2] = (short) (slices + 2 + i + 1);
			} else {
				indices[indexIndice + 2] = (short) (slices + 2);
			}
			indexIndice += 3;
			indices[indexIndice] = (short) (i + 1);
			if (i + 1 < slices) {
				indices[indexIndice + 1] = (short) (slices + 2 + i + 1);
			} else {
				indices[indexIndice + 1] = (short) (slices + 2);
			}
			indices[indexIndice + 2] = (short) (i + 2);
			indexIndice += 3;

			indexVertex++;

		}

		setIndices(indices);
		setVertices(vertices);

		setTextureCoordinates(textureCoordinates);

	}
}
