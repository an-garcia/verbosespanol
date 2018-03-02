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
package com.xengar.android.verbosespanol.ui

import android.app.SearchManager
import android.content.ContentResolver
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.adapter.VerbHolder
import com.xengar.android.verbosespanol.data.Verb
import com.xengar.android.verbosespanol.data.VerbContract
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_VERBS_URI
import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.Constants.LIST
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.PAGE_SEARCH

import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

/**
 * SearchActivity
 */
class SearchActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var mVerbs: MutableList<Verb>? = null // all verbs in the database
    private var mSearchView: SearchView? = null
    private var mAdapter: SearchAdapter? = null
    private var tts: TextToSpeech? = null

    //private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mToolbar = findViewById(R.id.toolbar)
        val mRecyclerView = findViewById<RecyclerView>(R.id.search_recycler_view)
        mSearchView = findViewById<SearchView>(R.id.search_view)
        setupActionBar()
        setupSearchView()

        mVerbs = ArrayList()
        mAdapter = SearchAdapter()
        // Get all verbs
        val fetch = FetchVerbs(contentResolver, mVerbs!!, mAdapter!!.verbs)
        fetch.execute()

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        // initialize Speaker
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.FRENCH)
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (LOG) {
                        Log.e("TTS", "This Language is not supported")
                    }
                }
            } else {
                if (LOG) {
                    Log.e("TTS", "Initilization Failed!")
                }
            }
        })

        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics!!,
        //        PAGE_SEARCH, PAGE_SEARCH, TYPE_PAGE)
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    private fun setupActionBar() {
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        mSearchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        mSearchView!!.isIconified = false
        mSearchView!!.queryHint = getString(R.string.action_search)
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    mAdapter!!.filter.filter(newText)
                    return true
                }

                return false
            }
        })
        mSearchView!!.setOnCloseListener {
            finish()
            true
        }
    }

    /**
     * SearchAdapter
     */
    internal inner class SearchAdapter : RecyclerView.Adapter<VerbHolder>(), Filterable {

        val verbs: MutableList<Verb> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerbHolder {
            val inflater = LayoutInflater.from(parent.context)
            val v = inflater.inflate(R.layout.verbs_list_item, parent, false)
            return VerbHolder(v)
        }

        override fun onBindViewHolder(holder: VerbHolder, position: Int) {
            val item = verbs[position]
            holder.bindVerb(item, LIST, tts!!)
        }

        override fun getItemCount(): Int {
            return verbs.size
        }

        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                    val results = Filter.FilterResults()
                    val language = ActivityUtils.getPreferenceTranslationLanguage(applicationContext)

                    // Iterate though the list and get the verbs that contain the string
                    verbs.clear()
                    mVerbs!!.filterTo(verbs) {
                        (ActivityUtils.getTranslation(it, language).contains(charSequence)
                                || it.infinitive.contains(charSequence))
                    }
                    results.values = verbs
                    results.count = verbs.size

                    //ActivityUtils.firebaseAnalyticsLogEventSearch(
                    //        mFirebaseAnalytics!!, charSequence.toString())
                    //ActivityUtils.firebaseAnalyticsLogEventViewSearchResults(
                    //        mFirebaseAnalytics!!, charSequence.toString())

                    return results
                }

                override fun publishResults(charSequence: CharSequence,
                                            filterResults: Filter.FilterResults) {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * FetchVerbs from the database.
     */
    inner class FetchVerbs// Constructor
    (private val contentResolver: ContentResolver,
     private val verbs: MutableList<Verb>, // list of all verbs
     private val search: MutableList<Verb> // list of search result verbs
    ) : AsyncTask<Void, Void, ArrayList<Verb>>() {

        private val TAG = FetchVerbs::class.java.simpleName

        override fun doInBackground(vararg voids: Void): ArrayList<Verb> {
            // Define a projection that specifies the columns from the table we care about.
            val columns = ActivityUtils.allVerbColumns()
            val sortOrder = COLUMN_INFINITIVE + " ASC"
            val cursor = contentResolver.query(
                    CONTENT_VERBS_URI, columns, null, null, sortOrder)

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
                search.addAll(list) // begin search with all verbs in screen.
            }
        }
    }

}
