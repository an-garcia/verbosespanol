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

import android.content.ContentValues
import android.content.Context
import android.content.res.XmlResourceParser
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v4.content.ContextCompat
import android.util.Log

import com.xengar.android.verbosespanol.R

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException

import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COLOR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COMMON
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PASSE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONDITIONNEL_PRESENT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONJUGATION_NUMBER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_DEFINITION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERONDIF_PASSE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERONDIF_PRESENT
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GROUP
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PASSE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PASSE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PASSE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PRESENT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PRESENT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIF_PRESENT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_ANTERIEUR_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_FUTUR_SIMPLE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_IMPERFAIT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_ANTERIEUR_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_COMPOSE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PASSE_SIMPLE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PLUS_QUE_PARFAIT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIF_PRESENT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE_PASSE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE_PRESENT
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_NOTES
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_PARTICIPE_PASSE_1
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_PARTICIPE_PASSE_2
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_PARTICIPE_PRESENT
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_RADICALS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_1
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_2
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_3
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SCORE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_IMPERFAIT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PASSE_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_IL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_ILS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_JE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_NOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJONTIF_PRESENT_VOUS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TERMINATION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_EN
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_FR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_TRANSLATION_PT
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONJUGATION_TBL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.FAVORITES_TBL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.VERBS_TBL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion._ID
import com.xengar.android.verbosespanol.utils.Constants.LOG

/**
 * Database helper for Verbs app. Manages database creation and version management.
 */
class VerbDBHelper
/**
 * Constructs a new instance of [VerbDBHelper].
 * @param context of the app
 */
