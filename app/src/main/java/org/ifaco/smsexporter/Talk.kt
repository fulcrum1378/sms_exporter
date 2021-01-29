package org.ifaco.smsexporter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.Fun.Companion.drawable
import org.ifaco.smsexporter.Fun.Companion.filter
import org.ifaco.smsexporter.Fun.Companion.night
import org.ifaco.smsexporter.Fun.Companion.vish
import org.ifaco.smsexporter.adap.SmsAdap
import org.ifaco.smsexporter.data.SMS
import org.ifaco.smsexporter.databinding.TalkBinding
import java.util.*

class Talk : AppCompatActivity() {
    lateinit var b: TalkBinding
    lateinit var m: Model
    lateinit var thread: SMS.Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = TalkBinding.inflate(layoutInflater)
        m = ViewModelProvider(this, Model.Factory()).get("Model", Model::class.java)
        setContentView(b.root)
        Fun.init(this)


        if (m.threads.value == null || m.viewThread.value == null) {
            onBackPressed(); return; }
        SMS.findThreadById(m.threads.value!!, m.viewThread.value!!)
            ?.let { thread = it }
        if (!::thread.isInitialized) {
            onBackPressed(); return; }
        Collections.sort(thread.list, SMS.Sort())
        b.list.adapter = SmsAdap(thread.list)
        b.list.scrollToPosition(thread.list.size - 1)
        b.list.viewTreeObserver.addOnScrollChangedListener {
            vish(b.tbShadow, b.list.computeVerticalScrollOffset() > 0)
        }

        // Toolbar
        b.tbNav.setOnClickListener { onBackPressed() }
        if (night) b.tbAction.colorFilter = filter(R.color.CP)
        b.tbAction.setOnClickListener { howExport() }
    }


    @SuppressLint("InflateParams")
    fun howExport() = AlertDialog.Builder(this).apply {
        if (!night) setIcon(R.drawable.icon_1)
        else setIcon(drawable(R.drawable.icon_1)!!.apply { colorFilter = filter(R.color.CP) })
        setTitle(R.string.export)
        setMessage(R.string.howExport)
        setView(
            (LayoutInflater.from(c).inflate(R.layout.export_methods, null) as LinearLayout)
                .apply { for (i in 0 until childCount) this[i].setOnClickListener { whereExport(i) } })
        create().show()
    }

    fun whereExport(how: Int) {
    }
}
