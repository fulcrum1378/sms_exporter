package org.ifaco.smsexporter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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
    }
}
