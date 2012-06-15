package org.amphiprion.trictrac.v2;

import java.io.File;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.util.DateUtil;
import org.amphiprion.trictrac.util.LogUtil;
import org.amphiprion.trictrac.v2.flip.FlipViewGroup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

public class Home extends Activity {
	/** Called when the activity is first created. */
	private static boolean init = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.v2_collection_page);
		FlipViewGroup contentView = new FlipViewGroup(this);

		View v = View.inflate(this, R.layout.v2_collection_page, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText("Page 1");
		contentView.addFlipView(v);

		v = View.inflate(this, R.layout.v2_collection_page, null);
		title = (TextView) v.findViewById(R.id.title);
		title.setText("Page 2");
		contentView.addFlipView(v);

		setContentView(contentView);

		contentView.startFlipping(); // make the first_page view flipping

		if (!init) {
			DateUtil.init(this);
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY).mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/logs").mkdirs();
			new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/stats").mkdirs();
		}

		SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
		// startupAction =
		// StartupAction.values()[pref.getInt(StartupAction.class.getName(),
		// 0)];
		LogUtil.traceEnabled = pref.getBoolean("ACTIVE_TRACE", false);
	}

}