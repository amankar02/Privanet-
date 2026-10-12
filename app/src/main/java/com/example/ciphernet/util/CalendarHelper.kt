package com.example.ciphernet.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast

object CalendarHelper {

    fun exportToDeviceCalendar(
        context: Context,
        title: String,
        description: String,
        startEpochMs: Long,
        endEpochMs: Long = startEpochMs + (60 * 60 * 1000L), // 1 hour duration
        location: String = "CipherNet P2P Decentralized Room"
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startEpochMs)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endEpochMs)
                putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Opening Calendar...", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "No compatible calendar app found: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
}
