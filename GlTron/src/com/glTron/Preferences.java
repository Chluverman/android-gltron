/*
 * Copyright © 2012 Ravi Agarwal (flide)
 *
 * Based on Android port of GLtron by Iain Churcher and original source code can be found at :
 * https://github.com/Chluverman/android-gltron.git
 *
 * This file is part of GL TRON.
 *
 * GL TRON is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GL TRON is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GL TRON.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.glTron;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.glTron.logging.Logger;

public class Preferences extends PreferenceActivity {
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
		Logger.v(this, "Setting up Preferences");
	        addPreferencesFromResource(R.layout.preferences);
	    }
}
