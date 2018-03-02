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
package com.xengar.android.verbosespanol.sync

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.util.Log

import com.bumptech.glide.Glide
//import com.google.firebase.analytics.FirebaseAnalytics
import com.xengar.android.verbosespanol.R
import com.xengar.android.verbosespanol.data.Verb
//import com.xengar.android.verbosespanol.ui.DetailsActivity
import com.xengar.android.verbosespanol.utils.ActivityUtils

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import com.xengar.android.verbosespanol.utils.Constants.CONJUGATION_ID
import com.xengar.android.verbosespanol.utils.Constants.TYPE_VERB_NOTIFICATION
import com.xengar.android.verbosespanol.utils.Constants.VERB_ID
import com.xengar.android.verbosespanol.utils.Constants.DEMO_MODE
import com.xengar.android.verbosespanol.utils.Constants.FAVORITES
import com.xengar.android.verbosespanol.utils.Constants.LOG
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_100
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_1000
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_25
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_300
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_50
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_500
import com.xengar.android.verbosespanol.utils.Constants.MOST_COMMON_ALL
import com.xengar.android.verbosespanol.utils.Constants.NOTIFICATION_VERB_ID
import com.xengar.android.verbosespanol.utils.Constants.SHARED_PREF_NAME
import com.xengar.android.verbosespanol.utils.Constants.VERB_NAME
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_CONJUGATION_NUMBER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_FAVORITE_VERBS_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.CONTENT_VERBS_URI
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.GROUP_ALL
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.OTHER
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_COMMON
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_ID
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.COLUMN_INFINITIVE
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_100
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_1000
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_25
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_300
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_50
import com.xengar.android.verbosespanol.data.VerbContract.VerbEntry.Companion.S_TOP_500
import com.xengar.android.verbosespanol.ui.MainActivity

/**
 * NotificationService
 */
class NotificationService : IntentService(NotificationService::class.java.name) {

    companion object {

        private val NOTIFICATION_ID = 4009
        private val CHANNEL_ID = "ChannelVerbosEspanol"
    }

    private val TAG = NotificationService::class.java.simpleName


    override fun onHandleIntent(intent: Intent?) {
        val verb = getNotificationVerb(applicationContext)
        if (verb != null) {
            notifyVerb(applicationContext, verb)

            //val mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
            //ActivityUtils.firebaseAnalyticsLogEventSelectContent(mFirebaseAnalytics,
            //        VERB_ID + " " + verb.id, verb.infinitive, TYPE_VERB_NOTIFICATION)
        }
    }

    @SuppressLint("NewApi")
    private fun notifyVerb(context: Context, verb: Verb) {
        val iconId = R.drawable.ic_notifications_verbos_espanol
        val largeIcon = readBitmapAsLargeIcon(context, R.drawable.art_notification_verbos_espanol)
        val notification: Notification

        // On Oreo devices use Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // https://medium.com/google-developers/migrating-mediastyle-notifications-to-support-android-o-29c7edeca9b7
            // You only need to create the channel on API 26+ devices
            createChannel(context)
            val mBuilder = Notification.Builder(context, CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
                    .setSmallIcon(iconId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(verb.infinitive)

            val resultIntent = createResultIntent(context, verb)
            val stackBuilder = createTaskStackBuilder(context, resultIntent)
            mBuilder.setContentIntent(
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
            notification = mBuilder.build()

        } else {

            // NotificationCompatBuilder is a very convenient way to build backward-compatible
            // notifications.  Just throw in some data.
            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimaryLight))
                    .setSmallIcon(iconId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(verb.infinitive)

            val resultIntent = createResultIntent(context, verb)
            val stackBuilder = createTaskStackBuilder(context, resultIntent)
            mBuilder.setContentIntent(
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
            notification = mBuilder.build()
        }

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID + 1, notification)
        }
    }

    private fun readBitmapAsLargeIcon(context: Context, artResourceId: Int): Bitmap {
        // On Honeycomb and higher devices, we can retrieve the size of the large icon
        val resources = context.resources
        val largeIconWidth = resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
        val largeIconHeight = resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)

