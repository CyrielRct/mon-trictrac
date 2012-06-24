package org.amphiprion.trictrac.v2.screen;

import org.amphiprion.gameengine3d.IObject;

public interface ICameraAnimation {
	public void updateCamera(float ratio, float[] posCameraToUpdate, float[] lookAtToUpdate, IObject object);

}
