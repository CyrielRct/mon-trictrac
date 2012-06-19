package org.amphiprion.trictrac.v2.flip;

import static org.amphiprion.trictrac.v2.flip.FlipRenderer.checkError;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.view.View;

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

public class FlipCards {
	public float angle;
	public boolean mustDestroyTexture;

	private Texture firstTexture;
	private Texture secondTexture;
	private Bitmap firstBitmap;
	private Bitmap secondBitmap;

	private Card firstTopCard;
	private Card firstBottomCard;
	private Card secondTopCard;
	private Card secondBottomCard;

	public FlipCards() {
		firstTopCard = new Card();
		firstBottomCard = new Card();
		secondTopCard = new Card();
		secondBottomCard = new Card();

		// firstTopCard.setAnimating(true);
		firstTopCard.setTop(true);
		secondTopCard.setTop(true);
		// bottomCard.setAnimating(true);
	}

	public void reloadTexture(View firstView, View secondView) {
		firstBitmap = GrabIt.takeScreenshot(firstView);
		secondBitmap = GrabIt.takeScreenshot(secondView);
	}

	public void draw(GL10 gl) {
		if (mustDestroyTexture) {
			firstTexture.destroy(gl);
			secondTexture.destroy(gl);
			mustDestroyTexture = false;
		}
		applyTexture(gl);

		if (firstTexture == null || secondTexture == null) {
			return;
		}

		if (angle == 0) {
			firstTopCard.angle = 0;
			firstTopCard.draw(gl);
			firstBottomCard.angle = 0;
			firstBottomCard.draw(gl);
		} else if (angle <= 90) {
			firstTopCard.angle = 0;
			firstTopCard.draw(gl);
			secondBottomCard.angle = 0;
			secondBottomCard.draw(gl);
			firstBottomCard.angle = angle;
			firstBottomCard.draw(gl);
		} else {
			firstTopCard.angle = 0;
			firstTopCard.draw(gl);
			secondTopCard.angle = 180 - angle;
			secondTopCard.draw(gl);
			secondBottomCard.angle = 0;
			secondBottomCard.draw(gl);
		}

	}

	private void applyTexture(GL10 gl) {
		for (int i = 1; i >= 0; i--) {
			Bitmap bitmap;
			Texture texture;
			Card topCard;
			Card bottomCard;

			if (i == 0) {
				bitmap = firstBitmap;
				texture = firstTexture;
				topCard = firstTopCard;
				bottomCard = firstBottomCard;
			} else {
				bitmap = secondBitmap;
				texture = secondTexture;
				topCard = secondTopCard;
				bottomCard = secondBottomCard;
			}

			if (bitmap != null) {
				if (texture != null) {
					texture.destroy(gl);
				}

				texture = Texture.createTexture(bitmap, gl);

				topCard.setTexture(texture);
				bottomCard.setTexture(texture);

				topCard.setCardVertices(new float[] { 0f, bitmap.getHeight(), 0f, // top
																					// left
						0f, bitmap.getHeight() / 2.0f, 0f, // bottom left
						bitmap.getWidth(), bitmap.getHeight() / 2f, 0f, // bottom
																		// right
						bitmap.getWidth(), bitmap.getHeight(), 0f // top right
				});

				topCard.setTextureCoordinates(new float[] { 0f, 0f, 0, bitmap.getHeight() / 2f / texture.getHeight(), bitmap.getWidth() / (float) texture.getWidth(),
						bitmap.getHeight() / 2f / texture.getHeight(), bitmap.getWidth() / (float) texture.getWidth(), 0f });

				bottomCard.setCardVertices(new float[] { 0f, bitmap.getHeight() / 2f, 0f, // top
																							// left
						0f, 0, 0f, // bottom left
						bitmap.getWidth(), 0f, 0f, // bottom right
						bitmap.getWidth(), bitmap.getHeight() / 2f, 0f // top
																		// right
						});

				bottomCard.setTextureCoordinates(new float[] { 0f, bitmap.getHeight() / 2f / texture.getHeight(), 0, bitmap.getHeight() / (float) texture.getHeight(),
						bitmap.getWidth() / (float) texture.getWidth(), bitmap.getHeight() / (float) texture.getHeight(), bitmap.getWidth() / (float) texture.getWidth(),
						bitmap.getHeight() / 2f / texture.getHeight() });

				checkError(gl);

				bitmap.recycle();
				bitmap = null;
				if (i == 0) {
					firstBitmap = null;
					firstTexture = texture;
				} else {
					secondBitmap = null;
					secondTexture = texture;
				}
			}
		}

	}

	public void destroyTexture() {
		mustDestroyTexture = true;
	}
}