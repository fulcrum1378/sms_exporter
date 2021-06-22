package com.mahdiparastesh.smsexporter.data

import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.ImageView
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.R
import java.io.FileOutputStream

class PdfExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {

    override fun run() { // DOES NOT OVERRIDE
        val document = PdfDocument()
        document.startPage(
            PdfDocument.PageInfo.Builder(420, 595, 1).create()// A5
        ).apply {
            ImageView(c).apply {
                setImageResource(R.drawable.export_1)
                draw(canvas)
            }
            document.finishPage(this)
        }
        c.contentResolver.openFileDescriptor(where, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fos ->
                document.writeTo(fos)
            }
        }
        document.close()
        done(1)
    }
}
