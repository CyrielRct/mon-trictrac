/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of MansionOfMadnessToolKit.
 *
 * MansionOfMadnessToolKit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MansionOfMadnessToolKit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MansionOfMadnessToolKit.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.gameengine3d.mesh;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.util.TextureUtil;

/**
 * @author Amphiprion
 * 
 */
public class FreeText2D extends Image2D {
	private int size = 16;

	public FreeText2D(String text, int size, Alignment alignment) {
		this(text);
		this.size = size;
		this.alignment = alignment;
	}

	public FreeText2D(String text) {
		super("@String/" + text);
	}

	public void setText(String text) {
		uri = "@String/" + text;
		texture = null;
	}

	@Override
	protected void loadGLTexture(GL10 gl) { // New function
		texture = TextureUtil.loadTexture(uri, gl, size);
		float[] vertices = {
				// X, Y
				0, 0, 0.2f, 0, (texture.originalHeight - 1) / 100.0f, 0.2f, (texture.originalWidth - 1) / 100.0f, (texture.originalHeight - 1) / 100.0f, 0.2f,
				(texture.originalWidth - 1) / 100.0f, 0, 0.2f };
		setVertices(vertices);
	}

}
