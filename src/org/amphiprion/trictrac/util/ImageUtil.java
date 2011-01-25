/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of MyTricTrac.
 *
 * MyTricTrac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyTricTrac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.trictrac.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.entity.Game;

import android.os.Environment;

/**
 * This is a facility class for image.
 * 
 * @author amphiprion
 * 
 */
public class ImageUtil {
	/**
	 * Store to the given path the file at the given uri.
	 * 
	 * @param uri
	 *            the source image
	 * @param game
	 *            the game linked to the image
	 */
	public static void downloadImage(Game game) {

		URL u;
		File file;
		String path = Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY;
		String imageName = game.getId() + ".jpg";
		game.setImageName(imageName);
		/* Convert the URL string into a URL object */

		try {
			String filename = path + "/" + imageName;
			file = new File(filename);
			if (file.exists()) {
				return;
			}
			String uri = "http://www.trictrac.net/jeux/centre/imagerie/boites/" + game.getId() + "_0.jpg";
			u = new URL(uri);
		} catch (MalformedURLException e) {
			return;
		}
		HttpURLConnection c;
		InputStream in = null;
		/*
		 * Connect to the server to get the thumbnail and download the thumbnail
		 * as an InputStream
		 */
		try {
			c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			in = c.getInputStream();
		} catch (IOException e) {
			return;
		}

		/* Create a file to copy the thumbnail into */
		FileOutputStream f = null;
		try {

			File dir = new File(path);
			dir.mkdirs();
			f = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			return;
		}

		try {
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (IOException e) {
			return;
		}
	}
}
