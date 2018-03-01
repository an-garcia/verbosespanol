/*
 * Copyright (C) 2018 Angel Garcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xengar.android.verbosespanol.utils

import android.content.Context
import android.content.Intent
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.text.Html
import android.text.Spanned
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.ui.HelpActivity
import com.xengar.android.verbosespanol.ui.SettingsActivity
import com.xengar.android.verbosespanol.utils.Constants.DEFAULT_FONT_SIZE
import com.xengar.android.verbosespanol.utils.Constants.ENGLISH
import com.xengar.android.verbosespanol.utils.Constants.FAVORITES
import com.xengar.android.verbosespanol.utils.Constants.LIST
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_100
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_1000
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_25
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_300
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_50
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_500
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_ALL
import com.xengar.android.verbosespanol.utils.Constants.NONE
import com.xengar.android.verbosespanol.utils.Constants.PORTUGUESE
import com.xengar.android.verbosespanol.utils.Constants.FRENCH

/**
 * ActivityUtils. To handle common tasks.
 */
object ActivityUtils {

    private val TAG = ActivityUtils::class.java.simpleName



    /**
     * Launches Help Activity.
     * @param context context
     */
    fun launchHelpActivity(context: Context) {
        val intent = Intent(context, HelpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Launches Settings Activity.
     * @param context context
     */
    fun launchSettingsActivity(context: Context) {
        val intent = Intent(context, SettingsActivity::class.java)
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                SettingsActivity.GeneralPreferenceFragment::class.java.name)
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true)
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, R.string.settings)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Helper class to handle deprecated method.
     * Source: http://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
     * @param html html string
     * @return Spanned
     */
    fun fromHtml(html: String): Spanned {
        val result: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
        return result
    }


    /**
     * Returns the value of show definitions from preferences.
     * @param context context
     * @return boolean or default(true)
     */
    fun getPreferenceShowDefinitions(context: Context): Boolean {
        val key = context.getString(R.string.pref_show_definitions_switch)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(key, true)
    }

    /**
     * Returns the value of show definitions from preferences.
     * @param context context
     * @return boolean or default(true)
     */
    fun getPreferenceFontSize(context: Context): String {
        val key = context.getString(R.string.pref_font_size)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(key, DEFAULT_FONT_SIZE)
    }

    /**
     * Returns the translation language from preferences.
     * @param context Context
     * @return code of language (default NONE)
     */
    fun getPreferenceTranslationLanguage(context: Context): String {
        val key = context.getString(R.string.pref_translation_language)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lang = prefs.getString(key, "None")
        return when (lang) {
            "", "None" -> NONE
            "en_EN" -> ENGLISH
            "fr_FR" -> FRENCH
            "pt_PT" -> PORTUGUESE
            else -> NONE
        }
    }

    /**
     * Returns the favorites mode from preferences.
     * @param context context
     * @return CARD or LIST
     */
    fun getPreferenceFavoritesMode(context: Context): String {
        val key = context.getString(R.string.pref_favorite_mode_list)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(key, LIST)
    }


    /**
     * Returns the value of enable verb notifications from preferences.
     * @param context context
     * @return boolean or default(true)
     */
    fun getPreferenceEnableNotifications(context: Context): Boolean {
        val key = context.getString(R.string.pref_enable_notifications)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(key, true)
    }

    /**
     * Returns the notification list from preferences.
     * @param context Context
     * @return list of notifications to use
     */
    fun getPreferenceNotificationList(context: Context): String {
        val key = context.getString(R.string.pref_notification_list)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val notificationList = prefs.getString(key, "25")
        return when (notificationList) {
            "1" -> FAVORITES
            "50" -> MOST_COMMON_50
            "100" -> MOST_COMMON_100
            "300" -> MOST_COMMON_300
            "500" -> MOST_COMMON_500
            "1000" -> MOST_COMMON_1000
            "9000" -> MOST_COMMON_ALL
            "25" -> MOST_COMMON_25
            else -> MOST_COMMON_25
        }
    }

    /**
     * Returns the notification frequency from preferences.
     * @param context Context
     * @return notification frequency
     */
    fun getPreferenceNotificationFrequency(context: Context): Int {
        val key = context.getString(R.string.pref_notification_frequency)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val frequency = prefs.getString(key, "24")
        return when (frequency) {
            "1", "3", "6", "12" -> Integer.parseInt(frequency)
            "24" -> 24
            else -> 24
        }
    }

    /**
     * Returns the notification time from preferences.
     * @param context Context
     * @return notification time
     */
    fun getPreferenceNotificationTime(context: Context): Long {
        val key = context.getString(R.string.pref_notification_time)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getLong(key, 0)
    }

}