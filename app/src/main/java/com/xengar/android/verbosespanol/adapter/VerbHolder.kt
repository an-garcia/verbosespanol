/*
 * Copyright (C) 2018 Angel Newton
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
package com.xengar.android.verbosespanol.adapter

import android.content.Context
import android.speech.tts.TextToSpeech
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.utils.ActivityUtils

import com.xengar.android.verbosespanol.utils.Constants.CARD
import com.xengar.android.verbosespanol.utils.Constants.DRAWABLE
import com.xengar.android.verbosespanol.utils.Constants.VERB

/**
 * VerbHolder. Represents an item view in screen
 */
class VerbHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val context: Context
    private val infinitive: TextView
    private val translation: TextView
    private val definition: TextView
    private var tts: TextToSpeech? = null
    private var verb: Verb? = null
    private var regular: TextView? = null
    private var definitionTitle: TextView? = null
    private var sample1: TextView? = null
    private var sample2: TextView? = null
    private var sample3: TextView? = null
    private var imageVerb: ImageView? = null
    private var playDefinition: LinearLayout? = null


    init {
        // List items
        infinitive = view.findViewById(R.id.infinitive)
        definition = view.findViewById(R.id.definition)
        translation = view.findViewById(R.id.translation)

        // Card items
        definitionTitle = view.findViewById(R.id.definition_title)
        regular = view.findViewById(R.id.regular)
        imageVerb = view.findViewById(R.id.verb_image)
        sample1 = view.findViewById(R.id.sample1)
        sample2 = view.findViewById(R.id.sample2)
        sample3 = view.findViewById(R.id.sample3)

        // define click listeners
        var header: LinearLayout? = view.findViewById(R.id.verb_list_item)
        if (header != null) {
            header.setOnClickListener(this)
        }
        header = view.findViewById(R.id.play_infinitive)
        if (header != null) {
            header.setOnClickListener(this)
        }
        header = view.findViewById(R.id.play_definition)
        playDefinition = header
        if (header != null) {
            header.setOnClickListener(this)
        }
        header = view.findViewById(R.id.play_sample1)
        if (header != null) {
            header.setOnClickListener(this)
        }
        header = view.findViewById(R.id.play_sample2)
        if (header != null) {
            header.setOnClickListener(this)
        }
        header = view.findViewById(R.id.play_sample3)
        if (header != null) {
            header.setOnClickListener(this)
        }

        context = view.context
        view.setOnClickListener(this)
    }

    /**
     * Fills the view with the verb object.
     * @param verb Verb
     * @param layoutType LIST, CARD
     * @param tts TextToSpeech
     */
    fun bindVerb(verb: Verb, layoutType: String, tts: TextToSpeech) {
        this.verb = verb
        this.tts = tts
        val fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(context))

        // Set values
        infinitive.text = verb.infinitive
        infinitive.setTextColor(verb.color)
        infinitive.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

        definition.text = verb.definition
        definition.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        if (!ActivityUtils.getPreferenceShowDefinitions(context)) {
            definition.visibility = View.GONE
            if (definitionTitle != null) {
                definitionTitle!!.visibility = View.GONE
            }
            if (playDefinition != null) {
                playDefinition!!.visibility = View.GONE
            }
        }

        if (layoutType.contentEquals(CARD)) {
            when (verb.regular) {
                0 -> regular!!.text = context.resources.getString(R.string.regular)
                1 -> regular!!.text = context.resources.getString(R.string.irregular)
            }
            regular!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

            sample1!!.text = verb.sample1
            sample2!!.text = verb.sample2
            sample3!!.text = verb.sample3
            sample1!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            sample2!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            sample3!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

            // Try to put the verb image
            //val imageName = VERB + ActivityUtils.generateImageName(verb.infinitive)
            val imageName = VERB + "ensenar"
            val imageId = context.resources.getIdentifier(imageName, DRAWABLE,
                    context.packageName)
            if (imageId != 0) {
                ActivityUtils.setImage(context, imageVerb!!, imageId)
            }
        }

        ActivityUtils.setTranslation(context, translation, verb)
    }

    // Handles the item click.
    override fun onClick(view: View) {
        val position = adapterPosition // gets item position
        // Check if an item was deleted, but the user clicked it before the UI removed it
        if (position == RecyclerView.NO_POSITION) {
            return
        }

        // Play the sounds
        when (view.id) {
            R.id.play_infinitive -> if (verb != null) {
                ActivityUtils.speak(context, tts, verb?.infinitive)
                Toast.makeText(context, verb!!.infinitive, Toast.LENGTH_SHORT).show()
            }

            R.id.play_definition -> ActivityUtils.speak(context, tts, verb?.definition)
            R.id.play_sample1 -> ActivityUtils.speak(context, tts!!, verb?.sample1)
            R.id.play_sample2 -> ActivityUtils.speak(context, tts, verb?.sample2)
            R.id.play_sample3 -> ActivityUtils.speak(context, tts, verb?.sample3)

            else -> if (verb != null) {
                ActivityUtils.launchDetailsActivity(context, verb!!.id,
                        verb!!.conjugation.toLong(), verb!!.infinitive, false)
                //Toast.makeText(context, verb!!.infinitive, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
