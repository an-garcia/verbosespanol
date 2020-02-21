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

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.widget.Toast

import com.xengar.android.verbosespanol.utils.Constants.LOG

/**
 * JobSchedulerService
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JobSchedulerService : JobService() {

    companion object {

        val JOB_ID = 4
    }


    private val mJobHandler = Handler(Handler.Callback { msg ->
        val context = applicationContext
        if (LOG) {
            Toast.makeText(context, "JobSchedulerService task", Toast.LENGTH_SHORT).show()
        }
        val notificationIntent = Intent(context, NotificationService::class.java)
        // https://developer.android.com/about/versions/oreo/background.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(notificationIntent)
        } else {
            context.startService(notificationIntent)
        }
        jobFinished(msg.obj as JobParameters, false)
        true
    })


    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, JOB_ID, jobParameters))
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        mJobHandler.removeMessages(JOB_ID)
        return false
    }
}
