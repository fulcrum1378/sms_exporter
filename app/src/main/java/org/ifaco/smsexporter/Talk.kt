package org.ifaco.smsexporter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import org.ifaco.smsexporter.adap.SmsAdap
import org.ifaco.smsexporter.databinding.TalkBinding
import java.util.*

class Talk : AppCompatActivity() {
    lateinit var b: TalkBinding
    lateinit var thread: SMS.Thread
    lateinit var model: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = TalkBinding.inflate(layoutInflater)
        model = ViewModelProvider(this, Model.Factory()).get("Model", Model::class.java)
        setContentView(b.root)
        Fun.init(this)


        if (model.threads.value == null || model.viewThread.value == null) {
            onBackPressed(); return; }
        SMS.findThreadById(model.threads.value!!, model.viewThread.value!!)
            ?.let { thread = it }
        if (!::thread.isInitialized) {
            onBackPressed(); return; }
        Collections.sort(thread.list, SMS.Sort())
        b.list.adapter = SmsAdap(thread.list)
        b.list.scrollToPosition(thread.list.size - 1)

        // Toolbar
        b.tbNav.setOnClickListener { onBackPressed() }
    }
}
