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
package com.xengar.android.verbosespanol.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatDelegate
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.Constants.TYPE_START_NOTIFICATIONS
import com.xengar.android.verbosespanol.utils.Constants.TYPE_STOP_NOTIFICATIONS
import com.xengar.android.verbosespanol.utils.Constants.TYPE_VERB_NOTIFICATION
import com.xengar.android.verbosespanol.utils.FontDialog

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    //private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setupActionBar()

        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    // Registers a shared preference change listener that gets notified when preferences change.
    override fun onResume() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.registerOnSharedPreferenceChangeListener(this)
        super.onResume()
    }

    // Unregisters a shared preference change listener.
    override fun onPause() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    /**
     * Called after a preference changes.
     * @param sharedPreferences SharedPreferences
     * @param key key
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.pref_enable_notifications)
                || key == getString(R.string.pref_notification_list)
                || key == getString(R.string.pref_notification_time)
                || key == getString(R.string.pref_notification_frequency)) {
            // Reconfigure Verb Notifications
            val enabled = ActivityUtils.getPreferenceEnableNotifications(applicationContext)
            if (!enabled) {
                ActivityUtils.cancelRepeatingNotifications(applicationContext)
                //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
                //        TYPE_STOP_NOTIFICATIONS, "Preferences", TYPE_VERB_NOTIFICATION)
            } else {
                ActivityUtils.scheduleRepeatingNotifications(applicationContext)
                //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
                //        TYPE_START_NOTIFICATIONS, "Preferences", TYPE_VERB_NOTIFICATION)
            }
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {

        private val sharedPrefsChangeListener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> updateSummary() }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // TODO: Find a way to calculate the start time and implement it
            val resourceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                R.xml.pref_general2
            else
                R.xml.pref_general
            addPreferencesFromResource(resourceId)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_translation_language)))
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_favorite_mode_list)))
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_font_size)))
            var dataPref = findPreference(getString(R.string.pref_notification_list)) as ListPreference
            bindPreferenceSummaryToValue(dataPref)
            if (dataPref.value == null) {
                dataPref.setValueIndex(1)
                dataPref.summary = getString(R.string.most_common_25)
            }
            dataPref = findPreference(getString(R.string.pref_notification_frequency)) as ListPreference
            bindPreferenceSummaryToValue(dataPref)
            if (dataPref.value == null) {
                dataPref.setValueIndex(4)
                dataPref.summary = getString(R.string.hour_24)
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, MainActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        private fun updateSummary() {
            val fontPref = findPreference(getString(R.string.pref_font_size))
            fontPref.summary = ActivityUtils.getPreferenceFontSize(activity)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen
                    .sharedPreferences
                    .unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener)
        }

        override fun onResume() {
            super.onResume()
            updateSummary()
            preferenceScreen
                    .sharedPreferences
                    .registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener)
        }
    }


    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener =
                Preference.OnPreferenceChangeListener { preference, value ->
                    val stringValue = value.toString()

                    when (preference) {
                        is ListPreference -> {
                            // For list preferences, look up the correct display value in
                            // the preference's 'entries' list.
                            val index = preference.findIndexOfValue(stringValue)

                            // Set the summary to reflect the new value.
                            preference.setSummary(
                                    if (index >= 0)
                                        preference.entries[index]
                                    else
                                        null)
                        }
                        is SwitchPreference -> // For a boolean value, set the default value "true"
                            preference.setDefaultValue(stringValue.contains("t"))
                        else -> // For all other preferences, set the summary to the value's
                            // simple string representation.
                            preference.summary = stringValue
                    }
                    true
                }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference' current value.
            if (preference is ListPreference
                    || preference is EditTextPreference
                    || preference is FontDialog) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.context)
                                .getString(preference.key, ""))
            } else if (preference is SwitchPreference || preference is CheckBoxPreference) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.context)
                                .getBoolean(preference.key, true))
            }
        }
    }
}
