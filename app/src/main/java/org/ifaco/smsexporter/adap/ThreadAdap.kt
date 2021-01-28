package org.ifaco.smsexporter.adap

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.Fun.Companion.vish
import org.ifaco.smsexporter.Main
import org.ifaco.smsexporter.Main.Companion.handler
import org.ifaco.smsexporter.R
import org.ifaco.smsexporter.SMS

class ThreadAdap(val list: List<SMS.Thread>) : RecyclerView.Adapter<ThreadAdap.MyViewHolder>() {
    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thread, parent, false) as ConstraintLayout
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val address = h.v[0] as TextView
        val separator = h.v[1]

        // Configurations
        vish(separator, i != list.size - 1)
        val loLP = h.v.layoutParams as ViewGroup.MarginLayoutParams
        loLP.apply {
            setMargins(
                leftMargin, topMargin, rightMargin,
                if (i < list.size - 1) 0
                else c.resources.getDimension(R.dimen.listBottomMar).toInt()
            )
        }
        h.v.layoutParams = loLP

        // Texts
        address.text = list[i].list[0].address

        // Clicks
        h.v.setOnClickListener {
            handler.obtainMessage(Main.Work.VIEW_THREAD.ordinal, list[h.layoutPosition].id)
                .sendToTarget()
        }
    }

    override fun getItemCount() = list.size
}
