package org.ifaco.smsexporter

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import org.ifaco.smsexporter.Main.Companion.handler

class Collector(val that: AppCompatActivity) : Thread() {
    @SuppressLint("Recycle", "SetTextI18n")
    override fun run() {
        that.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf("date", "type", "thread_id", "body", "error_code", "address", "date_sent"),
            null, null,
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )?.let {
            if (it.moveToFirst()) {
                val threads = arrayListOf<SMS.Thread>()
                val maps = hashMapOf<String, ArrayList<SMS>>()
                for (i in 0 until it.count) {
                    val key = col(it, "thread_id") ?: continue
                    if (!maps.containsKey(key)) maps[key] = arrayListOf()
                    var date = col(it, "date")
                    var type = col(it, "type")
                    if (type != "1" && type != "2")
                        throw  IllegalStateException("MAHDI: It's unknown whose SMS is this!!!")
                    var fromMe = type == "2"
                    maps[key] = maps[key]!!.apply {
                        add(
                            SMS(
                                date?.toLong(),
                                fromMe,
                                col(it, "body"),
                                col(it, "error_code"),
                                col(it, "address")
                            )
                        )
                    }
                    it.moveToNext()
                }
                for (m in maps) threads.add(SMS.Thread(m.key, m.value.toList()))
                handler.obtainMessage(Main.Work.THREADS.ordinal, threads.toList()).sendToTarget()
            } else handler.obtainMessage(Main.Work.VIEW_THREAD.ordinal, null).sendToTarget()
            it.close()
        }
    }

    fun col(it: Cursor, name: String) = it.getStringOrNull(it.getColumnIndex(name))
}
