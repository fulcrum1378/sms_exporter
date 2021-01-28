package org.ifaco.smsexporter

import java.util.*

data class SMS(
    val date: Long?,
    val fromMe: Boolean,
    val text: String?,
    val error_code: String?,
    val address: String?
) {
    data class Thread(
        val id: String,
        val list: List<SMS>
    )

    companion object {
        fun findThreadById(list: List<Thread>, id: String): Thread? {
            var thread: Thread? = null
            for (t in list) if (t.id == id) thread = t
            return thread
        }
    }

    class Sort(val by: Int = 0) : Comparator<SMS> {
        override fun compare(a: SMS, b: SMS) = when (by) {
            0 -> a.date!!.compareTo(b.date!!)
            else -> a.date!!.compareTo(b.date!!)
        }
    }
}
