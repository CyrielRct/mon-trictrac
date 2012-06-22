package org.amphiprion.gameengine3d.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.gameengine3d.IObject;
import org.amphiprion.gameengine3d.util.Texture;
import org.amphiprion.gameengine3d.util.TextureUtil;
import org.amphiprion.gameengine3d.util.Velocity;

/**
 * Mesh is a base class for 3D objects making it easier to create and maintain
 * new primitives.
 * 
 * @author Per-Erik Bergman (per-erik.bergman@jayway.com)
 * 
 */
public class Mesh implements IObject {
	// Our vertex buffer.
	private FloatBuffer mVerticesBuffer = null;

	// Our index buffer.
	private ShortBuffer mIndicesBuffer = null;

	// Our UV texture buffer.
	private FloatBuffer mTextureBuffer; // New variable.

	// The bitmap we want to load as a texture.
	private String uri; // New variable.
	public Texture texture; // New variable.

	// The number of indices.
	private int mNumOfIndices = -1;

	// Flat Color
	public final float[] mRGBA = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// Smooth Colors
	private FloatBuffer mColorBuffer = null;

	// Translate params.
	public float x = 0;

	public float y = 0;

	public float z = 0;

	// Rotate params.
	public float rx = 0;

	public float ry = 0;

	public float rz = 0;

	public Velocity v = new Velocity();

	private GL10 gl;
	private int nbDimension;
	protected IObject parent;

	public Mesh(String uri) {
		this(3);
		this.uri = uri;

	}

	public Mesh(int nbDimension) {
		this.nbDimension = nbDimension;
	}

	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	@Override
	public void draw(GL10 gl) {
		this.gl = gl;
		gl.glPushMatrix();

		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(nbDimension, GL10.GL_FLOAT, 0, mVerticesBuffer);
		// Set flat color
		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
		// Smooth color
		if (mColorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		}

		// New part...
		if (texture == null || !texture.loaded) {
			loadGLTexture(gl);
		}
		if (texture != null && mTextureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureId);
		}
		// ... end new part.

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices, GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		// New part...
		if (texture != null && mTextureBuffer != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		// ... end new part.

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glPopMatrix();
	}

	@Override
	public boolean isMoving() {
		if (v == null) {
			return false;
		}
		return v.isMoving();
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVerticesBuffer = vbb.asFloatBuffer();
		mVerticesBuffer.put(vertices);
		mVerticesBuffer.position(0);
	}

	/**
	 * Set the indices.
	 * 
	 * @param indices
	 */
	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndicesBuffer = ibb.asShortBuffer();
		mIndicesBuffer.put(indices);
		mIndicesBuffer.position(0);
		mNumOfIndices = indices.length;
	}

	/**
	 * Set the texture coordinates.
	 * 
	 * @param textureCoords
	 */
	protected void setTextureCoordinates(float[] textureCoords) { // New
																	// function.
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoords.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}

	/**
	 * Set one flat color on the mesh.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	protected void setColor(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
	}

	/**
	 * Set the colors
	 * 
	 * @param colors
	 */
	protected void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}

	/**
	 * Loads the texture.
	 * 
	 * @param gl
	 */
	protected void loadGLTexture(GL10 gl) { // New function
		texture = TextureUtil.loadTexture(uri, gl);
	}

	@Override
	public void update(float sElapsed, List<IObject> collidableMesh) {
		v.updateMesh(this, sElapsed);
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
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public Velocity getVelocity() {
		return v;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public void setVelocity(float v0x, float v0y, float v0z, float sElapsed) {
		v.setVelocity(v0x, v0y, v0z, sElapsed);
	}

	public void setVelocity(float[] v0, float sElapsed) {
		this.setVelocity(v0[0], v0[1], v0[2], sElapsed);
	}

	// @Override
	// public void unloadTexture() {
	// if (gl != null) {
	// int[] textures = new int[1];
	// textures[0] = mTextureId;
	// gl.glDeleteTextures(1, textures, 0);
	// }
	// }
}
