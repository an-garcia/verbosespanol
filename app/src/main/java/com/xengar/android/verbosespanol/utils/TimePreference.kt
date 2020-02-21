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
package com.xengar.android.verbosespanol.utils

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker

import com.xengar.android.verbosespanol.R

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

/**
 * TimePreference
 * Source: http://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 */
class TimePreference @JvmOverloads
constructor(ctxt: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.dialogPreferenceStyle)
    : DialogPreference(ctxt, attrs, defStyle) {

    private val calendar: Calendar?
    private var picker: TimePicker? = null

    init {

        setPositiveButtonText(R.string.ok)
        setNegativeButtonText(R.string.cancel)
        calendar = GregorianCalendar()
    }

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        return picker!!
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker!!.currentHour = calendar!!.get(Calendar.HOUR_OF_DAY)
        picker!!.currentMinute = calendar.get(Calendar.MINUTE)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar!!.set(Calendar.HOUR_OF_DAY, picker!!.currentHour)
            calendar.set(Calendar.MINUTE, picker!!.currentMinute)

            summary = summary
            if (callChangeListener(calendar.timeInMillis)) {
                persistLong(calendar.timeInMillis)
                notifyChanged()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {

        if (restoreValue) {
            if (defaultValue == null) {
                calendar!!.timeInMillis = getPersistedLong(System.currentTimeMillis())
            } else {
                calendar!!.timeInMillis = java.lang.Long.parseLong(getPersistedString(defaultValue as String?))
            }
        } else {
            if (defaultValue == null) {
                calendar!!.timeInMillis = System.currentTimeMillis()
            } else {
                calendar!!.timeInMillis = java.lang.Long.parseLong(defaultValue as String?)
            }
        }
        summary = summary
    }

    override fun getSummary(): CharSequence? {
        return if (calendar == null) {
            null
        } else DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
    }
}
