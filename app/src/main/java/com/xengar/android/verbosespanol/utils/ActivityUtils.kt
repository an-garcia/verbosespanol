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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatDrawableManager
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.xengar.android.verbosespanol.BuildConfig
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.data.Conjugation
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COLOR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COMMON
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONJUGATION_NUMBER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_DEFINITION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERUNDIO_COMPUESTO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERUNDIO_SIMPLE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GROUP
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVO_COMPUESTO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVO_SIMPLE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_NOTES
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_PARTICIPIO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_RADICALS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_1
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_2
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_3
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SCORE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TERMINATION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_EN
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_FR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_PT
import com.xengar.android.verbosespanol.sync.AlarmReceiver
import com.xengar.android.verbosespanol.sync.JobSchedulerService
import com.xengar.android.verbosespanol.ui.DetailsActivity
import com.xengar.android.verbosespanol.ui.HelpActivity
import com.xengar.android.verbosespanol.ui.SearchActivity
import com.xengar.android.verbosespanol.ui.SettingsActivity
import com.xengar.android.verbosespanol.utils.Constants.CONJUGATION_ID
import com.xengar.android.verbosespanol.utils.Constants.DEFAULT_FONT_SIZE
import com.xengar.android.verbosespanol.utils.Constants.DEMO_MODE
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
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.PREF_VERSION_CODE_KEY
import com.xengar.android.verbosespanol.utils.Constants.SHARED_PREF_NAME
import com.xengar.android.verbosespanol.utils.Constants.TYPE_START_NOTIFICATIONS
import com.xengar.android.verbosespanol.utils.Constants.TYPE_VERB_NOTIFICATION
import com.xengar.android.verbosespanol.utils.Constants.USE_TEST_ADS
import com.xengar.android.verbosespanol.utils.Constants.USE_TEST_ALARM_INTERVALS
import com.xengar.android.verbosespanol.utils.Constants.VERB_ID
import com.xengar.android.verbosespanol.utils.Constants.VERB_NAME

/**
 * ActivityUtils. To handle common tasks.
 */
object ActivityUtils {

    private val TAG = ActivityUtils::class.java.simpleName


    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    fun saveIntToPreferences(context: Context, name: String, value: Int) {
        val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putInt(name, value)
        e.commit()
    }

    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    fun saveLongToPreferences(context: Context, name: String, value: Long) {
        val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putLong(name, value)
        e.commit()
    }

    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    fun saveBooleanToPreferences(context: Context, name: String, value: Boolean) {
        val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putBoolean(name, value)
        e.commit()
    }

    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    fun saveStringToPreferences(context: Context, name: String, value: String) {
        val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val e = prefs.edit()
        e.putString(name, value)
        e.commit()
    }

    /**
     * Launches Details Activity.
     * @param context context
     * @param id verb id
     * @param cId conjugation id
     * @param verb verb name
     * @param demoMode demo
     */
    fun launchDetailsActivity(context: Context, id: Long, cId: Long, verb: String,
                              demoMode: Boolean) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val bundle = Bundle()
        bundle.putString(VERB_NAME, verb)
        bundle.putLong(VERB_ID, id)
        bundle.putLong(CONJUGATION_ID, cId)
        bundle.putBoolean(DEMO_MODE, demoMode)
        intent.putExtras(bundle)

