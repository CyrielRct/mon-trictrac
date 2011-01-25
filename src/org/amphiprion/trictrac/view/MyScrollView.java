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
package org.amphiprion.trictrac.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.ScrollView;

/**
 * @author amphiprion
 * 
 */
public class MyScrollView extends ScrollView {
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** The scroll change listener. */
	private OnScrollChangedListener listener;

	/**
	 * Default constructor.
	 * 
	 * @param context
	 *            the context
	 */
	public MyScrollView(Context context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		listener.onScrollChanged();
	}

	/**
	 * Define the scroll change listener.
	 * 
	 * @param l
	 *            the listener
	 */
	public void setOnScrollChanged(OnScrollChangedListener l) {
		listener = l;
	}
}
