package com.mahdiparastesh.smsexporter.adap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.mahdiparastesh.smsexporter.Fun
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.Fun.Companion.calendar
import com.mahdiparastesh.smsexporter.Fun.Companion.color
import com.mahdiparastesh.smsexporter.Fun.Companion.vis
import com.mahdiparastesh.smsexporter.Fun.Companion.z
import com.mahdiparastesh.smsexporter.R
import com.mahdiparastesh.smsexporter.data.SMS
import com.mahdiparastesh.smsexporter.etc.SolarHijri
import java.util.*

class SmsAdap(val list: List<SMS>) : RecyclerView.Adapter<SmsAdap.MyViewHolder>() {
    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms, parent, false) as ConstraintLayout
        val date = v[0] as TextView

        // Config
        date.id = View.generateViewId()

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        prepare(list, h.v, i)
    }

    override fun getItemCount() = list.size


    companion object {
        fun prepare(list: List<SMS>, cl: ConstraintLayout, i: Int) {
            val date = cl[0] as TextView
            val box = cl[1] as ConstraintLayout
            val address = box[0] as TextView
            val time = cl[2] as TextView

            // Layout
            val loLP = cl.layoutParams as ViewGroup.MarginLayoutParams
            loLP.apply {
                setMargins(
                    leftMargin, if (i != 0) 0
                    else c.resources.getDimension(R.dimen.smsTopMar).toInt(),
                    rightMargin, if (i < list.size - 1) 0
                    else c.resources.getDimension(R.dimen.listBottomMar).toInt()
                )
                cl.layoutParams = this
            }

            // Box
            box.setBackgroundResource(
                if (list[i].fromMe) R.drawable.box_self_1
                else R.drawable.box_contact_1
            )
            val boxLP = box.layoutParams as ConstraintLayout.LayoutParams
            boxLP.apply {
                horizontalBias = if (list[i].fromMe) 1f else 0f
                topToBottom = date.id
            }
            box.layoutParams = boxLP
            box.setOnClickListener { }

            // Body
            address.text = list[i].text
            address.setTextColor(color(if (list[i].fromMe) R.color.mySmsTV else R.color.smsTV))

            // Date
            val calendar = calendar(list[i].date!!)
            var showDate = true
            if (i > 0) {
                val previous = calendar(list[i - 1].date!!)
                if (calendar[Calendar.YEAR] == previous[Calendar.YEAR] &&
                    calendar[Calendar.MONTH] == previous[Calendar.MONTH] &&
                    calendar[Calendar.DAY_OF_MONTH] == previous[Calendar.DAY_OF_MONTH]
                ) showDate = false
            }
            vis(date, showDate)
            if (showDate) date.apply {
                var y = calendar[Calendar.YEAR]
                var m = calendar[Calendar.MONTH]
                var d = calendar[Calendar.DAY_OF_MONTH]
                var mArray = R.array.grMonth
                if (Fun.calendar == Fun.CalendarType.SOLAR_HIJRI) SolarHijri(calendar).apply {
                    y = Y; m = M; d = D
                    mArray = R.array.shMonth
                } // Fun.CalendarType.GREGORIAN
                text = c.getString(
                    R.string.smsDate,
                    c.resources.getStringArray(R.array.week)[calendar[Calendar.DAY_OF_WEEK] - 1],
                    d, c.resources.getStringArray(mArray)[m], y
                )
                setPaddingRelative(
                    paddingStart,
                    if (i == 0) 0 else c.resources.getDimension(R.dimen.smsMargin).toInt(),
                    paddingEnd,
                    paddingBottom
                )
            }

            // Time
            var showTime = true
            if (i < list.size - 1) {
                val next = calendar(list[i + 1].date!!)
                if (calendar[Calendar.HOUR_OF_DAY] == next[Calendar.HOUR_OF_DAY] &&
                    calendar[Calendar.MINUTE] == next[Calendar.MINUTE] &&
                    list[i].fromMe == list[i + 1].fromMe
                ) showTime = false
            }
            vis(time, showTime)
            if (showTime) time.apply {
                text = c.getString(
                    R.string.smsTime,
                    z(calendar[Calendar.HOUR_OF_DAY].toString()),
                    z(calendar[Calendar.MINUTE].toString())
                )
                val timeLP = time.layoutParams as ConstraintLayout.LayoutParams
                timeLP.horizontalBias = if (list[i].fromMe) 0f else 1f
                layoutParams = timeLP
                textAlignment =
                    if (list[i].fromMe) TextView.TEXT_ALIGNMENT_VIEW_END
                    else TextView.TEXT_ALIGNMENT_VIEW_START
            }
        }
    }
}
