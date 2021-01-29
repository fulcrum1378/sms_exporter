package org.ifaco.smsexporter.adap

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import org.ifaco.smsexporter.Fun
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.Fun.Companion.vish
import org.ifaco.smsexporter.Main
import org.ifaco.smsexporter.Main.Companion.handler
import org.ifaco.smsexporter.R
import org.ifaco.smsexporter.data.Contact
import org.ifaco.smsexporter.data.SMS
import java.io.File

class ThreadAdap(val list: List<SMS.Thread>, val contacts: List<Contact>?) :
    RecyclerView.Adapter<ThreadAdap.MyViewHolder>() {
    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thread, parent, false) as ConstraintLayout
        val main = v[0] as ConstraintLayout
        val pic = main[0] as ImageView
        val name = main[1] as TextView

        // Configurations
        pic.id = View.generateViewId()
        name.id = View.generateViewId()

        return MyViewHolder(v)
    }

    @Suppress("DEPRECATION")
    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val main = h.v[0] as ConstraintLayout
        val pic = main[0] as ImageView
        val name = main[1] as TextView
        val sep = h.v[1]

        // Layout
        (h.v.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(
                leftMargin, if (i != 0) 0
                else c.resources.getDimension(R.dimen.threadTopMar).toInt(),
                rightMargin, if (i < list.size - 1) 0
                else c.resources.getDimension(R.dimen.listBottomMar).toInt()
            )
            h.v.layoutParams = this
        }
        h.v.setOnClickListener {
            handler.obtainMessage(Main.Work.VIEW_THREAD.ordinal, list[h.layoutPosition].id)
                .sendToTarget()
        }
        vish(sep, i != list.size - 1)

        // Contact
        (name.layoutParams as ConstraintLayout.LayoutParams).apply {
            startToEnd = pic.id
            name.layoutParams = this
        }
        (pic.layoutParams as ConstraintLayout.LayoutParams).apply {
            topToTop = name.id
            bottomToBottom = name.id
            pic.layoutParams = this
        }
        var identified = false
        if (contacts != null) Contact.findContactByPhone(contacts, list[i].address)?.let {
            identified = true
            name.text = it.name
            if (it.photo != null) Fun.loadAvatar(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Uri.parse(it.photo!!)
                else Uri.fromFile(File(it.photo!!)), pic
            )
        }
        if (!identified) name.text = list[i].address
    }

    override fun getItemCount() = list.size
}
