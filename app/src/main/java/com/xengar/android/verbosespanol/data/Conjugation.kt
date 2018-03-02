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
(id: Long, termination: String, radicals: String,
 infinitivePresent: String,
 infinitivePasse: String,
 participePresent: String,
 participePasse1: String,
 participePasse2: String,
 gerondifPresent: String,
 gerondifPasse: String,
 imperatifPresentTu: String,
 imperatifPresentNous: String,
 imperatifPresentVous: String,
 imperatifPasseTu: String,
 imperatifPasseNous: String,
 imperatifPasseVous: String,

 indicatifPresentJe: String,
 indicatifPresentTu: String,
 indicatifPresentIl: String,
 indicatifPresentNous: String,
 indicatifPresentVous: String,
 indicatifPresentIls: String,
 indicatifPasseComposeJe: String,
 indicatifPasseComposeTu: String,
 indicatifPasseComposeIl: String,
 indicatifPasseComposeNous: String,
 indicatifPasseComposeVous: String,
 indicatifPasseComposeIls: String,
 indicatifImperfaitJe: String,
 indicatifImperfaitTu: String,
 indicatifImperfaitIl: String,
 indicatifImperfaitNous: String,
 indicatifImperfaitVous: String,
 indicatifImperfaitIls: String,
 indicatifPlusQueParfaitJe: String,
 indicatifPlusQueParfaitTu: String,
 indicatifPlusQueParfaitIl: String,
 indicatifPlusQueParfaitNous: String,
 indicatifPlusQueParfaitVous: String,
 indicatifPlusQueParfaitIls: String,
 indicatifPasseSimpleJe: String,
 indicatifPasseSimpleTu: String,
 indicatifPasseSimpleIl: String,
 indicatifPasseSimpleNous: String,
 indicatifPasseSimpleVous: String,
 indicatifPasseSimpleIls: String,
 indicatifPasseAnterieurJe: String,
 indicatifPasseAnterieurTu: String,
 indicatifPasseAnterieurIl: String,
 indicatifPasseAnterieurNous: String,
 indicatifPasseAnterieurVous: String,
 indicatifPasseAnterieurIls: String,
 indicatifFuturSimpleJe: String,
 indicatifFuturSimpleTu: String,
 indicatifFuturSimpleIl: String,
 indicatifFuturSimpleNous: String,
 indicatifFuturSimpleVous: String,
 indicatifFuturSimpleIls: String,
 indicatifFuturAnterieurJe: String,
 indicatifFuturAnterieurTu: String,
 indicatifFuturAnterieurIl: String,
 indicatifFuturAnterieurNous: String,
 indicatifFuturAnterieurVous: String,
 indicatifFuturAnterieurIls: String,

 subjonctifPresentJe: String,
 subjonctifPresentTu: String,
 subjonctifPresentIl: String,
 subjonctifPresentNous: String,
 subjonctifPresentVous: String,
 subjonctifPresentIls: String,
 subjonctifPasseJe: String,
 subjonctifPasseTu: String,
 subjonctifPasseIl: String,
 subjonctifPasseNous: String,
 subjonctifPasseVous: String,
 subjonctifPasseIls: String,
 subjonctifImperfaitJe: String,
 subjonctifImperfaitTu: String,
 subjonctifImperfaitIl: String,
 subjonctifImperfaitNous: String,
 subjonctifImperfaitVous: String,
 subjonctifImperfaitIls: String,
 subjonctifPlusQueParfaitJe: String,
 subjonctifPlusQueParfaitTu: String,
 subjonctifPlusQueParfaitIl: String,
 subjonctifPlusQueParfaitNous: String,
 subjonctifPlusQueParfaitVous: String,
 subjonctifPlusQueParfaitIls: String,

 conditionnelPresentJe: String,
 conditionnelPresentTu: String,
 conditionnelPresentIl: String,
 conditionnelPresentNous: String,
 conditionnelPresentVous: String,
 conditionnelPresentIls: String,
 conditionnelPasseJe: String,
 conditionnelPasseTu: String,
 conditionnelPasseIl: String,
 conditionnelPasseNous: String,
 conditionnelPasseVous: String,
 conditionnelPasseIls: String) {

    /* Getters and Setters */
    var id: Long = 0
    var termination = ""
    var radicals = ""
    var infinitivePresent = ""
    var infinitivePasse = ""
    var participePresent = ""
    var participePasse1 = ""
    var participePasse2 = ""
    var gerondifPresent = ""
    var gerondifPasse = ""
    var imperatifPresentTu = ""
    var imperatifPresentNous = ""
    var imperatifPresentVous = ""
    var imperatifPasseTu = ""
    var imperatifPasseNous = ""
    var imperatifPasseVous = ""

    var indicatifPresentJe = ""
    var indicatifPresentTu = ""
    var indicatifPresentIl = ""
    var indicatifPresentNous = ""
    var indicatifPresentVous = ""
    var indicatifPresentIls = ""
    var indicatifPasseComposeJe = ""
    var indicatifPasseComposeTu = ""
    var indicatifPasseComposeIl = ""
    var indicatifPasseComposeNous = ""
    var indicatifPasseComposeVous = ""
    var indicatifPasseComposeIls = ""
    var indicatifImperfaitJe = ""
    var indicatifImperfaitTu = ""
    var indicatifImperfaitIl = ""
    var indicatifImperfaitNous = ""
    var indicatifImperfaitVous = ""
    var indicatifImperfaitIls = ""
    var indicatifPlusQueParfaitJe = ""
    var indicatifPlusQueParfaitTu = ""
    var indicatifPlusQueParfaitIl = ""
    var indicatifPlusQueParfaitNous = ""
    var indicatifPlusQueParfaitVous = ""
    var indicatifPlusQueParfaitIls = ""
    var indicatifPasseSimpleJe = ""
    var indicatifPasseSimpleTu = ""
    var indicatifPasseSimpleIl = ""
    var indicatifPasseSimpleNous = ""
    var indicatifPasseSimpleVous = ""
    var indicatifPasseSimpleIls = ""
    var indicatifPasseAnterieurJe = ""
    var indicatifPasseAnterieurTu = ""
    var indicatifPasseAnterieurIl = ""
    var indicatifPasseAnterieurNous = ""
    var indicatifPasseAnterieurVous = ""
    var indicatifPasseAnterieurIls = ""
    var indicatifFuturSimpleJe = ""
    var indicatifFuturSimpleTu = ""
    var indicatifFuturSimpleIl = ""
    var indicatifFuturSimpleNous = ""
    var indicatifFuturSimpleVous = ""
    var indicatifFuturSimpleIls = ""
    var indicatifFuturAnterieurJe = ""
    var indicatifFuturAnterieurTu = ""
    var indicatifFuturAnterieurIl = ""
    var indicatifFuturAnterieurNous = ""
    var indicatifFuturAnterieurVous = ""
    var indicatifFuturAnterieurIls = ""

    var subjonctifPresentJe = ""
    var subjonctifPresentTu = ""
    var subjonctifPresentIl = ""
    var subjonctifPresentNous = ""
    var subjonctifPresentVous = ""
    var subjonctifPresentIls = ""
    var subjonctifPasseJe = ""
    var subjonctifPasseTu = ""
    var subjonctifPasseIl = ""
    var subjonctifPasseNous = ""
    var subjonctifPasseVous = ""
    var subjonctifPasseIls = ""
    var subjonctifImperfaitJe = ""
    var subjonctifImperfaitTu = ""
    var subjonctifImperfaitIl = ""
    var subjonctifImperfaitNous = ""
    var subjonctifImperfaitVous = ""
    var subjonctifImperfaitIls = ""
    var subjonctifPlusQueParfaitJe = ""
    var subjonctifPlusQueParfaitTu = ""
    var subjonctifPlusQueParfaitIl = ""
    var subjonctifPlusQueParfaitNous = ""
    var subjonctifPlusQueParfaitVous = ""
    var subjonctifPlusQueParfaitIls = ""

    var conditionnelPresentJe = ""
    var conditionnelPresentTu = ""
    var conditionnelPresentIl = ""
    var conditionnelPresentNous = ""
    var conditionnelPresentVous = ""
    var conditionnelPresentIls = ""
    var conditionnelPasseJe = ""
    var conditionnelPasseTu = ""
    var conditionnelPasseIl = ""
    var conditionnelPasseNous = ""
    var conditionnelPasseVous = ""
    var conditionnelPasseIls = ""

    init {
        this.id = id
        this.termination = termination
        this.radicals = radicals
        this.infinitivePresent = infinitivePresent
        this.infinitivePasse = infinitivePasse
        this.participePresent = participePresent
        this.participePasse1 = participePasse1
        this.participePasse2 = participePasse2
        this.gerondifPresent = gerondifPresent
        this.gerondifPasse = gerondifPasse
        this.imperatifPresentTu = imperatifPresentTu
        this.imperatifPresentNous = imperatifPresentNous
        this.imperatifPresentVous = imperatifPresentVous
        this.imperatifPasseTu = imperatifPasseTu
        this.imperatifPasseNous = imperatifPasseNous
        this.imperatifPasseVous = imperatifPasseVous

        this.indicatifPresentJe = indicatifPresentJe
        this.indicatifPresentTu = indicatifPresentTu
        this.indicatifPresentIl = indicatifPresentIl
        this.indicatifPresentNous = indicatifPresentNous
        this.indicatifPresentVous = indicatifPresentVous
        this.indicatifPresentIls = indicatifPresentIls
        this.indicatifPasseComposeJe = indicatifPasseComposeJe
        this.indicatifPasseComposeTu = indicatifPasseComposeTu
        this.indicatifPasseComposeIl = indicatifPasseComposeIl
        this.indicatifPasseComposeNous = indicatifPasseComposeNous
        this.indicatifPasseComposeVous = indicatifPasseComposeVous
        this.indicatifPasseComposeIls = indicatifPasseComposeIls
        this.indicatifImperfaitJe = indicatifImperfaitJe
        this.indicatifImperfaitTu = indicatifImperfaitTu
        this.indicatifImperfaitIl = indicatifImperfaitIl
        this.indicatifImperfaitNous = indicatifImperfaitNous
        this.indicatifImperfaitVous = indicatifImperfaitVous
        this.indicatifImperfaitIls = indicatifImperfaitIls
        this.indicatifPlusQueParfaitJe = indicatifPlusQueParfaitJe
        this.indicatifPlusQueParfaitTu = indicatifPlusQueParfaitTu
        this.indicatifPlusQueParfaitIl = indicatifPlusQueParfaitIl
        this.indicatifPlusQueParfaitNous = indicatifPlusQueParfaitNous
        this.indicatifPlusQueParfaitVous = indicatifPlusQueParfaitVous
        this.indicatifPlusQueParfaitIls = indicatifPlusQueParfaitIls
        this.indicatifPasseSimpleJe = indicatifPasseSimpleJe
        this.indicatifPasseSimpleTu = indicatifPasseSimpleTu
        this.indicatifPasseSimpleIl = indicatifPasseSimpleIl
        this.indicatifPasseSimpleNous = indicatifPasseSimpleNous
        this.indicatifPasseSimpleVous = indicatifPasseSimpleVous
        this.indicatifPasseSimpleIls = indicatifPasseSimpleIls
        this.indicatifPasseAnterieurJe = indicatifPasseAnterieurJe
        this.indicatifPasseAnterieurTu = indicatifPasseAnterieurTu
        this.indicatifPasseAnterieurIl = indicatifPasseAnterieurIl
        this.indicatifPasseAnterieurNous = indicatifPasseAnterieurNous
        this.indicatifPasseAnterieurVous = indicatifPasseAnterieurVous
        this.indicatifPasseAnterieurIls = indicatifPasseAnterieurIls
        this.indicatifFuturSimpleJe = indicatifFuturSimpleJe
        this.indicatifFuturSimpleTu = indicatifFuturSimpleTu
        this.indicatifFuturSimpleIl = indicatifFuturSimpleIl
        this.indicatifFuturSimpleNous = indicatifFuturSimpleNous
        this.indicatifFuturSimpleVous = indicatifFuturSimpleVous
        this.indicatifFuturSimpleIls = indicatifFuturSimpleIls
        this.indicatifFuturAnterieurJe = indicatifFuturAnterieurJe
        this.indicatifFuturAnterieurTu = indicatifFuturAnterieurTu
        this.indicatifFuturAnterieurIl = indicatifFuturAnterieurIl
        this.indicatifFuturAnterieurNous = indicatifFuturAnterieurNous
        this.indicatifFuturAnterieurVous = indicatifFuturAnterieurVous
        this.indicatifFuturAnterieurIls = indicatifFuturAnterieurIls

        this.subjonctifPresentJe = subjonctifPresentJe
        this.subjonctifPresentTu = subjonctifPresentTu
        this.subjonctifPresentIl = subjonctifPresentIl
        this.subjonctifPresentNous = subjonctifPresentNous
        this.subjonctifPresentVous = subjonctifPresentVous
        this.subjonctifPresentIls = subjonctifPresentIls
        this.subjonctifPasseJe = subjonctifPasseJe
        this.subjonctifPasseTu = subjonctifPasseTu
        this.subjonctifPasseIl = subjonctifPasseIl
        this.subjonctifPasseNous = subjonctifPasseNous
        this.subjonctifPasseVous = subjonctifPasseVous
        this.subjonctifPasseIls = subjonctifPasseIls
        this.subjonctifImperfaitJe = subjonctifImperfaitJe
        this.subjonctifImperfaitTu = subjonctifImperfaitTu
        this.subjonctifImperfaitIl = subjonctifImperfaitIl
        this.subjonctifImperfaitNous = subjonctifImperfaitNous
        this.subjonctifImperfaitVous = subjonctifImperfaitVous
        this.subjonctifImperfaitIls = subjonctifImperfaitIls
        this.subjonctifPlusQueParfaitJe = subjonctifPlusQueParfaitJe
        this.subjonctifPlusQueParfaitTu = subjonctifPlusQueParfaitTu
        this.subjonctifPlusQueParfaitIl = subjonctifPlusQueParfaitIl
        this.subjonctifPlusQueParfaitNous = subjonctifPlusQueParfaitNous
        this.subjonctifPlusQueParfaitVous = subjonctifPlusQueParfaitVous
        this.subjonctifPlusQueParfaitIls = subjonctifPlusQueParfaitIls

        this.conditionnelPresentJe = conditionnelPresentJe
        this.conditionnelPresentTu = conditionnelPresentTu
        this.conditionnelPresentIl = conditionnelPresentIl
        this.conditionnelPresentNous = conditionnelPresentNous
        this.conditionnelPresentVous = conditionnelPresentVous
        this.conditionnelPresentIls = conditionnelPresentIls
        this.conditionnelPasseJe = conditionnelPasseJe
        this.conditionnelPasseTu = conditionnelPasseTu
        this.conditionnelPasseIl = conditionnelPasseIl
        this.conditionnelPasseNous = conditionnelPasseNous
        this.conditionnelPasseVous = conditionnelPasseVous
        this.conditionnelPasseIls = conditionnelPasseIls
    }

}
