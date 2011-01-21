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
package org.amphiprion.trictrac.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.dao.PlayStatDao;
import org.amphiprion.trictrac.dao.PlayerDao;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.task.IProgressTask;
import org.amphiprion.trictrac.util.DateUtil;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This handler is used to synchronize parties with trictrac.
 * 
 * @author amphiprion
 * 
 */
public class PartyHandler {
	private Date date;
	private String memberId = null;
	private Context context;
	private List<Cookie> cookies = new ArrayList<Cookie>();
	private String ownerId;
	private String login;

	/**
	 * {@inheritDoc}
	 */
	public PartyHandler(Context context, Date date) throws Exception {
		this.date = date;
		this.context = context;

		SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);

		login = pref.getString("LOGIN", "");
		String pwd = pref.getString("PWD", "");
		ownerId = pref.getString("ACCOUNT_PLAYER_ID", "");

		String s = "http://www.trictrac.net/";

		HttpGet httpGet = new HttpGet(s);
		HttpClient httpclient = new DefaultHttpClient();
		// Execute HTTP Get Request
		HttpResponse response = httpclient.execute(httpGet);
		Header[] allHeaders = response.getAllHeaders();
		CookieOrigin origin = new CookieOrigin("www.trictrac.net", 80, "/", false);
		CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
		for (Header header : allHeaders) {
			List<Cookie> parse = cookieSpecBase.parse(header, origin);
			for (Cookie cookie : parse) {
				if (cookie.getName().equals("PHPSESSID")) {
					// THE cookie
					cookies.add(cookie);
				}
			}
		}

		String loginUrl = "http://www.trictrac.net/jeux/centre/membre/connexion.php";
		String data = "nom=" + login + "&pw=" + pwd + "&cookie=&slash=ok&submit=Envoyer";
		InputStream is = send(loginUrl, data);

