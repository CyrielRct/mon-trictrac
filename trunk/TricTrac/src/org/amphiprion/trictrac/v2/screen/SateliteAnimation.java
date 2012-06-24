package org.amphiprion.trictrac.v2.screen;

import org.amphiprion.gameengine3d.IObject;

public class SateliteAnimation implements ICameraAnimation {
	private float[] posStartCamera = new float[] { 0, -9f, 8f };
	private float[] posEndCamera = new float[] { 0, -0.1f, 9f };

	/**
	 * @param ratio
	 *            between 0 and 1
	 * @param posCameraToUpdate
	 * @return the camera angle and the given array is updated
	 */
	@Override
	public void updateCamera(float ratio, float[] posCameraToUpdate,
			float[] lookAtToUpdate, IObject object) {
		posCameraToUpdate[0] = posStartCamera[0]
				+ (posEndCamera[0] + object.getX() - posStartCamera[0]) * ratio;
		posCameraToUpdate[1] = posStartCamera[1]
				+ (posEndCamera[1] + object.getY() - posStartCamera[1]) * ratio;
		posCameraToUpdate[2] = posStartCamera[2]
				+ (posEndCamera[2] - posStartCamera[2]) * ratio;

		lookAtToUpdate[0] = object.getX() * ratio;
		lookAtToUpdate[1] = object.getY() * ratio;

	}
}
