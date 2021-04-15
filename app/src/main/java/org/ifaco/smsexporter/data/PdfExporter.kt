package org.ifaco.smsexporter.data

import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.view.View
import org.ifaco.smsexporter.Fun.Companion.c
import java.io.FileOutputStream

class PdfExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {

    override fun run() { // DOES NOT OVERRIDE
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(100, 100, 1).create())
        val content = View(c)
        content.draw(page.canvas)
        document.finishPage(page)
        c.contentResolver.openFileDescriptor(where, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fos ->
                document.writeTo(fos)
            }
        }
        document.close()
        done(1)
    }
}
