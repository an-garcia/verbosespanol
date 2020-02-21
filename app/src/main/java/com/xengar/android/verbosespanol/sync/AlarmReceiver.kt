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
import android.os.Build
import android.support.v4.content.WakefulBroadcastReceiver
import android.widget.Toast

import com.xengar.android.verbosespanol.utils.Constants.LOG

/**
 * AlarmReceiver
 * As of Android O , background check restrictions make this class no longer generally useful.
 */
class AlarmReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // For our recurring task, we'll just display a message
        if (LOG) {
            Toast.makeText(context, "Alarm Receiver", Toast.LENGTH_SHORT).show()
        }
        val notificationIntent = Intent(context, NotificationService::class.java)
        // https://developer.android.com/about/versions/oreo/background.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(notificationIntent)
        } else {
            context.startService(notificationIntent)
        }
    }

}
