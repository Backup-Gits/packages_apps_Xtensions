/*
 *  Copyright (C) 2020-21 The Project-Xtended
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.xtended.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.xtended.support.preferences.CustomSeekBarPreference;
import com.xtended.support.preferences.SystemSettingListPreference;
import com.xtended.support.preferences.SecureSettingListPreference;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;
import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class LockscreenUi extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String LOCKSCREEN_CLOCK = "lockscreen_clock";
    private static final String LOCKSCREEN_CUSTOM_TEXT_CLOCK = "lockscreen_custom_text_clock";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String CUSTOM_TEXT_CLOCK_FONTS = "custom_text_clock_fonts";
    private static final String LOCK_DATE_FONTS = "lock_date_fonts";
    private static final String CLOCK_FONT_SIZE  = "lockclock_font_size";
    private static final String CUSTOM_TEXT_CLOCK_FONT_SIZE  = "custom_text_clock_font_size";
    private static final String DATE_FONT_SIZE  = "lockdate_font_size";
    private static final String LOCK_OWNERINFO_FONTS = "lock_ownerinfo_fonts";
    private static final String LOCKOWNER_FONT_SIZE = "lockowner_font_size";
    private static final String PREF_LS_CLOCK_SELECTION = "lockscreen_clock_selection";
    private static final String PREF_LS_CLOCK_ANIM_SELECTION = "lockscreen_clock_animation_selection";
    private static final String LOTTIE_ANIMATION_SIZE = "lockscreen_clock_animation_size";

    private Preference mLockClockExt;
    private PreferenceCategory mLockCustomTextClock;
    private ListPreference mLockClockFonts;
    private ListPreference mTextClockFonts;
    private ListPreference mLockDateFonts;
    private ListPreference mLockOwnerInfoFonts;
    private CustomSeekBarPreference mClockFontSize;
    private CustomSeekBarPreference mCustomTextClockFontSize;
    private CustomSeekBarPreference mDateFontSize;
    private CustomSeekBarPreference mOwnerInfoFontSize;
    private SecureSettingListPreference mLockClockSelection;
    private SystemSettingListPreference mLockClockAnimSelection;
    private CustomSeekBarPreference mLottieAnimationSize;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.x_lockscreen_ui);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Lockscren Clock Fonts
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 34)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);

        // Lockscren Text Clock Fonts
        mTextClockFonts = (ListPreference) findPreference(CUSTOM_TEXT_CLOCK_FONTS);
        mTextClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.CUSTOM_TEXT_CLOCK_FONTS, 32)));
        mTextClockFonts.setSummary(mTextClockFonts.getEntry());
        mTextClockFonts.setOnPreferenceChangeListener(this);

        // Lockscren Date Fonts
        mLockDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mLockDateFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_DATE_FONTS, 32)));
        mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        mLockDateFonts.setOnPreferenceChangeListener(this);

        // Lockscren OwnerInfo Fonts
        mLockOwnerInfoFonts = (ListPreference) findPreference(LOCK_OWNERINFO_FONTS);
        mLockOwnerInfoFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_OWNERINFO_FONTS, 0)));
        mLockOwnerInfoFonts.setSummary(mLockOwnerInfoFonts.getEntry());
        mLockOwnerInfoFonts.setOnPreferenceChangeListener(this);

        // Lock Clock Size
        mClockFontSize = (CustomSeekBarPreference) findPreference(CLOCK_FONT_SIZE);
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKCLOCK_FONT_SIZE, 78));
        mClockFontSize.setOnPreferenceChangeListener(this);

        // Custom Text Clock Size
        mCustomTextClockFontSize = (CustomSeekBarPreference) findPreference(CUSTOM_TEXT_CLOCK_FONT_SIZE);
        mCustomTextClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.CUSTOM_TEXT_CLOCK_FONT_SIZE, 40));
        mCustomTextClockFontSize.setOnPreferenceChangeListener(this);

        // Lock Date Size
        mDateFontSize = (CustomSeekBarPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKDATE_FONT_SIZE,18));
        mDateFontSize.setOnPreferenceChangeListener(this);

        // Lockscren OwnerInfo Size
        mOwnerInfoFontSize = (CustomSeekBarPreference) findPreference(LOCKOWNER_FONT_SIZE);
        mOwnerInfoFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKOWNER_FONT_SIZE,21));
        mOwnerInfoFontSize.setOnPreferenceChangeListener(this);

        mLockClockAnimSelection = (SystemSettingListPreference) findPreference(PREF_LS_CLOCK_ANIM_SELECTION);

        mLottieAnimationSize = (CustomSeekBarPreference) findPreference(LOTTIE_ANIMATION_SIZE);
        int lottieSize = Settings.System.getIntForUser(ctx.getContentResolver(),
                Settings.System.LOCKSCREEN_CLOCK_ANIMATION_SIZE, res.getIdentifier("com.android.systemui:dimen/lottie_animation_width_height", null, null), UserHandle.USER_CURRENT);
        mLottieAnimationSize.setValue(lottieSize);
        mLottieAnimationSize.setOnPreferenceChangeListener(this);

        mLockClockExt = (Preference) findPreference(LOCKSCREEN_CLOCK);
        mLockCustomTextClock = (PreferenceCategory) findPreference(LOCKSCREEN_CUSTOM_TEXT_CLOCK);

        mLockClockSelection = (SecureSettingListPreference) findPreference(PREF_LS_CLOCK_SELECTION);
        int val = Settings.Secure.getIntForUser(resolver,
                Settings.Secure.LOCKSCREEN_CLOCK_SELECTION, 2, UserHandle.USER_CURRENT);
        mLockClockSelection.setOnPreferenceChangeListener(this);
        if (val > 3 && val < 8) {
            mLockClockAnimSelection.setEnabled(true);
            mLottieAnimationSize.setEnabled(true);
        } else {
            mLockClockAnimSelection.setEnabled(false);
            mLottieAnimationSize.setEnabled(false);
        }
        if (val == 1 || val == 2) {
            mLockClockExt.setEnabled(true);
        } else {
            mLockClockExt.setEnabled(false);
        }
        if (val == 8) {
            mLockCustomTextClock.setEnabled(true);
        } else {
            mLockCustomTextClock.setEnabled(false);
        }
        if (val > 1 && val < 8) {
            mLockClockFonts.setEnabled(true);
            mClockFontSize.setEnabled(true);
        } else {
            mLockClockFonts.setEnabled(false);
            mClockFontSize.setEnabled(false);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mLockClockFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
        } else if (preference == mTextClockFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.CUSTOM_TEXT_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mTextClockFonts.setValue(String.valueOf(newValue));
            mTextClockFonts.setSummary(mTextClockFonts.getEntry());
            return true;
        } else if (preference == mLockDateFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_DATE_FONTS,
                    Integer.valueOf((String) newValue));
            mLockDateFonts.setValue(String.valueOf(newValue));
            mLockDateFonts.setSummary(mLockDateFonts.getEntry());
            return true;
        } else if (preference == mLockOwnerInfoFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_OWNERINFO_FONTS,
                    Integer.valueOf((String) newValue));
            mLockOwnerInfoFonts.setValue(String.valueOf(newValue));
            mLockOwnerInfoFonts.setSummary(mLockOwnerInfoFonts.getEntry());
            return true;
        } else if (preference == mClockFontSize) {
            int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKCLOCK_FONT_SIZE, top*1);
            return true;
        } else if (preference == mCustomTextClockFontSize) {
            int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.CUSTOM_TEXT_CLOCK_FONT_SIZE, top*1);
            return true;
        } else if (preference == mDateFontSize) {
            int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKDATE_FONT_SIZE, top*1);
            return true;
        } else if (preference == mOwnerInfoFontSize) {
            int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKOWNER_FONT_SIZE, top*1);
            return true;
        } else if (preference == mLottieAnimationSize) {
            int value = (Integer) newValue;
            Settings.System.putIntForUser(getContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_ANIMATION_SIZE, value, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mLockClockSelection) {
            int val = Integer.parseInt((String) newValue);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.LOCKSCREEN_CLOCK_SELECTION, val);
            if (val > 3 && val < 8) {
                mLockClockAnimSelection.setEnabled(true);
                mLottieAnimationSize.setEnabled(true);
            } else {
                mLockClockAnimSelection.setEnabled(false);
                mLottieAnimationSize.setEnabled(false);
            }
            if (val == 1 || val == 2) {
                mLockClockExt.setEnabled(true);
            } else {
                mLockClockExt.setEnabled(false);
            }
            if (val == 8) {
                mLockCustomTextClock.setEnabled(true);
            } else {
                mLockCustomTextClock.setEnabled(false);
            }
            if (val > 1 && val < 8) {
                mLockClockFonts.setEnabled(true);
                mClockFontSize.setEnabled(true);
            } else {
                mLockClockFonts.setEnabled(false);
                mClockFontSize.setEnabled(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.XTENSIONS;
    }

    /**
     * For Search
     */

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.x_lockscreen_ui;
                    result.add(sir);
                    return result;
                }

           @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
    };
}
