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
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.xengar.android.verbosespanol.R

/**
 * CustomErrorView
 */
class CustomErrorView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val errorTextView: TextView
    private val errorMessageTextView: TextView

    init {
        val view = View.inflate(context, R.layout.custom_error_view, this)
        errorTextView = view.findViewById(R.id.error_text)
        errorMessageTextView = view.findViewById(R.id.error_message_text)
    }

    fun setError(t: Throwable) {
        errorTextView.text = resources.getString(R.string.database_error)
        errorMessageTextView.text = t.message
    }
}