        context.startActivity(intent)
    }

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
     * Launches Search Activity.
     * @param context context
     */
    fun launchSearchActivity(context: Context) {
        val intent = Intent(context, SearchActivity::class.java)
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

    /**
     * Set the correct translation or hide the view.
     * @param context Context
     * @param textView view
     * @param verb Verb
     */
    fun setTranslation(context: Context, textView: TextView, verb: Verb) {
        val fontSize = Integer.parseInt(getPreferenceFontSize(context))
        when (getPreferenceTranslationLanguage(context)) {
            NONE -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
                textView.visibility = View.GONE
            }
            ENGLISH -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
                textView.text = verb.translationEN
            }
            FRENCH -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
                textView.text = verb.translationFR
            }
            PORTUGUESE -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
                textView.text = verb.translationPT
            }
        }
    }

    /**
     * Returns the translation of the verb according to the language.
     * @param verb Verb
     * @param language language
     * @return String
     */
    fun getTranslation(verb: Verb, language: String): String {
        var translation = ""
        when (language) {
            ENGLISH -> translation = verb.translationEN
            FRENCH -> translation = verb.translationFR
            PORTUGUESE -> translation = verb.translationPT
        }
        return translation
    }

    /**
     * Text we want to speak.
     * @param text String
     */
    fun speak(context: Context, tts: TextToSpeech?, text: String?) {
        if (text == null || tts == null) {
            return
        }

        // Use the current media player volume
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

        // Speak
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    /**
     * Generate all table verb columns.
     * @return String[]
     */
    fun allVerbColumns(): Array<String> {
        return arrayOf(COLUMN_ID, COLUMN_CONJUGATION_NUMBER, COLUMN_INFINITIVE, COLUMN_DEFINITION,
                COLUMN_SAMPLE_1, COLUMN_SAMPLE_2, COLUMN_SAMPLE_3, COLUMN_COMMON, COLUMN_GROUP,
                COLUMN_COLOR, COLUMN_SCORE, COLUMN_NOTES,
                COLUMN_TRANSLATION_EN, COLUMN_TRANSLATION_FR, COLUMN_TRANSLATION_PT)
    }

    /**
     * Generate all table conjugation columns.
     * @return String[]
     */
    fun allConjugationColumns(): Array<String> {
        return arrayOf(COLUMN_ID, COLUMN_TERMINATION, COLUMN_RADICALS,
                COLUMN_INFINITIVO_SIMPLE,
                COLUMN_INFINITIVO_COMPUESTO,
                COLUMN_PARTICIPIO,
                COLUMN_GERUNDIO_SIMPLE,
                COLUMN_GERUNDIO_COMPUESTO,

                COLUMN_IMPERATIVO_TU,
                COLUMN_IMPERATIVO_EL,
                COLUMN_IMPERATIVO_NOSOTROS,
                COLUMN_IMPERATIVO_VOSOTROS,
                COLUMN_IMPERATIVO_ELLOS,
                COLUMN_IMPERATIVO_NEGACION_TU,
                COLUMN_IMPERATIVO_NEGACION_EL,
                COLUMN_IMPERATIVO_NEGACION_NOSOTROS,
                COLUMN_IMPERATIVO_NEGACION_VOSOTROS,
                COLUMN_IMPERATIVO_NEGACION_ELLOS,

                COLUMN_INDICATIVO_PRESENTE_YO,
                COLUMN_INDICATIVO_PRESENTE_TU,
                COLUMN_INDICATIVO_PRESENTE_EL,
                COLUMN_INDICATIVO_PRESENTE_NOSOTROS,
                COLUMN_INDICATIVO_PRESENTE_VOSOTROS,
                COLUMN_INDICATIVO_PRESENTE_ELLOS,

                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO,
                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU,
                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL,
                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS,

                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS,

                COLUMN_INDICATIVO_FUTURO_SIMPLE_YO,
                COLUMN_INDICATIVO_FUTURO_SIMPLE_TU,
                COLUMN_INDICATIVO_FUTURO_SIMPLE_EL,
                COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS,
                COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS,
                COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS,

                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO,
                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU,
                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL,
                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS,
                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS,
                COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS,

                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS,

                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO,
                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU,
                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL,
                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS,

                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO,
                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU,
                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL,
                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS,
                COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS,

                COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO,
                COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU,
                COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL,
                COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS,
                COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS,
                COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS,

                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO,
                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU,
                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL,
                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS,
                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS,
                COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS,

                COLUMN_SUBJUNTIVO_PRESENTE_YO,
                COLUMN_SUBJUNTIVO_PRESENTE_TU,
                COLUMN_SUBJUNTIVO_PRESENTE_EL,
                COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS,
                COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS,
                COLUMN_SUBJUNTIVO_PRESENTE_ELLOS,

                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO,
                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU,
                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL,
                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS,

                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO,
                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU,
                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL,
                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS,
                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS,
                COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS,

                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO,
                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU,
                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL,
                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS,

                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO,
                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU,
                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL,
                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS,
                COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS,

                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO,
                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU,
                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL,
                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS,
                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS,
                COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS )
    }


    /**
     * Create a Verb from the current cursor position.
     * Note: columns must exist.
     * @param cursor Cursor
     * @return Verb
     */
    fun verbFromCursor(cursor: Cursor): Verb {
        return Verb(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CONJUGATION_NUMBER)),
                cursor.getString(cursor.getColumnIndex(COLUMN_INFINITIVE)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_DEFINITION)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SAMPLE_1)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SAMPLE_2)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SAMPLE_3)) ?: "",
                cursor.getInt(cursor.getColumnIndex(COLUMN_COMMON)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_GROUP)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION_EN)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION_FR)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION_PT)) ?: "")
    }

    /**
     * Create a Conjugation from the current cursor position.
     * Note: columns must exist.
     * @param cursor Cursor
     * @return Conjugation
     */
    fun conjugationFromCursor(cursor: Cursor): Conjugation {
        return Conjugation(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TERMINATION)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_RADICALS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INFINITIVO_SIMPLE)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INFINITIVO_COMPUESTO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_PARTICIPIO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_GERUNDIO_SIMPLE)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_GERUNDIO_COMPUESTO)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_ELLOS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NEGACION_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NEGACION_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NEGACION_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NEGACION_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_IMPERATIVO_NEGACION_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRESENTE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRESENTE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS)) ?: "",

                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS)) ?: "",
                cursor.getString(cursor.getColumnIndex(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS)) ?: ""
        )
    }

    /**
     * Checks if we should replace the letter with apostrophe.
     * @param text String
     * @return true or false
     */
    fun useApostrophe(text: String): Boolean {
        // L'apostrophe ( ' ) est un signe qui remplace une des voyelles ( a, e, i )
        // quand le mot qui suit commence lui-même par une voyelle ou un h muet.
        return (text.startsWith("a") || text.startsWith("e") || text.startsWith("i")
                || text.startsWith("o") || text.startsWith("u") || text.startsWith("h")
                || text.startsWith("â") || text.startsWith("à") || text.startsWith("ë")
                || text.startsWith("é") || text.startsWith("ê") || text.startsWith("è")
                || text.startsWith("î") || text.startsWith("ï") || text.startsWith("ô")
                || text.startsWith("û") || text.startsWith("ù") || text.startsWith("ü"))
    }

    /**
     * Converts the verb infinitive into the file image name.
     * @param infinitive verb
     * @return String
     */
    fun generateImageName(infinitive: String): String {
        // File names should use no spaces and english alphabet lowercase letters
        var imageName = infinitive
        imageName = replaceOccurrences(imageName, "'", "")
        imageName = replaceOccurrences(imageName, " ", "")
        imageName = replaceOccurrences(imageName, "á", "a")
        imageName = replaceOccurrences(imageName, "é", "e")
        imageName = replaceOccurrences(imageName, "í", "i")
        imageName = replaceOccurrences(imageName, "ó", "o")
        imageName = replaceOccurrences(imageName, "ú", "u")
        imageName = replaceOccurrences(imageName, "ü", "u")
        imageName = replaceOccurrences(imageName, "ñ", "n")
        return imageName
    }

    /**
     * Replace occurrences of string
     * @param text String
     * @param oldString String
     * @param newString String
     * @return formatted String
     */
    private fun replaceOccurrences(text: String, oldString: String, newString: String): String {
        return if (text.contains(oldString)) {
            text.replace(oldString.toRegex(), newString)
        } else text
    }

    /**
     * Sets the image id into the view.
     * @param context context
     * @param view imageView
     * @param id id
     */
    @SuppressLint("RestrictedApi")
    fun setImage(context: Context, view: ImageView, id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(context.resources.getDrawable(id, context.theme))
        } else {
            view.setImageDrawable(AppCompatDrawableManager.get().getDrawable(context, id))
        }
        view.visibility = View.VISIBLE
    }

    /**
     * Launch share text intent.
     * @param activity Activity
     * @param text String
     */
    fun launchShareText(activity: Activity, text: String) {
        // https://medium.com/google-developers/sharing-content-between-android-apps-2e6db9d1368b#.6usvw9n9p
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(text)
                .intent
        if (shareIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(shareIntent)
        }
    }

    /**
     * Schedules a repeating event to launch the verb Notifications.
     * @param context Context
     */
    fun scheduleRepeatingNotifications(context: Context) {
        // JobScheduler works since 21+ (Lollipop).
        // But, it doesn't allow to configure start time.
        // So, use it since API 26+ (Oreo) because there it's not safe to use AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            scheduleTask(context)
        } else {
            startAlarm(context)
        }
    }

    /**
     * Cancels the repeating event that launches the verb Notifications.
     * @param context Context
     */
    fun cancelRepeatingNotifications(context: Context) {
        // JobScheduler works since 21+ (Lollipop).
        // But, it doesn't allow to configure start time.
        // So, use it since API 26+ (Oreo) because there it's not safe to use AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cancelTask(context)
        } else {
            cancelAlarm(context)
        }
    }

    /**
     * Start an Alarm
     * @param context Context
     */
    fun startAlarm(context: Context) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
        val startTime = getPreferenceNotificationTime(context)
        var interval = AlarmManager.INTERVAL_HOUR * getPreferenceNotificationFrequency(context)

        if (USE_TEST_ALARM_INTERVALS) {
            interval = 10000 // 10 seconds
        }
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent)
        if (LOG) {
            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cancel Alarm
     * @param context Context
     */
    fun cancelAlarm(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
        manager.cancel(pendingIntent)
        if (LOG) {
            Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Schedules a repeating task.
     * @param context Context
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun scheduleTask(context: Context) {
        // https://code.tutsplus.com/tutorials/using-the-jobscheduler-api-on-android-lollipop--cms-23562
        // TODO: Fix multiple starts when englishverbs resets notifications
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(JobSchedulerService.JOB_ID,
                ComponentName(context.packageName, JobSchedulerService::class.java.name))

        var interval = AlarmManager.INTERVAL_HOUR * getPreferenceNotificationFrequency(context)
        if (USE_TEST_ALARM_INTERVALS) {
            interval = 10000 // 10 seconds
        }

        builder.setPeriodic(interval)
        builder.setPersisted(true)

        if (jobScheduler.schedule(builder.build()) == JobScheduler.RESULT_FAILURE) {
            //If something goes wrong
            if (LOG) {
                Toast.makeText(context, "Scheduled Notification Task Failed",
                        Toast.LENGTH_SHORT).show()
            }
        } else {
            if (LOG) {
                Toast.makeText(context, "Scheduled Notification Task Set",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Cancel all tasks
     * @param context Context
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun cancelTask(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(JobSchedulerService.JOB_ID)
        if (LOG) {
            Toast.makeText(context, "Scheduled Notification Task Canceled",
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check if it's the first run, and launch the Verb Notifications with AlarmManager.
     * @param context Context
     * @param firebaseAnalytics FirebaseAnalytics
     */
    fun checkFirstRun(context: Context/*, firebaseAnalytics: FirebaseAnalytics*/) {
        val notFound = -1
        // Get current version code
        val currentVersionCode = BuildConfig.VERSION_CODE

        // Get saved version code
        val prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val savedVersionCode : Int = prefs.getLong(PREF_VERSION_CODE_KEY, notFound.toLong()).toInt()

        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return

        } else if (savedVersionCode == notFound || currentVersionCode > savedVersionCode) {
            // This is a new install (or the user cleared the shared preferences) or upgrade
            ActivityUtils.scheduleRepeatingNotifications(context)
            //ActivityUtils.firebaseAnalyticsLogEventSelectContent(firebaseAnalytics,
            //        TYPE_START_NOTIFICATIONS, "First Run", TYPE_VERB_NOTIFICATION)
        }

        // Update the shared preferences with the current version code
        saveLongToPreferences(context, PREF_VERSION_CODE_KEY, currentVersionCode.toLong())
    }


    /**
     * Initializes and show the AdMob banner.
     * Needs to be called in onCreate of the activity.
     * https://firebase.google.com/docs/admob/android/quick-start
     * @param activity activity
     * @param listener LogAdListener
     *//*
    fun createAdMobBanner(activity: AppCompatActivity, listener: LogAdListener): AdView {
        val adMobAppId = activity.getString(R.string.admob_app_id)
        // Initialize AdMob
        MobileAds.initialize(activity.applicationContext, adMobAppId)

        val adView = activity.findViewById<AdView>(R.id.adView)
        // Set listener
        // https://firebase.google.com/docs/admob/android/ad-events
        adView.adListener = listener

        // Load an ad into the AdMob banner view.
        val adRequest: AdRequest = if (USE_TEST_ADS) {
            AdRequest.Builder()
                    // Use AdRequest.Builder.addTestDevice() to get test ads on this device.
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // SLONE SLONE Pilot_S5004 (Android 6.0, API 23)
                    .addTestDevice("4DF5D2AB04EBFA06FB2656A06D2C0EE3")
                    .build()
        } else {
            AdRequest.Builder().build()
        }
        adView.loadAd(adRequest)

        return adView
    }*/

    /**
     * Logs a Firebase Analytics select content event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#SELECT_CONTENT
     * @param analytics FirebaseAnalytics
     * @param id id
     * @param name name
     * @param type type
     *//*
    fun firebaseAnalyticsLogEventSelectContent(analytics: FirebaseAnalytics, id: String,
                                               name: String, type: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type)
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }*/

    /**
     * Logs a Firebase Analytics search event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#SEARCH
     * @param analytics FirebaseAnalytics
     * @param search string to search
     *//*
    fun firebaseAnalyticsLogEventSearch(analytics: FirebaseAnalytics, search: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, search)
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }*/

    /**
     * Logs a Firebase Analytics view search results event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#VIEW_SEARCH_RESULTS
     * @param analytics FirebaseAnalytics
     * @param search string to search
     *//*
    fun firebaseAnalyticsLogEventViewSearchResults(analytics: FirebaseAnalytics, search: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, search)
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle)
    }*/

    /**
     * Logs a Firebase Analytics view item event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#VIEW_ITEM
     * @param analytics FirebaseAnalytics
     * @param id id
     * @param name name
     * @param category category
     *//*
    fun firebaseAnalyticsLogEventViewItem(analytics: FirebaseAnalytics, id: String, name: String,
                                          category: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }*/

}