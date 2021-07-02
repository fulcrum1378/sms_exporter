package com.mahdiparastesh.smsexporter.data

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.R
import com.mahdiparastesh.smsexporter.adap.SmsAdap
import java.io.FileOutputStream

class PdfExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {
    val size = Size(1190, 1680)

    @SuppressLint("InflateParams")
    override fun run() { // DOES NOT INHERIT SUPER'S
        val content = arrayListOf<ConstraintLayout>()
        for (i in thread.list.indices) content.add(
            (LayoutInflater.from(c)
                .inflate(R.layout.item_sms, null, false) as ConstraintLayout).apply {
                SmsAdap.prepare(thread.list, this, i)
            })
        val document = PdfDocument()

        var page = 0
        var mess = 0
        while (mess < content.size) {
            document.startPage(
                PdfDocument.PageInfo.Builder(size.width, size.height, page).create()// A4
            ).apply {
                canvas.scale(1f, 1f)
                mess = insert(canvas, content, mess, size.height)
                document.finishPage(this)
            }
            page++
        }

        c.contentResolver.openFileDescriptor(where, "w")?.use {
            FileOutputStream(it.fileDescriptor).use { fos ->
                document.writeTo(fos)
            }
        }
        document.close()
        done(1)
    }

    fun insert(canvas: Canvas, content: List<ConstraintLayout>, mess: Int, remnant: Int): Int {
        var iMess = mess
        var iRemnant = remnant
        content[iMess].apply {
            measure( // ESSENTIAL
                View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, canvas.width, canvas.height)
            draw(canvas)
            iRemnant -= this.measuredHeight
        }
        iMess++
        if (iMess < content.size && iRemnant > content[iMess].measuredHeight)
            iMess = insert(canvas, content, iMess, iRemnant)
        return iMess
    }
}
