package org.amphiprion.trictrac.task;

import android.content.Context;

public interface ITaskListener {
	void taskEnded(boolean success);

	Context getContext();
}
