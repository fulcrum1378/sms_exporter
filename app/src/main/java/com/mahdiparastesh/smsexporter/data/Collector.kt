package com.mahdiparastesh.smsexporter.data

import android.annotation.SuppressLint
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import com.mahdiparastesh.smsexporter.Fun.Companion.replaceAll
import com.mahdiparastesh.smsexporter.Main
import com.mahdiparastesh.smsexporter.Main.Companion.handler

class Collector(val that: AppCompatActivity) : Thread() {
    @SuppressLint("Recycle", "SetTextI18n")
    override fun run() {
        that.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf("date", "type", "thread_id", "body", "error_code", "address", "date_sent"),
            null, null,
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )?.let {
            if (!it.moveToFirst()) {
                handler.obtainMessage(Main.Work.VIEW_THREAD.ordinal, null).sendToTarget()
                it.close()
                return@let
            }
            val threads = arrayListOf<SMS.Thread>()
            val map1 = hashMapOf<String, ArrayList<SMS>>()
            val map2 = hashMapOf<String, String?>()
            for (i in 0 until it.count) {
                val key = col(it, "thread_id") ?: continue
                if (!map1.containsKey(key)) map1[key] = arrayListOf()
                var date = col(it, "date")
                var type = col(it, "type")
                var fromMe = type == "2"
                map1[key] = map1[key]!!.apply {
                    add(SMS(date?.toLong(), fromMe, col(it, "body"), col(it, "error_code")))
                }
                col(it, "address")?.let { num -> map2[key] = num }
                it.moveToNext()
            }
            for (m in map1) {
                val list = m.value.toList()
                threads.add(SMS.Thread(m.key, map2[m.key]!!, list, SMS.lastSMS(list)))
            }
            handler.obtainMessage(Main.Work.THREADS.ordinal, threads.toList()).sendToTarget()
            it.close()
        }

        that.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf("display_name", "data1", "photo_uri"),
            "(data1 LIKE '+%' OR data1 LIKE '0%') AND has_phone_number='1'",
            // account_type='com.google' AND
            null,
            "sort_key_alt"
        )?.let {
            if (!it.moveToFirst()) {
                handler.obtainMessage(Main.Work.CONTACTS.ordinal, "There are no contacts!")
                    .sendToTarget()
                it.close()
                return@let
            }
            val contacts = arrayListOf<Contact>()
            for (i in 0 until it.count) {
                val name = col(it, "display_name")!!
                val number = replaceAll(col(it, "data1")!!, " ", "")
                val found = Contact.findContactByName(contacts, name)
                if (found != null) contacts[contacts.indexOf(found)].numbers.add(number)
                else contacts.add(Contact(name, arrayListOf(number), col(it, "photo_uri")))
                it.moveToNext()
            }
            handler.obtainMessage(Main.Work.CONTACTS.ordinal, contacts).sendToTarget()
            it.close()
        }
    }

    fun col(it: Cursor, name: String) = it.getStringOrNull(it.getColumnIndex(name))
}