		boolean error = isStringFound("<img src=\"/jeux/centre/membre/imagerie/panneau_alert_2.gif\">", is);
		if (error) {
			throw new Exception("Mauvais identifiant ou mot de passe");
		}
		collectMemberId();
		if (memberId == null) {
			throw new Exception("Profile id non récupérable");
		}
	}

	private void collectMemberId() throws Exception {
		String profileUrl = "http://www.trictrac.net/index.php3?id=jeux&rub=membre&inf=profil";
		String pattern = "<input type='hidden' name=\"refabo\" value=\"";
		InputStream is = send(profileUrl, null);
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith(pattern)) {
				int pos = line.indexOf("\"", pattern.length());
				memberId = line.substring(pattern.length(), pos);
				break;
			}
		}
		is.close();
	}

	/**
	 * Synchronize the all players with trictrac.
	 */
	public void synchronizePlayers(IProgressTask task) throws Exception {
		synchronizePlayers(task, true);
	}

	private void synchronizePlayers(IProgressTask task, boolean firstPass) throws Exception {
		int nbTotal = 0;
		int deb = 0;
		while (true) {
			String start = "http://www.trictrac.net/index.php3?id=jeux&rub=membre&inf=joueurs_select&choix=&choix2=&deb="
					+ deb;
			// System.out.println(start);
			InputStream is = send(start, null);
			String pattern = "</b> <a href='index.php3?id=jeux&rub=membre&inf=joueurs_form&ref=";
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

			int finded = 0;
			String line;
			while ((line = rd.readLine()) != null) {
				int first = line.indexOf(pattern);

				if (first != -1) {
					int pos = line.indexOf("'", first + pattern.length());
					String id = line.substring(first + pattern.length(), pos);
					first = line.indexOf(">", pos);
					pos = line.indexOf("<", pos);
					String name = line.substring(first + 1, pos);

					Player p = PlayerDao.getInstance(context).getPlayerByTrictracId(id);
					if (p == null) {
						p = PlayerDao.getInstance(context).getPlayerByName(name);
						if (p == null) {
							// player created on trictrac > create in Android
							p = new Player();
							p.setPseudo(name);
							p.setTrictracId(id);
							Date date = new Date();
							p.setLastUpdateDate(date);
							p.setLastSyncDate(date);
							PlayerDao.getInstance(context).persist(p);
						} else {
							p.setTrictracId(id);
							Date date = new Date();
							p.setLastUpdateDate(date);
							p.setLastSyncDate(date);
							PlayerDao.getInstance(context).persist(p);
						}
					} else {
						// TODO merge
						Date date = new Date();
						p.setLastUpdateDate(date);
						p.setLastSyncDate(date);
						PlayerDao.getInstance(context).persist(p);
					}
					task.publishProgress(++nbTotal);
					finded++;
				}
			}
			is.close();
			if (finded == 0) {
				break;
			} else {
				deb += finded;
			}
		}
		// now send to trictrac player created on Android
		List<Player> players = PlayerDao.getInstance(context).getLocalPlayers();
		boolean created = false;
		for (Player player : players) {
			if (!ownerId.equals(player.getId())) {
				String s = "http://www.trictrac.net/jeux/centre/membre/include/joueurs_gestion.php";
				String data = "MAX_FILE_SIZE=1000000&slash=ok&categorie=&pseudo="
						+ URLEncoder.encode(player.getPseudo()) + "&id_adversaire=" + player.getTricTracProfileId();
				data += "&nation=" + URLEncoder.encode("Non évalué");
				data += "&nom=&annee=&adresse=&ville=&postal=&mail=&commentaire=";
				data += "&slash2=ok&date=" + DateUtil.yyyymmddFormat.format(new Date());
				data += "&modif_date=" + DateUtil.yyyymmddFormat.format(new Date()) + " 00:00:00";
				data += "&act=inser&table=membres_adv&gestion=joueurs&refabo=" + memberId;
				data += "&auteur_fiche=" + URLEncoder.encode(login);
				// data +="&upload' value="7684_1295478655">
				data += "&sequence=no";
				send(s, data).close();
				created = true;
				task.publishProgress(++nbTotal);
			}
		}
		if (firstPass && created) {
			synchronizePlayers(task, false);
		}
	}

	public void collectParties() {
		// TODO
	}

	/**
	 * Publish a local (android) party to the trictrac web site.
	 * 
	 * @param party
	 *            the party
	 * @throws Exception
	 *             if an error occurs
	 */
	public void publishParties(Party party) throws Exception {
		List<PlayStat> stats = PlayStatDao.getInstance(context).getPlayStat(party);
		party.setStats(stats);

		Calendar d = Calendar.getInstance();
		d.setTime(party.getDate());

		String day = "" + d.get(Calendar.DAY_OF_MONTH);
		if (day.length() < 2) {
			day = "0" + day;
		}
		String month = "" + d.get(Calendar.MONTH) + 1;
		if (month.length() < 2) {
			month = "0" + month;
		}
		String year = "" + d.get(Calendar.YEAR);

		String gameId = party.getGameId();
		List<String> oldPartyIds = collectPartyIds(memberId, gameId, day, month, year);

		String city = URLEncoder.encode(party.getCity());
		String event = URLEncoder.encode(party.getEvent());
		String comment = URLEncoder.encode(party.getComment());

		String start = "http://www.trictrac.net/jeux/centre/membre/include/gestion_partie_solo.php";
		String data = "djour=" + day + "&dmois=" + month + "&dannee=" + year + "&ville=" + city + "&occasion=" + event
				+ "&online=1";
		int i = 0;
		for (PlayStat stat : stats) {
			if (stat.getPlayer() == null) {
				data += "&joueur_" + i + "=y";
			} else if (!ownerId.equals(stat.getPlayer().getId())) {
				data += "&joueur_" + i + "=" + stat.getPlayer().getTrictracId();
			} else {
				data += "&joueur_" + i + "=x";
			}
			data += "&place_" + i + "=" + stat.getRank();
			data += "&score_" + i + "=" + stat.getScore();
			i++;
		}
		data += "&note=" + party.getHappyness() + "&nb=" + party.getDuration();
		data += "&commentaire_p=" + comment;
		data += "&cache_h=jeux";
		data += "&slash=ok";
		data += "&date=" + DateUtil.yyyymmddFormat.format(new Date());
		data += "&act=inser";
		data += "&table=membres_parties";
		data += "&gestion=fiches_jeux";
		data += "&refabo=" + memberId;
		data += "&id_membre=" + memberId;
		data += "&id_jeu=" + gameId;
		data += "&nbj=" + stats.size();
		data += "&ref_base=" + gameId;
		data += "&sequence=no";
		send(start, data).close();

		List<String> newPartyIds = collectPartyIds(memberId, gameId, day, month, year);
		newPartyIds.removeAll(oldPartyIds);
		if (newPartyIds.size() > 0) {
			String id = newPartyIds.get(0);
			party.setTrictracId(id);
			Date date = new Date();
			party.setLastSyncDate(date);
			party.setLastUpdateDate(date);
			PartyDao.getInstance(context).persist(party);
		}

	}

	/**
	 * Collect all existing trictrac party ids for a given date and game.
	 * 
	 * @param memberId
	 *            the trictrac member id
	 * @param gameId
	 *            the game id
	 * @param day
	 *            the day (2 digits)
	 * @param month
	 *            the month (2 digits)
	 * @param year
	 *            the year (4 digits)
	 * @return the list of party ids
	 */
	private List<String> collectPartyIds(String memberId, String gameId, String day, String month, String year) {
		List<String> partyIds = new ArrayList<String>();
		try {
			int deb = 0;
			while (true) {
				String start = "http://www.trictrac.net/index.php3?id=jeux&rub=ludoperso&inf=liste_parties&id_jeu="
						+ gameId + "&id_membre=" + memberId + "&groupby=0&djour=" + day + "&dmois=" + month
						+ "&dannee=" + year + "&fjour=" + day + "&fmois=" + month + "&fannee=" + year
						+ "&image2.x=22&image2.y=3&deb=" + deb;
				// System.out.println(start);
				InputStream is = send(start, null);
				String pattern = "        <A HREF=\"javascript:aide('partie_detail','";
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

				int finded = 0;
				String line;
				while ((line = rd.readLine()) != null) {
					if (line.startsWith(pattern)) {
						int pos = line.indexOf("'", pattern.length());
						String id = line.substring(pattern.length(), pos);
						partyIds.add(id);
						finded++;
					}
				}
				is.close();
				if (finded == 0) {
					break;
				} else {
					deb += finded;
				}
			}
		} catch (Exception e) {
			partyIds.clear();
		}
		return partyIds;
	}

	/**
	 * Call the given url and return the input stream
	 * 
	 * @param s
	 *            the url to call
	 * @param data
	 *            the data to post or null
	 * @return the input stream
	 * @throws Exception
	 *             if error occurs
	 */
	private InputStream send(String s, String data) throws Exception {
		if (data == null) {
			HttpGet httpGet = new HttpGet(s);
			CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
			List<Header> cookieHeader = cookieSpecBase.formatCookies(cookies);
			// Setting the cookie
			for (Header h : cookieHeader) {
				httpGet.setHeader(h);
			}
			httpGet.setHeader("Referer", "http://www.trictrac.net");
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			return response.getEntity().getContent();
		} else {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(s);
			CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
			List<Header> cookieHeader = cookieSpecBase.formatCookies(cookies);
			// Setting the cookie
			for (Header h : cookieHeader) {
				httpPost.setHeader(h);
			}
			httpPost.setHeader("Referer", "http://www.trictrac.net");

			String[] items = data.split("&");
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			for (String item : items) {
				int pos = item.indexOf("=");
				nameValuePairs.add(new BasicNameValuePair(item.substring(0, pos), item.substring(pos + 1)));
				// Log.d(ApplicationConstants.PACKAGE, "POST:" + item);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpPost);
			return response.getEntity().getContent();
		}
	}

	/**
	 * Return true if the given string is found in one line of the stream.
	 * 
	 * @param s
	 *            the string to find
	 * @param is
	 *            the input stream
	 * @return true if found
	 * @throws IOException
	 *             if an error occurs
	 */
	private boolean isStringFound(String s, InputStream is) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

		String line;
		while ((line = rd.readLine()) != null) {
			if (line.contains(s)) {
				is.close();
				return true;
			}
		}
		is.close();
		return false;
	}

}
