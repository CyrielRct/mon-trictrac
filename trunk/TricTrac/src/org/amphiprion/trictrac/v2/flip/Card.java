package org.amphiprion.trictrac.v2.flip;

import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_CULL_FACE;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_ONE;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT;
import static javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

/*
 Copyright 2012 Aphid Mobile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

public class Card {

	private float cardVertices[];

	private short[] indices = { 0, 1, 2, 0, 2, 3 };

	private FloatBuffer vertexBuffer;

	private ShortBuffer indexBuffer;

	private float textureCoordinates[];

	private FloatBuffer textureBuffer;

	private Texture texture;

	protected float angle = 0f;

	private boolean top = false;

	private boolean dirty = false;

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setCardVertices(float[] cardVertices) {
		this.cardVertices = cardVertices;
		dirty = true;
	}

	public void setTextureCoordinates(float[] textureCoordinates) {
		this.textureCoordinates = textureCoordinates;
		dirty = true;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public void draw(GL10 gl) {
		if (dirty) {
			updateVertices();
		}

		if (cardVertices == null) {
			return;
		}

		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);

		gl.glEnableClientState(GL_VERTEX_ARRAY);

		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		// gl.glColor4f(1, 0, 0, 0.5f);
		// gl.glColorMask(true, true, true, true);

		if (Utils.isValidTexture(texture)) {
			gl.glEnable(GL_TEXTURE_2D);
			gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			// gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
			// GL_CLAMP_TO_EDGE);
			// gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
			// GL_CLAMP_TO_EDGE);
			gl.glTexCoordPointer(2, GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL_TEXTURE_2D, texture.getId()[0]);
		}

		// checkError(gl);

		gl.glPushMatrix();

		if (angle > 0) {
			if (top) {
				gl.glTranslatef(0, cardVertices[4], 0f);
				gl.glRotatef(angle, 1f, 0f, 0f);
				gl.glTranslatef(0, -cardVertices[4], 0f);
			} else {
				gl.glTranslatef(0, cardVertices[1], 0f);
				gl.glRotatef(-angle, 1f, 0f, 0f);
				gl.glTranslatef(0, -cardVertices[1], 0f);
			}
		}

		gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
		gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);

		// checkError(gl);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

		gl.glPopMatrix();

		if (Utils.isValidTexture(texture)) {
			gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL_TEXTURE_2D);
		}

		// SHADOW
		if (angle > 0) {
			gl.glDisable(GL_LIGHTING);
			gl.glDisable(GL_DEPTH_TEST);
			float w;
			float h;
			float z;
			float[] shadowVertices;
			w = cardVertices[9] - cardVertices[0];
			h = (cardVertices[1] - cardVertices[4]) * (1f - FloatMath.cos(Utils.d2r(angle)));
			z = (cardVertices[1] - cardVertices[4]) * FloatMath.sin(Utils.d2r(angle));
			if (top) {
				shadowVertices = new float[] { cardVertices[0], cardVertices[1], 0, cardVertices[3], cardVertices[1] - h, z, w, cardVertices[1] - h, z, w, cardVertices[1], 0 };
			} else {
				shadowVertices = new float[] { cardVertices[0], h + cardVertices[4], z, cardVertices[3], cardVertices[4], 0f, w, cardVertices[4], 0f, w, h + cardVertices[4], z };
			}

			float alpha = 1f * (90f - angle) / 90f;

			gl.glColor4f(0f, 0.0f, 0f, alpha);
			gl.glVertexPointer(3, GL_FLOAT, 0, Utils.toFloatBuffer(shadowVertices));
			gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
			gl.glEnable(GL_DEPTH_TEST);
			gl.glEnable(GL_LIGHTING);
			gl.glColor4f(1f, 1.0f, 1f, 1);
		}

		// checkError(gl);

		gl.glDisable(GL_BLEND);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisable(GL_CULL_FACE);
	}

	private void updateVertices() {
		vertexBuffer = Utils.toFloatBuffer(cardVertices);
		indexBuffer = Utils.toShortBuffer(indices);
		textureBuffer = Utils.toFloatBuffer(textureCoordinates);
	}

	public void setTop(boolean b) {
		top = b;
	}
}