(private val context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private val TAG = VerbDBHelper::class.java.simpleName

        /** Name of the database file  */
        private val DATABASE_NAME = "verbs.db"

        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private val DATABASE_VERSION = 1

        // List of pre-loaded favorite verbs.
        private val favorites = arrayOf(arrayOf("1"), // être
                arrayOf("2"), // avoir
                arrayOf("3"), // faire
                arrayOf("4"), // dire
                arrayOf("5")  // pouvoir
        )

        /**
         * Count the predefined verbs in the xml file.
         * @param context Context
         * @return count
         */
        fun countPredefinedVerbs(context: Context): Int {
            val parser = context.resources.getXml(R.xml.verbs)
            var eventType = -1
            var count = 0
            try {
                // Loop through the XML data
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlResourceParser.START_TAG) {
                        val verbValue = parser.name
                        if (verbValue == "verb") {
                            count++
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                if (LOG) {
                    Log.e(TAG, "Error loading verbs xml file. ")
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                if (LOG) {
                    Log.e(TAG, "Error loading verbs xml file. ")
                }
            }

            return count
        }
    }


    /**
     * This is called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {
        createCurrentSchemaVersion(db)
        insertVerbs(db)
        insertFavorites(db)
        insertConjugation(db)
    }

    /**
     * Creates the schema for version.
     * NOTE: If the version changes, add code for the upgrade also.
     * @param db SQLiteDatabase
     */
    private fun createCurrentSchemaVersion(db: SQLiteDatabase) {
        createSchemaVersion01(db)
    }

    /**
     * Creates the schema for version 1.
     * @param db SQLiteDatabase
     */
    private fun createSchemaVersion01(db: SQLiteDatabase) {
        // Create a String that contains the SQL statement to create the verbs table
        // Execute the SQL statement
        db.execSQL("CREATE TABLE " + VERBS_TBL + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID + " INTEGER NOT NULL, "
                + COLUMN_CONJUGATION_NUMBER + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_INFINITIVE + " TEXT NOT NULL, "
                + COLUMN_SAMPLE_1 + " TEXT, "
                + COLUMN_SAMPLE_2 + " TEXT, "
                + COLUMN_SAMPLE_3 + " TEXT, "
                + COLUMN_COMMON + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_GROUP + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_COLOR + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_DEFINITION + " TEXT NOT NULL, "
                + COLUMN_NOTES + " TEXT, "
                + COLUMN_TRANSLATION_EN + " TEXT, "
                + COLUMN_TRANSLATION_FR + " TEXT, "
                + COLUMN_TRANSLATION_PT + " TEXT);")

        db.execSQL("CREATE TABLE " + FAVORITES_TBL + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID + " INTEGER NOT NULL); ")

        db.execSQL("CREATE TABLE " + CONJUGATION_TBL + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID + " INTEGER NOT NULL, "
                + COLUMN_TERMINATION + " TEXT, "
                + COLUMN_RADICALS + " TEXT, "
                + COLUMN_INFINITIVE_PRESENT + " TEXT NOT NULL, "
                + COLUMN_INFINITIVE_PASSE + " TEXT NOT NULL, "
                + COLUMN_PARTICIPE_PRESENT + " TEXT NOT NULL, "
                + COLUMN_PARTICIPE_PASSE_1 + " TEXT NOT NULL, "
                + COLUMN_PARTICIPE_PASSE_2 + " TEXT NOT NULL, "
                + COLUMN_GERONDIF_PRESENT + " TEXT NOT NULL, "
                + COLUMN_GERONDIF_PASSE + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PRESENT_TU + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PRESENT_NOUS + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PRESENT_VOUS + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PASSE_TU + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PASSE_NOUS + " TEXT NOT NULL, "
                + COLUMN_IMPERATIF_PASSE_VOUS + " TEXT NOT NULL, "
                + COLUMN_INDICATIF_PRESENT_JE + " TEXT, "
                + COLUMN_INDICATIF_PRESENT_TU + " TEXT, "
                + COLUMN_INDICATIF_PRESENT_IL + " TEXT, "
                + COLUMN_INDICATIF_PRESENT_NOUS + " TEXT, "
                + COLUMN_INDICATIF_PRESENT_VOUS + " TEXT, "
                + COLUMN_INDICATIF_PRESENT_ILS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_JE + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_TU + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_IL + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_NOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_VOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_COMPOSE_ILS + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_JE + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_TU + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_IL + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_NOUS + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_VOUS + " TEXT, "
                + COLUMN_INDICATIF_IMPERFAIT_ILS + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_JE + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_TU + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_IL + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_NOUS + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_VOUS + " TEXT, "
                + COLUMN_INDICATIF_PLUS_QUE_PARFAIT_ILS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_JE + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_TU + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_IL + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_NOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_VOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_SIMPLE_ILS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_JE + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_TU + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_IL + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_NOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_VOUS + " TEXT, "
                + COLUMN_INDICATIF_PASSE_ANTERIEUR_ILS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_JE + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_TU + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_IL + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_NOUS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_VOUS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_SIMPLE_ILS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_JE + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_TU + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_IL + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_NOUS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_VOUS + " TEXT, "
                + COLUMN_INDICATIF_FUTUR_ANTERIEUR_ILS + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_JE + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_TU + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_IL + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_NOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_VOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PRESENT_ILS + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_JE + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_TU + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_IL + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_NOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_VOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PASSE_ILS + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_JE + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_TU + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_IL + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_NOUS + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_VOUS + " TEXT, "
                + COLUMN_SUBJONTIF_IMPERFAIT_ILS + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_JE + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_TU + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_IL + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_NOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_VOUS + " TEXT, "
                + COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_ILS + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_JE + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_TU + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_IL + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_NOUS + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_VOUS + " TEXT, "
                + COLUMN_CONDITIONNEL_PRESENT_ILS + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_JE + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_TU + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_IL + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_NOUS + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_VOUS + " TEXT, "
                + COLUMN_CONDITIONNEL_PASSE_ILS + " TEXT);")
    }


    /**
     * This is called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion > newVersion) {
            // This should not happen, version numbers should increment. Start clean.
            db.execSQL("DROP TABLE IF EXISTS " + VERBS_TBL)
            db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TBL)
            db.execSQL("DROP TABLE IF EXISTS " + CONJUGATION_TBL)
        }

        // Update version by version using a method for the update.
        when (oldVersion) {
            -1 -> {
            }
            else -> {
            }
        }
    }

    /**
     * Insert the 5 most common verbs.
     * @param db SQLiteDatabase
     */
    private fun insertFavorites(db: SQLiteDatabase) {
        val values = ContentValues()
        val updateValues = ContentValues()
        val FAVORITES_COLOR = "" + ContextCompat.getColor(context, R.color.colorDeepOrange)
        for (i in favorites.indices) {
            values.put("_id", i)
            values.put(COLUMN_ID, favorites[i][0])
            db.insertWithOnConflict(FAVORITES_TBL, null, values, CONFLICT_REPLACE)

            // Change color
            updateValues.put(COLUMN_COLOR, FAVORITES_COLOR)
            db.updateWithOnConflict(VERBS_TBL, updateValues,
                    COLUMN_ID + " = ?", arrayOf(Integer.toString(i)), CONFLICT_REPLACE)
        }
    }

    /**
     * Insert default verbs.
     * NOTE: If the resources change, add code for the upgrade also.
     * @param db SQLiteDatabase
     */
    private fun insertVerbs(db: SQLiteDatabase) {
        insertVerbsToSchema01(db)
    }

    /**
     * Insert default verbs.
     * @param db SQLiteDatabase
     */
    private fun insertVerbsToSchema01(db: SQLiteDatabase) {
        val values = ContentValues()
        val DEFAULT_COLOR = "" + ContextCompat.getColor(context, R.color.colorBlack)
        val DEFAULT_SCORE = "0"

        // Initialize a XmlResourceParser instance
        val parser = context.resources.getXml(R.xml.verbs)
        var eventType = -1
        var i = 0
        var verbName: String
        var verbId: String
        try {
            // Loop through the XML data
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG) {
                    val item = parser.name
                    if (item == "verb") {
                        values.put("_id", i)
                        verbId = parser.getAttributeValue(null, "id")
                        verbName = parser.getAttributeValue(null, "in")
                        values.put(COLUMN_ID, verbId)
                        values.put(COLUMN_CONJUGATION_NUMBER, parser.getAttributeValue(null, "ta"))
                        values.put(COLUMN_INFINITIVE, verbName)
                        values.put(COLUMN_SAMPLE_1, parser.getAttributeValue(null, "s1"))
                        values.put(COLUMN_SAMPLE_2, parser.getAttributeValue(null, "s2"))
                        values.put(COLUMN_SAMPLE_3, parser.getAttributeValue(null, "s3"))
                        values.put(COLUMN_COMMON, parser.getAttributeValue(null, "co"))
                        values.put(COLUMN_GROUP, parser.getAttributeValue(null, "gr"))
                        values.put(COLUMN_COLOR, DEFAULT_COLOR)
                        values.put(COLUMN_SCORE, DEFAULT_SCORE)
                        values.put(COLUMN_DEFINITION, parser.getAttributeValue(null, "de"))
                        values.put(COLUMN_NOTES, parser.getAttributeValue(null, "no"))
                        values.put(COLUMN_TRANSLATION_EN, parser.getAttributeValue(null, "tren"))
                        values.put(COLUMN_TRANSLATION_FR, parser.getAttributeValue(null, "trfr"))
                        values.put(COLUMN_TRANSLATION_PT, parser.getAttributeValue(null, "trpt"))
                        try {
                            db.insertWithOnConflict(VERBS_TBL, null, values, CONFLICT_REPLACE)
                        } catch (e: Exception) {
                            if (LOG) {
                                Log.e(TAG, "Error inserting verb: $verbId $verbName")
                            }
                            throw e
                        }

                        i++
                    }
                }
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (LOG) {
                Log.e(TAG, "Error loading verbs xml file. ")
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            if (LOG) {
                Log.e(TAG, "Error loading verbs xml file. ")
            }
        }

    }

    /**
     * Insert conjugation verb models.
     * @param db SQLiteDatabase
     */
    private fun insertConjugation(db: SQLiteDatabase) {
        insertConjugationToSchema01(db)
    }

    /**
     * Insert conjugation verb models.
     * @param db SQLiteDatabase
     */
    private fun insertConjugationToSchema01(db: SQLiteDatabase) {
        val values = ContentValues()

        // Initialize a XmlResourceParser instance
        val parser = context.resources.getXml(R.xml.conjugations)
        var eventType = -1
        var i = 1
        var verbName: String
        var conjugationId: String
        try {
            // Loop through the XML data
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG) {
                    val item = parser.name
                    if (item == "conjugation") {
                        values.put("_id", i)
                        conjugationId = parser.getAttributeValue(null, "id")
                        verbName = parser.getAttributeValue(null, "inf_pr")
                        values.put(COLUMN_ID, conjugationId)
                        values.put(COLUMN_TERMINATION, parser.getAttributeValue(null, "term"))
                        values.put(COLUMN_RADICALS, parser.getAttributeValue(null, "radicals"))
                        values.put(COLUMN_INFINITIVE_PRESENT, verbName)
                        values.put(COLUMN_INFINITIVE_PASSE, parser.getAttributeValue(null, "inf_pa"))
                        values.put(COLUMN_PARTICIPE_PRESENT, parser.getAttributeValue(null, "pa_pr"))
                        values.put(COLUMN_PARTICIPE_PASSE_1, parser.getAttributeValue(null, "pa_pa1"))
                        values.put(COLUMN_PARTICIPE_PASSE_2, parser.getAttributeValue(null, "pa_pa2"))
                        values.put(COLUMN_GERONDIF_PRESENT, parser.getAttributeValue(null, "ge_pr"))
                        values.put(COLUMN_GERONDIF_PASSE, parser.getAttributeValue(null, "ge_pa"))

                        values.put(COLUMN_IMPERATIF_PRESENT_TU, parser.getAttributeValue(null, "im_pr_t"))
                        values.put(COLUMN_IMPERATIF_PRESENT_NOUS, parser.getAttributeValue(null, "im_pr_n"))
                        values.put(COLUMN_IMPERATIF_PRESENT_VOUS, parser.getAttributeValue(null, "im_pr_v"))
                        values.put(COLUMN_IMPERATIF_PASSE_TU, parser.getAttributeValue(null, "im_pa_t"))
                        values.put(COLUMN_IMPERATIF_PASSE_NOUS, parser.getAttributeValue(null, "im_pa_n"))
                        values.put(COLUMN_IMPERATIF_PASSE_VOUS, parser.getAttributeValue(null, "im_pa_v"))

                        values.put(COLUMN_INDICATIF_PRESENT_JE, parser.getAttributeValue(null, "in_pr_j"))
                        values.put(COLUMN_INDICATIF_PRESENT_TU, parser.getAttributeValue(null, "in_pr_t"))
                        values.put(COLUMN_INDICATIF_PRESENT_IL, parser.getAttributeValue(null, "in_pr_il"))
                        values.put(COLUMN_INDICATIF_PRESENT_NOUS, parser.getAttributeValue(null, "in_pr_n"))
                        values.put(COLUMN_INDICATIF_PRESENT_VOUS, parser.getAttributeValue(null, "in_pr_v"))
                        values.put(COLUMN_INDICATIF_PRESENT_ILS, parser.getAttributeValue(null, "in_pr_ils"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_JE, parser.getAttributeValue(null, "in_pc_j"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_TU, parser.getAttributeValue(null, "in_pc_t"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_IL, parser.getAttributeValue(null, "in_pc_il"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_NOUS, parser.getAttributeValue(null, "in_pc_n"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_VOUS, parser.getAttributeValue(null, "in_pc_v"))
                        values.put(COLUMN_INDICATIF_PASSE_COMPOSE_ILS, parser.getAttributeValue(null, "in_pc_ils"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_JE, parser.getAttributeValue(null, "in_im_j"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_TU, parser.getAttributeValue(null, "in_im_t"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_IL, parser.getAttributeValue(null, "in_im_il"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_NOUS, parser.getAttributeValue(null, "in_im_n"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_VOUS, parser.getAttributeValue(null, "in_im_v"))
                        values.put(COLUMN_INDICATIF_IMPERFAIT_ILS, parser.getAttributeValue(null, "in_im_ils"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_JE, parser.getAttributeValue(null, "in_pqp_j"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_TU, parser.getAttributeValue(null, "in_pqp_t"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_IL, parser.getAttributeValue(null, "in_pqp_il"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_NOUS, parser.getAttributeValue(null, "in_pqp_n"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_VOUS, parser.getAttributeValue(null, "in_pqp_v"))
                        values.put(COLUMN_INDICATIF_PLUS_QUE_PARFAIT_ILS, parser.getAttributeValue(null, "in_pqp_ils"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_JE, parser.getAttributeValue(null, "in_ps_j"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_TU, parser.getAttributeValue(null, "in_ps_t"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_IL, parser.getAttributeValue(null, "in_ps_il"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_NOUS, parser.getAttributeValue(null, "in_ps_n"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_VOUS, parser.getAttributeValue(null, "in_ps_v"))
                        values.put(COLUMN_INDICATIF_PASSE_SIMPLE_ILS, parser.getAttributeValue(null, "in_ps_ils"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_JE, parser.getAttributeValue(null, "in_pa_j"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_TU, parser.getAttributeValue(null, "in_pa_t"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_IL, parser.getAttributeValue(null, "in_pa_il"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_NOUS, parser.getAttributeValue(null, "in_pa_n"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_VOUS, parser.getAttributeValue(null, "in_pa_v"))
                        values.put(COLUMN_INDICATIF_PASSE_ANTERIEUR_ILS, parser.getAttributeValue(null, "in_pa_ils"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_JE, parser.getAttributeValue(null, "in_fs_j"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_TU, parser.getAttributeValue(null, "in_fs_t"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_IL, parser.getAttributeValue(null, "in_fs_il"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_NOUS, parser.getAttributeValue(null, "in_fs_n"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_VOUS, parser.getAttributeValue(null, "in_fs_v"))
                        values.put(COLUMN_INDICATIF_FUTUR_SIMPLE_ILS, parser.getAttributeValue(null, "in_fs_ils"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_JE, parser.getAttributeValue(null, "in_fa_j"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_TU, parser.getAttributeValue(null, "in_fa_t"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_IL, parser.getAttributeValue(null, "in_fa_il"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_NOUS, parser.getAttributeValue(null, "in_fa_n"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_VOUS, parser.getAttributeValue(null, "in_fa_v"))
                        values.put(COLUMN_INDICATIF_FUTUR_ANTERIEUR_ILS, parser.getAttributeValue(null, "in_fa_ils"))

                        values.put(COLUMN_SUBJONTIF_PRESENT_JE, parser.getAttributeValue(null, "su_pr_j"))
                        values.put(COLUMN_SUBJONTIF_PRESENT_TU, parser.getAttributeValue(null, "su_pr_t"))
                        values.put(COLUMN_SUBJONTIF_PRESENT_IL, parser.getAttributeValue(null, "su_pr_il"))
                        values.put(COLUMN_SUBJONTIF_PRESENT_NOUS, parser.getAttributeValue(null, "su_pr_n"))
                        values.put(COLUMN_SUBJONTIF_PRESENT_VOUS, parser.getAttributeValue(null, "su_pr_v"))
                        values.put(COLUMN_SUBJONTIF_PRESENT_ILS, parser.getAttributeValue(null, "su_pr_ils"))
                        values.put(COLUMN_SUBJONTIF_PASSE_JE, parser.getAttributeValue(null, "su_pa_j"))
                        values.put(COLUMN_SUBJONTIF_PASSE_TU, parser.getAttributeValue(null, "su_pa_t"))
                        values.put(COLUMN_SUBJONTIF_PASSE_IL, parser.getAttributeValue(null, "su_pa_il"))
                        values.put(COLUMN_SUBJONTIF_PASSE_NOUS, parser.getAttributeValue(null, "su_pa_n"))
                        values.put(COLUMN_SUBJONTIF_PASSE_VOUS, parser.getAttributeValue(null, "su_pa_v"))
                        values.put(COLUMN_SUBJONTIF_PASSE_ILS, parser.getAttributeValue(null, "su_pa_ils"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_JE, parser.getAttributeValue(null, "su_im_j"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_TU, parser.getAttributeValue(null, "su_im_t"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_IL, parser.getAttributeValue(null, "su_im_il"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_NOUS, parser.getAttributeValue(null, "su_im_n"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_VOUS, parser.getAttributeValue(null, "su_im_v"))
                        values.put(COLUMN_SUBJONTIF_IMPERFAIT_ILS, parser.getAttributeValue(null, "su_im_ils"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_JE, parser.getAttributeValue(null, "su_pqp_j"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_TU, parser.getAttributeValue(null, "su_pqp_t"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_IL, parser.getAttributeValue(null, "su_pqp_il"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_NOUS, parser.getAttributeValue(null, "su_pqp_n"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_VOUS, parser.getAttributeValue(null, "su_pqp_v"))
                        values.put(COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_ILS, parser.getAttributeValue(null, "su_pqp_ils"))

                        values.put(COLUMN_CONDITIONNEL_PRESENT_JE, parser.getAttributeValue(null, "co_pr_j"))
                        values.put(COLUMN_CONDITIONNEL_PRESENT_TU, parser.getAttributeValue(null, "co_pr_t"))
                        values.put(COLUMN_CONDITIONNEL_PRESENT_IL, parser.getAttributeValue(null, "co_pr_il"))
                        values.put(COLUMN_CONDITIONNEL_PRESENT_NOUS, parser.getAttributeValue(null, "co_pr_n"))
                        values.put(COLUMN_CONDITIONNEL_PRESENT_VOUS, parser.getAttributeValue(null, "co_pr_v"))
                        values.put(COLUMN_CONDITIONNEL_PRESENT_ILS, parser.getAttributeValue(null, "co_pr_ils"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_JE, parser.getAttributeValue(null, "co_pa_j"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_TU, parser.getAttributeValue(null, "co_pa_t"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_IL, parser.getAttributeValue(null, "co_pa_il"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_NOUS, parser.getAttributeValue(null, "co_pa_n"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_VOUS, parser.getAttributeValue(null, "co_pa_v"))
                        values.put(COLUMN_CONDITIONNEL_PASSE_ILS, parser.getAttributeValue(null, "co_pa_ils"))

                        try {
                            db.insertWithOnConflict(CONJUGATION_TBL, null, values, CONFLICT_REPLACE)
                        } catch (e: Exception) {
                            if (LOG) {
                                Log.e(TAG, "Error inserting conjugation: $conjugationId $verbName")
                            }
                            throw e
                        }

                        i++
                    }
                }
                eventType = parser.next()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (LOG) {
                Log.e(TAG, "Error loading conjugations xml file. ")
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            if (LOG) {
                Log.e(TAG, "Error loading conjugations xml file. ")
            }
        }

    }
}