        // Retrieve the large icon
        val largeIcon: Bitmap = try {
            Glide.with(context).load(artResourceId).asBitmap().fitCenter()
                    .into(largeIconWidth, largeIconHeight).get()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error loading large icon from resource " + artResourceId, e)
            BitmapFactory.decodeResource(resources, artResourceId)
        } catch (e: ExecutionException) {
            Log.e(TAG, "Error loading large icon from resource " + artResourceId, e)
            BitmapFactory.decodeResource(resources, artResourceId)
        }
        return largeIcon
    }

    private fun createResultIntent(context: Context, verb: Verb): Intent {
        // See https://stuff.mit.edu/afs/sipb/project/android/docs/guide/topics/ui/notifiers/notifications.html
        // When the user clicks on the notification, open the verb description
        /*val resultIntent = Intent(context, DetailsActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val bundle = Bundle()
        bundle.putString(VERB_NAME, verb.infinitive)
        bundle.putLong(VERB_ID, verb.id)
        bundle.putLong(CONJUGATION_ID, verb.conjugation.toLong())
        bundle.putBoolean(DEMO_MODE, false)
        resultIntent.putExtras(bundle)

        return resultIntent*/
        return Intent(context, MainActivity::class.java)
    }

    private fun createTaskStackBuilder(context: Context, resultIntent: Intent): TaskStackBuilder {
        val stackBuilder = TaskStackBuilder.create(context)
        //stackBuilder.addParentStack(DetailsActivity::class.java)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)

        return stackBuilder
    }

    /**
     * Notification channels must be used in order to display notifications.
     * @param context Context
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // The user-visible name of the channel.
        val name = context.getString(R.string.app_name)
        val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)

        // Configure the notification channel.
        // The user-visible description of the channel.
        val description = context.getString(R.string.pref_header_notifications)
        mChannel.description = description
        mChannel.setShowBadge(false)
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        mNotificationManager.createNotificationChannel(mChannel)
    }

    /**
     * Finds the new Notification verb according to the Preference notification list.
     * @return Verb
     */
    private fun getNotificationVerb(context: Context): Verb? {
        val notificationList = ActivityUtils.getPreferenceNotificationList(context)
        val columns = arrayOf(COLUMN_ID)
        val where: String
        val whereArgs: Array<String>
        val cursor: Cursor?
        when (notificationList) {
            FAVORITES -> cursor = context.contentResolver.query(CONTENT_FAVORITE_VERBS_URI, columns,
                    null, null, null)

            MOST_COMMON_25 -> {
                where = COLUMN_COMMON + " = ?"
                whereArgs = arrayOf(S_TOP_25)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_50 -> {
                where = "$COLUMN_COMMON = ? OR $COLUMN_COMMON = ?"
                whereArgs = arrayOf(S_TOP_25, S_TOP_50)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_100 -> {
                where = "$COLUMN_COMMON = ? OR $COLUMN_COMMON = ? OR $COLUMN_COMMON = ?"
                whereArgs = arrayOf(S_TOP_25, S_TOP_50, S_TOP_100)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_300 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ?")
                whereArgs = arrayOf(S_TOP_25, S_TOP_50, S_TOP_100, S_TOP_300)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_500 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ? OR "
                        + COLUMN_COMMON + " = ?")
                whereArgs = arrayOf(S_TOP_25, S_TOP_50, S_TOP_100, S_TOP_300, S_TOP_500)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_1000 -> {
                where = (COLUMN_COMMON + " = ? OR " + COLUMN_COMMON
                        + " = ? OR " + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ? OR "
                        + COLUMN_COMMON + " = ? OR " + COLUMN_COMMON + " = ?")
                whereArgs = arrayOf(S_TOP_25, S_TOP_50, S_TOP_100, S_TOP_300, S_TOP_500, S_TOP_1000)
                cursor = context.contentResolver.query(
                        CONTENT_VERBS_URI, columns, where, whereArgs, null)
            }

            MOST_COMMON_ALL -> cursor = context.contentResolver.query(
                    CONTENT_VERBS_URI, columns, null, null, null)
            else -> cursor = context.contentResolver.query(CONTENT_VERBS_URI, columns, null,
                    null, null)
        }

        val verbIds = ArrayList<Long>()
        if (cursor != null && cursor.count != 0) {
            while (cursor.moveToNext()) {
                verbIds.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
            }
        } else {
            if (LOG) {
                Log.d(TAG, "Cursor is empty")
            }
        }
        cursor?.close()

        // Find the correct verbId
        val verbId = findNextVerbId(context, verbIds)
        verbIds.clear()

        return getVerb(verbId)
    }

    /**
     * Finds the next verb in the list.
     * @param context Context
     * @param verbIds ArrayList<Long>
     * @return Verb
     */
    private fun findNextVerbId(context: Context, verbIds: ArrayList<Long>): Long {
        var verbId: Long? = null
        if (!verbIds.isEmpty()) {
            val prefs = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val notificationVerbId = prefs.getLong(NOTIFICATION_VERB_ID, -1)
            verbId = verbIds[0]
            // find next
            var v: Long?
            for (index in 0 until verbIds.size - 1) {
                v = verbIds[index]
                if (v == notificationVerbId) {
                    verbId = verbIds[if (index + 1 < verbIds.size) index + 1 else 0]
                    break
                }
            }
        }
        val value: Long = if (verbId != null) verbId else 0L   // first verb  être
        ActivityUtils.saveLongToPreferences(context, NOTIFICATION_VERB_ID, value)
        return value
    }

    /**
     * Get the Verb from the database.
     * @param verbId id
     * @return verb
     */
    private fun getVerb(verbId: Long?): Verb? {
        val columns = arrayOf(COLUMN_ID, COLUMN_INFINITIVE, COLUMN_CONJUGATION_NUMBER)
        val cursor = contentResolver.query(
                CONTENT_VERBS_URI,
                columns, // select
                COLUMN_ID + " = ?", // where
                arrayOf(java.lang.Long.toString(verbId!!)), null)// whereArgs

        if (cursor != null && cursor.count != 0) {
            if (cursor.moveToFirst()) {
                return Verb(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_CONJUGATION_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_INFINITIVE)) ?: "",
                        "", "", "", "", OTHER, GROUP_ALL,
                        0, 0, "", "", "", "")
            }
        } else {
            if (LOG) {
                Log.d(TAG, "Cursor is empty")
            }
        }
        cursor?.close()

        return null
    }
}
