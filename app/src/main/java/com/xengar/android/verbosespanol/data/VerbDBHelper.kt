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
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONJUGATION_NUMBER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_DEFINITION
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERUNDIO_COMPUESTO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_GERUNDIO_SIMPLE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NEGACION_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_IMPERATIVO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_FUTURO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRESENTE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVO_COMPUESTO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVO_SIMPLE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_NOTES
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_PARTICIPIO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_RADICALS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_REGULAR
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_1
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_2
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SAMPLE_3
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SCORE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRESENTE_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO
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
        private val favorites = arrayOf(arrayOf("1"), // ser
                arrayOf("2"), // estar
                arrayOf("3"), // decir
                arrayOf("4"), // venir
                arrayOf("5")  // ir
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
                + COLUMN_REGULAR + " INTEGER NOT NULL DEFAULT 0, "
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
                + COLUMN_INFINITIVO_SIMPLE + " TEXT NOT NULL, "
                + COLUMN_INFINITIVO_COMPUESTO + " TEXT NOT NULL, "
                + COLUMN_PARTICIPIO + " TEXT NOT NULL, "
                + COLUMN_GERUNDIO_SIMPLE + " TEXT NOT NULL, "
                + COLUMN_GERUNDIO_COMPUESTO + " TEXT NOT NULL, "

                + COLUMN_IMPERATIVO_TU + " TEXT, "
                + COLUMN_IMPERATIVO_EL + " TEXT, "
                + COLUMN_IMPERATIVO_NOSOTROS + " TEXT, "
                + COLUMN_IMPERATIVO_VOSOTROS + " TEXT, "
                + COLUMN_IMPERATIVO_ELLOS + " TEXT, "
                + COLUMN_IMPERATIVO_NEGACION_TU + " TEXT, "
                + COLUMN_IMPERATIVO_NEGACION_EL + " TEXT, "
                + COLUMN_IMPERATIVO_NEGACION_NOSOTROS + " TEXT, "
                + COLUMN_IMPERATIVO_NEGACION_VOSOTROS + " TEXT, "
                + COLUMN_IMPERATIVO_NEGACION_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRESENTE_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRESENTE_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRESENTE_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRESENTE_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRESENTE_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRESENTE_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_FUTURO_SIMPLE_YO + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_SIMPLE_TU + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_SIMPLE_EL + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS + " TEXT, "

                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS + " TEXT, "
                + COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_PRESENTE_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRESENTE_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRESENTE_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRESENTE_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS + " TEXT, "

                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS + " TEXT, "
                + COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS + " TEXT);")
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
                        values.put(COLUMN_REGULAR, parser.getAttributeValue(null, "re"))
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
                        verbName = parser.getAttributeValue(null, "inf_si")
                        values.put(COLUMN_ID, conjugationId)
                        values.put(COLUMN_TERMINATION, parser.getAttributeValue(null, "term"))
                        values.put(COLUMN_RADICALS, parser.getAttributeValue(null, "radicals"))
                        values.put(COLUMN_INFINITIVO_SIMPLE, verbName)
                        values.put(COLUMN_INFINITIVO_COMPUESTO, parser.getAttributeValue(null, "inf_co"))
                        values.put(COLUMN_PARTICIPIO, parser.getAttributeValue(null, "pa"))
                        values.put(COLUMN_GERUNDIO_SIMPLE, parser.getAttributeValue(null, "ge_si"))
                        values.put(COLUMN_GERUNDIO_COMPUESTO, parser.getAttributeValue(null, "ge_co"))

                        values.put(COLUMN_IMPERATIVO_TU, parser.getAttributeValue(null, "imp_t"))
                        values.put(COLUMN_IMPERATIVO_EL, parser.getAttributeValue(null, "imp_el"))
                        values.put(COLUMN_IMPERATIVO_NOSOTROS, parser.getAttributeValue(null, "imp_n"))
                        values.put(COLUMN_IMPERATIVO_VOSOTROS, parser.getAttributeValue(null, "imp_v"))
                        values.put(COLUMN_IMPERATIVO_ELLOS, parser.getAttributeValue(null, "imp_ell"))
                        values.put(COLUMN_IMPERATIVO_NEGACION_TU, parser.getAttributeValue(null, "imp_neg_t"))
                        values.put(COLUMN_IMPERATIVO_NEGACION_EL, parser.getAttributeValue(null, "imp_neg_el"))
                        values.put(COLUMN_IMPERATIVO_NEGACION_NOSOTROS, parser.getAttributeValue(null, "imp_neg_n"))
                        values.put(COLUMN_IMPERATIVO_NEGACION_VOSOTROS, parser.getAttributeValue(null, "imp_neg_v"))
                        values.put(COLUMN_IMPERATIVO_NEGACION_ELLOS, parser.getAttributeValue(null, "imp_neg_ell"))

                        values.put(COLUMN_INDICATIVO_PRESENTE_YO, parser.getAttributeValue(null, "in_pres_y"))
                        values.put(COLUMN_INDICATIVO_PRESENTE_TU, parser.getAttributeValue(null, "in_pres_t"))
                        values.put(COLUMN_INDICATIVO_PRESENTE_EL, parser.getAttributeValue(null, "in_pres_el"))
                        values.put(COLUMN_INDICATIVO_PRESENTE_NOSOTROS, parser.getAttributeValue(null, "in_pres_n"))
                        values.put(COLUMN_INDICATIVO_PRESENTE_VOSOTROS, parser.getAttributeValue(null, "in_pres_v"))
                        values.put(COLUMN_INDICATIVO_PRESENTE_ELLOS, parser.getAttributeValue(null, "in_pres_ell"))

                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_YO, parser.getAttributeValue(null, "in_pret_imp_y"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_TU, parser.getAttributeValue(null, "in_pret_imp_t"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_EL, parser.getAttributeValue(null, "in_pret_imp_el"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_NOSOTROS, parser.getAttributeValue(null, "in_pret_imp_n"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_VOSOTROS, parser.getAttributeValue(null, "in_pret_imp_v"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_IMPERFECTO_ELLOS, parser.getAttributeValue(null, "in_pret_imp_ell"))

                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_YO, parser.getAttributeValue(null, "in_pret_perf_si_y"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_TU, parser.getAttributeValue(null, "in_pret_perf_si_t"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_EL, parser.getAttributeValue(null, "in_pret_perf_si_el"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_NOSOTROS, parser.getAttributeValue(null, "in_pret_perf_si_n"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_VOSOTROS, parser.getAttributeValue(null, "in_pret_perf_si_v"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_SIMPLE_ELLOS, parser.getAttributeValue(null, "in_pret_perf_si_ell"))

                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_YO, parser.getAttributeValue(null, "in_fu_si_y"))
                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_TU, parser.getAttributeValue(null, "in_fu_si_t"))
                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_EL, parser.getAttributeValue(null, "in_fu_si_el"))
                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_NOSOTROS, parser.getAttributeValue(null, "in_fu_si_n"))
                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_VOSOTROS, parser.getAttributeValue(null, "in_fu_si_v"))
                        values.put(COLUMN_INDICATIVO_FUTURO_SIMPLE_ELLOS, parser.getAttributeValue(null, "in_fu_si_ell"))

                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_YO, parser.getAttributeValue(null, "in_cond_si_y"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_TU, parser.getAttributeValue(null, "in_cond_si_t"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_EL, parser.getAttributeValue(null, "in_cond_si_el"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_NOSOTROS, parser.getAttributeValue(null, "in_cond_si_n"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_VOSOTROS, parser.getAttributeValue(null, "in_cond_si_v"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_SIMPLE_ELLOS, parser.getAttributeValue(null, "in_cond_si_ell"))

                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_YO, parser.getAttributeValue(null, "in_pret_perf_comp_y"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_TU, parser.getAttributeValue(null, "in_pret_perf_comp_t"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_EL, parser.getAttributeValue(null, "in_pret_perf_comp_el"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS, parser.getAttributeValue(null, "in_pret_perf_comp_n"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS, parser.getAttributeValue(null, "in_pret_perf_comp_v"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS, parser.getAttributeValue(null, "in_pret_perf_comp_ell"))

                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_YO, parser.getAttributeValue(null, "in_pret_plus_y"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_TU, parser.getAttributeValue(null, "in_pret_plus_t"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_EL, parser.getAttributeValue(null, "in_pret_plus_el"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS, parser.getAttributeValue(null, "in_pret_plus_n"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS, parser.getAttributeValue(null, "in_pret_plus_v"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS, parser.getAttributeValue(null, "in_pret_plus_ell"))

                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_YO, parser.getAttributeValue(null, "in_pret_ant_y"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_TU, parser.getAttributeValue(null, "in_pret_ant_t"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_EL, parser.getAttributeValue(null, "in_pret_ant_el"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_NOSOTROS, parser.getAttributeValue(null, "in_pret_ant_n"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_VOSOTROS, parser.getAttributeValue(null, "in_pret_ant_v"))
                        values.put(COLUMN_INDICATIVO_PRETERITO_ANTERIOR_ELLOS, parser.getAttributeValue(null, "in_pret_ant_ell"))

                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_YO, parser.getAttributeValue(null, "in_fu_comp_y"))
                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_TU, parser.getAttributeValue(null, "in_fu_comp_t"))
                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_EL, parser.getAttributeValue(null, "in_fu_comp_el"))
                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_NOSOTROS, parser.getAttributeValue(null, "in_fu_comp_n"))
                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_VOSOTROS, parser.getAttributeValue(null, "in_fu_comp_v"))
                        values.put(COLUMN_INDICATIVO_FUTURO_COMPUESTO_ELLOS, parser.getAttributeValue(null, "in_fu_comp_ell"))

                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_YO, parser.getAttributeValue(null, "in_cond_co_y"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_TU, parser.getAttributeValue(null, "in_cond_co_t"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_EL, parser.getAttributeValue(null, "in_cond_co_el"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_NOSOTROS, parser.getAttributeValue(null, "in_cond_co_n"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_VOSOTROS, parser.getAttributeValue(null, "in_cond_co_v"))
                        values.put(COLUMN_INDICATIVO_CONDICIONAL_COMPUESTO_ELLOS, parser.getAttributeValue(null, "in_cond_co_ell"))

                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_YO, parser.getAttributeValue(null, "su_pres_y"))
                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_TU, parser.getAttributeValue(null, "su_pres_t"))
                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_EL, parser.getAttributeValue(null, "su_pres_el"))
                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_NOSOTROS, parser.getAttributeValue(null, "su_pres_n"))
                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_VOSOTROS, parser.getAttributeValue(null, "su_pres_v"))
                        values.put(COLUMN_SUBJUNTIVO_PRESENTE_ELLOS, parser.getAttributeValue(null, "su_pres_ell"))

                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_YO, parser.getAttributeValue(null, "su_pret_imp_y"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_TU, parser.getAttributeValue(null, "su_pret_imp_t"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_EL, parser.getAttributeValue(null, "su_pret_imp_el"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_NOSOTROS, parser.getAttributeValue(null, "su_pret_imp_n"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_VOSOTROS, parser.getAttributeValue(null, "su_pret_imp_v"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_IMPERFECTO_ELLOS, parser.getAttributeValue(null, "su_pret_imp_ell"))

                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_YO, parser.getAttributeValue(null, "su_fu_si_y"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_TU, parser.getAttributeValue(null, "su_fu_si_t"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_EL, parser.getAttributeValue(null, "su_fu_si_el"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_NOSOTROS, parser.getAttributeValue(null, "su_fu_si_n"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_VOSOTROS, parser.getAttributeValue(null, "su_fu_si_v"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_SIMPLE_ELLOS, parser.getAttributeValue(null, "su_fu_si_ell"))

                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_YO, parser.getAttributeValue(null, "su_pret_perf_comp_y"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_TU, parser.getAttributeValue(null, "su_pret_perf_comp_t"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_EL, parser.getAttributeValue(null, "su_pret_perf_comp_el"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_NOSOTROS, parser.getAttributeValue(null, "su_pret_perf_comp_n"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_VOSOTROS, parser.getAttributeValue(null, "su_pret_perf_comp_v"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PERFECTO_COMPUESTO_ELLOS, parser.getAttributeValue(null, "su_pret_perf_comp_ell"))

                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_YO, parser.getAttributeValue(null, "su_pret_plus_y"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_TU, parser.getAttributeValue(null, "su_pret_plus_t"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_EL, parser.getAttributeValue(null, "su_pret_plus_el"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_NOSOTROS, parser.getAttributeValue(null, "su_pret_plus_n"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_VOSOTROS, parser.getAttributeValue(null, "su_pret_plus_v"))
                        values.put(COLUMN_SUBJUNTIVO_PRETERITO_PLUSCUAMPERFECTO_ELLOS, parser.getAttributeValue(null, "su_pret_plus_ell"))

                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_YO, parser.getAttributeValue(null, "su_fu_comp_y"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_TU, parser.getAttributeValue(null, "su_fu_comp_t"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_EL, parser.getAttributeValue(null, "su_fu_comp_el"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_NOSOTROS, parser.getAttributeValue(null, "su_fu_comp_n"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_VOSOTROS, parser.getAttributeValue(null, "su_fu_comp_v"))
                        values.put(COLUMN_SUBJUNTIVO_FUTURO_COMPUESTO_ELLOS, parser.getAttributeValue(null, "su_fu_comp_ell"))

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
