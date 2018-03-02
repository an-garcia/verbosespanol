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

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.xengar.android.verbosespanol.BuildConfig
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.Constants.PAGE_HELP
import com.xengar.android.verbosespanol.utils.Constants.TYPE_CONTEXT_HELP
import com.xengar.android.verbosespanol.utils.Constants.TYPE_PAGE

import kotlinx.android.synthetic.main.activity_help.*

/**
 * HelpActivity
 */
class HelpActivity : AppCompatActivity(), View.OnClickListener {

    //private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // define click listeners
        val header = findViewById<LinearLayout>(R.id.header_verbs)
        header.setOnClickListener(this)

        // Change text size
        val fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(applicationContext))
        (findViewById<View>(R.id.description_main) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        (findViewById<View>(R.id.description_volume) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
        //        PAGE_HELP, PAGE_HELP, TYPE_PAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.help, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_feedback -> {
                giveFeedback()
                return true
            }

            R.id.action_problem -> {
                reportProblem()
                return true
            }

            R.id.action_license -> {
                showLicense()
                return true
            }

            R.id.action_version -> {
                showVersion()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * Send feedback email.
     */
    private fun reportProblem() {
        val sendMessage = Intent(Intent.ACTION_SEND)
        sendMessage.type = "message/rfc822"
        sendMessage.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.feedback_email)))
        sendMessage.putExtra(Intent.EXTRA_SUBJECT, "Verbos Español Problem")
        sendMessage.putExtra(Intent.EXTRA_TEXT,
                resources.getString(R.string.problem_message))
        try {
            startActivity(Intent.createChooser(sendMessage, "Report a problem"))
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(applicationContext, "Communication app not found",
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Send feedback email.
     */
    private fun giveFeedback() {
        val sendMessage = Intent(Intent.ACTION_SEND)
        sendMessage.type = "message/rfc822"
        sendMessage.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.feedback_email)))
        sendMessage.putExtra(Intent.EXTRA_SUBJECT, "Verbos Español Feedback")
        sendMessage.putExtra(Intent.EXTRA_TEXT,
                resources.getString(R.string.feedback_message))
        try {
            startActivity(Intent.createChooser(sendMessage, "Give Feedback"))
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(applicationContext, "Communication app not found",
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Show version information
     */
    private fun showVersion() {
        //set up dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.version_dialog)
        dialog.setCancelable(true)

        //set up text
        val text = dialog.findViewById<TextView>(R.id.version_number)
        text.text = BuildConfig.VERSION_NAME

        dialog.show()
    }

    /**
     * Show License information
     */
    private fun showLicense() {
        // retrieve display dimensions
        val displayRectangle = Rect()
        val window = window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.license_dialog, null)
        layout.minimumWidth = (displayRectangle.width() * 0.75f).toInt()
        layout.minimumHeight = (displayRectangle.height() * 0.75f).toInt()

        //set up dialog
        val dialog = Dialog(this)
        dialog.setContentView(layout)
        dialog.setCancelable(true)

        //set up text
        val text = dialog.findViewById<TextView>(R.id.copyright)
        text.text = ActivityUtils.fromHtml(getString(R.string.eula_string))

        dialog.show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.header_verbs -> {
                // amar
                //ActivityUtils.launchDetailsActivity(applicationContext, 1, true)
                //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
                //        "Contextual help", "buy", TYPE_CONTEXT_HELP)
            }
        }
    }

}
