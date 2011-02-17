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
package org.amphiprion.trictrac;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.trictrac.dao.GameDao;
import org.amphiprion.trictrac.dao.PartyDao;
import org.amphiprion.trictrac.dao.PlayStatDao;
import org.amphiprion.trictrac.entity.Game;
import org.amphiprion.trictrac.entity.Party;
import org.amphiprion.trictrac.entity.PartyForList;
import org.amphiprion.trictrac.entity.PlayStat;
import org.amphiprion.trictrac.task.ITaskListener;
import org.amphiprion.trictrac.task.LoadPartiesTask;
import org.amphiprion.trictrac.task.LoadPartiesTask.LoadPartyListener;
import org.amphiprion.trictrac.task.SynchronizePartiesTask;
import org.amphiprion.trictrac.util.DateUtil;
import org.amphiprion.trictrac.view.MyScrollView;
import org.amphiprion.trictrac.view.PartySummaryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author amphiprion
 * 
 */
public class PartyList extends Activity implements LoadPartyListener {
	private static final int PAGE_SIZE = 20;

	public static PartyList instance;

	private int loadedPage;
	private List<PartyForList> parties;
	private MyScrollView scrollView;
	private boolean allLoaded;
	private boolean loading;
	private String lastGameName = null;
	private Game game;
	private PartyForList current;
	private LoadPartiesTask task;
	private String ownerId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
		ownerId = pref.getString("ACCOUNT_PLAYER_ID", null);

		setContentView(R.layout.party_list);

