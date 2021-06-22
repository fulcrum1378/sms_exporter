package com.mahdiparastesh.smsexporter.data

import android.net.Uri
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.Talk
import com.mahdiparastesh.smsexporter.Talk.Companion.handler
import java.io.FileOutputStream

open class BaseExporter(val thread: SMS.Thread, val contact: Contact?, val where: Uri) : Thread() {
    var digital: ByteArray? = null

    override fun run() {
        c.contentResolver.openFileDescriptor(where, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fos ->
                fos.write(digital)
            }
        }
    }

    fun done(obj: Any?) {
        handler.obtainMessage(Talk.Work.EXPORTED.ordinal, obj).sendToTarget()
    }
}
