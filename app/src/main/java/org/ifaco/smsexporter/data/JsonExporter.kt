package org.ifaco.smsexporter.data

import android.net.Uri
import com.google.gson.Gson

class JsonExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {

    override fun run() {
        digital = Gson().toJson(thread, SMS.Thread::class.java).toByteArray()
        super.run()
        done(1)
    }
}
