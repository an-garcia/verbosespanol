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
package com.xengar.android.verbosespanol.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.app.FragmentActivity
import android.view.View

import fr.castorflex.android.circularprogressbar.CircularProgressBar

/**
 * FragmentUtils
 */
object FragmentUtils {

    /**
     * Checks for internet connection.
     * @return true if connected or connecting
     */
    fun checkInternetConnection(fragmentActivity: FragmentActivity): Boolean {
        val cm = fragmentActivity.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    /**
     * Changes to visible or gone the circular progress bar.
     * @param progressBar progress Bar
     * @param visibility boolean
     */
    fun updateProgressBar(progressBar: CircularProgressBar?, visibility: Boolean) {
        if (progressBar != null) {
            progressBar.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }
}
