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

import android.app.LoaderManager
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.colorpicker.ColorPickerPalette
import com.android.colorpicker.ColorPickerSwatch
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.data.Conjugation
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COLOR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_CONJUGATIONS_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_FAVORITES_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_VERBS_URI
import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.Constants.CONJUGATION_ID
import com.xengar.android.verbosespanol.utils.Constants.DEMO_MODE
import com.xengar.android.verbosespanol.utils.Constants.DRAWABLE
import com.xengar.android.verbosespanol.utils.Constants.EL
import com.xengar.android.verbosespanol.utils.Constants.ELLOS
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.NOSOTROS
import com.xengar.android.verbosespanol.utils.Constants.QUE
import com.xengar.android.verbosespanol.utils.Constants.TU
import com.xengar.android.verbosespanol.utils.Constants.VERB
import com.xengar.android.verbosespanol.utils.Constants.VERB_ID
import com.xengar.android.verbosespanol.utils.Constants.VERB_NAME
import com.xengar.android.verbosespanol.utils.Constants.VOSOTROS
import com.xengar.android.verbosespanol.utils.Constants.YO

import kotlinx.android.synthetic.main.activity_details.*
import java.util.*

/**
 * DetailsActivity
 */
class DetailsActivity
    : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    companion object {

        private val TAG = DetailsActivity::class.java.simpleName
        private val VERB_LOADER = 0
        private val CONJUGATION_LOADER = 1
    }


    private var fabAdd: FloatingActionButton? = null
    private var fabDel: FloatingActionButton? = null
    private var verbName = ""
    private var verbID:Long = -1
    private var conjugationID:Long = -1
    private var verb: Verb? = null
    private var conjugation: Conjugation? = null
    private var tts: TextToSpeech? = null
    private var infinitive: TextView? = null
    private var regular: TextView? = null
    private var definition: TextView? = null
    private var translation: TextView? = null
    private var sample1: TextView? = null
    private var sample2: TextView? = null
    private var sample3: TextView? = null

    //private var mFirebaseAnalytics:FirebaseAnalytics? = null
    //private var mAdView:AdView? = null

    // Demo
    private var showcaseView: ShowcaseView? = null
    private var scrollView: NestedScrollView? = null
    private var demo = false
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        // changing status bar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorPrimaryDark)
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras
        if (bundle != null) {
            demo = bundle.getBoolean(DEMO_MODE, false)
            verbName = bundle.getString(VERB_NAME, "")
            verbID = bundle.getLong(VERB_ID, -1)
            conjugationID = bundle.getLong(CONJUGATION_ID, -1)
        }
        else {
            if (LOG) {
                Log.e(TAG, "bundle is null! This should not happen. verbId needed")
            }
        }

        //Text
        infinitive = findViewById(R.id.infinitive)
        regular = findViewById(R.id.regular)
        definition = findViewById(R.id.definition)
        translation = findViewById(R.id.translation)
        sample1 = findViewById(R.id.sample1)
        sample2 = findViewById(R.id.sample2)
        sample3 = findViewById(R.id.sample3)

        // define click listeners
        setClickListeners()

        // initialize Speaker
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.FRENCH)
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (LOG) {
                        Log.e("TTS", "This Language is not supported")
                    }
                }
            }
            else {
                if (LOG) {
                    Log.e("TTS", "Initilization Failed!")
                }
            }
        })

        // Initialize a loader to read the verb data from the database and display it
        loaderManager.initLoader(VERB_LOADER, null, this)
        showFavoriteButtons()

        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!, PAGE_VERB_DETAILS,
        //        PAGE_VERB_DETAILS, TYPE_PAGE)

        // create AdMob banner
        //val listener = LogAdListener(mFirebaseAnalytics!!, DETAILS_ACTIVITY)
        //mAdView = ActivityUtils.createAdMobBanner(this, listener)

        scrollView = findViewById(R.id.scroll)
        if (demo) {
            defineDemoMode()
        }
    }

    /**
     * Set click listeners to this object.
     */
    private fun setClickListeners() {
        findViewById<View>(R.id.play_infinitive).setOnClickListener(this)
        findViewById<View>(R.id.play_definition).setOnClickListener(this)
        findViewById<View>(R.id.play_sample1).setOnClickListener(this)
        findViewById<View>(R.id.play_sample2).setOnClickListener(this)
        findViewById<View>(R.id.play_sample3).setOnClickListener(this)

        findViewById<View>(R.id.infinitivo_simple).setOnClickListener(this)
        findViewById<View>(R.id.infinitivo_compuesto).setOnClickListener(this)
        findViewById<View>(R.id.participio).setOnClickListener(this)
        findViewById<View>(R.id.gerundio_simple).setOnClickListener(this)
        findViewById<View>(R.id.gerundio_compuesto).setOnClickListener(this)

        findViewById<View>(R.id.imperativo_presente_tu).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_presente_el).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_presente_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_presente_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_presente_ellos).setOnClickListener(this)

        findViewById<View>(R.id.imperativo_negativo_tu).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_negativo_el).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_negativo_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_negativo_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.imperativo_negativo_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_presente_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_presente_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_presente_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_presente_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_presente_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_presente_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_preterito_imperfecto_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_imperfecto_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_imperfecto_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_imperfecto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_imperfecto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_imperfecto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_perfecto_simple_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_preterito_anterior_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_anterior_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_anterior_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_anterior_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_anterior_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_preterito_anterior_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_futuro_simple_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_simple_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_simple_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_simple_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_simple_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_futuro_compuesto_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_compuesto_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_compuesto_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_compuesto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_compuesto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_futuro_compuesto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_condicional_simple_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_simple_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_simple_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_simple_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_simple_ellos).setOnClickListener(this)

        findViewById<View>(R.id.indicativo_condicional_compuesto_yo).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_compuesto_tu).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_compuesto_el).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_compuesto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_compuesto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.indicativo_condicional_compuesto_ellos).setOnClickListener(this)


        findViewById<View>(R.id.subjuntivo_presente_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_presente_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_presente_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_presente_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_presente_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_presente_ellos).setOnClickListener(this)

        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_imperfecto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_ellos).setOnClickListener(this)

        findViewById<View>(R.id.subjuntivo_futuro_simple_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_simple_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_simple_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_simple_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_simple_ellos).setOnClickListener(this)

        findViewById<View>(R.id.subjuntivo_futuro_compuesto_yo).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_compuesto_tu).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_compuesto_el).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_compuesto_nosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_compuesto_vosotros).setOnClickListener(this)
        findViewById<View>(R.id.subjuntivo_futuro_compuesto_ellos).setOnClickListener(this)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        /*if (mAdView != null) {
            mAdView!!.pause()
        }*/
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        /*if (mAdView != null) {
            mAdView!!.resume()
        }*/
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        /*if (mAdView != null) {
            mAdView!!.destroy()
        }*/
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_change_color -> {
                changeColorDialog()
                return true
            }

            R.id.action_search -> {
                ActivityUtils.launchSearchActivity(applicationContext)
                return true
            }

            R.id.action_share -> {
                ActivityUtils.launchShareText(this, createShareText())
                val verbName = if (verb != null) verb!!.infinitive else "verb name not available"
                //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
                //        "Verb: $verbName, VerbId: $verbID", TYPE_SHARE, TYPE_SHARE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Create the text to share.
     * @return String
     */
    private fun createShareText():String {
        var text = ""
        if (verb != null && conjugation != null)
        {
            text = ("Verb: " + verb?.infinitive
                    + "\n\n" + getString(R.string.definition) + ":\n" + verb!!.definition
                    + "\n\n" + getString(R.string.examples) + ":\n1. " + verb!!.sample1
                    + "\n2. " + verb!!.sample2
                    + "\n3. " + verb!!.sample3
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.presente) + ":"
                    + "\n" + conjugation!!.indicativoPresenteYo
                    + "\n" + conjugation!!.indicativoPresenteTu
                    + "\n" + conjugation!!.indicativoPresenteEl
                    + "\n" + conjugation!!.indicativoPresenteN
                    + "\n" + conjugation!!.indicativoPresenteV
                    + "\n" + conjugation!!.indicativoPresenteEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.preterito_perfecto_compuesto) + ":"
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoYo
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoTu
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoEl
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoN
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoV
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoCompuestoEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.preterito_imperfecto) + ":"
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoYo
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoTu
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoEl
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoN
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoV
                    + "\n" + conjugation!!.indicativoPreteritoImperfectoEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.preterito_pluscuamperfecto) + ":"
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoYo
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoTu
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoEl
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoN
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoV
                    + "\n" + conjugation!!.indicativoPreteritoPluscuamperfectoEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.preterito_perfecto_simple) + ":"
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleYo
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleTu
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleEl
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleN
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleV
                    + "\n" + conjugation!!.indicativoPreteritoPerfectoSimpleEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.preterito_anterior) + ":"
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorYo
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorTu
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorEl
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorN
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorV
                    + "\n" + conjugation!!.indicativoPreteritoAnteriorEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.futuro_simple) + ":"
                    + "\n" + conjugation!!.indicativoFuturoSimpleYo
                    + "\n" + conjugation!!.indicativoFuturoSimpleTu
                    + "\n" + conjugation!!.indicativoFuturoSimpleEl
                    + "\n" + conjugation!!.indicativoFuturoSimpleN
                    + "\n" + conjugation!!.indicativoFuturoSimpleV
                    + "\n" + conjugation!!.indicativoFuturoSimpleEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.futuro_compuesto) + ":"
                    + "\n" + conjugation!!.indicativoFuturoCompuestoYo
                    + "\n" + conjugation!!.indicativoFuturoCompuestoTu
                    + "\n" + conjugation!!.indicativoFuturoCompuestoEl
                    + "\n" + conjugation!!.indicativoFuturoCompuestoN
                    + "\n" + conjugation!!.indicativoFuturoCompuestoV
                    + "\n" + conjugation!!.indicativoFuturoCompuestoEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.condicional_simple) + ":"
                    + "\n" + conjugation!!.indicativoCondicionalSimpleYo
                    + "\n" + conjugation!!.indicativoCondicionalSimpleTu
                    + "\n" + conjugation!!.indicativoCondicionalSimpleEl
                    + "\n" + conjugation!!.indicativoCondicionalSimpleN
                    + "\n" + conjugation!!.indicativoCondicionalSimpleV
                    + "\n" + conjugation!!.indicativoCondicionalSimpleEll
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.condicional_compuesto) + ":"
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoYo
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoTu
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoEl
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoN
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoV
                    + "\n" + conjugation!!.indicativoCondicionalCompuestoEll

                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.presente) + ":"
                    + "\n" + conjugation!!.subjuntivoPresenteYo
                    + "\n" + conjugation!!.subjuntivoPresenteTu
                    + "\n" + conjugation!!.subjuntivoPresenteEl
                    + "\n" + conjugation!!.subjuntivoPresenteN
                    + "\n" + conjugation!!.subjuntivoPresenteV
                    + "\n" + conjugation!!.subjuntivoPresenteEll
                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.preterito_perfecto_compuesto) + ":"
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoYo
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoTu
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoEl
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoN
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoV
                    + "\n" + conjugation!!.subjuntivoPreteritoPerfectoCompuestoEll
                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.preterito_imperfecto) + ":"
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoYo
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoTu
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoEl
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoN
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoV
                    + "\n" + conjugation!!.subjuntivoPreteritoImperfectoEll
                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.preterito_pluscuamperfecto) + ":"
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoYo
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoTu
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoEl
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoN
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoV
                    + "\n" + conjugation!!.subjuntivoPreteritoPluscuamperfectoEll
                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.futuro_simple) + ":"
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleYo
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleTu
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleEl
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleN
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleV
                    + "\n" + conjugation!!.subjuntivoFuturoSimpleEll
                    + "\n\n" + getString(R.string.subjuntivo) + " " + getString(R.string.futuro_compuesto) + ":"
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoYo
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoTu
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoEl
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoN
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoV
                    + "\n" + conjugation!!.subjuntivoFuturoCompuestoEll

                    + "\n\n" + getString(R.string.imperativo) + " " + getString(R.string.presente) + ":"
                    + "\n" + conjugation!!.imperativoTu
                    + "\n" + conjugation!!.imperativoEl
                    + "\n" + conjugation!!.imperativoN
                    + "\n" + conjugation!!.imperativoV
                    + "\n" + conjugation!!.imperativoEll
                    + "\n\n" + getString(R.string.imperativo) + " " + getString(R.string.negativo) + ":"
                    + "\n" + conjugation!!.imperativoNegTu
                    + "\n" + conjugation!!.imperativoNegEl
                    + "\n" + conjugation!!.imperativoNegN
                    + "\n" + conjugation!!.imperativoNegV
                    + "\n" + conjugation!!.imperativoNegEll
                    + "\n\n" + getString(R.string.infinitive) + " " + getString(R.string.simple) + ":"
                    + "\n" + conjugation!!.infinitivoSimple
                    + "\n\n" + getString(R.string.infinitive) + " " + getString(R.string.compuesto) + ":"
                    + "\n" + conjugation!!.infinitivoCompuesto
                    + "\n\n" + getString(R.string.participio) + ":"
                    + "\n" + conjugation!!.participio
                    + "\n\n" + getString(R.string.gerundio) + " " + getString(R.string.simple) + ":"
                    + "\n" + conjugation!!.gerundioSimple
                    + "\n\n" + getString(R.string.gerundio) + " " + getString(R.string.compuesto) + ":"
                    + "\n" + conjugation!!.gerundioCompuesto )
        }
        return text
    }

    /**
     * Changes the color
     */
    private fun changeColorDialog() {
        val colors = intArrayOf(
                ContextCompat.getColor(applicationContext, R.color.colorBlack),
                ContextCompat.getColor(applicationContext, R.color.colorRed),
                ContextCompat.getColor(applicationContext, R.color.colorGreen),
                ContextCompat.getColor(applicationContext, R.color.colorBlue),
                ContextCompat.getColor(applicationContext, R.color.colorPink),
                ContextCompat.getColor(applicationContext, R.color.colorPurple),
                ContextCompat.getColor(applicationContext, R.color.colorDeepPurple),
                ContextCompat.getColor(applicationContext, R.color.colorIndigo),
                ContextCompat.getColor(applicationContext, R.color.colorOrange),
                ContextCompat.getColor(applicationContext, R.color.colorDeepOrange),
                ContextCompat.getColor(applicationContext, R.color.colorBrown),
                ContextCompat.getColor(applicationContext, R.color.colorBlueGray))

        val selectedColor = intArrayOf(colors[0])
        if (verb != null) {
            selectedColor[0] = verb!!.color
        }
        val layoutInflater = LayoutInflater.from(applicationContext)
        val colorPickerPalette = layoutInflater.inflate(R.layout.custom_picker, null) as ColorPickerPalette

        val listener = ColorPickerSwatch.OnColorSelectedListener { color ->
            selectedColor[0] = color
            colorPickerPalette.drawPalette(colors, selectedColor[0])
        }

        colorPickerPalette.init(colors.size, 4, listener)
        colorPickerPalette.drawPalette(colors, selectedColor[0])

        val alert = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle(R.string.select_color)
                .setPositiveButton(android.R.string.ok ){ dialog, which ->
                    // Save changes
                    saveColor(selectedColor[0])
                    setVerbColor(selectedColor[0])
                    verb!!.color = selectedColor[0]
                }
                .setView(colorPickerPalette)
                .create()
        alert.show()
    }

    /**
     * Save the color to database.
     * @param color Color
     */
    private fun saveColor(color:Int) {
        val values = ContentValues()
        values.put(COLUMN_COLOR, "" + color)
        val rowsAffected = contentResolver.update(CONTENT_VERBS_URI, values,
                "$COLUMN_ID = ?", arrayOf(java.lang.Long.toString(verbID)))

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            if (LOG) {
                Log.e(TAG, "Failed to change color to verb!")
            }
        }
    }

    /**
     * Defines if add or remove from Favorites should be initially visible for this movieId.
     */
    private fun showFavoriteButtons() {
        fabAdd = findViewById(R.id.fab_add)
        fabDel = findViewById(R.id.fab_minus)

        val cursor = contentResolver.query(CONTENT_FAVORITES_URI, arrayOf(COLUMN_ID), //select
                "$COLUMN_ID = ?", // where
                arrayOf(java.lang.Long.toString(verbID)), null)//whereArgs
        if (cursor != null && cursor.count != 0) {
            fabDel!!.visibility = View.VISIBLE
        }
        else {
            fabAdd!!.visibility = View.VISIBLE
        }
        cursor?.close()
    }

    /**
     * Defines what to do when click on add/remove from Favorites buttons.
     */
    private fun defineClickFavoriteButtons() {
        val DURATION = 1000

        fabAdd!!.setOnClickListener{ view ->
            Snackbar.make(view, getString(R.string.favorites_add_message), DURATION)
                    .setAction("Action", null).show()
            val values = ContentValues()
            values.put(COLUMN_ID, verbID)
            contentResolver.insert(CONTENT_FAVORITES_URI, values)

            fabAdd!!.visibility = View.INVISIBLE
            fabDel!!.visibility = View.VISIBLE
            val verbName = if (verb != null) verb!!.infinitive else "verb name not available"
            //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
            //        VERB_ID + " " + verbID, verbName, TYPE_ADD_FAV)
        }

        fabDel!!.setOnClickListener{ view ->
            Snackbar.make(view, getString(R.string.favorites_del_message), DURATION)
                    .setAction("Action", null).show()
            contentResolver.delete(CONTENT_FAVORITES_URI,
                    COLUMN_ID + " = ?",
                    arrayOf(java.lang.Long.toString(verbID)))

            fabAdd!!.visibility = View.VISIBLE
            fabDel!!.visibility = View.INVISIBLE
            val verbName = if (verb != null) verb!!.infinitive else "verb name not available"
            //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
            //        VERB_ID + " " + verbID, verbName, TYPE_DEL_FAV)
        }
    }


    override fun onCreateLoader(id:Int, args:Bundle?): Loader<Cursor>? {
        var cursorLoader: CursorLoader? = null
        when (id) {
            CONJUGATION_LOADER -> cursorLoader = CursorLoader(this, CONTENT_CONJUGATIONS_URI,
                    ActivityUtils.allConjugationColumns(), // Columns in the resulting Cursor
                    "$COLUMN_ID = ?", // selection clause
                    arrayOf(java.lang.Long.toString(conjugationID)), null)// selection arguments
        // Default sort order

            VERB_LOADER -> cursorLoader = CursorLoader(this, // Parent activity context
                    CONTENT_VERBS_URI, ActivityUtils.allVerbColumns(), // Columns in the resulting Cursor
                    "$COLUMN_ID = ?", // selection clause
                    arrayOf(java.lang.Long.toString(verbID)), null)// selection arguments
        // Default sort order
            else -> {}
        }
        return cursorLoader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.count < 1) {
            //finish();
            return   // the verb or conjugation doesn't exist, this should not happen.
        }

        when (loader.id) {
            CONJUGATION_LOADER -> if (cursor.moveToFirst()) {
                conjugation = ActivityUtils.conjugationFromCursor(cursor)
                processConjugation(conjugation!!)
                fillConjugationDetails(conjugation!!)
            }

            VERB_LOADER -> {
                // Proceed with moving to the first row of the cursor and reading data from it
                // (This should be the only row in the cursor)
                if (cursor.moveToFirst()) {
                    verb = ActivityUtils.verbFromCursor(cursor)
                    supportActionBar!!.title = verb!!.infinitive
                    setVerbColor(verb!!.color)
                    fillVerbDetails(verb!!)
                    defineClickFavoriteButtons()
                }
                loaderManager.initLoader(CONJUGATION_LOADER, null, this)
            }
            else -> {}
        }
    }

    /**
     * Handles the conjugation of the verb.
     * @param c Conjugation
     */
    private fun processConjugation(c:Conjugation) {
        if (!c.infinitivoSimple.isEmpty() && !c.infinitivoSimple.contentEquals(verbName)) {
            // if we need, conjugate the verb model.
            conjugateVerb(c, verbName)
        }
        addPronoms(c)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    /**
     * Fill verb details.
     * @param verb Verb
     */
    private fun fillVerbDetails(verb:Verb) {
        // Update the views on the screen with the values from the database
        infinitive!!.text = verb.infinitive
        when (verb.regular) {
            0 -> regular!!.text = getString(R.string.regular)
            1 -> regular!!.text = getString(R.string.irregular)
        }

        definition!!.text = verb.definition
        sample1!!.text = verb.sample1
        sample2!!.text = verb.sample2
        sample3!!.text = verb.sample3

        val fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(applicationContext))
        (findViewById<View>(R.id.regular) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        (findViewById<View>(R.id.definition_title) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        (findViewById<View>(R.id.examples_title) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        definition!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        sample1!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        sample2!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        sample3!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

        ActivityUtils.setTranslation(applicationContext, translation!!, verb)

        // Try to put the verb image
        val imageVerb = findViewById<ImageView>(R.id.verb_image)
        /*val imageId = resources.getIdentifier(
                VERB + removeAccents(verb.infinitive), DRAWABLE, packageName) */
        val imageId = resources.getIdentifier(VERB + "ensenar", DRAWABLE, packageName)
        if (imageId != 0) {
            ActivityUtils.setImage(applicationContext, imageVerb, imageId)
        }

        //ActivityUtils.firebaseAnalyticsLogEventViewItem(
        //        mFirebaseAnalytics!!, "" + verbID, verb.infinitive, VERBS)
    }

    /**
     * Returns the name of file with a-z characters.
     * @param verbName verb name
     * @return verb name
     */
    private fun removeAccents(verbName:String):String {
        var name = verbName
        name = name.replace('á', 'a')
        name = name.replace('é', 'e')
        name = name.replace('í', 'i')
        name = name.replace('ó', 'o')
        name = name.replace('ú', 'u')
        name = name.replace('ü', 'u')
        name = name.replace('ñ', 'n')
        return name
    }


    /**
     * Fills the conjugation section.
     * @param c Conjugation ready to display
     */
    private fun fillConjugationDetails(c: Conjugation) {
        val fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(applicationContext))
        changeTextFontInConjugation(fontSize.toFloat())

        (findViewById<View>(R.id.infinitivo_simple) as TextView).text = c.infinitivoSimple
        (findViewById<View>(R.id.infinitivo_compuesto) as TextView).text = c.infinitivoCompuesto
        (findViewById<View>(R.id.participio) as TextView).text = c.participio
        (findViewById<View>(R.id.gerundio_simple) as TextView).text = c.gerundioSimple
        (findViewById<View>(R.id.gerundio_compuesto) as TextView).text = c.gerundioCompuesto

        (findViewById<View>(R.id.imperativo_presente_tu) as TextView).text = c.imperativoTu
        (findViewById<View>(R.id.imperativo_presente_el) as TextView).text = c.imperativoEl
        (findViewById<View>(R.id.imperativo_presente_nosotros) as TextView).text = c.imperativoN
        (findViewById<View>(R.id.imperativo_presente_vosotros) as TextView).text = c.imperativoV
        (findViewById<View>(R.id.imperativo_presente_ellos) as TextView).text = c.imperativoEll

        (findViewById<View>(R.id.imperativo_negativo_tu) as TextView).text = c.imperativoNegTu
        (findViewById<View>(R.id.imperativo_negativo_el) as TextView).text = c.imperativoNegEl
        (findViewById<View>(R.id.imperativo_negativo_nosotros) as TextView).text = c.imperativoNegN
        (findViewById<View>(R.id.imperativo_negativo_vosotros) as TextView).text = c.imperativoNegV
        (findViewById<View>(R.id.imperativo_negativo_ellos) as TextView).text = c.imperativoNegEll

        (findViewById<View>(R.id.indicativo_presente_yo) as TextView).text = c.indicativoPresenteYo
        (findViewById<View>(R.id.indicativo_presente_tu) as TextView).text = c.indicativoPresenteTu
        (findViewById<View>(R.id.indicativo_presente_el) as TextView).text = c.indicativoPresenteEl
        (findViewById<View>(R.id.indicativo_presente_nosotros) as TextView).text = c.indicativoPresenteN
        (findViewById<View>(R.id.indicativo_presente_vosotros) as TextView).text = c.indicativoPresenteV
        (findViewById<View>(R.id.indicativo_presente_ellos) as TextView).text = c.indicativoPresenteEll

        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_yo) as TextView).text = c.indicativoPreteritoPerfectoCompuestoYo
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_tu) as TextView).text = c.indicativoPreteritoPerfectoCompuestoTu
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_el) as TextView).text = c.indicativoPreteritoPerfectoCompuestoEl
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_nosotros) as TextView).text = c.indicativoPreteritoPerfectoCompuestoN
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_vosotros) as TextView).text = c.indicativoPreteritoPerfectoCompuestoV
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_ellos) as TextView).text = c.indicativoPreteritoPerfectoCompuestoEll

        (findViewById<View>(R.id.indicativo_preterito_imperfecto_yo) as TextView).text = c.indicativoPreteritoImperfectoYo
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_tu) as TextView).text = c.indicativoPreteritoImperfectoTu
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_el) as TextView).text = c.indicativoPreteritoImperfectoEl
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_nosotros) as TextView).text = c.indicativoPreteritoImperfectoN
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_vosotros) as TextView).text = c.indicativoPreteritoImperfectoV
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_ellos) as TextView).text = c.indicativoPreteritoImperfectoEll

        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_yo) as TextView).text = c.indicativoPreteritoPluscuamperfectoYo
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_tu) as TextView).text = c.indicativoPreteritoPluscuamperfectoTu
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_el) as TextView).text = c.indicativoPreteritoPluscuamperfectoEl
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_nosotros) as TextView).text = c.indicativoPreteritoPluscuamperfectoN
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_vosotros) as TextView).text = c.indicativoPreteritoPluscuamperfectoV
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_ellos) as TextView).text = c.indicativoPreteritoPluscuamperfectoEll

        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_yo) as TextView).text = c.indicativoPreteritoPerfectoSimpleYo
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_tu) as TextView).text = c.indicativoPreteritoPerfectoSimpleTu
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_el) as TextView).text = c.indicativoPreteritoPerfectoSimpleEl
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_nosotros) as TextView).text = c.indicativoPreteritoPerfectoSimpleN
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_vosotros) as TextView).text = c.indicativoPreteritoPerfectoSimpleV
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_ellos) as TextView).text = c.indicativoPreteritoPerfectoSimpleEll

        (findViewById<View>(R.id.indicativo_preterito_anterior_yo) as TextView).text = c.indicativoPreteritoAnteriorYo
        (findViewById<View>(R.id.indicativo_preterito_anterior_tu) as TextView).text = c.indicativoPreteritoAnteriorTu
        (findViewById<View>(R.id.indicativo_preterito_anterior_el) as TextView).text = c.indicativoPreteritoAnteriorEl
        (findViewById<View>(R.id.indicativo_preterito_anterior_nosotros) as TextView).text = c.indicativoPreteritoAnteriorN
        (findViewById<View>(R.id.indicativo_preterito_anterior_vosotros) as TextView).text = c.indicativoPreteritoAnteriorV
        (findViewById<View>(R.id.indicativo_preterito_anterior_ellos) as TextView).text = c.indicativoPreteritoAnteriorEll

        (findViewById<View>(R.id.indicativo_futuro_simple_yo) as TextView).text = c.indicativoFuturoSimpleYo
        (findViewById<View>(R.id.indicativo_futuro_simple_tu) as TextView).text = c.indicativoFuturoSimpleTu
        (findViewById<View>(R.id.indicativo_futuro_simple_el) as TextView).text = c.indicativoFuturoSimpleEl
        (findViewById<View>(R.id.indicativo_futuro_simple_nosotros) as TextView).text = c.indicativoFuturoSimpleN
        (findViewById<View>(R.id.indicativo_futuro_simple_vosotros) as TextView).text = c.indicativoFuturoSimpleV
        (findViewById<View>(R.id.indicativo_futuro_simple_ellos) as TextView).text = c.indicativoFuturoSimpleEll

        (findViewById<View>(R.id.indicativo_futuro_compuesto_yo) as TextView).text = c.indicativoFuturoCompuestoYo
        (findViewById<View>(R.id.indicativo_futuro_compuesto_tu) as TextView).text = c.indicativoFuturoCompuestoTu
        (findViewById<View>(R.id.indicativo_futuro_compuesto_el) as TextView).text = c.indicativoFuturoCompuestoEl
        (findViewById<View>(R.id.indicativo_futuro_compuesto_nosotros) as TextView).text = c.indicativoFuturoCompuestoN
        (findViewById<View>(R.id.indicativo_futuro_compuesto_vosotros) as TextView).text = c.indicativoFuturoCompuestoV
        (findViewById<View>(R.id.indicativo_futuro_compuesto_ellos) as TextView).text = c.indicativoFuturoCompuestoEll

        (findViewById<View>(R.id.indicativo_condicional_simple_yo) as TextView).text = c.indicativoCondicionalSimpleYo
        (findViewById<View>(R.id.indicativo_condicional_simple_tu) as TextView).text = c.indicativoCondicionalSimpleTu
        (findViewById<View>(R.id.indicativo_condicional_simple_el) as TextView).text = c.indicativoCondicionalSimpleEl
        (findViewById<View>(R.id.indicativo_condicional_simple_nosotros) as TextView).text = c.indicativoCondicionalSimpleN
        (findViewById<View>(R.id.indicativo_condicional_simple_vosotros) as TextView).text = c.indicativoCondicionalSimpleV
        (findViewById<View>(R.id.indicativo_condicional_simple_ellos) as TextView).text = c.indicativoCondicionalSimpleEll

        (findViewById<View>(R.id.indicativo_condicional_compuesto_yo) as TextView).text = c.indicativoCondicionalCompuestoYo
        (findViewById<View>(R.id.indicativo_condicional_compuesto_tu) as TextView).text = c.indicativoCondicionalCompuestoTu
        (findViewById<View>(R.id.indicativo_condicional_compuesto_el) as TextView).text = c.indicativoCondicionalCompuestoEl
        (findViewById<View>(R.id.indicativo_condicional_compuesto_nosotros) as TextView).text = c.indicativoCondicionalCompuestoN
        (findViewById<View>(R.id.indicativo_condicional_compuesto_vosotros) as TextView).text = c.indicativoCondicionalCompuestoV
        (findViewById<View>(R.id.indicativo_condicional_compuesto_ellos) as TextView).text = c.indicativoCondicionalCompuestoEll


        (findViewById<View>(R.id.subjuntivo_presente_yo) as TextView).text = c.subjuntivoPresenteYo
        (findViewById<View>(R.id.subjuntivo_presente_tu) as TextView).text = c.subjuntivoPresenteTu
        (findViewById<View>(R.id.subjuntivo_presente_el) as TextView).text = c.subjuntivoPresenteEl
        (findViewById<View>(R.id.subjuntivo_presente_nosotros) as TextView).text = c.subjuntivoPresenteN
        (findViewById<View>(R.id.subjuntivo_presente_vosotros) as TextView).text = c.subjuntivoPresenteV
        (findViewById<View>(R.id.subjuntivo_presente_ellos) as TextView).text = c.subjuntivoPresenteEll

        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_yo) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoYo
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_tu) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoTu
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_el) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoEl
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_nosotros) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoN
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_vosotros) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoV
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_ellos) as TextView).text = c.subjuntivoPreteritoPerfectoCompuestoEll

        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_yo) as TextView).text = c.subjuntivoPreteritoImperfectoYo
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_tu) as TextView).text = c.subjuntivoPreteritoImperfectoTu
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_el) as TextView).text = c.subjuntivoPreteritoImperfectoEl
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_nosotros) as TextView).text = c.subjuntivoPreteritoImperfectoN
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_vosotros) as TextView).text = c.subjuntivoPreteritoImperfectoV
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_ellos) as TextView).text = c.subjuntivoPreteritoImperfectoEll

        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_yo) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoYo
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_tu) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoTu
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_el) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoEl
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_nosotros) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoN
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_vosotros) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoV
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_ellos) as TextView).text = c.subjuntivoPreteritoPluscuamperfectoEll

        (findViewById<View>(R.id.subjuntivo_futuro_simple_yo) as TextView).text = c.subjuntivoFuturoSimpleYo
        (findViewById<View>(R.id.subjuntivo_futuro_simple_tu) as TextView).text = c.subjuntivoFuturoSimpleTu
        (findViewById<View>(R.id.subjuntivo_futuro_simple_el) as TextView).text = c.subjuntivoFuturoSimpleEl
        (findViewById<View>(R.id.subjuntivo_futuro_simple_nosotros) as TextView).text = c.subjuntivoFuturoSimpleN
        (findViewById<View>(R.id.subjuntivo_futuro_simple_vosotros) as TextView).text = c.subjuntivoFuturoSimpleV
        (findViewById<View>(R.id.subjuntivo_futuro_simple_ellos) as TextView).text = c.subjuntivoFuturoSimpleEll

        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_yo) as TextView).text = c.subjuntivoFuturoCompuestoYo
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_tu) as TextView).text = c.subjuntivoFuturoCompuestoTu
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_el) as TextView).text = c.subjuntivoFuturoCompuestoEl
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_nosotros) as TextView).text = c.subjuntivoFuturoCompuestoN
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_vosotros) as TextView).text = c.subjuntivoFuturoCompuestoV
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_ellos) as TextView).text = c.subjuntivoFuturoCompuestoEll
    }


    /**
     * Changes text font size.
     * @param fontSize float
     */
    private fun changeTextFontInConjugation(fontSize: Float) {
        val unit = TypedValue.COMPLEX_UNIT_SP

        (findViewById<View>(R.id.infinitivo_simple) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.infinitivo_compuesto) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.participio) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.gerundio_simple) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.gerundio_compuesto) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.imperativo_presente_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_presente_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_presente_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_presente_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_presente_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.imperativo_negativo_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_negativo_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_negativo_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_negativo_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.imperativo_negativo_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_presente_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_presente_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_presente_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_presente_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_presente_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_presente_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_compuesto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_preterito_imperfecto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_imperfecto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_pluscuamperfecto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_perfecto_simple_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_preterito_anterior_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_anterior_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_anterior_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_anterior_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_anterior_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_preterito_anterior_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_futuro_simple_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_simple_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_simple_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_simple_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_simple_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_simple_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_futuro_compuesto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_compuesto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_compuesto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_compuesto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_compuesto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_futuro_compuesto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_condicional_simple_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_simple_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_simple_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_simple_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_simple_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_simple_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.indicativo_condicional_compuesto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_compuesto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_compuesto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_compuesto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_compuesto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.indicativo_condicional_compuesto_ellos) as TextView).setTextSize(unit, fontSize)


        (findViewById<View>(R.id.subjuntivo_presente_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_presente_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_presente_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_presente_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_presente_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_presente_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_perfecto_compuesto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_imperfecto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_preterito_pluscuamperfecto_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.subjuntivo_futuro_simple_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_simple_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_simple_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_simple_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_simple_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_simple_ellos) as TextView).setTextSize(unit, fontSize)

        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_yo) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_tu) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_el) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_nosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_vosotros) as TextView).setTextSize(unit, fontSize)
        (findViewById<View>(R.id.subjuntivo_futuro_compuesto_ellos) as TextView).setTextSize(unit, fontSize)
    }

    /**
     * Set the text color.
     * @param color color
     */
    private fun setVerbColor(color: Int) {
        infinitive!!.setTextColor(color)
    }

    override fun onClick(view: View) {
        // Play the sounds
        when (view.id) {
            R.id.play_infinitive -> {
                ActivityUtils.speak(applicationContext, tts, verb?.infinitive)
                Toast.makeText(applicationContext, verb!!.infinitive, Toast.LENGTH_SHORT).show()
            }

            R.id.play_definition -> ActivityUtils.speak(applicationContext, tts, verb?.definition)
            R.id.play_sample1 -> ActivityUtils.speak(applicationContext, tts, verb?.sample1)
            R.id.play_sample2 -> ActivityUtils.speak(applicationContext, tts, verb?.sample2)
            R.id.play_sample3 -> ActivityUtils.speak(applicationContext, tts, verb?.sample3)

            R.id.imperativo_presente_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoTu)
            R.id.imperativo_presente_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoEl)
            R.id.imperativo_presente_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoN)
            R.id.imperativo_presente_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoV)
            R.id.imperativo_presente_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoEll)

            R.id.imperativo_negativo_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoNegTu)
            R.id.imperativo_negativo_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoNegEl)
            R.id.imperativo_negativo_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoNegN)
            R.id.imperativo_negativo_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoNegV)
            R.id.imperativo_negativo_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.imperativoEll)

            R.id.play_indicativo_presente_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteYo)
            R.id.play_indicativo_presente_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteTu)
            R.id.play_indicativo_presente_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteEl)
            R.id.play_indicativo_presente_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteN)
            R.id.play_indicativo_presente_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteV)
            R.id.play_indicativo_presente_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPresenteEll)

            R.id.indicativo_preterito_perfecto_compuesto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoYo)
            R.id.indicativo_preterito_perfecto_compuesto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoTu)
            R.id.indicativo_preterito_perfecto_compuesto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoEl)
            R.id.indicativo_preterito_perfecto_compuesto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoN)
            R.id.indicativo_preterito_perfecto_compuesto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoV)
            R.id.indicativo_preterito_perfecto_compuesto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoCompuestoEll)

            R.id.indicativo_preterito_imperfecto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoYo)
            R.id.indicativo_preterito_imperfecto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoTu)
            R.id.indicativo_preterito_imperfecto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoEl)
            R.id.indicativo_preterito_imperfecto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoN)
            R.id.indicativo_preterito_imperfecto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoV)
            R.id.indicativo_preterito_imperfecto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoImperfectoEll)

            R.id.indicativo_preterito_pluscuamperfecto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoYo)
            R.id.indicativo_preterito_pluscuamperfecto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoTu)
            R.id.indicativo_preterito_pluscuamperfecto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoEl)
            R.id.indicativo_preterito_pluscuamperfecto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoN)
            R.id.indicativo_preterito_pluscuamperfecto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoV)
            R.id.indicativo_preterito_pluscuamperfecto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPluscuamperfectoEll)

            R.id.indicativo_preterito_perfecto_simple_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleYo)
            R.id.indicativo_preterito_perfecto_simple_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleTu)
            R.id.indicativo_preterito_perfecto_simple_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleEl)
            R.id.indicativo_preterito_perfecto_simple_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleN)
            R.id.indicativo_preterito_perfecto_simple_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleV)
            R.id.indicativo_preterito_perfecto_simple_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoPerfectoSimpleEll)

            R.id.indicativo_preterito_anterior_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorYo)
            R.id.indicativo_preterito_anterior_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorTu)
            R.id.indicativo_preterito_anterior_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorEl)
            R.id.indicativo_preterito_anterior_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorN)
            R.id.indicativo_preterito_anterior_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorV)
            R.id.indicativo_preterito_anterior_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoPreteritoAnteriorEll)

            R.id.indicativo_futuro_simple_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleYo)
            R.id.indicativo_futuro_simple_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleTu)
            R.id.indicativo_futuro_simple_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleEl)
            R.id.indicativo_futuro_simple_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleN)
            R.id.indicativo_futuro_simple_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleV)
            R.id.indicativo_futuro_simple_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoSimpleEll)

            R.id.indicativo_futuro_compuesto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoYo)
            R.id.indicativo_futuro_compuesto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoTu)
            R.id.indicativo_futuro_compuesto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoEl)
            R.id.indicativo_futuro_compuesto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoN)
            R.id.indicativo_futuro_compuesto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoV)
            R.id.indicativo_futuro_compuesto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoFuturoCompuestoEll)

            R.id.indicativo_condicional_simple_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleYo)
            R.id.indicativo_condicional_simple_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleTu)
            R.id.indicativo_condicional_simple_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleEl)
            R.id.indicativo_condicional_simple_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleN)
            R.id.indicativo_condicional_simple_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleV)
            R.id.indicativo_condicional_simple_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalSimpleEll)

            R.id.indicativo_condicional_compuesto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoYo)
            R.id.indicativo_condicional_compuesto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoTu)
            R.id.indicativo_condicional_compuesto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoEl)
            R.id.indicativo_condicional_compuesto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoN)
            R.id.indicativo_condicional_compuesto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoV)
            R.id.indicativo_condicional_compuesto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.indicativoCondicionalCompuestoEll)


            R.id.subjuntivo_presente_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteYo)
            R.id.subjuntivo_presente_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteTu)
            R.id.subjuntivo_presente_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteEl)
            R.id.subjuntivo_presente_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteN)
            R.id.subjuntivo_presente_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteV)
            R.id.subjuntivo_presente_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPresenteEll)

            R.id.subjuntivo_preterito_perfecto_compuesto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoYo)
            R.id.subjuntivo_preterito_perfecto_compuesto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoTu)
            R.id.subjuntivo_preterito_perfecto_compuesto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoEl)
            R.id.subjuntivo_preterito_perfecto_compuesto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoN)
            R.id.subjuntivo_preterito_perfecto_compuesto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoV)
            R.id.subjuntivo_preterito_perfecto_compuesto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPerfectoCompuestoEll)

            R.id.subjuntivo_preterito_imperfecto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoYo)
            R.id.subjuntivo_preterito_imperfecto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoTu)
            R.id.subjuntivo_preterito_imperfecto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoEl)
            R.id.subjuntivo_preterito_imperfecto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoN)
            R.id.subjuntivo_preterito_imperfecto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoV)
            R.id.subjuntivo_preterito_imperfecto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoImperfectoEll)

            R.id.subjuntivo_preterito_pluscuamperfecto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoYo)
            R.id.subjuntivo_preterito_pluscuamperfecto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoTu)
            R.id.subjuntivo_preterito_pluscuamperfecto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoEl)
            R.id.subjuntivo_preterito_pluscuamperfecto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoN)
            R.id.subjuntivo_preterito_pluscuamperfecto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoV)
            R.id.subjuntivo_preterito_pluscuamperfecto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoPreteritoPluscuamperfectoEll)

            R.id.subjuntivo_futuro_simple_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleYo)
            R.id.subjuntivo_futuro_simple_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleTu)
            R.id.subjuntivo_futuro_simple_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleEl)
            R.id.subjuntivo_futuro_simple_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleN)
            R.id.subjuntivo_futuro_simple_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleV)
            R.id.subjuntivo_futuro_simple_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoSimpleEll)

            R.id.subjuntivo_futuro_compuesto_yo ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoYo)
            R.id.subjuntivo_futuro_compuesto_tu ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoTu)
            R.id.subjuntivo_futuro_compuesto_el ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoEl)
            R.id.subjuntivo_futuro_compuesto_nosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoN)
            R.id.subjuntivo_futuro_compuesto_vosotros ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoV)
            R.id.subjuntivo_futuro_compuesto_ellos ->
                ActivityUtils.speak(applicationContext, tts, conjugation?.subjuntivoFuturoCompuestoEll)

            else -> onClickDemo()
        }
    }


    /**
     * Conjugates the verb according to the model.
     * @param c Conjugation conjugation
     * @param isPronominal boolean
     */
    private fun conjugateVerb(c:Conjugation, verbInfinitive:String) {
        // Generate verb radicals for each time and person based on the radical's model.
        val modelRadicals = ArrayList<String>()
        val verbRadicals = ArrayList<String>()
        val modelRs = c.radicals
        if (!modelRs.isEmpty()) {
            val arrayModelRs = modelRs.split(", ".toRegex()).dropLastWhile{ it.isEmpty() }.toTypedArray()
            for (modelR in arrayModelRs) {
                modelRadicals.add(modelR)
                val verbR = generateRadical(verbInfinitive, modelR, c.id.toInt())
                verbRadicals.add(verbR)
            }

            val participio = c.participio
            replaceRadicals(c, modelRadicals, verbRadicals)
            replaceParticipio(c, participio, c.participio)
        }
    }

    /**
     * Generates the verb radical based on the model.
     * @param infinitive String verb
     * @param modelR String radical of the model
     * @param id int model id
     * @return list of radicals
     */
    private fun generateRadical(infinitive:String, modelR:String, id:Int):String {
        var verbR = infinitive
        // remove termination
        when {
            infinitive.endsWith("ar") -> verbR = infinitive.substring(0, infinitive.length - 2)
            infinitive.endsWith("er") -> verbR = infinitive.substring(0, infinitive.length - 2)
            infinitive.endsWith("ir") -> verbR = infinitive.substring(0, infinitive.length - 2)
        }

        // know models
        when (id) {
            /*
            88 -> // cuire, cuit : all verbes,
                // known: cuire, recuire, conduire, deduire, econduire, enduire, introduire, produire, (.)uire
                if (modelR.contentEquals("cui")) {
                    verbR = if (infinitive.endsWith("uire")) infinitive.replace("uire", "ui") else verbR
                } */
        }

        return verbR
    }

    /**
     * Replaces the radicals with the ones from the verb.
     * @param c Conjugation
     * @param modelR  List of model radicals
     * @param verbR  List of verb radicals
     */
    private fun replaceRadicals(c:Conjugation, modelR:List<String>, verbR:List<String>) {
        c.infinitivoSimple = verbName
        c.participio = replaceRadical(c.participio, modelR, verbR)
        c.gerundioSimple = replaceRadical(c.gerundioSimple, modelR, verbR)


        c.imperativoTu = replaceRadical(c.imperativoTu, modelR, verbR)
        c.imperativoEl = replaceRadical(c.imperativoEl, modelR, verbR)
        c.imperativoN = replaceRadical(c.imperativoN, modelR, verbR)
        c.imperativoV = replaceRadical(c.imperativoV, modelR, verbR)
        c.imperativoEll = replaceRadical(c.imperativoEll, modelR, verbR)

        c.imperativoNegTu = replaceRadical(c.imperativoNegTu, modelR, verbR)
        c.imperativoNegEl = replaceRadical(c.imperativoNegEl, modelR, verbR)
        c.imperativoNegN = replaceRadical(c.imperativoNegN, modelR, verbR)
        c.imperativoNegV = replaceRadical(c.imperativoNegV, modelR, verbR)
        c.imperativoNegEll = replaceRadical(c.imperativoNegEll, modelR, verbR)


        c.indicativoPresenteYo = replaceRadical(c.indicativoPresenteYo, modelR, verbR)
        c.indicativoPresenteTu = replaceRadical(c.indicativoPresenteTu, modelR, verbR)
        c.indicativoPresenteEl = replaceRadical(c.indicativoPresenteEl, modelR, verbR)
        c.indicativoPresenteN = replaceRadical(c.indicativoPresenteN, modelR, verbR)
        c.indicativoPresenteV = replaceRadical(c.indicativoPresenteV, modelR, verbR)
        c.indicativoPresenteEll = replaceRadical(c.indicativoPresenteEll, modelR, verbR)

        c.indicativoPreteritoImperfectoYo = replaceRadical(c.indicativoPreteritoImperfectoYo, modelR, verbR)
        c.indicativoPreteritoImperfectoTu = replaceRadical(c.indicativoPreteritoImperfectoTu, modelR, verbR)
        c.indicativoPreteritoImperfectoEl = replaceRadical(c.indicativoPreteritoImperfectoEl, modelR, verbR)
        c.indicativoPreteritoImperfectoN = replaceRadical(c.indicativoPreteritoImperfectoN, modelR, verbR)
        c.indicativoPreteritoImperfectoV = replaceRadical(c.indicativoPreteritoImperfectoV, modelR, verbR)
        c.indicativoPreteritoImperfectoEll = replaceRadical(c.indicativoPreteritoImperfectoEll, modelR, verbR)

        c.indicativoPreteritoPerfectoSimpleYo = replaceRadical(c.indicativoPreteritoPerfectoSimpleYo, modelR, verbR)
        c.indicativoPreteritoPerfectoSimpleTu = replaceRadical(c.indicativoPreteritoPerfectoSimpleTu, modelR, verbR)
        c.indicativoPreteritoPerfectoSimpleEl = replaceRadical(c.indicativoPreteritoPerfectoSimpleEl, modelR, verbR)
        c.indicativoPreteritoPerfectoSimpleN = replaceRadical(c.indicativoPreteritoPerfectoSimpleN, modelR, verbR)
        c.indicativoPreteritoPerfectoSimpleV = replaceRadical(c.indicativoPreteritoPerfectoSimpleV, modelR, verbR)
        c.indicativoPreteritoPerfectoSimpleEll = replaceRadical(c.indicativoPreteritoPerfectoSimpleEll, modelR, verbR)

        c.indicativoFuturoSimpleYo = replaceRadical(c.indicativoFuturoSimpleYo, modelR, verbR)
        c.indicativoFuturoSimpleTu = replaceRadical(c.indicativoFuturoSimpleTu, modelR, verbR)
        c.indicativoFuturoSimpleEl = replaceRadical(c.indicativoFuturoSimpleEl, modelR, verbR)
        c.indicativoFuturoSimpleN = replaceRadical(c.indicativoFuturoSimpleN, modelR, verbR)
        c.indicativoFuturoSimpleV = replaceRadical(c.indicativoFuturoSimpleV, modelR, verbR)
        c.indicativoFuturoSimpleEll = replaceRadical(c.indicativoFuturoSimpleEll, modelR, verbR)

        c.indicativoCondicionalSimpleYo = replaceRadical(c.indicativoCondicionalSimpleYo, modelR, verbR)
        c.indicativoCondicionalSimpleTu = replaceRadical(c.indicativoCondicionalSimpleTu, modelR, verbR)
        c.indicativoCondicionalSimpleEl = replaceRadical(c.indicativoCondicionalSimpleEl, modelR, verbR)
        c.indicativoCondicionalSimpleN = replaceRadical(c.indicativoCondicionalSimpleN, modelR, verbR)
        c.indicativoCondicionalSimpleV = replaceRadical(c.indicativoCondicionalSimpleV, modelR, verbR)
        c.indicativoCondicionalSimpleEll = replaceRadical(c.indicativoCondicionalSimpleEll, modelR, verbR)


        c.subjuntivoPresenteYo = replaceRadical(c.subjuntivoPresenteYo, modelR, verbR)
        c.subjuntivoPresenteTu = replaceRadical(c.subjuntivoPresenteTu, modelR, verbR)
        c.subjuntivoPresenteEl = replaceRadical(c.subjuntivoPresenteEl, modelR, verbR)
        c.subjuntivoPresenteN = replaceRadical(c.subjuntivoPresenteN, modelR, verbR)
        c.subjuntivoPresenteV = replaceRadical(c.subjuntivoPresenteV, modelR, verbR)
        c.subjuntivoPresenteEll = replaceRadical(c.subjuntivoPresenteEll, modelR, verbR)

        c.subjuntivoPreteritoImperfectoYo = replaceRadical(c.subjuntivoPreteritoImperfectoYo, modelR, verbR)
        c.subjuntivoPreteritoImperfectoTu = replaceRadical(c.subjuntivoPreteritoImperfectoTu, modelR, verbR)
        c.subjuntivoPreteritoImperfectoEl = replaceRadical(c.subjuntivoPreteritoImperfectoEl, modelR, verbR)
        c.subjuntivoPreteritoImperfectoN = replaceRadical(c.subjuntivoPreteritoImperfectoN, modelR, verbR)
        c.subjuntivoPreteritoImperfectoV = replaceRadical(c.subjuntivoPreteritoImperfectoV, modelR, verbR)
        c.subjuntivoPreteritoImperfectoEll = replaceRadical(c.subjuntivoPreteritoImperfectoEll, modelR, verbR)

        c.subjuntivoFuturoSimpleYo = replaceRadical(c.subjuntivoFuturoSimpleYo, modelR, verbR)
        c.subjuntivoFuturoSimpleTu = replaceRadical(c.subjuntivoFuturoSimpleTu, modelR, verbR)
        c.subjuntivoFuturoSimpleEl = replaceRadical(c.subjuntivoFuturoSimpleEl, modelR, verbR)
        c.subjuntivoFuturoSimpleN = replaceRadical(c.subjuntivoFuturoSimpleN, modelR, verbR)
        c.subjuntivoFuturoSimpleV = replaceRadical(c.subjuntivoFuturoSimpleV, modelR, verbR)
        c.subjuntivoFuturoSimpleEll = replaceRadical(c.subjuntivoFuturoSimpleEll, modelR, verbR)
    }

    /**
     * Replaces the radical in the conjugation form.
     * @param text  verb conjugation
     * @param modelR List of model radicals
     * @param verbR List of verb radicals
     * @return radical
     */
    private fun replaceRadical(text:String, modelR:List<String>, verbR:List<String>):String {
        var newText = text
        var radicalM:String
        var radicalV:String
        for (i in modelR.indices)
        {
            radicalM = modelR[i]
            radicalV = verbR[i]
            if (!radicalM.isEmpty() && !radicalV.isEmpty() && text.contains(radicalM))
            {
                // There could be only 2 radicals at most.
                // forms like: 'tu temes (temés)' or 'yo temiera o temiese'
                if (text.contains(" o ")) {
                    newText = newText.replaceFirst(" o $radicalM", " o $radicalV")
                } else if (text.endsWith(")")){
                    newText = newText.replaceFirst("($radicalM", "($radicalV")
                }
                newText = newText.replaceFirst(radicalM, radicalV)
            }
        }
        return newText
    }

    /**
     * Replaces the participio string with the one for the verb.
     * @param c Conjugation
     * @param old String
     * @param new String
     */
    private fun replaceParticipio(c:Conjugation, old:String, new:String) {
        c.infinitivoCompuesto = c.infinitivoCompuesto.replace(old, new)
        c.gerundioCompuesto = c.gerundioCompuesto.replace(old, new)

        c.indicativoPreteritoPerfectoCompuestoYo = c.indicativoPreteritoPerfectoCompuestoYo.replace(old, new)
        c.indicativoPreteritoPerfectoCompuestoTu = c.indicativoPreteritoPerfectoCompuestoTu.replace(old, new)
        c.indicativoPreteritoPerfectoCompuestoEl = c.indicativoPreteritoPerfectoCompuestoEl.replace(old, new)
        c.indicativoPreteritoPerfectoCompuestoN = c.indicativoPreteritoPerfectoCompuestoN.replace(old, new)
        c.indicativoPreteritoPerfectoCompuestoV = c.indicativoPreteritoPerfectoCompuestoV.replace(old, new)
        c.indicativoPreteritoPerfectoCompuestoEll = c.indicativoPreteritoPerfectoCompuestoEll.replace(old, new)

        c.indicativoPreteritoPluscuamperfectoYo = c.indicativoPreteritoPluscuamperfectoYo.replace(old, new)
        c.indicativoPreteritoPluscuamperfectoTu = c.indicativoPreteritoPluscuamperfectoTu.replace(old, new)
        c.indicativoPreteritoPluscuamperfectoEl = c.indicativoPreteritoPluscuamperfectoEl.replace(old, new)
        c.indicativoPreteritoPluscuamperfectoN = c.indicativoPreteritoPluscuamperfectoN.replace(old, new)
        c.indicativoPreteritoPluscuamperfectoV = c.indicativoPreteritoPluscuamperfectoV.replace(old, new)
        c.indicativoPreteritoPluscuamperfectoEll = c.indicativoPreteritoPluscuamperfectoEll.replace(old, new)

        c.indicativoPreteritoAnteriorYo = c.indicativoPreteritoAnteriorYo.replace(old, new)
        c.indicativoPreteritoAnteriorTu = c.indicativoPreteritoAnteriorTu.replace(old, new)
        c.indicativoPreteritoAnteriorEl = c.indicativoPreteritoAnteriorEl.replace(old, new)
        c.indicativoPreteritoAnteriorN = c.indicativoPreteritoAnteriorN.replace(old, new)
        c.indicativoPreteritoAnteriorV = c.indicativoPreteritoAnteriorV.replace(old, new)
        c.indicativoPreteritoAnteriorEll = c.indicativoPreteritoAnteriorEll.replace(old, new)

        c.indicativoFuturoCompuestoYo = c.indicativoFuturoCompuestoYo.replace(old, new)
        c.indicativoFuturoCompuestoTu = c.indicativoFuturoCompuestoTu.replace(old, new)
        c.indicativoFuturoCompuestoEl = c.indicativoFuturoCompuestoEl.replace(old, new)
        c.indicativoFuturoCompuestoN = c.indicativoFuturoCompuestoN.replace(old, new)
        c.indicativoFuturoCompuestoV = c.indicativoFuturoCompuestoV.replace(old, new)
        c.indicativoFuturoCompuestoEll = c.indicativoFuturoCompuestoEll.replace(old, new)

        c.indicativoCondicionalCompuestoYo = c.indicativoCondicionalCompuestoYo.replace(old, new)
        c.indicativoCondicionalCompuestoTu = c.indicativoCondicionalCompuestoTu.replace(old, new)
        c.indicativoCondicionalCompuestoEl = c.indicativoCondicionalCompuestoEl.replace(old, new)
        c.indicativoCondicionalCompuestoN = c.indicativoCondicionalCompuestoN.replace(old, new)
        c.indicativoCondicionalCompuestoV = c.indicativoCondicionalCompuestoV.replace(old, new)
        c.indicativoCondicionalCompuestoEll = c.indicativoCondicionalCompuestoEll.replace(old, new)


        c.subjuntivoPreteritoPerfectoCompuestoYo = c.subjuntivoPreteritoPerfectoCompuestoYo.replace(old, new)
        c.subjuntivoPreteritoPerfectoCompuestoTu = c.subjuntivoPreteritoPerfectoCompuestoTu.replace(old, new)
        c.subjuntivoPreteritoPerfectoCompuestoEl = c.subjuntivoPreteritoPerfectoCompuestoEl.replace(old, new)
        c.subjuntivoPreteritoPerfectoCompuestoN = c.subjuntivoPreteritoPerfectoCompuestoN.replace(old, new)
        c.subjuntivoPreteritoPerfectoCompuestoV = c.subjuntivoPreteritoPerfectoCompuestoV.replace(old, new)
        c.subjuntivoPreteritoPerfectoCompuestoEll = c.subjuntivoPreteritoPerfectoCompuestoEll.replace(old, new)

        c.subjuntivoPreteritoPluscuamperfectoYo = c.subjuntivoPreteritoPluscuamperfectoYo.replace(old, new)
        c.subjuntivoPreteritoPluscuamperfectoTu = c.subjuntivoPreteritoPluscuamperfectoTu.replace(old, new)
        c.subjuntivoPreteritoPluscuamperfectoEl = c.subjuntivoPreteritoPluscuamperfectoEl.replace(old, new)
        c.subjuntivoPreteritoPluscuamperfectoN = c.subjuntivoPreteritoPluscuamperfectoN.replace(old, new)
        c.subjuntivoPreteritoPluscuamperfectoV = c.subjuntivoPreteritoPluscuamperfectoV.replace(old, new)
        c.subjuntivoPreteritoPluscuamperfectoEll = c.subjuntivoPreteritoPluscuamperfectoEll.replace(old, new)

        c.subjuntivoFuturoCompuestoYo = c.subjuntivoFuturoCompuestoYo.replace(old, new)
        c.subjuntivoFuturoCompuestoTu = c.subjuntivoFuturoCompuestoTu.replace(old, new)
        c.subjuntivoFuturoCompuestoEl = c.subjuntivoFuturoCompuestoEl.replace(old, new)
        c.subjuntivoFuturoCompuestoN = c.subjuntivoFuturoCompuestoN.replace(old, new)
        c.subjuntivoFuturoCompuestoV = c.subjuntivoFuturoCompuestoV.replace(old, new)
        c.subjuntivoFuturoCompuestoEll = c.subjuntivoFuturoCompuestoEll.replace(old, new)
    }


    /**
     * Ads the pronoms.
     * @param c Conjugation
     */
    private fun addPronoms(c:Conjugation) {
        // TODO: Show pronoms in different color
        addPronomsIndicativoPresente(c)
        addPronomsIndicativoPreteritoPerfectoCompuesto(c)
        addPronomsIndicativoPreteritoImperfecto(c)
        addPronomsIndicativoPreteritoPluscuamperfecto(c)
        addPronomsIndicativoPreteritoPerfectoSimple(c)
        addPronomsIndicativoPreteritoAnterior(c)
        addPronomsIndicativoFuturoSimple(c)
        addPronomsIndicativoFuturoCompuesto(c)
        addPronomsIndicativoCondicionalSimple(c)
        addPronomsIndicativoCondicionalCompuesto(c)

        addPronomsSubjuntivoPresente(c)
        addPronomsSubjuntivoPreteritoPerfectoCompuesto(c)
        addPronomsSubjuntivoPreteritoImperfecto(c)
        addPronomsSubjuntivoPreteritoPluscuamperfecto(c)
        addPronomsSubjuntivoFuturoSimple(c)
        addPronomsSubjuntivoFuturoCompuesto(c)
    }

    private fun addPronomsIndicativoPresente(c: Conjugation) {
        if (!c.indicativoPresenteYo.contentEquals("-")) {
            c.indicativoPresenteYo = YO + c.indicativoPresenteYo
        }
        if (!c.indicativoPresenteTu.contentEquals("-")) {
            c.indicativoPresenteTu = TU + c.indicativoPresenteTu
        }
        if (!c.indicativoPresenteEl.contentEquals("-")) {
            c.indicativoPresenteEl = EL + c.indicativoPresenteEl
        }
        if (!c.indicativoPresenteN.contentEquals("-")) {
            c.indicativoPresenteN = NOSOTROS + c.indicativoPresenteN
        }
        if (!c.indicativoPresenteV.contentEquals("-")) {
            c.indicativoPresenteV = VOSOTROS + c.indicativoPresenteV
        }
        if (!c.indicativoPresenteEll.contentEquals("-")) {
            c.indicativoPresenteEll = ELLOS + c.indicativoPresenteEll
        }
    }

    private fun addPronomsIndicativoPreteritoPerfectoCompuesto(c: Conjugation) {
        if (!c.indicativoPreteritoPerfectoCompuestoYo.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoYo = YO + c.indicativoPreteritoPerfectoCompuestoYo
        }
        if (!c.indicativoPreteritoPerfectoCompuestoTu.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoTu = TU + c.indicativoPreteritoPerfectoCompuestoTu
        }
        if (!c.indicativoPreteritoPerfectoCompuestoEl.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoEl = EL + c.indicativoPreteritoPerfectoCompuestoEl
        }
        if (!c.indicativoPreteritoPerfectoCompuestoN.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoN = NOSOTROS + c.indicativoPreteritoPerfectoCompuestoN
        }
        if (!c.indicativoPreteritoPerfectoCompuestoV.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoV = VOSOTROS + c.indicativoPreteritoPerfectoCompuestoV
        }
        if (!c.indicativoPreteritoPerfectoCompuestoEll.contentEquals("-")) {
            c.indicativoPreteritoPerfectoCompuestoEll = ELLOS + c.indicativoPreteritoPerfectoCompuestoEll
        }
    }

    private fun addPronomsIndicativoPreteritoImperfecto(c: Conjugation) {
        if (!c.indicativoPreteritoImperfectoYo.contentEquals("-")) {
            c.indicativoPreteritoImperfectoYo = YO + c.indicativoPreteritoImperfectoYo
        }
        if (!c.indicativoPreteritoImperfectoTu.contentEquals("-")) {
            c.indicativoPreteritoImperfectoTu = TU + c.indicativoPreteritoImperfectoTu
        }
        if (!c.indicativoPreteritoImperfectoEl.contentEquals("-")) {
            c.indicativoPreteritoImperfectoEl = EL + c.indicativoPreteritoImperfectoEl
        }
        if (!c.indicativoPreteritoImperfectoN.contentEquals("-")) {
            c.indicativoPreteritoImperfectoN = NOSOTROS + c.indicativoPreteritoImperfectoN
        }
        if (!c.indicativoPreteritoImperfectoV.contentEquals("-")) {
            c.indicativoPreteritoImperfectoV = VOSOTROS + c.indicativoPreteritoImperfectoV
        }
        if (!c.indicativoPreteritoImperfectoEll.contentEquals("-")) {
            c.indicativoPreteritoImperfectoEll = ELLOS + c.indicativoPreteritoImperfectoEll
        }
    }

    private fun addPronomsIndicativoPreteritoPluscuamperfecto(c: Conjugation) {
        if (!c.indicativoPreteritoPluscuamperfectoYo.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoYo = YO + c.indicativoPreteritoPluscuamperfectoYo
        }
        if (!c.indicativoPreteritoPluscuamperfectoTu.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoTu = TU + c.indicativoPreteritoPluscuamperfectoTu
        }
        if (!c.indicativoPreteritoPluscuamperfectoEl.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoEl = EL + c.indicativoPreteritoPluscuamperfectoEl
        }
        if (!c.indicativoPreteritoPluscuamperfectoN.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoN = NOSOTROS + c.indicativoPreteritoPluscuamperfectoN
        }
        if (!c.indicativoPreteritoPluscuamperfectoV.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoV = VOSOTROS + c.indicativoPreteritoPluscuamperfectoV
        }
        if (!c.indicativoPreteritoPluscuamperfectoEll.contentEquals("-")) {
            c.indicativoPreteritoPluscuamperfectoEll = ELLOS + c.indicativoPreteritoPluscuamperfectoEll
        }
    }

    private fun addPronomsIndicativoPreteritoPerfectoSimple(c: Conjugation) {
        if (!c.indicativoPreteritoPerfectoSimpleYo.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleYo = YO + c.indicativoPreteritoPerfectoSimpleYo
        }
        if (!c.indicativoPreteritoPerfectoSimpleTu.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleTu = TU + c.indicativoPreteritoPerfectoSimpleTu
        }
        if (!c.indicativoPreteritoPerfectoSimpleEl.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleEl = EL + c.indicativoPreteritoPerfectoSimpleEl
        }
        if (!c.indicativoPreteritoPerfectoSimpleN.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleN = NOSOTROS + c.indicativoPreteritoPerfectoSimpleN
        }
        if (!c.indicativoPreteritoPerfectoSimpleV.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleV = VOSOTROS + c.indicativoPreteritoPerfectoSimpleV
        }
        if (!c.indicativoPreteritoPerfectoSimpleEll.contentEquals("-")) {
            c.indicativoPreteritoPerfectoSimpleEll = ELLOS + c.indicativoPreteritoPerfectoSimpleEll
        }
    }

    private fun addPronomsIndicativoPreteritoAnterior(c: Conjugation) {
        if (!c.indicativoPreteritoAnteriorYo.contentEquals("-")) {
            c.indicativoPreteritoAnteriorYo = YO + c.indicativoPreteritoAnteriorYo
        }
        if (!c.indicativoPreteritoAnteriorTu.contentEquals("-")) {
            c.indicativoPreteritoAnteriorTu = TU + c.indicativoPreteritoAnteriorTu
        }
        if (!c.indicativoPreteritoAnteriorEl.contentEquals("-")) {
            c.indicativoPreteritoAnteriorEl = EL + c.indicativoPreteritoAnteriorEl
        }
        if (!c.indicativoPreteritoAnteriorN.contentEquals("-")) {
            c.indicativoPreteritoAnteriorN = NOSOTROS + c.indicativoPreteritoAnteriorN
        }
        if (!c.indicativoPreteritoAnteriorV.contentEquals("-")) {
            c.indicativoPreteritoAnteriorV = VOSOTROS + c.indicativoPreteritoAnteriorV
        }
        if (!c.indicativoPreteritoAnteriorEll.contentEquals("-")) {
            c.indicativoPreteritoAnteriorEll = ELLOS + c.indicativoPreteritoAnteriorEll
        }
    }

    private fun addPronomsIndicativoFuturoSimple(c: Conjugation) {
        if (!c.indicativoFuturoSimpleYo.contentEquals("-")) {
            c.indicativoFuturoSimpleYo = YO + c.indicativoFuturoSimpleYo
        }
        if (!c.indicativoFuturoSimpleTu.contentEquals("-")) {
            c.indicativoFuturoSimpleTu = TU + c.indicativoFuturoSimpleTu
        }
        if (!c.indicativoFuturoSimpleEl.contentEquals("-")) {
            c.indicativoFuturoSimpleEl = EL + c.indicativoFuturoSimpleEl
        }
        if (!c.indicativoFuturoSimpleN.contentEquals("-")) {
            c.indicativoFuturoSimpleN = NOSOTROS + c.indicativoFuturoSimpleN
        }
        if (!c.indicativoFuturoSimpleV.contentEquals("-")) {
            c.indicativoFuturoSimpleV = VOSOTROS + c.indicativoFuturoSimpleV
        }
        if (!c.indicativoFuturoSimpleEll.contentEquals("-")) {
            c.indicativoFuturoSimpleEll = ELLOS + c.indicativoFuturoSimpleEll
        }
    }

    private fun addPronomsIndicativoFuturoCompuesto(c: Conjugation) {
        if (!c.indicativoFuturoCompuestoYo.contentEquals("-")) {
            c.indicativoFuturoCompuestoYo = YO + c.indicativoFuturoCompuestoYo
        }
        if (!c.indicativoFuturoCompuestoTu.contentEquals("-")) {
            c.indicativoFuturoCompuestoTu = TU + c.indicativoFuturoCompuestoTu
        }
        if (!c.indicativoFuturoCompuestoEl.contentEquals("-")) {
            c.indicativoFuturoCompuestoEl = EL + c.indicativoFuturoCompuestoEl
        }
        if (!c.indicativoFuturoCompuestoN.contentEquals("-")) {
            c.indicativoFuturoCompuestoN = NOSOTROS + c.indicativoFuturoCompuestoN
        }
        if (!c.indicativoFuturoCompuestoV.contentEquals("-")) {
            c.indicativoFuturoCompuestoV = VOSOTROS + c.indicativoFuturoCompuestoV
        }
        if (!c.indicativoFuturoCompuestoEll.contentEquals("-")) {
            c.indicativoFuturoCompuestoEll = ELLOS + c.indicativoFuturoCompuestoEll
        }
    }

    private fun addPronomsIndicativoCondicionalSimple(c: Conjugation) {
        if (!c.indicativoCondicionalSimpleYo.contentEquals("-")) {
            c.indicativoCondicionalSimpleYo = YO + c.indicativoCondicionalSimpleYo
        }
        if (!c.indicativoCondicionalSimpleTu.contentEquals("-")) {
            c.indicativoCondicionalSimpleTu = TU + c.indicativoCondicionalSimpleTu
        }
        if (!c.indicativoCondicionalSimpleEl.contentEquals("-")) {
            c.indicativoCondicionalSimpleEl = EL + c.indicativoCondicionalSimpleEl
        }
        if (!c.indicativoCondicionalSimpleN.contentEquals("-")) {
            c.indicativoCondicionalSimpleN = NOSOTROS + c.indicativoCondicionalSimpleN
        }
        if (!c.indicativoCondicionalSimpleV.contentEquals("-")) {
            c.indicativoCondicionalSimpleV = VOSOTROS + c.indicativoCondicionalSimpleV
        }
        if (!c.indicativoCondicionalSimpleEll.contentEquals("-")) {
            c.indicativoCondicionalSimpleEll = ELLOS + c.indicativoCondicionalSimpleEll
        }
    }

    private fun addPronomsIndicativoCondicionalCompuesto(c: Conjugation) {
        if (!c.indicativoCondicionalCompuestoYo.contentEquals("-")) {
            c.indicativoCondicionalCompuestoYo = YO + c.indicativoCondicionalCompuestoYo
        }
        if (!c.indicativoCondicionalCompuestoTu.contentEquals("-")) {
            c.indicativoCondicionalCompuestoTu = TU + c.indicativoCondicionalCompuestoTu
        }
        if (!c.indicativoCondicionalCompuestoEl.contentEquals("-")) {
            c.indicativoCondicionalCompuestoEl = EL + c.indicativoCondicionalCompuestoEl
        }
        if (!c.indicativoCondicionalCompuestoN.contentEquals("-")) {
            c.indicativoCondicionalCompuestoN = NOSOTROS + c.indicativoCondicionalCompuestoN
        }
        if (!c.indicativoCondicionalCompuestoV.contentEquals("-")) {
            c.indicativoCondicionalCompuestoV = VOSOTROS + c.indicativoCondicionalCompuestoV
        }
        if (!c.indicativoCondicionalCompuestoEll.contentEquals("-")) {
            c.indicativoCondicionalCompuestoEll = ELLOS + c.indicativoCondicionalCompuestoEll
        }
    }

    private fun addPronomsSubjuntivoPresente(c: Conjugation) {
        if (!c.subjuntivoPresenteYo.contentEquals("-")) {
            c.subjuntivoPresenteYo = QUE + YO + c.subjuntivoPresenteYo
        }
        if (!c.subjuntivoPresenteTu.contentEquals("-")) {
            c.subjuntivoPresenteTu = QUE + TU + c.subjuntivoPresenteTu
        }
        if (!c.subjuntivoPresenteEl.contentEquals("-")) {
            c.subjuntivoPresenteEl = QUE + EL + c.subjuntivoPresenteEl
        }
        if (!c.subjuntivoPresenteN.contentEquals("-")) {
            c.subjuntivoPresenteN = QUE + NOSOTROS + c.subjuntivoPresenteN
        }
        if (!c.subjuntivoPresenteV.contentEquals("-")) {
            c.subjuntivoPresenteV = QUE + VOSOTROS + c.subjuntivoPresenteV
        }
        if (!c.subjuntivoPresenteEll.contentEquals("-")) {
            c.subjuntivoPresenteEll = QUE + ELLOS + c.subjuntivoPresenteEll
        }
    }

    private fun addPronomsSubjuntivoPreteritoPerfectoCompuesto(c: Conjugation) {
        if (!c.subjuntivoPreteritoPerfectoCompuestoYo.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoYo = QUE + YO + c.subjuntivoPreteritoPerfectoCompuestoYo
        }
        if (!c.subjuntivoPreteritoPerfectoCompuestoTu.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoTu = QUE + TU + c.subjuntivoPreteritoPerfectoCompuestoTu
        }
        if (!c.subjuntivoPreteritoPerfectoCompuestoEl.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoEl = QUE + EL + c.subjuntivoPreteritoPerfectoCompuestoEl
        }
        if (!c.subjuntivoPreteritoPerfectoCompuestoN.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoN = QUE + NOSOTROS + c.subjuntivoPreteritoPerfectoCompuestoN
        }
        if (!c.subjuntivoPreteritoPerfectoCompuestoV.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoV = QUE + VOSOTROS + c.subjuntivoPreteritoPerfectoCompuestoV
        }
        if (!c.subjuntivoPreteritoPerfectoCompuestoEll.contentEquals("-")) {
            c.subjuntivoPreteritoPerfectoCompuestoEll = QUE + ELLOS + c.subjuntivoPreteritoPerfectoCompuestoEll
        }
    }

    private fun addPronomsSubjuntivoPreteritoImperfecto(c: Conjugation) {
        if (!c.subjuntivoPreteritoImperfectoYo.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoYo = QUE + YO + c.subjuntivoPreteritoImperfectoYo
        }
        if (!c.subjuntivoPreteritoImperfectoTu.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoTu = QUE + TU + c.subjuntivoPreteritoImperfectoTu
        }
        if (!c.subjuntivoPreteritoImperfectoEl.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoEl = QUE + EL + c.subjuntivoPreteritoImperfectoEl
        }
        if (!c.subjuntivoPreteritoImperfectoN.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoN = QUE + NOSOTROS + c.subjuntivoPreteritoImperfectoN
        }
        if (!c.subjuntivoPreteritoImperfectoV.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoV = QUE + VOSOTROS + c.subjuntivoPreteritoImperfectoV
        }
        if (!c.subjuntivoPreteritoImperfectoEll.contentEquals("-")) {
            c.subjuntivoPreteritoImperfectoEll = QUE + ELLOS + c.subjuntivoPreteritoImperfectoEll
        }
    }

    private fun addPronomsSubjuntivoPreteritoPluscuamperfecto(c: Conjugation) {
        if (!c.subjuntivoPreteritoPluscuamperfectoYo.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoYo = QUE + YO + c.subjuntivoPreteritoPluscuamperfectoYo
        }
        if (!c.subjuntivoPreteritoPluscuamperfectoTu.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoTu = QUE + TU + c.subjuntivoPreteritoPluscuamperfectoTu
        }
        if (!c.subjuntivoPreteritoPluscuamperfectoEl.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoEl = QUE + EL + c.subjuntivoPreteritoPluscuamperfectoEl
        }
        if (!c.subjuntivoPreteritoPluscuamperfectoN.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoN = QUE + NOSOTROS + c.subjuntivoPreteritoPluscuamperfectoN
        }
        if (!c.subjuntivoPreteritoPluscuamperfectoV.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoV = QUE + VOSOTROS + c.subjuntivoPreteritoPluscuamperfectoV
        }
        if (!c.subjuntivoPreteritoPluscuamperfectoEll.contentEquals("-")) {
            c.subjuntivoPreteritoPluscuamperfectoEll = QUE + ELLOS + c.subjuntivoPreteritoPluscuamperfectoEll
        }
    }

    private fun addPronomsSubjuntivoFuturoSimple(c: Conjugation) {
        if (!c.subjuntivoFuturoSimpleYo.contentEquals("-")) {
            c.subjuntivoFuturoSimpleYo = QUE + YO + c.subjuntivoFuturoSimpleYo
        }
        if (!c.subjuntivoFuturoSimpleTu.contentEquals("-")) {
            c.subjuntivoFuturoSimpleTu = QUE + TU + c.subjuntivoFuturoSimpleTu
        }
        if (!c.subjuntivoFuturoSimpleEl.contentEquals("-")) {
            c.subjuntivoFuturoSimpleEl = QUE + EL + c.subjuntivoFuturoSimpleEl
        }
        if (!c.subjuntivoFuturoSimpleN.contentEquals("-")) {
            c.subjuntivoFuturoSimpleN = QUE + NOSOTROS + c.subjuntivoFuturoSimpleN
        }
        if (!c.subjuntivoFuturoSimpleV.contentEquals("-")) {
            c.subjuntivoFuturoSimpleV = QUE + VOSOTROS + c.subjuntivoFuturoSimpleV
        }
        if (!c.subjuntivoFuturoSimpleEll.contentEquals("-")) {
            c.subjuntivoFuturoSimpleEll = QUE + ELLOS + c.subjuntivoFuturoSimpleEll
        }
    }

    private fun addPronomsSubjuntivoFuturoCompuesto(c: Conjugation) {
        if (!c.subjuntivoFuturoCompuestoYo.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoYo = QUE + YO + c.subjuntivoFuturoCompuestoYo
        }
        if (!c.subjuntivoFuturoCompuestoTu.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoTu = QUE + TU + c.subjuntivoFuturoCompuestoTu
        }
        if (!c.subjuntivoFuturoCompuestoEl.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoEl = QUE + EL + c.subjuntivoFuturoCompuestoEl
        }
        if (!c.subjuntivoFuturoCompuestoN.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoN = QUE + NOSOTROS + c.subjuntivoFuturoCompuestoN
        }
        if (!c.subjuntivoFuturoCompuestoV.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoV = QUE + VOSOTROS + c.subjuntivoFuturoCompuestoV
        }
        if (!c.subjuntivoFuturoCompuestoEll.contentEquals("-")) {
            c.subjuntivoFuturoCompuestoEll = QUE + ELLOS + c.subjuntivoFuturoCompuestoEll
        }
    }


    /**
     * Start a show case view for demo mode.
     */
    private fun defineDemoMode() {
        showcaseView = ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(ViewTarget(findViewById(R.id.infinitive)))
                .setContentTitle(getString(R.string.details))
                .setContentText(getString(R.string.infinitive))
                .setStyle(R.style.CustomShowcaseTheme2)
                .replaceEndButton(R.layout.view_custom_button)
                .setOnClickListener(this)
                .build()
        showcaseView!!.setButtonText(getString(R.string.next))
    }

    /**
     * Defines what item to show case view for demo mode.
     */
    private fun onClickDemo() {
        if (!demo) return
        when (counter) {
            0 -> {
                showcaseView!!.setShowcase(ViewTarget(findViewById(R.id.regular)), true)
                showcaseView!!.setContentText(getString(R.string.regular))
            }

            1 -> {
                showcaseView!!.setShowcase(ViewTarget(findViewById(R.id.definition)), true)
                showcaseView!!.setContentText(getString(R.string.definition))
            }

            2 -> {
                showcaseView!!.setShowcase(ViewTarget(findViewById(R.id.sample2)), true)
                showcaseView!!.setContentText(getString(R.string.examples))
            }

            3 -> {
                scrollView!!.requestChildFocus(findViewById(R.id.indicativo_presente_ellos), findViewById(R.id.indicativo_presente_ellos))
                scrollView!!.requestChildFocus(findViewById(R.id.indicativo_presente_yo), findViewById(R.id.indicativo_presente_yo))
                showcaseView!!.setShowcase(ViewTarget(findViewById(R.id.indicativo_presente_yo)), true)
                showcaseView!!.setContentTitle(getString(R.string.conjugations))
                showcaseView!!.setContentText(getString(R.string.conjugations_description))
            }

            4 -> {
                showcaseView!!.setShowcase(ViewTarget(fabAdd), true)
                showcaseView!!.setContentTitle(getString(R.string.favorites))
                showcaseView!!.setContentText(getString(R.string.add_remove_from_favorites))
                showcaseView!!.setButtonText(getString(R.string.got_it))
            }

            5 -> {
                showcaseView!!.hide()
                demo = false
            }
        }
        counter++
    }
}
