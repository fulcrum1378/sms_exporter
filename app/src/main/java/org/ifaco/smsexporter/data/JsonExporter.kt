package org.ifaco.smsexporter.data

import android.net.Uri

class JsonExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {

    override fun run() {
        super.run()
        done(null)
    }
}
