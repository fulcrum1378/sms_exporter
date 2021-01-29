package org.ifaco.smsexporter.data

import java.util.*

data class SMS(
    val date: Long?,
    val fromMe: Boolean,
    val text: String?,
    val error_code: String?
) {
    data class Thread(
        val id: String,
        val address: String,
        val list: List<SMS>,
        val lastDate: Long
    ) {
        class Sort : Comparator<Thread> {
            override fun compare(a: Thread, b: Thread) = b.lastDate.compareTo(a.lastDate)
        }
    }

    companion object {
        fun findThreadById(list: List<Thread>, id: String): Thread? {
            var thread: Thread? = null
            for (t in list) if (t.id == id) thread = t
            return thread
        }

        fun lastSMS(list: List<SMS>): Long {
            var last = 0L
            for (i in list) if (i.date!! > last) last = i.date
            return last
        }
    }

    class Sort : Comparator<SMS> {
        override fun compare(a: SMS, b: SMS) = a.date!!.compareTo(b.date!!)
    }
}
