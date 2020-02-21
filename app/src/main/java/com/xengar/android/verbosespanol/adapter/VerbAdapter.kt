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
package com.xengar.android.verbosespanol.adapter

import android.speech.tts.TextToSpeech
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.data.Verb

import com.xengar.android.verbosespanol.utils.Constants.LIST

/**
 * VerbAdapter
 */
class VerbAdapter(private val verbs: List<Verb>?, layoutType: String, tts: TextToSpeech)
    : RecyclerView.Adapter<VerbHolder>() {

    private var layoutType = LIST
    private var tts: TextToSpeech? = null

    init {
        this.layoutType = layoutType
        this.tts = tts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerbHolder {
        val inflater = LayoutInflater.from(parent.context)

        val v = inflater.inflate(
                if (layoutType.contentEquals(LIST))
                    R.layout.verbs_list_item
                else
                    R.layout.verbs_card_item, parent, false)
        return VerbHolder(v)
    }

    override fun onBindViewHolder(holder: VerbHolder, position: Int) {
        holder.bindVerb(verbs!![position], layoutType, tts!!)
    }

    override fun getItemCount(): Int {
        return verbs?.size ?: 0
    }

}
