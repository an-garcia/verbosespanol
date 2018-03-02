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

/**
 * Data that is contained for a conjugation.
 */
class Conjugation
/*** Constructor  */
(   id: Long, termination: String, radicals: String, 
    infinitivoSimple: String,
    infinitivoCompuesto: String,
    participio: String,
    gerundioSimple: String,
    gerundioCompuesto: String,

    imperativoTu: String,  
    imperativoEl: String,
    imperativoN: String,
    imperativoV: String,
    imperativoEll: String,
    imperativoNegTu: String,
    imperativoNegEl: String,
    imperativoNegN: String,
    imperativoNegV: String,
    imperativoNegEll: String,

    indicativoPresenteYo: String,
    indicativoPresenteTu: String,
    indicativoPresenteEl: String,
    indicativoPresenteN: String,
    indicativoPresenteV: String,
    indicativoPresenteEll: String,

    indicativoPreteritoImperfectoYo: String,
    indicativoPreteritoImperfectoTu: String,
    indicativoPreteritoImperfectoEl: String,
    indicativoPreteritoImperfectoN: String,
    indicativoPreteritoImperfectoV: String,
    indicativoPreteritoImperfectoEll: String,

    indicativoPreteritoPerfectoSimpleYo: String,
    indicativoPreteritoPerfectoSimpleTu: String,
    indicativoPreteritoPerfectoSimpleEl: String,
    indicativoPreteritoPerfectoSimpleN: String,
    indicativoPreteritoPerfectoSimpleV: String,
    indicativoPreteritoPerfectoSimpleEll: String,

    indicativoFuturoSimpleYo: String,
    indicativoFuturoSimpleTu: String,
    indicativoFuturoSimpleEl: String,
    indicativoFuturoSimpleN: String,
    indicativoFuturoSimpleV: String,
    indicativoFuturoSimpleEll: String,

    indicativoCondicionalSimpleYo: String,
    indicativoCondicionalSimpleTu: String,
    indicativoCondicionalSimpleEl: String,
    indicativoCondicionalSimpleN: String,
    indicativoCondicionalSimpleV: String,
    indicativoCondicionalSimpleEll: String,

    indicativoPreteritoPerfectoCompuestoYo: String,
    indicativoPreteritoPerfectoCompuestoTu: String,
    indicativoPreteritoPerfectoCompuestoEl: String,
    indicativoPreteritoPerfectoCompuestoN: String,
    indicativoPreteritoPerfectoCompuestoV: String,
    indicativoPreteritoPerfectoCompuestoEll: String,

    indicativoPreteritoPluscuamperfectoYo: String,
    indicativoPreteritoPluscuamperfectoTu: String,
    indicativoPreteritoPluscuamperfectoEl: String,
    indicativoPreteritoPluscuamperfectoN: String,
    indicativoPreteritoPluscuamperfectoV: String,
    indicativoPreteritoPluscuamperfectoEll: String,

    indicativoPreteritoAnteriorYo: String,
    indicativoPreteritoAnteriorTu: String,
    indicativoPreteritoAnteriorEl: String,
    indicativoPreteritoAnteriorN: String,
    indicativoPreteritoAnteriorV: String,
    indicativoPreteritoAnteriorEll: String,

    indicativoFuturoCompuestoYo: String,
    indicativoFuturoCompuestoTu: String,
    indicativoFuturoCompuestoEl: String,
    indicativoFuturoCompuestoN: String,
    indicativoFuturoCompuestoV: String,
    indicativoFuturoCompuestoEll: String,

    indicativoCondicionalCompuestoYo: String,
    indicativoCondicionalCompuestoTu: String,
    indicativoCondicionalCompuestoEl: String,
    indicativoCondicionalCompuestoN: String,
    indicativoCondicionalCompuestoV: String,
    indicativoCondicionalCompuestoEll: String,

    subjuntivoPresenteYo: String,
    subjuntivoPresenteTu: String,
    subjuntivoPresenteEl: String,
    subjuntivoPresenteN: String,
    subjuntivoPresenteV: String,
    subjuntivoPresenteEll: String,

    subjuntivoPreteritoImperfectoYo: String,
    subjuntivoPreteritoImperfectoTu: String,
    subjuntivoPreteritoImperfectoEl: String,
    subjuntivoPreteritoImperfectoN: String,
    subjuntivoPreteritoImperfectoV: String,
    subjuntivoPreteritoImperfectoEll: String,

    subjuntivoFuturoSimpleYo: String,
    subjuntivoFuturoSimpleTu: String,
    subjuntivoFuturoSimpleEl: String,
    subjuntivoFuturoSimpleN: String,
    subjuntivoFuturoSimpleV: String,
    subjuntivoFuturoSimpleEll: String,

    subjuntivoPreteritoPerfectoCompuestoYo: String,
    subjuntivoPreteritoPerfectoCompuestoTu: String,
    subjuntivoPreteritoPerfectoCompuestoEl: String,
    subjuntivoPreteritoPerfectoCompuestoN: String,
    subjuntivoPreteritoPerfectoCompuestoV: String,
    subjuntivoPreteritoPerfectoCompuestoEll: String,

    subjuntivoPreteritoPluscuamperfectoYo: String,
    subjuntivoPreteritoPluscuamperfectoTu: String,
    subjuntivoPreteritoPluscuamperfectoEl: String,
    subjuntivoPreteritoPluscuamperfectoN: String,
    subjuntivoPreteritoPluscuamperfectoV: String,
    subjuntivoPreteritoPluscuamperfectoEll: String,

    subjuntivoFuturoCompuestoYo: String,
    subjuntivoFuturoCompuestoTu: String,
    subjuntivoFuturoCompuestoEl: String,
    subjuntivoFuturoCompuestoN: String,
    subjuntivoFuturoCompuestoV: String,
    subjuntivoFuturoCompuestoEll: String ) {

    /* Getters and Setters */
    var id: Long = 0
    var termination = ""
    var radicals = ""
    var infinitivoSimple = ""
    var infinitivoCompuesto = ""
    var participio = ""
    var gerundioSimple = ""
    var gerundioCompuesto = ""

    var imperativoTu = ""
    var imperativoEl = ""
    var imperativoN = ""
    var imperativoV = ""
    var imperativoEll = ""
    var imperativoNegTu = ""
    var imperativoNegEl = ""
    var imperativoNegN = ""
    var imperativoNegV = ""
    var imperativoNegEll = ""

    var indicativoPresenteYo = ""
    var indicativoPresenteTu = ""
    var indicativoPresenteEl = ""
    var indicativoPresenteN = ""
    var indicativoPresenteV = ""
    var indicativoPresenteEll = ""

    var indicativoPreteritoImperfectoYo = ""
    var indicativoPreteritoImperfectoTu = ""
    var indicativoPreteritoImperfectoEl = ""
    var indicativoPreteritoImperfectoN = ""
    var indicativoPreteritoImperfectoV = ""
    var indicativoPreteritoImperfectoEll = ""

    var indicativoPreteritoPerfectoSimpleYo = ""
    var indicativoPreteritoPerfectoSimpleTu = ""
    var indicativoPreteritoPerfectoSimpleEl = ""
    var indicativoPreteritoPerfectoSimpleN = ""
    var indicativoPreteritoPerfectoSimpleV = ""
    var indicativoPreteritoPerfectoSimpleEll = ""

    var indicativoFuturoSimpleYo = ""
    var indicativoFuturoSimpleTu = ""
    var indicativoFuturoSimpleEl = ""
    var indicativoFuturoSimpleN = ""
    var indicativoFuturoSimpleV = ""
    var indicativoFuturoSimpleEll = ""

    var indicativoCondicionalSimpleYo = ""
    var indicativoCondicionalSimpleTu = ""
    var indicativoCondicionalSimpleEl = ""
    var indicativoCondicionalSimpleN = ""
    var indicativoCondicionalSimpleV = ""
    var indicativoCondicionalSimpleEll = ""

    var indicativoPreteritoPerfectoCompuestoYo = ""
    var indicativoPreteritoPerfectoCompuestoTu = ""
    var indicativoPreteritoPerfectoCompuestoEl = ""
    var indicativoPreteritoPerfectoCompuestoN = ""
    var indicativoPreteritoPerfectoCompuestoV = ""
    var indicativoPreteritoPerfectoCompuestoEll = ""

    var indicativoPreteritoPluscuamperfectoYo = ""
    var indicativoPreteritoPluscuamperfectoTu = ""
    var indicativoPreteritoPluscuamperfectoEl = ""
    var indicativoPreteritoPluscuamperfectoN = ""
    var indicativoPreteritoPluscuamperfectoV = ""
    var indicativoPreteritoPluscuamperfectoEll = ""

    var indicativoPreteritoAnteriorYo = ""
    var indicativoPreteritoAnteriorTu = ""
    var indicativoPreteritoAnteriorEl = ""
    var indicativoPreteritoAnteriorN = ""
    var indicativoPreteritoAnteriorV = ""
    var indicativoPreteritoAnteriorEll = ""

    var indicativoFuturoCompuestoYo = ""
    var indicativoFuturoCompuestoTu = ""
    var indicativoFuturoCompuestoEl = ""
    var indicativoFuturoCompuestoN = ""
    var indicativoFuturoCompuestoV = ""
    var indicativoFuturoCompuestoEll = ""

    var indicativoCondicionalCompuestoYo = ""
    var indicativoCondicionalCompuestoTu = ""
    var indicativoCondicionalCompuestoEl = ""
    var indicativoCondicionalCompuestoN = ""
    var indicativoCondicionalCompuestoV = ""
    var indicativoCondicionalCompuestoEll = ""

    var subjuntivoPresenteYo = ""
    var subjuntivoPresenteTu = ""
    var subjuntivoPresenteEl = ""
    var subjuntivoPresenteN = ""
    var subjuntivoPresenteV = ""
    var subjuntivoPresenteEll = ""

    var subjuntivoPreteritoImperfectoYo = ""
    var subjuntivoPreteritoImperfectoTu = ""
    var subjuntivoPreteritoImperfectoEl = ""
    var subjuntivoPreteritoImperfectoN = ""
    var subjuntivoPreteritoImperfectoV = ""
    var subjuntivoPreteritoImperfectoEll = ""

    var subjuntivoFuturoSimpleYo = ""
    var subjuntivoFuturoSimpleTu = ""
    var subjuntivoFuturoSimpleEl = ""
    var subjuntivoFuturoSimpleN = ""
    var subjuntivoFuturoSimpleV = ""
    var subjuntivoFuturoSimpleEll = ""

    var subjuntivoPreteritoPerfectoCompuestoYo = ""
    var subjuntivoPreteritoPerfectoCompuestoTu = ""
    var subjuntivoPreteritoPerfectoCompuestoEl = ""
    var subjuntivoPreteritoPerfectoCompuestoN = ""
    var subjuntivoPreteritoPerfectoCompuestoV = ""
    var subjuntivoPreteritoPerfectoCompuestoEll = ""

    var subjuntivoPreteritoPluscuamperfectoYo = ""
    var subjuntivoPreteritoPluscuamperfectoTu = ""
    var subjuntivoPreteritoPluscuamperfectoEl = ""
    var subjuntivoPreteritoPluscuamperfectoN = ""
    var subjuntivoPreteritoPluscuamperfectoV = ""
    var subjuntivoPreteritoPluscuamperfectoEll = ""

    var subjuntivoFuturoCompuestoYo = ""
    var subjuntivoFuturoCompuestoTu = ""
    var subjuntivoFuturoCompuestoEl = ""
    var subjuntivoFuturoCompuestoN = ""
    var subjuntivoFuturoCompuestoV = ""
    var subjuntivoFuturoCompuestoEll = ""


    init {
        this.id = id
        this.termination = termination
        this.radicals = radicals
        this.infinitivoSimple = infinitivoSimple
        this.infinitivoCompuesto = infinitivoCompuesto
        this.participio = participio
        this.gerundioSimple = gerundioSimple
        this.gerundioCompuesto = gerundioCompuesto

        this.imperativoTu = imperativoTu
        this.imperativoEl = imperativoEl
        this.imperativoN = imperativoN
        this.imperativoV = imperativoV
        this.imperativoEll = imperativoEll
        this.imperativoNegTu = imperativoNegTu
        this.imperativoNegEl = imperativoNegEl
        this.imperativoNegN = imperativoNegN
        this.imperativoNegV = imperativoNegV
        this.imperativoNegEll = imperativoNegEll

        this.indicativoPresenteYo = indicativoPresenteYo
        this.indicativoPresenteTu = indicativoPresenteTu
        this.indicativoPresenteEl = indicativoPresenteEl
        this.indicativoPresenteN = indicativoPresenteN
        this.indicativoPresenteV = indicativoPresenteV
        this.indicativoPresenteEll = indicativoPresenteEll

        this.indicativoPreteritoImperfectoYo = indicativoPreteritoImperfectoYo
        this.indicativoPreteritoImperfectoTu = indicativoPreteritoImperfectoTu
        this.indicativoPreteritoImperfectoEl = indicativoPreteritoImperfectoEl
        this.indicativoPreteritoImperfectoN = indicativoPreteritoImperfectoN
        this.indicativoPreteritoImperfectoV = indicativoPreteritoImperfectoV
        this.indicativoPreteritoImperfectoEll = indicativoPreteritoImperfectoEll

        this.indicativoPreteritoPerfectoSimpleYo = indicativoPreteritoPerfectoSimpleYo
        this.indicativoPreteritoPerfectoSimpleTu = indicativoPreteritoPerfectoSimpleTu
        this.indicativoPreteritoPerfectoSimpleEl = indicativoPreteritoPerfectoSimpleEl
        this.indicativoPreteritoPerfectoSimpleN = indicativoPreteritoPerfectoSimpleN
        this.indicativoPreteritoPerfectoSimpleV = indicativoPreteritoPerfectoSimpleV
        this.indicativoPreteritoPerfectoSimpleEll = indicativoPreteritoPerfectoSimpleEll

        this.indicativoFuturoSimpleYo = indicativoFuturoSimpleYo
        this.indicativoFuturoSimpleTu = indicativoFuturoSimpleTu
        this.indicativoFuturoSimpleEl = indicativoFuturoSimpleEl
        this.indicativoFuturoSimpleN = indicativoFuturoSimpleN
        this.indicativoFuturoSimpleV = indicativoFuturoSimpleV
        this.indicativoFuturoSimpleEll = indicativoFuturoSimpleEll

        this.indicativoCondicionalSimpleYo = indicativoCondicionalSimpleYo
        this.indicativoCondicionalSimpleTu = indicativoCondicionalSimpleTu
        this.indicativoCondicionalSimpleEl = indicativoCondicionalSimpleEl
        this.indicativoCondicionalSimpleN = indicativoCondicionalSimpleN
        this.indicativoCondicionalSimpleV = indicativoCondicionalSimpleV
        this.indicativoCondicionalSimpleEll = indicativoCondicionalSimpleEll

        this.indicativoPreteritoPerfectoCompuestoYo = indicativoPreteritoPerfectoCompuestoYo
        this.indicativoPreteritoPerfectoCompuestoTu = indicativoPreteritoPerfectoCompuestoTu
        this.indicativoPreteritoPerfectoCompuestoEl = indicativoPreteritoPerfectoCompuestoEl
        this.indicativoPreteritoPerfectoCompuestoN = indicativoPreteritoPerfectoCompuestoN
        this.indicativoPreteritoPerfectoCompuestoV = indicativoPreteritoPerfectoCompuestoV
        this.indicativoPreteritoPerfectoCompuestoEll = indicativoPreteritoPerfectoCompuestoEll

        this.indicativoPreteritoPluscuamperfectoYo = indicativoPreteritoPluscuamperfectoYo
        this.indicativoPreteritoPluscuamperfectoTu = indicativoPreteritoPluscuamperfectoTu
        this.indicativoPreteritoPluscuamperfectoEl = indicativoPreteritoPluscuamperfectoEl
        this.indicativoPreteritoPluscuamperfectoN = indicativoPreteritoPluscuamperfectoN
        this.indicativoPreteritoPluscuamperfectoV = indicativoPreteritoPluscuamperfectoV
        this.indicativoPreteritoPluscuamperfectoEll = indicativoPreteritoPluscuamperfectoEll

        this.indicativoPreteritoAnteriorYo = indicativoPreteritoAnteriorYo
        this.indicativoPreteritoAnteriorTu = indicativoPreteritoAnteriorTu
        this.indicativoPreteritoAnteriorEl = indicativoPreteritoAnteriorEl
        this.indicativoPreteritoAnteriorN = indicativoPreteritoAnteriorN
        this.indicativoPreteritoAnteriorV = indicativoPreteritoAnteriorV
        this.indicativoPreteritoAnteriorEll = indicativoPreteritoAnteriorEll

        this.indicativoFuturoCompuestoYo = indicativoFuturoCompuestoYo
        this.indicativoFuturoCompuestoTu = indicativoFuturoCompuestoTu
        this.indicativoFuturoCompuestoEl = indicativoFuturoCompuestoEl
        this.indicativoFuturoCompuestoN = indicativoFuturoCompuestoN
        this.indicativoFuturoCompuestoV = indicativoFuturoCompuestoV
        this.indicativoFuturoCompuestoEll = indicativoFuturoCompuestoEll

        this.indicativoCondicionalCompuestoYo = indicativoCondicionalCompuestoYo
        this.indicativoCondicionalCompuestoTu = indicativoCondicionalCompuestoTu
        this.indicativoCondicionalCompuestoEl = indicativoCondicionalCompuestoEl
        this.indicativoCondicionalCompuestoN = indicativoCondicionalCompuestoN
        this.indicativoCondicionalCompuestoV = indicativoCondicionalCompuestoV
        this.indicativoCondicionalCompuestoEll = indicativoCondicionalCompuestoEll

        this.subjuntivoPresenteYo = subjuntivoPresenteYo
        this.subjuntivoPresenteTu = subjuntivoPresenteTu
        this.subjuntivoPresenteEl = subjuntivoPresenteEl
        this.subjuntivoPresenteN = subjuntivoPresenteN
        this.subjuntivoPresenteV = subjuntivoPresenteV
        this.subjuntivoPresenteEll = subjuntivoPresenteEll

        this.subjuntivoPreteritoImperfectoYo = subjuntivoPreteritoImperfectoYo
        this.subjuntivoPreteritoImperfectoTu = subjuntivoPreteritoImperfectoTu
        this.subjuntivoPreteritoImperfectoEl = subjuntivoPreteritoImperfectoEl
        this.subjuntivoPreteritoImperfectoN = subjuntivoPreteritoImperfectoN
        this.subjuntivoPreteritoImperfectoV = subjuntivoPreteritoImperfectoV
        this.subjuntivoPreteritoImperfectoEll = subjuntivoPreteritoImperfectoEll

        this.subjuntivoFuturoSimpleYo = subjuntivoFuturoSimpleYo
        this.subjuntivoFuturoSimpleTu = subjuntivoFuturoSimpleTu
        this.subjuntivoFuturoSimpleEl = subjuntivoFuturoSimpleEl
        this.subjuntivoFuturoSimpleN = subjuntivoFuturoSimpleN
        this.subjuntivoFuturoSimpleV = subjuntivoFuturoSimpleV
        this.subjuntivoFuturoSimpleEll = subjuntivoFuturoSimpleEll

        this.subjuntivoPreteritoPerfectoCompuestoYo = subjuntivoPreteritoPerfectoCompuestoYo
        this.subjuntivoPreteritoPerfectoCompuestoTu = subjuntivoPreteritoPerfectoCompuestoTu
        this.subjuntivoPreteritoPerfectoCompuestoEl = subjuntivoPreteritoPerfectoCompuestoEl
        this.subjuntivoPreteritoPerfectoCompuestoN = subjuntivoPreteritoPerfectoCompuestoN
        this.subjuntivoPreteritoPerfectoCompuestoV = subjuntivoPreteritoPerfectoCompuestoV
        this.subjuntivoPreteritoPerfectoCompuestoEll = subjuntivoPreteritoPerfectoCompuestoEll

        this.subjuntivoPreteritoPluscuamperfectoYo = subjuntivoPreteritoPluscuamperfectoYo
        this.subjuntivoPreteritoPluscuamperfectoTu = subjuntivoPreteritoPluscuamperfectoTu
        this.subjuntivoPreteritoPluscuamperfectoEl = subjuntivoPreteritoPluscuamperfectoEl
        this.subjuntivoPreteritoPluscuamperfectoN = subjuntivoPreteritoPluscuamperfectoN
        this.subjuntivoPreteritoPluscuamperfectoV = subjuntivoPreteritoPluscuamperfectoV
        this.subjuntivoPreteritoPluscuamperfectoEll = subjuntivoPreteritoPluscuamperfectoEll

        this.subjuntivoFuturoCompuestoYo = subjuntivoFuturoCompuestoYo
        this.subjuntivoFuturoCompuestoTu = subjuntivoFuturoCompuestoTu
        this.subjuntivoFuturoCompuestoEl = subjuntivoFuturoCompuestoEl
        this.subjuntivoFuturoCompuestoN = subjuntivoFuturoCompuestoN
        this.subjuntivoFuturoCompuestoV = subjuntivoFuturoCompuestoV
        this.subjuntivoFuturoCompuestoEll = subjuntivoFuturoCompuestoEll
    }

}
