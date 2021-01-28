package org.ifaco.smsexporter

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class Fun {
    companion object {
        lateinit var c: Context
        val calendar = CalendarType.SOLAR_HIJRI


        fun init(that: AppCompatActivity) {
            c = that.applicationContext
        }

        fun vis(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.GONE
        }

        fun vish(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.INVISIBLE
        }

        fun color(res: Int) = ContextCompat.getColor(c, res)

        fun calendar(unix: Long): Calendar = Calendar.getInstance().apply { timeInMillis = unix }
    }


    enum class CalendarType { GREGORIAN, SOLAR_HIJRI }
}
