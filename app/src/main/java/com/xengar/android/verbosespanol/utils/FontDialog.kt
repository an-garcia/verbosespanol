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
import android.preference.DialogPreference
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.utils.ActivityUtils

/**
 * FontDialog in Preferences.
 */
class FontDialog : DialogPreference, View.OnClickListener {
    private var fontSize = 0
    private var text: TextView? = null


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        isPersistent = false
        dialogLayoutResource = R.layout.font_size_dialog
    }

    override fun onBindDialogView(view: View) {
        text = view.findViewById(R.id.font_size)
        fontSize = Integer.parseInt(ActivityUtils.getPreferenceFontSize(context))
        text!!.text = fontSize.toString()
        text!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

        // Set listener
        val imageMinus = view.findViewById<ImageView>(R.id.minus)
        imageMinus.setOnClickListener(this)
        val imagePlus = view.findViewById<ImageView>(R.id.plus)
        imagePlus.setOnClickListener(this)

        super.onBindDialogView(view)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.minus -> if (fontSize > MIN_FONT_SIZE) {
                fontSize--
                text!!.text = fontSize.toString()
                text!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            }

            R.id.plus -> if (fontSize < MAX_FONT_SIZE) {
                fontSize++
                text!!.text = fontSize.toString()
                text!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)
        // save shared preferences
        val key = context.getString(R.string.pref_font_size)
        val editor = editor
        editor.putString(key, fontSize.toString())
        editor.commit()
    }

    companion object {

        private val MIN_FONT_SIZE = 6
        private val MAX_FONT_SIZE = 100
    }
}
