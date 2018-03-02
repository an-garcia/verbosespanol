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

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/**
 * API Contract for the Verbs
 */

object VerbContract {

    @JvmField val CONTENT_AUTHORITY = "com.xengar.android.verbosespanol"

    private val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)

    @JvmField val PATH_VERBS = "verbs"
    @JvmField val PATH_CONJUGATIONS = "conjugations"
    @JvmField val PATH_FAVORITES = "favorites"
    @JvmField val PATH_FAVORITE_VERBS = "favorite_verbs"

    /**
     * Inner class that defines constant values for the verbs database table.
     * Each entry in the table represents a single verb.
     */
    class VerbEntry : BaseColumns {
        companion object {
            /** The content URI to access the verb data in the provider  */
            @JvmField val CONTENT_VERBS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VERBS)!!
            @JvmField val CONTENT_CONJUGATIONS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONJUGATIONS)!!

            @JvmField val CONTENT_FAVORITES_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                    PATH_FAVORITES)!!
            @JvmField val CONTENT_FAVORITE_VERBS_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                    PATH_FAVORITE_VERBS)!!

            /** The MIME type of the [.CONTENT_VERBS_URI] for a list of verbs.  */
            @JvmField val CONTENT_LIST_TYPE_VERB =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VERBS
            @JvmField val CONTENT_LIST_TYPE_CONJUGATION =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONJUGATIONS
            @JvmField val CONTENT_LIST_TYPE_FAVORITE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES

            /** The MIME type of the [.CONTENT_VERBS_URI] for a single verb.  */
            @JvmField val CONTENT_ITEM_TYPE_VERB =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VERBS
            @JvmField val CONTENT_ITEM_TYPE_CONJUGATION =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONJUGATIONS
            @JvmField val CONTENT_ITEM_TYPE_FAVORITE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES

            /** Name of database table for verbs  */
            @JvmField val VERBS_TBL = "VERBS_TBL"
            @JvmField val FAVORITES_TBL = "FAVORITES_TBL"
            @JvmField val CONJUGATION_TBL = "CONJUGATION_TBL"

            /** Unique ID number for the verb (only for use in the database table). - Type: INTEGER  */
            @JvmField val _ID = BaseColumns._ID
            @JvmField val COLUMN_ID = "ID"

            /** Table of Conjugation number - Type: TEXT  */
            @JvmField val COLUMN_CONJUGATION_NUMBER = "CONJUGATION_ID"

            /** Verb in infinitive form. - Type: TEXT   */
            @JvmField val COLUMN_INFINITIVE = "INFINITIVE"

            /** Common usage of the verb. - Type: INTEGER  */
            @JvmField val COLUMN_COMMON = "COMMON"

            /** Possible values for the common usage of the verb.  */
            @JvmField val TOP_25 = 1
            @JvmField val TOP_50 = 2
            @JvmField val TOP_100 = 3
            @JvmField val TOP_300 = 4
            @JvmField val TOP_500 = 5
            @JvmField val TOP_1000 = 6
            @JvmField val OTHER = 99
            @JvmField val S_TOP_25 = "" + TOP_25
            @JvmField val S_TOP_50 = "" + TOP_50
            @JvmField val S_TOP_100 = "" + TOP_100
            @JvmField val S_TOP_300 = "" + TOP_300
            @JvmField val S_TOP_500 = "" + TOP_500
            @JvmField val S_TOP_1000 = "" + TOP_1000

            fun isValidCommonUsage(usage: Int): Boolean {
                return (usage == TOP_25 || usage == TOP_50 || usage == TOP_100 || usage == TOP_300
                        || usage == TOP_500 || usage == TOP_1000 || usage == OTHER)
            }

            /** Group verbs (1st, 2nd, 3rd, all). - Type: INTEGER  */
            @JvmField val COLUMN_GROUP = "GROUPE"

            /** Possible values for verb groups.  */
            @JvmField val GROUP_1 = 1
            @JvmField val GROUP_2 = 2
            @JvmField val GROUP_3 = 3
            @JvmField val GROUP_ALL = 0

            fun isValidGroup(value: Int): Boolean {
                return value == GROUP_1 || value == GROUP_2
                        || value == GROUP_3 || value == GROUP_ALL
            }

            /** Definition of the verb. - Type: TEXT  */
            @JvmField val COLUMN_DEFINITION = "DEFINITION"

            /** Examples of the verb. - Type: TEXT   */
            @JvmField val COLUMN_SAMPLE_1 = "SAMPLE1"
            @JvmField val COLUMN_SAMPLE_2 = "SAMPLE2"
            @JvmField val COLUMN_SAMPLE_3 = "SAMPLE3"

            /** Other information for the verb. - Type: TEXT  */
            @JvmField val COLUMN_NOTES = "NOTES"

            /** Color assigned by the user. - Type: INTEGER  */
            @JvmField val COLUMN_COLOR = "COLOR"

            /** Score assigned by using the exercises or tests. - Type: INTEGER  */
            @JvmField val COLUMN_SCORE = "SCORE"

            /**
             * Translations of the verb into the language.
             * Type: TEXT
             */
            @JvmField val COLUMN_TRANSLATION_EN = "TRANSLATION_EN"
            @JvmField val COLUMN_TRANSLATION_FR = "TRANSLATION_FR"
            @JvmField val COLUMN_TRANSLATION_PT = "TRANSLATION_PT"


            /**
             * Verb CONJUGATION columns.
             * Type: TEXT
             */
            @JvmField val COLUMN_TERMINATION = "TERMINATION"
            @JvmField val COLUMN_RADICALS = "RADICALS"
            @JvmField val COLUMN_INFINITIVE_PRESENT = "INFINITIVE_PRESENT"
            @JvmField val COLUMN_INFINITIVE_PASSE = "INFINITIVE_PASSE"
            @JvmField val COLUMN_PARTICIPE_PRESENT = "PARTICIPE_PRESENT"
            @JvmField val COLUMN_PARTICIPE_PASSE_1 = "PARTICIPE_PASSE_1"
            @JvmField val COLUMN_PARTICIPE_PASSE_2 = "PARTICIPE_PASSE_2"
            @JvmField val COLUMN_GERONDIF_PRESENT = "GERONDIF_PRESENT"
            @JvmField val COLUMN_GERONDIF_PASSE = "GERONDIF_PASSE"
            @JvmField val COLUMN_IMPERATIF_PRESENT_TU = "IMPERATIF_PRESENT_TU"
            @JvmField val COLUMN_IMPERATIF_PRESENT_NOUS = "IMPERATIF_PRESENT_NOUS"
            @JvmField val COLUMN_IMPERATIF_PRESENT_VOUS = "IMPERATIF_PRESENT_VOUS"
            @JvmField val COLUMN_IMPERATIF_PASSE_TU = "IMPERATIF_PASSE_TU"
            @JvmField val COLUMN_IMPERATIF_PASSE_NOUS = "IMPERATIF_PASSE_NOUS"
            @JvmField val COLUMN_IMPERATIF_PASSE_VOUS = "IMPERATIF_PASSE_VOUS"

            @JvmField val COLUMN_INDICATIF_PRESENT_JE = "INDICATIF_PRESENT_JE"
            @JvmField val COLUMN_INDICATIF_PRESENT_TU = "INDICATIF_PRESENT_TU"
            @JvmField val COLUMN_INDICATIF_PRESENT_IL = "INDICATIF_PRESENT_IL"
            @JvmField val COLUMN_INDICATIF_PRESENT_NOUS = "INDICATIF_PRESENT_NOUS"
            @JvmField val COLUMN_INDICATIF_PRESENT_VOUS = "INDICATIF_PRESENT_VOUS"
            @JvmField val COLUMN_INDICATIF_PRESENT_ILS = "INDICATIF_PRESENT_ILS"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_JE = "INDICATIF_PASSE_COMPOSE_JE"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_TU = "INDICATIF_PASSE_COMPOSE_TU"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_IL = "INDICATIF_PASSE_COMPOSE_IL"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_NOUS = "INDICATIF_PASSE_COMPOSE_NOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_VOUS = "INDICATIF_PASSE_COMPOSE_VOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_COMPOSE_ILS = "INDICATIF_PASSE_COMPOSE_ILS"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_JE = "INDICATIF_IMPERFAIT_JE"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_TU = "INDICATIF_IMPERFAIT_TU"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_IL = "INDICATIF_IMPERFAIT_IL"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_NOUS = "INDICATIF_IMPERFAIT_NOUS"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_VOUS = "INDICATIF_IMPERFAIT_VOUS"
            @JvmField val COLUMN_INDICATIF_IMPERFAIT_ILS = "INDICATIF_IMPERFAIT_ILS"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_JE = "INDICATIF_PLUS_QUE_PARFAIT_JE"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_TU = "INDICATIF_PLUS_QUE_PARFAIT_TU"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_IL = "INDICATIF_PLUS_QUE_PARFAIT_IL"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_NOUS = "INDICATIF_PLUS_QUE_PARFAIT_NOUS"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_VOUS = "INDICATIF_PLUS_QUE_PARFAIT_VOUS"
            @JvmField val COLUMN_INDICATIF_PLUS_QUE_PARFAIT_ILS = "INDICATIF_PLUS_QUE_PARFAIT_ILS"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_JE = "INDICATIF_PASSE_SIMPLE_JE"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_TU = "INDICATIF_PASSE_SIMPLE_TU"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_IL = "INDICATIF_PASSE_SIMPLE_IL"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_NOUS = "INDICATIF_PASSE_SIMPLE_NOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_VOUS = "INDICATIF_PASSE_SIMPLE_VOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_SIMPLE_ILS = "INDICATIF_PASSE_SIMPLE_ILS"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_JE = "INDICATIF_PASSE_ANTERIEUR_JE"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_TU = "INDICATIF_PASSE_ANTERIEUR_TU"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_IL = "INDICATIF_PASSE_ANTERIEUR_IL"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_NOUS = "INDICATIF_PASSE_ANTERIEUR_NOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_VOUS = "INDICATIF_PASSE_ANTERIEUR_VOUS"
            @JvmField val COLUMN_INDICATIF_PASSE_ANTERIEUR_ILS = "INDICATIF_PASSE_ANTERIEUR_ILS"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_JE = "INDICATIF_FUTUR_SIMPLE_JE"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_TU = "INDICATIF_FUTUR_SIMPLE_TU"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_IL = "INDICATIF_FUTUR_SIMPLE_IL"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_NOUS = "INDICATIF_FUTUR_SIMPLE_NOUS"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_VOUS = "INDICATIF_FUTUR_SIMPLE_VOUS"
            @JvmField val COLUMN_INDICATIF_FUTUR_SIMPLE_ILS = "INDICATIF_FUTUR_SIMPLE_ILS"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_JE = "INDICATIF_FUTUR_ANTERIEUR_JE"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_TU = "INDICATIF_FUTUR_ANTERIEUR_TU"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_IL = "INDICATIF_FUTUR_ANTERIEUR_IL"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_NOUS = "INDICATIF_FUTUR_ANTERIEUR_NOUS"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_VOUS = "INDICATIF_FUTUR_ANTERIEUR_VOUS"
            @JvmField val COLUMN_INDICATIF_FUTUR_ANTERIEUR_ILS = "INDICATIF_FUTUR_ANTERIEUR_ILS"

            @JvmField val COLUMN_SUBJONTIF_PRESENT_JE = "SUBJONTIF_PRESENT_JE"
            @JvmField val COLUMN_SUBJONTIF_PRESENT_TU = "SUBJONTIF_PRESENT_TU"
            @JvmField val COLUMN_SUBJONTIF_PRESENT_IL = "SUBJONTIF_PRESENT_IL"
            @JvmField val COLUMN_SUBJONTIF_PRESENT_NOUS = "SUBJONTIF_PRESENT_NOUS"
            @JvmField val COLUMN_SUBJONTIF_PRESENT_VOUS = "SUBJONTIF_PRESENT_VOUS"
            @JvmField val COLUMN_SUBJONTIF_PRESENT_ILS = "SUBJONTIF_PRESENT_ILS"
            @JvmField val COLUMN_SUBJONTIF_PASSE_JE = "SUBJONTIF_PASSE_JE"
            @JvmField val COLUMN_SUBJONTIF_PASSE_TU = "SUBJONTIF_PASSE_TU"
            @JvmField val COLUMN_SUBJONTIF_PASSE_IL = "SUBJONTIF_PASSE_IL"
            @JvmField val COLUMN_SUBJONTIF_PASSE_NOUS = "SUBJONTIF_PASSE_NOUS"
            @JvmField val COLUMN_SUBJONTIF_PASSE_VOUS = "SUBJONTIF_PASSE_VOUS"
            @JvmField val COLUMN_SUBJONTIF_PASSE_ILS = "SUBJONTIF_PASSE_ILS"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_JE = "SUBJONTIF_IMPERFAIT_JE"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_TU = "SUBJONTIF_IMPERFAIT_TU"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_IL = "SUBJONTIF_IMPERFAIT_IL"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_NOUS = "SUBJONTIF_IMPERFAIT_NOUS"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_VOUS = "SUBJONTIF_IMPERFAIT_VOUS"
            @JvmField val COLUMN_SUBJONTIF_IMPERFAIT_ILS = "SUBJONTIF_IMPERFAIT_ILS"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_JE = "SUBJONTIF_PLUS_QUE_PARFAIT_JE"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_TU = "SUBJONTIF_PLUS_QUE_PARFAIT_TU"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_IL = "SUBJONTIF_PLUS_QUE_PARFAIT_IL"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_NOUS = "SUBJONTIF_PLUS_QUE_PARFAIT_NOUS"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_VOUS = "SUBJONTIF_PLUS_QUE_PARFAIT_VOUS"
            @JvmField val COLUMN_SUBJONTIF_PLUS_QUE_PARFAIT_ILS = "SUBJONTIF_PLUS_QUE_PARFAIT_ILS"

            @JvmField val COLUMN_CONDITIONNEL_PRESENT_JE = "CONDITIONNEL_PRESENT_JE"
            @JvmField val COLUMN_CONDITIONNEL_PRESENT_TU = "CONDITIONNEL_PRESENT_TU"
            @JvmField val COLUMN_CONDITIONNEL_PRESENT_IL = "CONDITIONNEL_PRESENT_IL"
            @JvmField val COLUMN_CONDITIONNEL_PRESENT_NOUS = "CONDITIONNEL_PRESENT_NOUS"
            @JvmField val COLUMN_CONDITIONNEL_PRESENT_VOUS = "CONDITIONNEL_PRESENT_VOUS"
            @JvmField val COLUMN_CONDITIONNEL_PRESENT_ILS = "CONDITIONNEL_PRESENT_ILS"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_JE = "CONDITIONNEL_PASSE_JE"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_TU = "CONDITIONNEL_PASSE_TU"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_IL = "CONDITIONNEL_PASSE_IL"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_NOUS = "CONDITIONNEL_PASSE_NOUS"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_VOUS = "CONDITIONNEL_PASSE_VOUS"
            @JvmField val COLUMN_CONDITIONNEL_PASSE_ILS = "CONDITIONNEL_PASSE_ILS"
        }
    }

}// To prevent someone from accidentally instantiating the contract class,
// give it an empty constructor.
