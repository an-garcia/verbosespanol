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

import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.OTHER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.REGULAR

/**
 * Data that is contained for a verb item in the list mode in the main list.
 */
class Verb
/*** Constructor  */
(id: Long, conjugation: Int, infinitive: String, definition: String,
 sample1: String, sample2: String, sample3: String,
 common: Int, regular: Int, color: Int, score: Int, notes: String,
 translationEN: String, translationFR: String, translationPT: String) {

    /* Getters and Setters */
    var id: Long = 0
    var conjugation = 0      // Conjugation id
    var infinitive = ""
    var definition = ""
    var sample1 = ""
    var sample2 = ""
    var sample3 = ""
    var common = OTHER
    var regular = REGULAR
    var color = 0
    var score = 0
    var notes = ""
    var translationEN = ""
    var translationFR = ""
    var translationPT = ""

    init {
        this.id = id
        this.conjugation = conjugation
        this.infinitive = infinitive
        this.definition = definition
        this.sample1 = sample1
        this.sample2 = sample2
        this.sample3 = sample3
        this.common = common
        this.regular = regular
        this.color = color
        this.score = score
        this.notes = notes
        this.translationEN = translationEN
        this.translationFR = translationFR
        this.translationPT = translationPT
    }

}
