package org.ifaco.smsexporter.data

import android.net.Uri
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.Talk
import org.ifaco.smsexporter.Talk.Companion.handler
import java.io.FileOutputStream
import java.lang.StringBuilder

open class BaseExporter(val thread: SMS.Thread, val contact: Contact?, val where: Uri) : Thread() {
    var ink: StringBuilder = StringBuilder()

    override fun run() {
        c.contentResolver.openFileDescriptor(where, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fos ->
                fos.write(ink.toString().toByteArray())
            }
        }
    }

    fun done(obj: Any?) {
        handler.obtainMessage(Talk.Work.EXPORTED.ordinal, obj).sendToTarget()
    }
}
