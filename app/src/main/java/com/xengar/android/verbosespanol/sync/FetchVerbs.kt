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
package com.xengar.android.verbosespanol.sync

import android.content.ContentResolver
import android.database.Cursor
import android.os.AsyncTask
import android.util.Log

import com.xengar.android.verbosespanol.adapter.VerbAdapter
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.FragmentUtils

import java.util.ArrayList

import fr.castorflex.android.circularprogressbar.CircularProgressBar

import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COLOR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COMMON
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_REGULAR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_FAVORITE_VERBS_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_VERBS_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_100
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_1000
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_25
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_300
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_50
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_500
import com.xengar.android.verbosespanol.utils.Constants.ALPHABET
import com.xengar.android.verbosespanol.utils.Constants.COLOR
import com.xengar.android.verbosespanol.utils.Constants.FAVORITES
import com.xengar.android.verbosespanol.utils.Constants.REGULAR
import com.xengar.android.verbosespanol.utils.Constants.BOTH
import com.xengar.android.verbosespanol.utils.Constants.IRREGULAR
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_100
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_1000
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_25
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_300
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_50
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_500
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_ALL

/**
 * FetchVerbs from the database.
 */
class FetchVerbs// Constructor
(private val type: String, // Verb type (regular, irregular, both)
 private val sort: String, // Sort order (alphabet, color, groups)
 private val common: String, // Common (Top50, Top100, Top25, all)
 private val adapter: VerbAdapter,
 private val contentResolver: ContentResolver,
 private val verbs: MutableList<Verb>,
 private val progressBar: CircularProgressBar)
    : AsyncTask<Void, Void, ArrayList<Verb>>() {

    private val TAG = FetchVerbs::class.java.simpleName

    override fun doInBackground(vararg voids: Void): ArrayList<Verb> {
        // Define a projection that specifies the columns from the table we care about.
        val columns = ActivityUtils.allVerbColumns()
        val sortOrder: String = when (sort) {
            ALPHABET -> COLUMN_INFINITIVE + " ASC"
            COLOR -> "$COLUMN_COLOR DESC, $COLUMN_INFINITIVE ASC"
            REGULAR -> "$COLUMN_REGULAR ASC, $COLUMN_INFINITIVE ASC"
            else -> COLUMN_INFINITIVE + " ASC"
        }

        var where: String? = null
        val listArgs = ArrayList<String>()
        when (common) {
            MOST_COMMON_25 -> {
                where = COLUMN_COMMON + " = ?"
                listArgs.add(S_TOP_25)
            }
            MOST_COMMON_50 -> {
                where = "$COLUMN_COMMON = ? OR $COLUMN_COMMON = ?"
                listArgs.add(S_TOP_25)
                listArgs.add(S_TOP_50)
            }
            MOST_COMMON_100 -> {
                where = "$COLUMN_COMMON = ? OR $COLUMN_COMMON = ? OR $COLUMN_COMMON = ?"
                listArgs.add(S_TOP_25)
                listArgs.add(S_TOP_50)
                listArgs.add(S_TOP_100)
            }
            MOST_COMMON_300 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ?")
                listArgs.add(S_TOP_25)
                listArgs.add(S_TOP_50)
                listArgs.add(S_TOP_100)
                listArgs.add(S_TOP_300)
            }
            MOST_COMMON_500 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ? OR "
                        + COLUMN_COMMON + " = ?")
                listArgs.add(S_TOP_25)
                listArgs.add(S_TOP_50)
                listArgs.add(S_TOP_100)
                listArgs.add(S_TOP_300)
                listArgs.add(S_TOP_500)
            }
            MOST_COMMON_1000 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ? OR "
                        + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ?")
                listArgs.add(S_TOP_25)
                listArgs.add(S_TOP_50)
                listArgs.add(S_TOP_100)
                listArgs.add(S_TOP_300)
                listArgs.add(S_TOP_500)
                listArgs.add(S_TOP_1000)
            }
            MOST_COMMON_ALL -> {
            }
            else -> {
            }
        }

        val cursor: Cursor?
        when (type) {
            REGULAR, IRREGULAR -> {
                where = if (where == null) {
                    COLUMN_REGULAR + " = ?"
                } else {
                    "($where) AND $COLUMN_REGULAR = ?"
                }
                listArgs.add(if (type.contentEquals(REGULAR)) "0" else "1")
                val whereArgs = if (listArgs.size > 0) listArgs.toTypedArray() else null
                cursor = contentResolver.query(CONTENT_VERBS_URI, columns, where, whereArgs, sortOrder)
            }
            BOTH -> {
                val whereArgs = if (listArgs.size > 0) listArgs.toTypedArray() else null
                cursor = contentResolver.query(CONTENT_VERBS_URI, columns, where, whereArgs, sortOrder)
            }

            FAVORITES -> cursor = contentResolver.query(CONTENT_FAVORITE_VERBS_URI, columns,
                    null, null, sortOrder)
            else -> {
                val whereArgs = if (listArgs.size > 0) listArgs.toTypedArray() else null
                cursor = contentResolver.query(CONTENT_VERBS_URI, columns, where, whereArgs, sortOrder)
            }
        }

        val verbs = ArrayList<Verb>()
        if (cursor != null && cursor.count != 0) {
            while (cursor.moveToNext()) {
                verbs.add(ActivityUtils.verbFromCursor(cursor))
            }
        } else {
            if (LOG) {
                Log.d(TAG, "Cursor is empty")
            }
        }
        cursor?.close()
        return verbs
    }

    override fun onPostExecute(list: ArrayList<Verb>?) {
        super.onPostExecute(list)
        if (list != null) {
            verbs.addAll(list)
            adapter.notifyDataSetChanged()
        }
        FragmentUtils.updateProgressBar(progressBar, false)
    }
}
