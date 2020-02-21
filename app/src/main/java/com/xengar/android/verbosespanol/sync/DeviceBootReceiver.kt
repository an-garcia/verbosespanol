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
package com.xengar.android.verbosespanol.sync

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver

//import com.google.firebase.analytics.FirebaseAnalytics

import com.xengar.android.verbosespanol.utils.ActivityUtils
import com.xengar.android.verbosespanol.utils.Constants.TYPE_START_NOTIFICATIONS
import com.xengar.android.verbosespanol.utils.Constants.TYPE_VERB_NOTIFICATION

/**
 * DeviceBootReceiver
 * Broadcast receiver, starts when the device gets starts.
 * Start your repeating alarm here.
 * As of Android O , background check restrictions make this class no longer generally useful.
 */
class DeviceBootReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && intent.action == "android.intent.action.BOOT_COMPLETED") {
            if (ActivityUtils.getPreferenceEnableNotifications(context)) {
                // Setting the alarm here
                ActivityUtils.startAlarm(context)
                //val mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
                //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics,
                //        TYPE_START_NOTIFICATIONS, "Reboot", TYPE_VERB_NOTIFICATION)
            }
        }
    }
}
