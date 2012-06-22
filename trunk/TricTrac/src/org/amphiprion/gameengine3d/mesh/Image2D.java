package org.amphiprion.gameengine3d.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import org.amphiprion.gameengine3d.IHMIComponent;
import org.amphiprion.gameengine3d.IObject2D;
import org.amphiprion.gameengine3d.util.Texture;
import org.amphiprion.gameengine3d.util.TextureUtil;

public class Image2D implements IObject2D, IHMIComponent {
	private boolean enable = true;

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	// Our UV texture buffer.
	private FloatBuffer textureBuffer; // New variable.

	// The bitmap we want to load as a texture.
	protected String uri; // New variable.
	protected Texture texture;

	// Indicates if we need to load the texture.

	// Our texture id.
	public int x;
	public int y;
	private IObject2D parent;
	private float scale = 1.0f;

	public enum Alignment {
		CENTER, LEFT
	}

	protected Alignment alignment = Alignment.CENTER;

	/**
	 * 
	 * @param bitmap
	 * @param contentWidth
	 *            the content bitmap width (in opposition to the real width that
	 *            must be a power of 2). the content must be centered inside the
	 *            image
	 * @param contentHeight
	 *            the content bitmap height (in opposition to the real height
	 *            that must be a power of 2). the content must be centered
	 *            inside the image
	 */
	public Image2D(String uri) {
		this.uri = uri;

		// this.contentWidth = contentWidth;
		// this.contentHeight = contentHeight;
		//
		// int mStrikeWidth = 1;
		// while (mStrikeWidth < contentWidth) {
		// mStrikeWidth <<= 1;
		// }
		//
		// int mStrikeHeight = 1;
		// while (mStrikeHeight < contentHeight) {
		// mStrikeHeight <<= 1;
		// }
		//
		// Bitmap.Config config = Bitmap.Config.ARGB_4444;
		// mBitmap = Bitmap.createBitmap(mStrikeWidth, mStrikeHeight, config);
		// Canvas mCanvas = new Canvas(mBitmap);
		// mCanvas.drawBitmap(bitmap, 0, 0, new Paint());
		// mCanvas = null;

		float textureCoordinates[] = { 0.0f, 0.0f, //
				0.0f, 1.0f, //
				1.0f, 1.0f, //
				1.0f, 0.0f, //
		};
		// Triangle Strip definition:
		short[] indices = { 0, 1, 2, 2, 3, 0 };

		setIndices(indices);

		setTextureCoordinates(textureCoordinates);
		// bitmap = null;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

	protected void setVertices(float[] coords) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(coords);
		vertexBuffer.position(0);
	}

	private void setIndices(short[] vertex_strip) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(vertex_strip.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(vertex_strip);
		indexBuffer.position(0);
		// mNumOfIndices = vertex_strip.length;
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
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
	}

	/**
	 * Loads the texture.
	 * 
	 * @param gl
	 */
	protected void loadGLTexture(GL10 gl) { // New function
		texture = TextureUtil.loadTexture(uri, gl);
		float[] vertices = {
				// X, Y
				0, 0, 0.2f, 0, (texture.originalHeight - 1) / 100.0f, 0.2f, (texture.originalWidth - 1) / 100.0f, (texture.originalHeight - 1) / 100.0f, 0.2f,
				(texture.originalWidth - 1) / 100.0f, 0, 0.2f };
		setVertices(vertices);
	}

	@Override
	public void draw(GL10 gl, float screenScaleX, float screenScaleY, int screenWidth, int screenHeight) {
		// Log.d("OPENGL", "w=" + screenWidth + "    h=" + screenHeight +
		// "  cc=" + contentHeight + "  scale=" + screenScale);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// Set VERTEX buffer (see that we specify 2 coords per element
		// instead 3
		// because we have defined coords as 2 elements

		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		gl.glPushMatrix();
		// gl.glTranslatef(x * screenScale, y * screenScale, 0); // X and Y are
		// real pixel
		// coords

		// gl.glScalef(screenScale, screenScale, 1);
		if (texture == null || !texture.loaded) {
			loadGLTexture(gl);
		}
		if (!enable) {
			// gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
			// GL10.GL_MODULATE);
			gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);
		} else {
			// gl.glColor4f(1f, 1f, 1f, 1f);
		}
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		if (texture != null && textureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureId);
		}
		int[] crop = new int[4];
		crop[0] = 0;
		crop[1] = 0 + texture.originalHeight;
		crop[2] = texture.originalWidth;
		crop[3] = -texture.originalHeight;
		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop, 0);
		// ((GL11Ext) gl).glDrawTexiOES(0, 0, 0, 212, 401);

		if (alignment == Alignment.CENTER) {
			((GL11Ext) gl).glDrawTexiOES((int) ((x - texture.originalWidth * scale / 2.0f) * screenScaleX), (int) (screenHeight + (-texture.originalHeight * scale / 2.0f - y)
					* screenScaleY), 0, (int) (texture.originalWidth * screenScaleX * scale), (int) (texture.originalHeight * screenScaleY * scale));
		} else {
			((GL11Ext) gl).glDrawTexiOES((int) (x * screenScaleX), (int) (screenHeight + (-texture.originalHeight * scale / 2.0f - y) * screenScaleY), 0,
					(int) (texture.originalWidth * screenScaleX * scale), (int) (texture.originalHeight * screenScaleY * scale));
		}
		// gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, mNumOfIndices,
		// GL10.GL_UNSIGNED_SHORT, indexBuffer);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		gl.glColor4f(1f, 1f, 1f, 1f);

		gl.glPopMatrix();

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
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public boolean contains(int px, int py) {
		if (px < x - texture.originalWidth / 2) {
			return false;
		}
		if (px > x + texture.originalWidth / 2) {
			return false;
		}
		if (py < y - texture.originalHeight / 2) {
			return false;
		}
		if (py > y + texture.originalHeight / 2) {
			return false;
		}
		return true;
	}
}
