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
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.VERB
import com.xengar.android.verbosespanol.utils.Constants.VERB_ID
import com.xengar.android.verbosespanol.utils.Constants.VERB_NAME

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
    private var group: TextView? = null
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
        group = findViewById(R.id.groupe)
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
        /*

        findViewById<View>(R.id.play_indicative_present_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_present_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_present_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_present_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_present_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_present_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_compose_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_imperfait_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_plus_que_parfait_ils).setOnClickListener(this)

        findViewById<View>(R.id.play_indicative_passe_simple_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_simple_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_simple_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_simple_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_simple_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_passe_anterieur_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_simple_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_je).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_il).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_indicative_futur_anterieur_ils).setOnClickListener(this)

        findViewById<View>(R.id.play_conditionnel_present_je).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_present_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_present_il).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_present_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_present_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_present_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_je).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_il).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_conditionnel_passe_ils).setOnClickListener(this)

        findViewById<View>(R.id.play_subjonctif_present_je).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_present_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_present_il).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_present_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_present_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_present_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_je).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_il).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_passe_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_je).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_il).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_imperfait_ils).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_je).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_il).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_subjonctif_plus_que_parfait_ils).setOnClickListener(this)

        findViewById<View>(R.id.play_imperatif_present_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_imperatif_present_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_imperatif_present_vous).setOnClickListener(this)
        findViewById<View>(R.id.play_imperatif_passe_tu).setOnClickListener(this)
        findViewById<View>(R.id.play_imperatif_passe_nous).setOnClickListener(this)
        findViewById<View>(R.id.play_imperatif_passe_vous).setOnClickListener(this)

        findViewById<View>(R.id.play_infinitive_present).setOnClickListener(this)
        findViewById<View>(R.id.play_infinitive_passe).setOnClickListener(this)
        findViewById<View>(R.id.play_participe_present).setOnClickListener(this)
        findViewById<View>(R.id.play_participe_passe1).setOnClickListener(this)
        findViewById<View>(R.id.play_participe_passe2).setOnClickListener(this)
        findViewById<View>(R.id.play_gerondif_present).setOnClickListener(this)
        findViewById<View>(R.id.play_gerondif_passe).setOnClickListener(this) */
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
        if (verb != null)
        {
            text = ("Verb: " + verb!!.infinitive
                    + "\n" + getString(R.string.group) + ": " + verb!!.group
                    + "\n\n" + getString(R.string.definition) + ":\n" + verb!!.definition
                    + "\n\n" + getString(R.string.examples) + ":\n" + verb!!.sample1
                    + "\n" + verb!!.sample2
                    + "\n" + verb!!.sample3
                    + "\n\n" + getString(R.string.indicativo) + " " + getString(R.string.presente) + ":"
                    /*+ "\n" + conjugation!!.indicatifPresentJe
                    + "\n" + conjugation!!.indicatifPresentTu
                    + "\n" + conjugation!!.indicatifPresentIl
                    + "\n" + conjugation!!.indicatifPresentNous
                    + "\n" + conjugation!!.indicatifPresentVous
                    + "\n" + conjugation!!.indicatifPresentIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.passe_compose) + ":"
                    + "\n" + conjugation!!.indicatifPasseComposeJe
                    + "\n" + conjugation!!.indicatifPasseComposeTu
                    + "\n" + conjugation!!.indicatifPasseComposeIl
                    + "\n" + conjugation!!.indicatifPasseComposeNous
                    + "\n" + conjugation!!.indicatifPasseComposeVous
                    + "\n" + conjugation!!.indicatifPasseComposeIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.imperfait) + ":"
                    + "\n" + conjugation!!.indicatifImperfaitJe
                    + "\n" + conjugation!!.indicatifImperfaitTu
                    + "\n" + conjugation!!.indicatifImperfaitIl
                    + "\n" + conjugation!!.indicatifImperfaitNous
                    + "\n" + conjugation!!.indicatifImperfaitVous
                    + "\n" + conjugation!!.indicatifImperfaitIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.plus_que_parfait) + ":"
                    + "\n" + conjugation!!.indicatifPlusQueParfaitJe
                    + "\n" + conjugation!!.indicatifPlusQueParfaitTu
                    + "\n" + conjugation!!.indicatifPlusQueParfaitIl
                    + "\n" + conjugation!!.indicatifPlusQueParfaitNous
                    + "\n" + conjugation!!.indicatifPlusQueParfaitVous
                    + "\n" + conjugation!!.indicatifPlusQueParfaitIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.passe_simple) + ":"
                    + "\n" + conjugation!!.indicatifPasseSimpleJe
                    + "\n" + conjugation!!.indicatifPasseSimpleTu
                    + "\n" + conjugation!!.indicatifPasseSimpleIl
                    + "\n" + conjugation!!.indicatifPasseSimpleNous
                    + "\n" + conjugation!!.indicatifPasseSimpleVous
                    + "\n" + conjugation!!.indicatifPasseSimpleIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.passe_anterieur) + ":"
                    + "\n" + conjugation!!.indicatifPasseAnterieurJe
                    + "\n" + conjugation!!.indicatifPasseAnterieurTu
                    + "\n" + conjugation!!.indicatifPasseAnterieurIl
                    + "\n" + conjugation!!.indicatifPasseAnterieurNous
                    + "\n" + conjugation!!.indicatifPasseAnterieurVous
                    + "\n" + conjugation!!.indicatifPasseAnterieurIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.futur_simple) + ":"
                    + "\n" + conjugation!!.indicatifFuturSimpleJe
                    + "\n" + conjugation!!.indicatifFuturSimpleTu
                    + "\n" + conjugation!!.indicatifFuturSimpleIl
                    + "\n" + conjugation!!.indicatifFuturSimpleNous
                    + "\n" + conjugation!!.indicatifFuturSimpleVous
                    + "\n" + conjugation!!.indicatifFuturSimpleIls
                    + "\n\n" + getString(R.string.indicatif) + " " + getString(R.string.futur_anterieur) + ":"
                    + "\n" + conjugation!!.indicatifFuturAnterieurJe
                    + "\n" + conjugation!!.indicatifFuturAnterieurTu
                    + "\n" + conjugation!!.indicatifFuturAnterieurIl
                    + "\n" + conjugation!!.indicatifFuturAnterieurNous
                    + "\n" + conjugation!!.indicatifFuturAnterieurVous
                    + "\n" + conjugation!!.indicatifFuturAnterieurIls
                    + "\n\n" + getString(R.string.conditionnel) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.conditionnelPresentJe
                    + "\n" + conjugation!!.conditionnelPresentTu
                    + "\n" + conjugation!!.conditionnelPresentIl
                    + "\n" + conjugation!!.conditionnelPresentNous
                    + "\n" + conjugation!!.conditionnelPresentVous
                    + "\n" + conjugation!!.conditionnelPresentIls
                    + "\n\n" + getString(R.string.conditionnel) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.conditionnelPasseJe
                    + "\n" + conjugation!!.conditionnelPasseTu
                    + "\n" + conjugation!!.conditionnelPasseIl
                    + "\n" + conjugation!!.conditionnelPasseNous
                    + "\n" + conjugation!!.conditionnelPasseVous
                    + "\n" + conjugation!!.conditionnelPasseIls
                    + "\n\n" + getString(R.string.subjonctif) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.subjonctifPresentJe
                    + "\n" + conjugation!!.subjonctifPresentTu
                    + "\n" + conjugation!!.subjonctifPresentIl
                    + "\n" + conjugation!!.subjonctifPresentNous
                    + "\n" + conjugation!!.subjonctifPresentVous
                    + "\n" + conjugation!!.subjonctifPresentIls
                    + "\n\n" + getString(R.string.subjonctif) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.subjonctifPasseJe
                    + "\n" + conjugation!!.subjonctifPasseTu
                    + "\n" + conjugation!!.subjonctifPasseIl
                    + "\n" + conjugation!!.subjonctifPasseNous
                    + "\n" + conjugation!!.subjonctifPasseVous
                    + "\n" + conjugation!!.subjonctifPasseIls
                    + "\n\n" + getString(R.string.subjonctif) + " " + getString(R.string.imperfait) + ":"
                    + "\n" + conjugation!!.subjonctifImperfaitJe
                    + "\n" + conjugation!!.subjonctifImperfaitTu
                    + "\n" + conjugation!!.subjonctifImperfaitIl
                    + "\n" + conjugation!!.subjonctifImperfaitNous
                    + "\n" + conjugation!!.subjonctifImperfaitVous
                    + "\n" + conjugation!!.subjonctifImperfaitIls
                    + "\n\n" + getString(R.string.subjonctif) + " " + getString(R.string.plus_que_parfait) + ":"
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitJe
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitTu
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitIl
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitNous
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitVous
                    + "\n" + conjugation!!.subjonctifPlusQueParfaitIls
                    + "\n\n" + getString(R.string.imperatif) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.imperatifPresentTu
                    + "\n" + conjugation!!.imperatifPresentNous
                    + "\n" + conjugation!!.imperatifPresentVous
                    + "\n\n" + getString(R.string.imperatif) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.imperatifPasseTu
                    + "\n" + conjugation!!.imperatifPasseNous
                    + "\n" + conjugation!!.imperatifPasseVous
                    + "\n\n" + getString(R.string.infinitive) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.infinitivePresent
                    + "\n\n" + getString(R.string.infinitive) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.infinitivePasse
                    + "\n\n" + getString(R.string.participe) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.participePresent
                    + "\n\n" + getString(R.string.participe) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.participePasse1
                    + "\n" + conjugation!!.participePasse2
                    + "\n\n" + getString(R.string.gerondif) + " " + getString(R.string.present) + ":"
                    + "\n" + conjugation!!.gerondifPresent
                    + "\n\n" + getString(R.string.gerondif) + " " + getString(R.string.passe) + ":"
                    + "\n" + conjugation!!.gerondifPasse*/ )
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
                COLUMN_ID + " = ?", arrayOf(java.lang.Long.toString(verbID)))

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
                COLUMN_ID + " = ?", // where
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
                    COLUMN_ID + " = ?", // selection clause
                    arrayOf(java.lang.Long.toString(conjugationID)), null)// selection arguments
        // Default sort order

            VERB_LOADER -> cursorLoader = CursorLoader(this, // Parent activity context
                    CONTENT_VERBS_URI, ActivityUtils.allVerbColumns(), // Columns in the resulting Cursor
                    COLUMN_ID + " = ?", // selection clause
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
        when (verb.group) {
            1 -> group!!.text = getString(R.string.group1)
            2 -> group!!.text = getString(R.string.group2)
            3 -> group!!.text = getString(R.string.group3)
        }

        definition!!.text = verb.definition
        sample1!!.text = verb.sample1
        sample2!!.text = verb.sample2
        sample3!!.text = verb.sample3

        val fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(applicationContext))
        (findViewById<View>(R.id.groupe) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
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
        changeTextFontInConjugation(fontSize)

        /*
        (findViewById<View>(R.id.infinitive_present) as TextView).text = c.infinitivePresent
        (findViewById<View>(R.id.infinitive_passe) as TextView).text = c.infinitivePasse
        (findViewById<View>(R.id.participe_present) as TextView).text = c.participePresent
        (findViewById<View>(R.id.participe_passe1) as TextView).text = c.participePasse1
        (findViewById<View>(R.id.participe_passe2) as TextView).text = c.participePasse2
        (findViewById<View>(R.id.gerondif_present) as TextView).text = c.gerondifPresent
        (findViewById<View>(R.id.gerondif_passe) as TextView).text = c.gerondifPasse

        (findViewById<View>(R.id.imperatif_present_tu) as TextView).text = c.imperatifPresentTu
        (findViewById<View>(R.id.imperatif_present_nous) as TextView).text = c.imperatifPresentNous
        (findViewById<View>(R.id.imperatif_present_vous) as TextView).text = c.imperatifPresentVous
        (findViewById<View>(R.id.imperatif_passe_tu) as TextView).text = c.imperatifPasseTu
        (findViewById<View>(R.id.imperatif_passe_nous) as TextView).text = c.imperatifPasseNous
        (findViewById<View>(R.id.imperatif_passe_vous) as TextView).text = c.imperatifPasseVous

        (findViewById<View>(R.id.indicative_present_je) as TextView).text = c.indicatifPresentJe
        (findViewById<View>(R.id.indicative_present_tu) as TextView).text = c.indicatifPresentTu
        (findViewById<View>(R.id.indicative_present_il) as TextView).text = c.indicatifPresentIl
        (findViewById<View>(R.id.indicative_present_nous) as TextView).text = c.indicatifPresentNous
        (findViewById<View>(R.id.indicative_present_vous) as TextView).text = c.indicatifPresentVous
        (findViewById<View>(R.id.indicative_present_ils) as TextView).text = c.indicatifPresentIls
        (findViewById<View>(R.id.indicative_passe_compose_je) as TextView).text = c.indicatifPasseComposeJe
        (findViewById<View>(R.id.indicative_passe_compose_tu) as TextView).text = c.indicatifPasseComposeTu
        (findViewById<View>(R.id.indicative_passe_compose_il) as TextView).text = c.indicatifPasseComposeIl
        (findViewById<View>(R.id.indicative_passe_compose_nous) as TextView).text = c.indicatifPasseComposeNous
        (findViewById<View>(R.id.indicative_passe_compose_vous) as TextView).text = c.indicatifPasseComposeVous
        (findViewById<View>(R.id.indicative_passe_compose_ils) as TextView).text = c.indicatifPasseComposeIls
        (findViewById<View>(R.id.indicative_imperfait_je) as TextView).text = c.indicatifImperfaitJe
        (findViewById<View>(R.id.indicative_imperfait_tu) as TextView).text = c.indicatifImperfaitTu
        (findViewById<View>(R.id.indicative_imperfait_il) as TextView).text = c.indicatifImperfaitIl
        (findViewById<View>(R.id.indicative_imperfait_nous) as TextView).text = c.indicatifImperfaitNous
        (findViewById<View>(R.id.indicative_imperfait_vous) as TextView).text = c.indicatifImperfaitVous
        (findViewById<View>(R.id.indicative_imperfait_ils) as TextView).text = c.indicatifImperfaitIls
        (findViewById<View>(R.id.indicative_plus_que_parfait_je) as TextView).text = c.indicatifPlusQueParfaitJe
        (findViewById<View>(R.id.indicative_plus_que_parfait_tu) as TextView).text = c.indicatifPlusQueParfaitTu
        (findViewById<View>(R.id.indicative_plus_que_parfait_il) as TextView).text = c.indicatifPlusQueParfaitIl
        (findViewById<View>(R.id.indicative_plus_que_parfait_nous) as TextView).text = c.indicatifPlusQueParfaitNous
        (findViewById<View>(R.id.indicative_plus_que_parfait_vous) as TextView).text = c.indicatifPlusQueParfaitVous
        (findViewById<View>(R.id.indicative_plus_que_parfait_ils) as TextView).text = c.indicatifPlusQueParfaitIls
        (findViewById<View>(R.id.indicative_passe_simple_je) as TextView).text = c.indicatifPasseSimpleJe
        (findViewById<View>(R.id.indicative_passe_simple_tu) as TextView).text = c.indicatifPasseSimpleTu
        (findViewById<View>(R.id.indicative_passe_simple_il) as TextView).text = c.indicatifPasseSimpleIl
        (findViewById<View>(R.id.indicative_passe_simple_nous) as TextView).text = c.indicatifPasseSimpleNous
        (findViewById<View>(R.id.indicative_passe_simple_vous) as TextView).text = c.indicatifPasseSimpleVous
        (findViewById<View>(R.id.indicative_passe_simple_ils) as TextView).text = c.indicatifPasseSimpleIls
        (findViewById<View>(R.id.indicative_passe_anterieur_je) as TextView).text = c.indicatifPasseAnterieurJe
        (findViewById<View>(R.id.indicative_passe_anterieur_tu) as TextView).text = c.indicatifPasseAnterieurTu
        (findViewById<View>(R.id.indicative_passe_anterieur_il) as TextView).text = c.indicatifPasseAnterieurIl
        (findViewById<View>(R.id.indicative_passe_anterieur_nous) as TextView).text = c.indicatifPasseAnterieurNous
        (findViewById<View>(R.id.indicative_passe_anterieur_vous) as TextView).text = c.indicatifPasseAnterieurVous
        (findViewById<View>(R.id.indicative_passe_anterieur_ils) as TextView).text = c.indicatifPasseAnterieurIls
        (findViewById<View>(R.id.indicative_futur_simple_je) as TextView).text = c.indicatifFuturSimpleJe
        (findViewById<View>(R.id.indicative_futur_simple_tu) as TextView).text = c.indicatifFuturSimpleTu
        (findViewById<View>(R.id.indicative_futur_simple_il) as TextView).text = c.indicatifFuturSimpleIl
        (findViewById<View>(R.id.indicative_futur_simple_nous) as TextView).text = c.indicatifFuturSimpleNous
        (findViewById<View>(R.id.indicative_futur_simple_vous) as TextView).text = c.indicatifFuturSimpleVous
        (findViewById<View>(R.id.indicative_futur_simple_ils) as TextView).text = c.indicatifFuturSimpleIls
        (findViewById<View>(R.id.indicative_futur_anterieur_je) as TextView).text = c.indicatifFuturAnterieurJe
        (findViewById<View>(R.id.indicative_futur_anterieur_tu) as TextView).text = c.indicatifFuturAnterieurTu
        (findViewById<View>(R.id.indicative_futur_anterieur_il) as TextView).text = c.indicatifFuturAnterieurIl
        (findViewById<View>(R.id.indicative_futur_anterieur_nous) as TextView).text = c.indicatifFuturAnterieurNous
        (findViewById<View>(R.id.indicative_futur_anterieur_vous) as TextView).text = c.indicatifFuturAnterieurVous
        (findViewById<View>(R.id.indicative_futur_anterieur_ils) as TextView).text = c.indicatifFuturAnterieurIls

        (findViewById<View>(R.id.conditionnel_present_je) as TextView).text = c.conditionnelPresentJe
        (findViewById<View>(R.id.conditionnel_present_tu) as TextView).text = c.conditionnelPresentTu
        (findViewById<View>(R.id.conditionnel_present_il) as TextView).text = c.conditionnelPresentIl
        (findViewById<View>(R.id.conditionnel_present_nous) as TextView).text = c.conditionnelPresentNous
        (findViewById<View>(R.id.conditionnel_present_vous) as TextView).text = c.conditionnelPresentVous
        (findViewById<View>(R.id.conditionnel_present_ils) as TextView).text = c.conditionnelPresentIls
        (findViewById<View>(R.id.conditionnel_passe_je) as TextView).text = c.conditionnelPasseJe
        (findViewById<View>(R.id.conditionnel_passe_tu) as TextView).text = c.conditionnelPasseTu
        (findViewById<View>(R.id.conditionnel_passe_il) as TextView).text = c.conditionnelPasseIl
        (findViewById<View>(R.id.conditionnel_passe_nous) as TextView).text = c.conditionnelPasseNous
        (findViewById<View>(R.id.conditionnel_passe_vous) as TextView).text = c.conditionnelPasseVous
        (findViewById<View>(R.id.conditionnel_passe_ils) as TextView).text = c.conditionnelPasseIls

        (findViewById<View>(R.id.subjonctif_present_je) as TextView).text = c.subjonctifPresentJe
        (findViewById<View>(R.id.subjonctif_present_tu) as TextView).text = c.subjonctifPresentTu
        (findViewById<View>(R.id.subjonctif_present_il) as TextView).text = c.subjonctifPresentIl
        (findViewById<View>(R.id.subjonctif_present_nous) as TextView).text = c.subjonctifPresentNous
        (findViewById<View>(R.id.subjonctif_present_vous) as TextView).text = c.subjonctifPresentVous
        (findViewById<View>(R.id.subjonctif_present_ils) as TextView).text = c.subjonctifPresentIls
        (findViewById<View>(R.id.subjonctif_passe_je) as TextView).text = c.subjonctifPasseJe
        (findViewById<View>(R.id.subjonctif_passe_tu) as TextView).text = c.subjonctifPasseTu
        (findViewById<View>(R.id.subjonctif_passe_il) as TextView).text = c.subjonctifPasseIl
        (findViewById<View>(R.id.subjonctif_passe_nous) as TextView).text = c.subjonctifPasseNous
        (findViewById<View>(R.id.subjonctif_passe_vous) as TextView).text = c.subjonctifPasseVous
        (findViewById<View>(R.id.subjonctif_passe_ils) as TextView).text = c.subjonctifPasseIls
        (findViewById<View>(R.id.subjonctif_imperfait_je) as TextView).text = c.subjonctifImperfaitJe
        (findViewById<View>(R.id.subjonctif_imperfait_tu) as TextView).text = c.subjonctifImperfaitTu
        (findViewById<View>(R.id.subjonctif_imperfait_il) as TextView).text = c.subjonctifImperfaitIl
        (findViewById<View>(R.id.subjonctif_imperfait_nous) as TextView).text = c.subjonctifImperfaitNous
        (findViewById<View>(R.id.subjonctif_imperfait_vous) as TextView).text = c.subjonctifImperfaitVous
        (findViewById<View>(R.id.subjonctif_imperfait_ils) as TextView).text = c.subjonctifImperfaitIls
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_je) as TextView).text = c.subjonctifPlusQueParfaitJe
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_tu) as TextView).text = c.subjonctifPlusQueParfaitTu
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_il) as TextView).text = c.subjonctifPlusQueParfaitIl
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_nous) as TextView).text = c.subjonctifPlusQueParfaitNous
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_vous) as TextView).text = c.subjonctifPlusQueParfaitVous
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_ils) as TextView).text = c.subjonctifPlusQueParfaitIls*/
    }


    /**
     * Changes text font size.
     * @param fontSize int
     */
    private fun changeTextFontInConjugation(fontSize: Int) {
        val unit = TypedValue.COMPLEX_UNIT_SP
        /*
        (findViewById<View>(R.id.indicative_present_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_present_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_present_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_present_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_present_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_present_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_compose_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_imperfait_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_plus_que_parfait_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_simple_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_passe_anterieur_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_simple_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.indicative_futur_anterieur_ils) as TextView).setTextSize(unit, fontSize.toFloat())

        (findViewById<View>(R.id.conditionnel_present_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_present_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_present_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_present_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_present_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_present_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.conditionnel_passe_ils) as TextView).setTextSize(unit, fontSize.toFloat())

        (findViewById<View>(R.id.subjonctif_present_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_present_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_present_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_present_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_present_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_present_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_passe_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_imperfait_ils) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_je) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_il) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.subjonctif_plus_que_parfait_ils) as TextView).setTextSize(unit, fontSize.toFloat())

        (findViewById<View>(R.id.imperatif_present_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.imperatif_present_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.imperatif_present_vous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.imperatif_passe_tu) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.imperatif_passe_nous) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.imperatif_passe_vous) as TextView).setTextSize(unit, fontSize.toFloat())

        (findViewById<View>(R.id.infinitive_present) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.infinitive_passe) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.participe_present) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.participe_passe1) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.participe_passe2) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.gerondif_present) as TextView).setTextSize(unit, fontSize.toFloat())
        (findViewById<View>(R.id.gerondif_passe) as TextView).setTextSize(unit, fontSize.toFloat())
        */
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
            R.id.play_infinitive -> if (verb != null) {
                ActivityUtils.speak(applicationContext, tts!!, verb!!.infinitive)
                Toast.makeText(applicationContext, verb!!.infinitive, Toast.LENGTH_SHORT).show()
            }

            R.id.play_definition -> if (verb != null) {
                ActivityUtils.speak(applicationContext, tts!!, verb!!.definition)
            }

            R.id.play_sample1 -> if (verb != null) {
                ActivityUtils.speak(applicationContext, tts!!, verb!!.sample1)
            }

            R.id.play_sample2 -> if (verb != null) {
                ActivityUtils.speak(applicationContext, tts!!, verb!!.sample2)
            }

            R.id.play_sample3 -> if (verb != null) {
                ActivityUtils.speak(applicationContext, tts!!, verb!!.sample3)
            }

            /*
            R.id.play_indicative_present_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentJe)
            }
            R.id.play_indicative_present_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentTu)
            }
            R.id.play_indicative_present_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentIl)
            }
            R.id.play_indicative_present_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentNous)
            }
            R.id.play_indicative_present_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentVous)
            }
            R.id.play_indicative_present_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPresentIls)
            }

            R.id.play_indicative_passe_compose_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeJe)
            }
            R.id.play_indicative_passe_compose_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeTu)
            }
            R.id.play_indicative_passe_compose_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeIl)
            }
            R.id.play_indicative_passe_compose_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeNous)
            }
            R.id.play_indicative_passe_compose_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeVous)
            }
            R.id.play_indicative_passe_compose_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseComposeIls)
            }

            R.id.play_indicative_imperfait_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitJe)
            }
            R.id.play_indicative_imperfait_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitTu)
            }
            R.id.play_indicative_imperfait_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitIl)
            }
            R.id.play_indicative_imperfait_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitNous)
            }
            R.id.play_indicative_imperfait_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitVous)
            }
            R.id.play_indicative_imperfait_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifImperfaitIls)
            }

            R.id.play_indicative_plus_que_parfait_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitJe)
            }
            R.id.play_indicative_plus_que_parfait_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitTu)
            }
            R.id.play_indicative_plus_que_parfait_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitIl)
            }
            R.id.play_indicative_plus_que_parfait_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitNous)
            }
            R.id.play_indicative_plus_que_parfait_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitVous)
            }
            R.id.play_indicative_plus_que_parfait_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPlusQueParfaitIls)
            }

            R.id.play_indicative_passe_simple_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleJe)
            }
            R.id.play_indicative_passe_simple_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleTu)
            }
            R.id.play_indicative_passe_simple_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleIl)
            }
            R.id.play_indicative_passe_simple_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleNous)
            }
            R.id.play_indicative_passe_simple_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleVous)
            }
            R.id.play_indicative_passe_simple_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseSimpleIls)
            }

            R.id.play_indicative_passe_anterieur_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurJe)
            }
            R.id.play_indicative_passe_anterieur_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurTu)
            }
            R.id.play_indicative_passe_anterieur_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurIl)
            }
            R.id.play_indicative_passe_anterieur_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurNous)
            }
            R.id.play_indicative_passe_anterieur_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurVous)
            }
            R.id.play_indicative_passe_anterieur_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifPasseAnterieurIls)
            }

            R.id.play_indicative_futur_simple_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleJe)
            }
            R.id.play_indicative_futur_simple_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleTu)
            }
            R.id.play_indicative_futur_simple_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleIl)
            }
            R.id.play_indicative_futur_simple_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleNous)
            }
            R.id.play_indicative_futur_simple_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleVous)
            }
            R.id.play_indicative_futur_simple_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturSimpleIls)
            }

            R.id.play_indicative_futur_anterieur_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurJe)
            }
            R.id.play_indicative_futur_anterieur_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurTu)
            }
            R.id.play_indicative_futur_anterieur_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurIl)
            }
            R.id.play_indicative_futur_anterieur_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurNous)
            }
            R.id.play_indicative_futur_anterieur_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurVous)
            }
            R.id.play_indicative_futur_anterieur_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.indicatifFuturAnterieurIls)
            }

            R.id.play_conditionnel_present_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentJe)
            }
            R.id.play_conditionnel_present_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentTu)
            }
            R.id.play_conditionnel_present_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentIl)
            }
            R.id.play_conditionnel_present_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentNous)
            }
            R.id.play_conditionnel_present_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentVous)
            }
            R.id.play_conditionnel_present_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPresentIls)
            }

            R.id.play_conditionnel_passe_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseJe)
            }
            R.id.play_conditionnel_passe_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseTu)
            }
            R.id.play_conditionnel_passe_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseIl)
            }
            R.id.play_conditionnel_passe_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseNous)
            }
            R.id.play_conditionnel_passe_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseVous)
            }
            R.id.play_conditionnel_passe_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.conditionnelPasseIls)
            }

            R.id.play_subjonctif_present_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentJe)
            }
            R.id.play_subjonctif_present_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentTu)
            }
            R.id.play_subjonctif_present_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentIl)
            }
            R.id.play_subjonctif_present_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentNous)
            }
            R.id.play_subjonctif_present_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentVous)
            }
            R.id.play_subjonctif_present_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPresentIls)
            }

            R.id.play_subjonctif_passe_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseJe)
            }
            R.id.play_subjonctif_passe_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseTu)
            }
            R.id.play_subjonctif_passe_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseIl)
            }
            R.id.play_subjonctif_passe_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseNous)
            }
            R.id.play_subjonctif_passe_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseVous)
            }
            R.id.play_subjonctif_passe_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPasseIls)
            }

            R.id.play_subjonctif_imperfait_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitJe)
            }
            R.id.play_subjonctif_imperfait_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitTu)
            }
            R.id.play_subjonctif_imperfait_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitIl)
            }
            R.id.play_subjonctif_imperfait_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitNous)
            }
            R.id.play_subjonctif_imperfait_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitVous)
            }
            R.id.play_subjonctif_imperfait_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifImperfaitIls)
            }

            R.id.play_subjonctif_plus_que_parfait_je -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitJe)
            }
            R.id.play_subjonctif_plus_que_parfait_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitTu)
            }
            R.id.play_subjonctif_plus_que_parfait_il -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitIl)
            }
            R.id.play_subjonctif_plus_que_parfait_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitNous)
            }
            R.id.play_subjonctif_plus_que_parfait_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitVous)
            }
            R.id.play_subjonctif_plus_que_parfait_ils -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.subjonctifPlusQueParfaitIls)
            }

            R.id.play_imperatif_present_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPresentTu)
            }
            R.id.play_imperatif_present_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPresentNous)
            }
            R.id.play_imperatif_present_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPresentVous)
            }
            R.id.play_imperatif_passe_tu -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPasseTu)
            }
            R.id.play_imperatif_passe_nous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPasseNous)
            }
            R.id.play_imperatif_passe_vous -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.imperatifPasseVous)
            }

            R.id.play_infinitive_present -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.infinitivePresent)
            }
            R.id.play_infinitive_passe -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.infinitivePasse)
            }
            R.id.play_participe_present -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.participePresent)
            }
            R.id.play_participe_passe1 -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.participePasse1)
            }
            R.id.play_participe_passe2 -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.participePasse2)
            }
            R.id.play_gerondif_present -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.gerondifPresent)
            }
            R.id.play_gerondif_passe -> if (conjugation != null) {
                ActivityUtils.speak(applicationContext, tts!!, conjugation!!.gerondifPasse)
            }*/

            else -> onClickDemo()
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
                showcaseView!!.setShowcase(ViewTarget(findViewById(R.id.groupe)), true)
                showcaseView!!.setContentText(getString(R.string.group))
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
