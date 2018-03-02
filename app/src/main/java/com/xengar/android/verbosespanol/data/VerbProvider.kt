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
package com.xengar.android.verbosespanol.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry

import com.xengar.android.verbosespanol.data.VerbContract.CONTENT_AUTHORITY
import com.xengar.android.verbosespanol.data.VerbContract.PATH_CONJUGATIONS
import com.xengar.android.verbosespanol.data.VerbContract.PATH_FAVORITES
import com.xengar.android.verbosespanol.data.VerbContract.PATH_FAVORITE_VERBS
import com.xengar.android.verbosespanol.data.VerbContract.PATH_VERBS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COLOR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COMMON
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_DEFINITION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GROUP
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SCORE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONJUGATION_TBL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_ITEM_TYPE_CONJUGATION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_ITEM_TYPE_FAVORITE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_ITEM_TYPE_VERB
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_LIST_TYPE_CONJUGATION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_LIST_TYPE_FAVORITE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_LIST_TYPE_VERB
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.FAVORITES_TBL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.VERBS_TBL
import com.xengar.android.verbosespanol.utils.Constants.LOG


/**
 * [ContentProvider] for Verbs app.
 */
class VerbProvider : ContentProvider() {

    companion object {

        /** Tag for the log messages  */
        private val TAG = VerbProvider::class.java.simpleName

        /** URI matcher code for the content URI for the verbs table  */
        private val VERBS = 100

        /** URI matcher code for the content URI for a single pet in the verbs table  */
        private val VERB_ID = 101
        private val FAVORITE_VERBS = 104

        private val FAVORITES = 200
        private val FAVORITE_ID = 201

        private val CONJUGATIONS = 300
        private val CONJUGATION_ID = 301


        /**
         * UriMatcher object to match a content URI to a corresponding code.
         * The input passed into the constructor represents the code to return for the root URI.
         * It's common to use NO_MATCH as the input for this case.
         */
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        // Static initializer. This is run the first time anything is called from this class.
        init {
            // The calls to addURI() go here, for all of the content URI patterns that the provider
            // should recognize. All paths added to the UriMatcher have a corresponding code to return
            // when a match is found.

            // The content URI of the form "content://com.xengar.android.verbosespanol/verbs" will map to
            // the integer code {@link #VERBS}. This URI is used to provide access to MULTIPLE rows
            // of the verbs table.
            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_VERBS, VERBS)
            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITE_VERBS, FAVORITE_VERBS)

            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITES, FAVORITES)

            // The content URI of the form "content://com.xengar.android.verbosespanol/verbs/#" will map
            // to the integer code {@link #VERB_ID}. This URI is used to provide access to ONE single
            // row of the verbs table.
            //
            // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
            // For example, "content://com.xengar.android.verbosespanol/verbs/3" matches, but
            // "content://com.xengar.android.verbosespanol/verbs" (without a number at the end) doesn't.
            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_VERBS + "/#", VERB_ID)
            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITES + "/#", FAVORITE_ID)

            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CONJUGATIONS, CONJUGATIONS)
            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CONJUGATIONS + "/#", CONJUGATION_ID)
        }
    }


    /** Database helper object  */
    private var mDbHelper: VerbDBHelper? = null

    override fun onCreate(): Boolean {
        mDbHelper = VerbDBHelper(context!!)
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get readable database
        val database = mDbHelper!!.readableDatabase

        // This cursor will hold the result of the query
        val cursor: Cursor

        // Figure out if the URI matcher can match the URI to a specific code
        val match = sUriMatcher.match(uri)
        when (match) {
            VERBS ->
                // For the VERBS code, query the verbs table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the verbs table.
                cursor = database.query(VERBS_TBL, projection, selection, selectionArgs,
                        null, null, sortOrder)

            CONJUGATIONS -> cursor = database.query(CONJUGATION_TBL, projection,
                    selection, selectionArgs, null, null, sortOrder)

            FAVORITES -> cursor = database.query(FAVORITES_TBL, projection,
                    selection, selectionArgs, null, null, sortOrder)

            VERB_ID -> {
                // For the VERB_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.xengar.android.verbosespanol/verbs/3",
                // the selection will be "COLUMN_ID=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = COLUMN_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                // This will perform a query on the verbs table where the COLUMN_ID equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(VERBS_TBL, projection, selection, selectionArgs,
                        null, null, sortOrder)
            }

            CONJUGATION_ID -> {
                selection = COLUMN_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database.query(CONJUGATION_TBL, projection, selection, selectionArgs,
                        null, null, sortOrder)
            }

            FAVORITE_ID -> {
                selection = COLUMN_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database.query(FAVORITES_TBL, projection, selection, selectionArgs,
                        null, null, sortOrder)
            }

            FAVORITE_VERBS -> {
                val columns = StringBuilder()
                for (i in projection!!.indices) {
                    if (i > 0) {
                        columns.append(",")
                    }
                    columns.append(projection[i])
                }
                val sort = if (sortOrder != null) " ORDER BY " + sortOrder else ""
                cursor = database.rawQuery("SELECT " + columns + " FROM " + VERBS_TBL
                        + " WHERE " + COLUMN_ID + " in "
                        + " (SELECT " + COLUMN_ID + " FROM " + FAVORITES_TBL + ")"
                        + sort, null)
            }

            else -> throw IllegalArgumentException("Cannot query unknown URI " + uri)
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        val context = context
        if (context != null) {
            cursor.setNotificationUri(getContext()!!.contentResolver, uri)
        }

        // Return the cursor
        return cursor
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        return when (match) {
            VERBS, FAVORITE_VERBS -> CONTENT_LIST_TYPE_VERB
            CONJUGATIONS -> CONTENT_LIST_TYPE_CONJUGATION
            VERB_ID -> CONTENT_ITEM_TYPE_VERB
            CONJUGATION_ID -> CONTENT_ITEM_TYPE_CONJUGATION
            FAVORITES -> CONTENT_LIST_TYPE_FAVORITE
            FAVORITE_ID -> CONTENT_ITEM_TYPE_FAVORITE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        when (match) {
            FAVORITES -> return insertFavorite(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for " + uri)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get writable database
        val database = mDbHelper!!.writableDatabase

        // Track the number of rows that were deleted
        val rowsDeleted: Int

        val match = sUriMatcher.match(uri)
        when (match) {
            FAVORITES -> rowsDeleted = database.delete(FAVORITES_TBL, selection, selectionArgs)
            FAVORITE_ID -> {
                selection = COLUMN_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                rowsDeleted = database.delete(FAVORITES_TBL, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for " + uri)
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        val context = context
        if (context != null && rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        // Return the number of rows deleted
        return rowsDeleted
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val match = sUriMatcher.match(uri)
        return when (match) {
            VERBS -> updateVerb(uri, contentValues, selection, selectionArgs)
            VERB_ID -> {
                // For the VERB_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = COLUMN_ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updateVerb(uri, contentValues, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for " + uri)
        }
    }

    private fun checkNull(value: String?, message: String) {
        if (value == null) {
            throw IllegalArgumentException(message)
        }
    }

    private fun checkNullAndPositive(value: Int?, message: String) {
        if (value != null && value < 0) {
            throw IllegalArgumentException(message)
        }
    }

    private fun checkCommonUsage(commonUsage: Int?, message: String) {
        if (commonUsage == null || !VerbEntry.isValidCommonUsage(commonUsage)) {
            throw IllegalArgumentException(message)
        }
    }

    private fun checkGroup(regular: Int?, message: String) {
        if (regular == null || !VerbEntry.isValidGroup(regular)) {
            throw IllegalArgumentException(message)
        }
    }

    /**
     * Insert a favorite into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private fun insertFavorite(uri: Uri, values: ContentValues?): Uri? {
        // Check the values
        checkNull(values!!.getAsString(COLUMN_ID), "Favorite requires verb id")

        // Get writable database
        val database = mDbHelper!!.writableDatabase

        // Insert the new verb with the given values
        val id = database.insert(FAVORITES_TBL, null, values)
        if (id.toInt() == -1) {
            if (LOG) {
                Log.e(TAG, "Failed to insert row for " + uri)
            }
            return null
        }

        // Notify all listeners that the data has changed for the favorite content URI
        val context = context
        context?.contentResolver?.notifyChange(uri, null)

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id)
    }

    /** If the key is there, checks that is not null  */
    private fun checkNotNullKeyString(values: ContentValues?, key: String, message: String) {
        if (values!!.containsKey(key)) {
            checkNull(values.getAsString(key), message)
        }
    }

    /** If the key is there, checks that is valid  */
    private fun checkCommonUsage(values: ContentValues?, key: String, message: String) {
        if (values!!.containsKey(key)) {
            checkCommonUsage(values.getAsInteger(key), message)
        }
    }

    /** If the key is there, checks that is valid  */
    private fun checkGroup(values: ContentValues?, key: String, message: String) {
        if (values!!.containsKey(key)) {
            checkGroup(values.getAsInteger(key), message)
        }
    }

    /** If the key is there, checks that is valid  */
    private fun checkNullAndPositive(values: ContentValues?, key: String, message: String) {
        if (values!!.containsKey(key)) {
            checkNullAndPositive(values.getAsInteger(key), message)
        }
    }

    /**
     * Update verbs in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more verbs).
     * Return the number of rows that were successfully updated.
     */
    private fun updateVerb(uri: Uri, values: ContentValues?, selection: String?,
                           selectionArgs: Array<String>?): Int {
        // Check possible value changes
        checkNotNullKeyString(values, COLUMN_INFINITIVE, "Verb requires infinitive")
        checkCommonUsage(values, COLUMN_COMMON,
                "Verb requires valid common usage (top 50, top 100)")
        checkGroup(values, COLUMN_GROUP, "Verb requires valid group value (1, 2, 3)")
        checkNotNullKeyString(values, COLUMN_DEFINITION, "Verb requires a definition")
        /*checkNotNullKeyString(values, COLUMN_SAMPLE_1, "Verb requires sample 1");
        checkNotNullKeyString(values, COLUMN_SAMPLE_2, "Verb requires sample 2");
        checkNotNullKeyString(values, COLUMN_SAMPLE_3, "Verb requires sample 3");*/
        checkNotNullKeyString(values, COLUMN_COLOR, "Verb requires valid color")
        checkNullAndPositive(values, COLUMN_SCORE, "Verb requires valid score")

        // If there are no values to update, then don't try to update the database
        if (values!!.size() == 0) {
            return 0
        }

        // Otherwise, get writeable database to update the data
        val database = mDbHelper!!.writableDatabase

        // Perform the update on the database and get the number of rows affected
        val rowsUpdated = database.update(VERBS_TBL, values, selection, selectionArgs)

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        val context = context
        if (context != null && rowsUpdated != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        // Return the number of rows updated
        return rowsUpdated
    }
}
