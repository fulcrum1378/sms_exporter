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
        val list: List<SMS>
    ) {
        class Sort : Comparator<Thread> {
            override fun compare(a: Thread, b: Thread) =
                lastSMS(b).date!!.compareTo(lastSMS(a).date!!)

            fun lastSMS(th: Thread): SMS {
                var last = th.list[0]
                for (i in th.list) if (i.date!! > last.date!!) last = i
                return last
            }
        }
    }

    companion object {
        fun findThreadById(list: List<Thread>, id: String): Thread? {
            var thread: Thread? = null
            for (t in list) if (t.id == id) thread = t
            return thread
        }
    }

    class Sort : Comparator<SMS> {
        override fun compare(a: SMS, b: SMS) = a.date!!.compareTo(b.date!!)
    }
}