		final Rect r = new Rect();
		scrollView = (MyScrollView) findViewById(R.id.scroll_view);
		scrollView.setOnScrollChanged(new OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (!allLoaded && !loading) {
					LinearLayout ln = (LinearLayout) scrollView.getChildAt(0);
					if (ln.getChildCount() > 3) {
						boolean b = ln.getChildAt(ln.getChildCount() - 3).getLocalVisibleRect(r);
						if (b) {
							loading = true;
							loadNextPage();
						}
					}
				}
			}
		});

		handleIntent(getIntent());

	}

	public void handleIntent(Intent intent) {
		game = (Game) intent.getSerializableExtra("GAME");
		if (game == null) {
			Home.setTopTitle(getResources().getString(R.string.my_parties, ""));
		} else {
			Home.setTopTitle(getResources().getString(R.string.my_parties, game.getName()));
		}
		init();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && game != null) {
			Home.goToParties(this, null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void init() {
		loadedPage = 0;
		if (parties == null) {
			parties = new ArrayList<PartyForList>();
		} else {
			parties.clear();
		}
		loadNextPage();
	}

	private void loadNextPage() {
		if (loadedPage == 0) {
			// int nb = GameDao.getInstance(this).getGameCount(collection,
			// search, query);
			// Toast.makeText(this,
			// getResources().getString(R.string.message_nb_result, nb),
			// Toast.LENGTH_LONG).show();
			List<PartyForList> newParties = PartyDao.getInstance(this).getParties(game, loadedPage, PAGE_SIZE, ownerId);
			importEnded(true, newParties);
		} else {
			task = new LoadPartiesTask(this, game, loadedPage, PAGE_SIZE);
			task.execute();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (game != null) {
			MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_CREATE_PARTY, 0, R.string.add_party);
			addAccount.setIcon(android.R.drawable.ic_menu_add);
		}

		MenuItem searchTrictrac = menu.add(0, ApplicationConstants.MENU_ID_SEARCH_TRICTRAC_GAME, 1, R.string.menu_search_trictrac);
		searchTrictrac.setIcon(android.R.drawable.ic_menu_search);

		MenuItem synchParty = menu.add(1, ApplicationConstants.MENU_ID_SYNCH_PARTY, 2, R.string.synch_parties);
		synchParty.setIcon(android.R.drawable.ic_menu_share);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_CREATE_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			i.putExtra("GAME", game);
			// i.putExtra("PARTY", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_SEARCH_TRICTRAC_GAME) {
			Intent i = new Intent(this, TricTracGameList.class);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_SEARCH_TRICTRAC_GAME);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_SYNCH_PARTY) {
			SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
			long time = pref.getLong("SYNCH_PARTY_DATE", new Date(100, 0, 1).getTime());

			OnDateSetListener l = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
					SynchronizePartiesTask task = new SynchronizePartiesTask(new ITaskListener() {

						@Override
						public void taskEnded(boolean success) {
							if (success) {
								SharedPreferences pref = getSharedPreferences(ApplicationConstants.GLOBAL_PREFERENCE, 0);
								Editor editor = pref.edit();
								editor.putLong("SYNCH_PARTY_DATE", new Date().getTime());
								editor.commit();
								init();
							}

						}

						@Override
						public Context getContext() {
							return PartyList.this;
						}
					});
					task.execute(date);
				}
			};

			Date date = new Date(time);
			DatePickerDialog dlg = new DatePickerDialog(getContext(), l, date.getYear() + 1900, date.getMonth(), date.getDate());
			dlg.setTitle(getResources().getText(R.string.synch_start_date));
			dlg.show();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				party.setLastUpdateDate(new Date());
				PartyDao.getInstance(this).persist(party);
				init();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_SEARCH_TRICTRAC_GAME) {
				init();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_UPDATE_PARTY) {
				Party party = (Party) data.getSerializableExtra("PARTY");
				party.setLastUpdateDate(new Date());
				PartyDao.getInstance(this).persist(party);
				init();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof PartySummaryView) {
			current = ((PartySummaryView) v).getParty();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_PARTY, 0, R.string.edit_party);
			menu.add(2, ApplicationConstants.MENU_ID_DELETE_PARTY, 1, R.string.delete_party);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_PARTY) {
			Intent i = new Intent(this, EditParty.class);
			Party party = PartyDao.getInstance(this).getParty(current.getId());
			i.putExtra("PARTY", party);
			if (game == null) {
				game = GameDao.getInstance(this).getGame(party.getGameId());
			}
			i.putExtra("GAME", game);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_UPDATE_PARTY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_PARTY) {
			Party party = PartyDao.getInstance(this).getParty(current.getId());
			PartyDao.getInstance(this).delete(party);
			init();
		}
		return true;
	}

	private void buildList() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.party_list);
		ln.removeAllViews();
		if (parties.size() > 0) {
			addElementToList(parties);
		} else {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_party_list);
			ln.addView(tv);
		}
	}

	private void addElementToList(List<PartyForList> newParties) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.party_list);
		if (newParties != parties) {
			parties.addAll(newParties);
			if (ln.getChildCount() > 0) {
				ln.removeViewAt(ln.getChildCount() - 1);
			}
		}
		for (final PartyForList party : newParties) {
			if (game == null && !party.getGameName().equals(lastGameName)) {
				lastGameName = party.getGameName();
				TextView txt = new TextView(this);
				txt.setBackgroundColor(getResources().getColor(R.color.black));
				txt.setText(party.getGameName());
				ln.addView(txt);
			}
			PartySummaryView view = new PartySummaryView(this, party);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					viewParty(party);
				}
			});
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					registerForContextMenu(v);
					openContextMenu(v);
					unregisterForContextMenu(v);
					return true;
				}
			});

			ln.addView(view);
		}

		if (!allLoaded) {
			LinearLayout lnExpand = new LinearLayout(this);
			LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lnExpand.setLayoutParams(lp);
			ImageView im = new ImageView(this);
			LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			imglp.gravity = Gravity.CENTER_VERTICAL;
			imglp.rightMargin = 5;
			im.setLayoutParams(imglp);

			im.setImageDrawable(getResources().getDrawable(R.drawable.loading));
			lnExpand.addView(im);

			LinearLayout accountLayout = new LinearLayout(this);
			LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
			accountLayout.setLayoutParams(aclp);

			TextView tv = new TextView(this);
			tv.setText(getResources().getText(R.string.loading));
			LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(tlp);
			accountLayout.addView(tv);
			lnExpand.addView(accountLayout);

			ln.addView(lnExpand);
			Animation a = new RotateAnimation(0, 360, 23.5f, 23.5f);
			a.setInterpolator(new LinearInterpolator());
			a.setRepeatCount(Animation.INFINITE);
			a.setDuration(2000);
			im.startAnimation(a);
		}
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void importEnded(boolean succeed, List<PartyForList> newParties) {
		if (succeed) {
			task = null;
			if (newParties != null && newParties.size() > 0) {
				if (newParties.size() == PAGE_SIZE + 1) {
					newParties.remove(PAGE_SIZE);
					allLoaded = false;
				} else {
					allLoaded = true;
				}
			} else {
				allLoaded = true;
			}
			if (loadedPage != 0) {
				addElementToList(newParties);
			} else {
				parties = newParties;
				buildList();
			}
			loadedPage++;
		}
		loading = false;
	}

	private void viewParty(PartyForList partyForList) {
		Party party = PartyDao.getInstance(this).getParty(partyForList.getId());
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final View vvv = LayoutInflater.from(this).inflate(R.layout.view_party, null);
		// vvv.setBackgroundColor(getResources().getColor(R.color.white));
		alert.setView(vvv);

		Game game = GameDao.getInstance(this).getGame(party.getGameId());
		ImageView imgGame = (ImageView) vvv.findViewById(R.id.img_game);
		File f = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.DIRECTORY + "/" + game.getImageName());
		Bitmap bitmap = null;
		if (f.exists()) {
			bitmap = BitmapFactory.decodeFile(f.toString());
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_game_image);
		}

		imgGame.setImageBitmap(bitmap);

		final TextView cbDate = (TextView) vvv.findViewById(R.id.cbDate);
		cbDate.setText(DateUtil.defaultDateFormat.format(party.getDate()));
		final TextView txtCity = (TextView) vvv.findViewById(R.id.txtCity);
		txtCity.setText(party.getCity());
		final TextView txtEvent = (TextView) vvv.findViewById(R.id.txtEvent);
		txtEvent.setText(party.getEvent());

		ImageView img = (ImageView) vvv.findViewById(R.id.imgRating);

		if (party.getHappyness() > 0 && party.getHappyness() < 6) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(
					getContext().getResources().getIdentifier("happy_" + party.getHappyness() + "_on", "drawable", ApplicationConstants.PACKAGE)));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.happy_0));
		}

		final TextView txtDuration = (TextView) vvv.findViewById(R.id.txtDuration);
		txtDuration.setText("" + party.getDuration());
		final TextView txtComment = (TextView) vvv.findViewById(R.id.txtComment);
		txtComment.setText("" + party.getComment());

		final LinearLayout ll = (LinearLayout) vvv.findViewById(R.id.play_stats_list);

		List<PlayStat> playStats = PlayStatDao.getInstance(this).getPlayStat(party);
		for (PlayStat playStat : playStats) {
			LinearLayout playStatLayout = new LinearLayout(getContext());
			LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
			playStatLayout.setOrientation(LinearLayout.VERTICAL);
			playStatLayout.setLayoutParams(aclp);
			TextView txtPlayerName = new TextView(getContext());
			LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			txtPlayerName.setLayoutParams(tlp);
			if (playStat != null) {
				if (playStat.getPlayer() == null) {
					txtPlayerName.setText(getResources().getText(R.string.default_player));
				} else {
					txtPlayerName.setText(playStat.getPlayer().getPseudo());
				}
			} else {
				txtPlayerName.setText("");
				playStatLayout.setVisibility(LinearLayout.INVISIBLE);
			}
			txtPlayerName.setTextSize(16);
			txtPlayerName.setTypeface(Typeface.DEFAULT_BOLD);
			// txtPlayerName.setTextColor(getContext().getResources().getColor(R.color.black));
			playStatLayout.addView(txtPlayerName);

			LinearLayout hl = new LinearLayout(getContext());
			LayoutParams hlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			hl.setLayoutParams(hlp);

			LinearLayout vl1 = new LinearLayout(getContext());
			LayoutParams vl1p = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
			vl1.setLayoutParams(vl1p);
			vl1.setOrientation(LinearLayout.VERTICAL);
			TextView txtRank = new TextView(getContext());
			if (playStat == null) {
				txtRank.setText(getResources().getText(R.string.rank));
			} else {
				txtRank.setText(getResources().getText(R.string.rank) + ": " + playStat.getRank());
			}
			vl1.addView(txtRank);

			hl.addView(vl1);

			LinearLayout vl2 = new LinearLayout(getContext());
			LayoutParams vl2p = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
			vl2.setLayoutParams(vl2p);
			vl2.setOrientation(LinearLayout.VERTICAL);
			TextView txtScore = new TextView(getContext());
			if (playStat == null) {
				txtScore.setText(getResources().getText(R.string.score));
			} else {
				txtScore.setText(getResources().getText(R.string.score) + ": " + playStat.getScore());
			}
			vl2.addView(txtScore);

			hl.addView(vl2);
			playStatLayout.addView(hl);
			ll.addView(playStatLayout);
		}
		alert.show();
	}
}
