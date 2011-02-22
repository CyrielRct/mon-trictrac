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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.ApplicationConstants;
import org.amphiprion.trictrac.R;
import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.dao.PlayStatDao;
import org.amphiprion.trictrac.dao.PlayerDao;
import org.amphiprion.trictrac.entity.Entity.DbState;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.entity.Player;
import org.amphiprion.trictrac.task.IProgressTask;
import org.amphiprion.trictrac.util.DateUtil;
import org.amphiprion.trictrac.util.LogUtil;
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
import android.util.Log;

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
	private PrintWriter pw;

	/**
	 * {@inheritDoc}
	 */
	public PartyHandler(Context context, Date date, PrintWriter pw) throws Exception {
		this.date = date;
		this.context = context;
		this.pw = pw;
		SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);

		login = pref.getString("LOGIN", "");
		String pwd = pref.getString("PWD", "");
		ownerId = pref.getString("ACCOUNT_PLAYER_ID", "");

		LogUtil.trace(pw, "call trictrac to retrieve session cookie");
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
		if (cookies == null) {
			LogUtil.trace(pw, "NO COOKIE !!!");
		} else {
			LogUtil.trace(pw, "cookies:" + cookies.size());
			if (cookies.size() > 0) {
				LogUtil.trace(pw, "cookie   name=" + cookies.get(0).getName() + "  value=" + cookies.get(0).getValue());
			}
		}
		LogUtil.trace(pw, "appel de l'url de login");
		String loginUrl = "http://www.trictrac.net/jeux/centre/membre/connexion.php";
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair("nom", login));
		data.add(new BasicNameValuePair("pw", pwd));
		data.add(new BasicNameValuePair("cookie", ""));
		data.add(new BasicNameValuePair("slash", "ok"));
		data.add(new BasicNameValuePair("submit", "Envoyer"));

		InputStream is = send(loginUrl, data);

		boolean error = isStringFound("<img src=\"/jeux/centre/membre/imagerie/panneau_alert_2.gif\">", is);
		if (error) {
			LogUtil.trace(pw, "Mauvais login ou passe word");
			throw new Exception("Mauvais identifiant ou mot de passe");
		}
		LogUtil.trace(pw, "On recherche le profile id");
		collectMemberId();
		if (memberId == null) {
			LogUtil.trace(pw, "Profile id non recuperable");
			throw new Exception("Profile id non récupérable");
		}
		LogUtil.trace(pw, "Login SUCCEED, profile id=" + memberId);
	}

	private void collectMemberId() throws Exception {
		String profileUrl = "http://www.trictrac.net/index.php3?id=jeux&rub=membre&inf=profil";
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "on appel " + profileUrl);
		String pattern = "<input type='hidden' name=\"refabo\" value=\"";
		InputStream is = send(profileUrl, null);
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

		String line;
		while ((line = rd.readLine()) != null) {
			LogUtil.trace(pw, line);
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
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "on recherhce les joueurs existant sur trictac");
		while (true) {
			String start = "http://www.trictrac.net/index.php3?id=jeux&rub=membre&inf=joueurs_select&choix=&choix2=&deb=" + deb;
			// System.out.println(start);
			InputStream is = send(start, null);
			String pattern = "</b> <a href='index.php3?id=jeux&rub=membre&inf=joueurs_form&ref=";
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

			int finded = 0;
			String line;
			while ((line = rd.readLine()) != null) {
				int first = line.indexOf(pattern);

				if (first != -1) {
					task.publishProgress(++nbTotal);
					int pos = line.indexOf("'", first + pattern.length());
					String id = line.substring(first + pattern.length(), pos);
					first = line.indexOf(">", pos);
					pos = line.indexOf("<", pos);
					String name = line.substring(first + 1, pos);
					LogUtil.trace(pw, "joueur trouve: " + name);
					Player p = PlayerDao.getInstance(context).getPlayerByTrictracId(id);
					if (p == null) {
						LogUtil.trace(pw, "   " + name + " via son id n'est pas sur le telephone");
						p = PlayerDao.getInstance(context).getPlayerByName(name);
						if (p == null) {
							LogUtil.trace(pw, "   " + name + " via son nom n'est pas sur le telephone");
							// player created on trictrac > create in Android
							p = new Player();
							p.setPseudo(name);
							p.setTrictracId(id);
							Date date = new Date();
							p.setLastUpdateDate(date);
							p.setLastSyncDate(date);
							PlayerDao.getInstance(context).persist(p);
							LogUtil.trace(pw, "   " + name + " vient d'etre cree en local");
						} else {
							p.setTrictracId(id);
							Date date = new Date();
							p.setLastUpdateDate(date);
							p.setLastSyncDate(date);
							PlayerDao.getInstance(context).persist(p);
							LogUtil.trace(pw, "   " + name + " vient d'etre relie au joueur local " + p.getId() + "/" + p.getPseudo());
						}
					} else {
						Date date = new Date();
						if (p.getLastSyncDate() != null && p.getLastUpdateDate().after(p.getLastSyncDate())) {
							task.publishProgress(R.string.upload_players, nbTotal);
							// mise à jour depuis Android, on envoie vers
							// trictrac
							LogUtil.trace(pw, "   " + name + " a ete mis a jour sur le telephone, on met a jour trictrac");
							uploadPlayer(p, true);
						} else {
							// on recup trictrac au cas où il y aurait une modif
							p.setPseudo(name);
							LogUtil.trace(pw, "   " + name + " mis a jour en local");
							// uploadPlayer(p, true);
						}
						p.setLastUpdateDate(date);
						p.setLastSyncDate(date);
						PlayerDao.getInstance(context).persist(p);
					}
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
		LogUtil.trace(pw, "   nb recup depuis trictrac:" + nbTotal);
		LogUtil.trace(pw, "##################################");
		nbTotal = 0;
		// now send to trictrac player created on Android
		LogUtil.trace(pw, "maintenant on envoie sur trictrac les joueurs locaux");
		LogUtil.trace(pw, "##################################");
		List<Player> players = PlayerDao.getInstance(context).getLocalPlayers();
		boolean created = false;
		for (Player player : players) {
			if (!ownerId.equals(player.getId())) {
				task.publishProgress(R.string.upload_players, ++nbTotal);
				LogUtil.trace(pw, "on upload " + player.getId() + "/" + player.getPseudo());
				uploadPlayer(player, false);
				created = true;
			}
		}
		if (firstPass && created) {
			LogUtil.trace(pw, "Comme il n'y a pas d acknoledgement sur la creation on refait une pass pour lier les joueurs qui viennent d'etre uploade, c reparti....");
			synchronizePlayers(task, false);
		}
	}

	private void uploadPlayer(Player player, boolean isUpdate) throws Exception {
		String s = "http://www.trictrac.net/jeux/centre/membre/include/joueurs_gestion.php";
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair("MAX_FILE_SIZE", "1000000"));
		data.add(new BasicNameValuePair("slash", "ok"));
		data.add(new BasicNameValuePair("categorie", ""));
		data.add(new BasicNameValuePair("pseudo", player.getPseudo()));
		data.add(new BasicNameValuePair("id_adversaire", player.getTricTracProfileId()));
		if (!isUpdate) {
			data.add(new BasicNameValuePair("nation", "Non évalué"));
			data.add(new BasicNameValuePair("nom", ""));
			data.add(new BasicNameValuePair("annee", ""));
			data.add(new BasicNameValuePair("adresse", ""));
			data.add(new BasicNameValuePair("ville", ""));
			data.add(new BasicNameValuePair("postal", ""));
			data.add(new BasicNameValuePair("mail", ""));
			data.add(new BasicNameValuePair("commentaire", ""));
		}
		data.add(new BasicNameValuePair("slash2", "ok"));
		data.add(new BasicNameValuePair("date", DateUtil.yyyymmddFormat.format(new Date())));
		data.add(new BasicNameValuePair("modif_date", DateUtil.yyyymmddFormat.format(new Date()) + " 00:00:00"));
		if (isUpdate) {
			data.add(new BasicNameValuePair("act", "modif"));
		} else {
			data.add(new BasicNameValuePair("act", "inser"));
		}
		data.add(new BasicNameValuePair("table", "membres_adv"));
		data.add(new BasicNameValuePair("gestion", "joueurs"));
		data.add(new BasicNameValuePair("refabo", memberId));
		if (isUpdate) {
			data.add(new BasicNameValuePair("ref_base", player.getTrictracId()));
			data.add(new BasicNameValuePair("upload", memberId + "_" + player.getTrictracId() + "_" + new Date().getTime()));
		}
		data.add(new BasicNameValuePair("auteur_fiche", login));
		data.add(new BasicNameValuePair("sequence", "no"));

		send(s, data).close();
	}

	/**
	 * Download parties from trictrac
	 */
	public void synchronizeParties(IProgressTask task) throws Exception {
		Calendar d = Calendar.getInstance();
		d.setTime(date);

		String day = "" + d.get(Calendar.DAY_OF_MONTH);
		if (day.length() < 2) {
			day = "0" + day;
		}
		String month = "" + (d.get(Calendar.MONTH) + 1);
		if (month.length() < 2) {
			month = "0" + month;
		}
		String year = "" + d.get(Calendar.YEAR);

		String endDay = day;
		String endMonth = month;
		String endYear = "" + (d.get(Calendar.YEAR) + 100);

		List<String> partyIds = collectPartyIds(memberId, null, day, month, year, endDay, endMonth, endYear);
		LogUtil.trace(pw, "Ids de parties present sur trictrac a partir: " + day + "/" + month + "/" + year + "=" + partyIds.size());
		int nb = 0;
		for (String partyId : partyIds) {
			task.publishProgress(++nb);

			LogUtil.trace(pw, "   on regarde si en local on a deja la partie avec trictracid=" + partyId);
			Party party = PartyDao.getInstance(context).getPartyByTrictracId(partyId);
			if (party == null) {
				LogUtil.trace(pw, "   party inconnu sur android, on recup les data depuis trictrac");
				// new party on trictrac, download it
				party = retrieveTrictracParty(partyId);
				if (party != null) {
					LogUtil.trace(pw, "   partie recuperre! gameid=" + party.getGameId());
					Date sync = new Date();
					party.setLastUpdateDate(sync);
					party.setLastSyncDate(sync);
					PartyDao.getInstance(context).persist(party);
				} else {
					LogUtil.trace(pw, "   partie irrecuperale....");
				}
			} else {
				if (party.getLastSyncDate() != null && party.getLastUpdateDate().after(party.getLastSyncDate())) {
					LogUtil.trace(pw, "   partie mis a jour sur android depuis derniere snchro....");
					// update from android > send data to TricTrac
					uploadParty(party, true);
					LogUtil.trace(pw, "   tric trac a et mis a jour");
				} else {
					// on recup trictrac au cas où il y aurait une modif
					LogUtil.trace(pw, "   on recuperer la partie depuis trictrac....");
					Party ttParty = retrieveTrictracParty(partyId);
					if (ttParty != null) {
						party.setCity(ttParty.getCity());
						party.setComment(ttParty.getComment());
						party.setDate(ttParty.getDate());
						party.setDuration(ttParty.getDuration());
						party.setEvent(ttParty.getEvent());
						party.setHappyness(ttParty.getHappyness());
						party.setStats(ttParty.getStats());
						Date sync = new Date();
						party.setLastUpdateDate(sync);
						party.setLastSyncDate(sync);
						PartyDao.getInstance(context).persist(party);
						LogUtil.trace(pw, "   android a ete mis a jour");
					} else {
						LogUtil.trace(pw, "   partie irrecuperale pour la maj android....");
					}
				}
			}
		}
	}

	public Party retrieveTrictracParty(String partyId) throws Exception {
		LogUtil.trace(pw, "###################################");
		LogUtil.trace(pw, "###################################");
		LogUtil.trace(pw, "###################################");

		String s = "http://www.trictrac.net/aides/aide.php?rub=jeux&aide=partie_detail&ref=" + partyId;
		LogUtil.trace(pw, "retrieveTrictracParty:" + s);
		InputStream is = send(s, null);

		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
		Party party = new Party();
		party.setTrictracId(partyId);
		String pattern = "<select name=\"djour\"";
		String pattern2 = "";
		int step = 0;
		int endStep = 14;
		int day = 0;
		int month = 0;
		int year = 0;
		List<PlayStat> stats = new ArrayList<PlayStat>();
		PlayStat playStat = null;

		String line;
		while ((line = rd.readLine()) != null) {
			int pos = line.indexOf(pattern);
			if (pos == -1 && step == 10) {
				pos = line.indexOf(pattern2);
				if (pos != -1) {
					step++;
				}
			}
			if (pos != -1) {
				if (step == 0) {
					pattern = "SELECTED>";
					step++;
				} else if (step == 1) {
					int end = line.indexOf("<", pos);
					day = Integer.parseInt(line.substring(pos + pattern.length(), end));
					pattern = "<select name=\"dmois\"";
					step++;
				} else if (step == 2) {
					pattern = "\"SELECTED>";
					step++;
				} else if (step == 3) {
					int deb = line.indexOf("\"");
					month = Integer.parseInt(line.substring(deb + 1, pos));
					pattern = "<input type=\"text\" name=\"dannee\"";
					step++;
				} else if (step == 4) {
					pattern = "value=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					year = Integer.parseInt(line.substring(pos + pattern.length(), end));
					party.setDate(new Date(year - 1900, month - 1, day));
					pattern = "<input name=\"ville\"";
					step++;
				} else if (step == 5) {
					pattern = "value=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					party.setCity(line.substring(pos + pattern.length(), end));
					pattern = "<input name=\"occasion\"";
					step++;
				} else if (step == 6) {
					pattern = "value=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					party.setEvent(line.substring(pos + pattern.length(), end));
					pattern = "<select name=\"joueur_";
					step++;
				} else if (step == 7) {
					playStat = new PlayStat();
					playStat.setPartyId(party.getId());
					pattern = "\"SELECTED>";
					if (line.indexOf(pattern) != -1) {
						pattern = "<input name=\"place_";
						step += 2;
					} else {
						step++;
					}
				} else if (step == 8) {
					int deb = line.indexOf("\"");
					String pId = line.substring(deb + 1, pos);
					Player player = null;
					if ("x".equals(pId)) {
						player = PlayerDao.getInstance(context).getPlayerById(ownerId);
					} else {
						player = PlayerDao.getInstance(context).getPlayerByTrictracId(pId);
					}
					playStat.setPlayer(player);

					pattern = "<input name=\"place_";
					step++;
				} else if (step == 9) {
					pattern = "VALUE=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					String val = line.substring(pos + pattern.length(), end);
					if (val.length() > 0) {
						playStat.setRank(Integer.parseInt(val));
					}
					pattern = "<input name=\"score_";
					pos = line.indexOf(pattern);
					pattern = "VALUE=\"";
					pos = line.indexOf(pattern, pos);
					end = line.indexOf("\"", pos + pattern.length());
					val = line.substring(pos + pattern.length(), end);
					if (val.length() > 0) {
						playStat.setScore(Double.parseDouble(val));
					}
					stats.add(playStat);
					pattern = "<select name=\"joueur_";
					if (line.indexOf(pattern) != -1) {
						playStat = new PlayStat();
						playStat.setPartyId(party.getId());
						pattern = "\"SELECTED>";
						if (line.indexOf(pattern) != -1) {
							pattern = "<input name=\"place_";
							step += 0;
						} else {
							step--;
						}
					} else {
						party.setStats(stats);
						pattern = "\" CHECKED>";
						pattern2 = "<input type=\"text\" name=\"nb\"";
						step++;
					}
				} else if (step == 10) {
					pattern = "VALUE=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					party.setHappyness(Integer.parseInt(line.substring(pos + pattern.length(), end)));
					pattern = "<input type=\"text\" name=\"nb\"";
					step++;
				} else if (step == 11) {
					pattern = "value=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					party.setDuration(Integer.parseInt(line.substring(pos + pattern.length(), end)));
					pattern = "<TEXTAREA NAME=\"commentaire_p\"";
					step++;
				} else if (step == 12) {
					int deb = line.indexOf(">");
					pos = line.indexOf("</TEXTAREA>");
					String comment = null;
					if (pos != -1) {
						comment = line.substring(deb + 1, pos);
					} else {
						comment = line.substring(deb + 1) + "\n";
						while (true) {
							line = rd.readLine();
							pos = line.indexOf("</TEXTAREA>");
							if (pos != -1) {
								comment += line.substring(0, pos);
								break;
							} else {
								comment += line.substring(0) + "\n";
							}
						}
					}
					party.setComment(comment);
					pattern = "<input type='hidden' name='id_jeu'";
					step++;
				} else if (step == 13) {
					pattern = "value=\"";
					pos = line.indexOf(pattern);
					int end = line.indexOf("\"", pos + pattern.length());
					party.setGameId(line.substring(pos + pattern.length(), end));
					if (!GameDao.getInstance(context).exists(party.getGameId())) {
						Game game = new Game(party.getGameId());
						game.setState(DbState.NEW);
						new GameHandler().parse(game);
						GameDao.getInstance(context).persist(game);
					}
					step++;
					break;
				}
			}

		}
		if (step != endStep) {
			Log.e(ApplicationConstants.PACKAGE, "Error: Incomplete steps, stop at " + step);
			LogUtil.trace(pw, "#### ERROR:Incomplete steps, stop at " + step);
			return null;
		}
		return party;
	}

	/**
	 * Publish a local (android) party to the trictrac web site.
	 * 
	 * @param party
	 *            the party
	 * @throws Exception
	 *             if an error occurs
	 */
	public void uploadParty(Party party, boolean isUpdate) throws Exception {
		LogUtil.trace(pw, "#############################");
		LogUtil.trace(pw, "#############################");
		LogUtil.trace(pw, "#############################");
		LogUtil.trace(pw, "on upload la party, isUpdate=" + isUpdate);
		List<PlayStat> stats = PlayStatDao.getInstance(context).getPlayStat(party);
		party.setStats(stats);

		Calendar d = Calendar.getInstance();
		d.setTime(party.getDate());

		String day = "" + d.get(Calendar.DAY_OF_MONTH);
		if (day.length() < 2) {
			day = "0" + day;
		}
		String month = "" + (d.get(Calendar.MONTH) + 1);
		if (month.length() < 2) {
			month = "0" + month;
		}
		String year = "" + d.get(Calendar.YEAR);

		String gameId = party.getGameId();
		List<String> oldPartyIds = null;

		if (!isUpdate) {
			oldPartyIds = collectPartyIds(memberId, gameId, day, month, year, day, month, year);
		}
		String city = party.getCity();
		String event = party.getEvent();
		String comment = party.getComment();

		String start = "http://www.trictrac.net/jeux/centre/membre/include/gestion_partie_solo.php";
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair("djour", day));
		data.add(new BasicNameValuePair("dmois", month));
		data.add(new BasicNameValuePair("dannee", year));
		data.add(new BasicNameValuePair("ville", city));
		data.add(new BasicNameValuePair("occasion", event));
		if (!isUpdate) {
			data.add(new BasicNameValuePair("online", "1"));
		}
		int i = 0;
		for (PlayStat stat : stats) {
			if (stat.getPlayer() == null) {
				data.add(new BasicNameValuePair("joueur_" + i, "y"));
			} else if (!ownerId.equals(stat.getPlayer().getId())) {
				data.add(new BasicNameValuePair("joueur_" + i, stat.getPlayer().getTrictracId()));
			} else {
				data.add(new BasicNameValuePair("joueur_" + i, "x"));
			}
			data.add(new BasicNameValuePair("place_" + i, "" + stat.getRank()));
			data.add(new BasicNameValuePair("score_" + i, "" + stat.getScore()));
			i++;
		}
		data.add(new BasicNameValuePair("note", "" + party.getHappyness()));
		data.add(new BasicNameValuePair("nb", "" + party.getDuration()));
		data.add(new BasicNameValuePair("commentaire_p", comment));
		data.add(new BasicNameValuePair("cache_h", "jeux"));
		data.add(new BasicNameValuePair("slash", "ok"));
		data.add(new BasicNameValuePair("date", DateUtil.yyyymmddFormat.format(new Date())));
		if (!isUpdate) {
			data.add(new BasicNameValuePair("act", "inser"));
		} else {
			data.add(new BasicNameValuePair("act", "modif"));
		}
		data.add(new BasicNameValuePair("table", "membres_parties"));
		data.add(new BasicNameValuePair("gestion", "fiches_jeux"));
		data.add(new BasicNameValuePair("refabo", memberId));
		data.add(new BasicNameValuePair("id_membre", memberId));
		data.add(new BasicNameValuePair("id_jeu", gameId));
		data.add(new BasicNameValuePair("nbj", "" + stats.size()));
		if (!isUpdate) {
			data.add(new BasicNameValuePair("ref_base", gameId));
		} else {
			data.add(new BasicNameValuePair("ref_base", party.getTrictracId()));
		}
		data.add(new BasicNameValuePair("sequence", "no"));
		send(start, data).close();

		if (!isUpdate) {
			LogUtil.trace(pw, "On cherche les parties crees a cette date " + day + "/" + month + "/" + year);
			List<String> newPartyIds = collectPartyIds(memberId, gameId, day, month, year, day, month, year);
			LogUtil.trace(pw, "oldszie=" + oldPartyIds.size() + "  newsize=" + newPartyIds.size());
			newPartyIds.removeAll(oldPartyIds);
			LogUtil.trace(pw, "difference entre les deux (doit etre 1)=" + newPartyIds.size());
			if (newPartyIds.size() > 0) {
				String id = newPartyIds.get(0);
				party.setTrictracId(id);
				Date date = new Date();
				party.setLastSyncDate(date);
				party.setLastUpdateDate(date);
				PartyDao.getInstance(context).persist(party);
			}
		} else {
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
	private List<String> collectPartyIds(String memberId, String gameId, String day, String month, String year, String endDay, String endMonth, String endYear) {
		List<String> partyIds = new ArrayList<String>();
		try {
			int deb = 0;
			while (true) {
				String start = "http://www.trictrac.net/index.php3?id=jeux&rub=ludoperso&inf=liste_parties";
				if (gameId != null) {
					start += "&id_jeu=" + gameId;
				}
				start += "&id_membre=" + memberId + "&groupby=0&djour=" + day + "&dmois=" + month + "&dannee=" + year + "&fjour=" + endDay + "&fmois=" + endMonth + "&fannee="
						+ endYear + "&image2.x=22&image2.y=3&deb=" + deb;
				LogUtil.trace(pw, "start=" + start);
				InputStream is = send(start, null);
				String pattern = "        <A HREF=\"javascript:aide('partie_detail','";
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

				int finded = 0;
				String line;
				while ((line = rd.readLine()) != null) {
					LogUtil.trace(pw, line);
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
			LogUtil.trace(pw, e);
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
	private InputStream send(String s, List<BasicNameValuePair> nameValuePairs) throws Exception {
		if (nameValuePairs == null) {
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

			// String[] items = data.split("&");
			// List<BasicNameValuePair> nameValuePairs = new
			// ArrayList<BasicNameValuePair>();
			// for (String item : items) {
			// int pos = item.indexOf("=");
			// nameValuePairs.add(new BasicNameValuePair(item.substring(0, pos),
			// item.substring(pos + 1)));
			// // Log.d(ApplicationConstants.PACKAGE, "POST:" + item);
			// }
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
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "###########################################");
		LogUtil.trace(pw, "      IS STRING FOUND:" + s);
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));

		String line;
		while ((line = rd.readLine()) != null) {
			LogUtil.trace(pw, line);
			if (line.contains(s)) {
				is.close();
				LogUtil.trace(pw, "      STRING FOUND:" + s);
				return true;
			}
		}
		is.close();
		LogUtil.trace(pw, "      STRING NOT FOUND:" + s);
		return false;
	}
}
