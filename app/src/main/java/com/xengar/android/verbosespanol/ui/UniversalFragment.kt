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
package com.xengar.android.verbosespanol.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.adapter.VerbAdapter
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.sync.FetchVerbs
import com.xengar.android.verbosespanol.utils.Constants.ALPHABET
import com.xengar.android.verbosespanol.utils.Constants.BOTH
import com.xengar.android.verbosespanol.utils.Constants.COMMON_TYPE
import com.xengar.android.verbosespanol.utils.Constants.ITEM_TYPE
import com.xengar.android.verbosespanol.utils.Constants.LIST
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_ALL
import com.xengar.android.verbosespanol.utils.Constants.SORT_TYPE
import com.xengar.android.verbosespanol.utils.Constants.VERB_TYPE
import com.xengar.android.verbosespanol.utils.CustomErrorView
import com.xengar.android.verbosespanol.utils.FragmentUtils
import fr.castorflex.android.circularprogressbar.CircularProgressBar
import java.util.ArrayList


/**
 * UniversalFragment
 */
class UniversalFragment : Fragment() {

    private var mCustomErrorView: CustomErrorView? = null
    private var mRecyclerView: RecyclerView? = null
    private var progressBar: CircularProgressBar? = null
    private var mAdapter: VerbAdapter? = null
    private var mVerbs: MutableList<Verb>? = null
    var verbsType = BOTH
        private set    // regular, irregular, both
    var sortType = ALPHABET
        private set     // alphabet, color, group
    private var itemType = LIST         // card, list
    var commonType = MOST_COMMON_ALL
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            verbsType = arguments!!.getString(VERB_TYPE, BOTH)
            itemType = arguments!!.getString(ITEM_TYPE, LIST)
            sortType = arguments!!.getString(SORT_TYPE, ALPHABET)
            commonType = arguments!!.getString(COMMON_TYPE, MOST_COMMON_ALL)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_universal, container, false)

        mCustomErrorView = view.findViewById(R.id.error)
        mRecyclerView = view.findViewById(R.id.recycler)
        progressBar = view.findViewById(R.id.progressBar)
        mVerbs = ArrayList()

        val tts = (activity as MainActivity).tts
        mAdapter = VerbAdapter(mVerbs, itemType, tts!!)

        return view
    }

    override fun onResume() {
        super.onResume()
        mVerbs!!.clear()
        fillVerbs()
    }

    private fun onLoadFailed(t: Throwable) {
        mCustomErrorView!!.setError(t)
        mCustomErrorView!!.visibility = View.VISIBLE
        FragmentUtils.updateProgressBar(progressBar, false)
    }

    private fun fillVerbs() {
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,
                false)
        mRecyclerView!!.adapter = mAdapter
        FragmentUtils.updateProgressBar(progressBar, true)

        val fetch = FetchVerbs(verbsType, sortType, commonType, mAdapter!!,
                activity!!.contentResolver, mVerbs!!, progressBar!!)
        fetch.execute()
    }

}// Required empty public constructor
