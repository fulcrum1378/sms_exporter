package com.mahdiparastesh.smsexporter.data

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.R
import com.mahdiparastesh.smsexporter.adap.SmsAdap
import java.io.FileOutputStream

class PdfExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {
    val size = Size(1190, 1680)

    @SuppressLint("InflateParams")
    override fun run() { // DOES NOT INHERIT SUPER'S
        val document = PdfDocument()

        var page = 0
        var mess = 0
        while (mess < thread.list.size) document.startPage(
            PdfDocument.PageInfo.Builder(size.width, size.height, page).create()
        ).apply {
            canvas.scale(1f, 1f)
            mess = insert(canvas, mess)
            document.finishPage(this)
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

    fun insert(canvas: Canvas, mess: Int): Int {
        var iMess = mess
        LinearLayout(c).apply {
            orientation = LinearLayout.VERTICAL
            do {
                addView((LayoutInflater.from(c)
                    .inflate(R.layout.item_sms, this, false) as ConstraintLayout).apply {
                    (this[0] as TextView).id = View.generateViewId()
                    SmsAdap.prepare(thread.list, this, iMess)
                })
                measure( // ESSENTIAL BOTH FOR draw() and measuredHeight
                    View.MeasureSpec.makeMeasureSpec(canvas.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(canvas.height, View.MeasureSpec.AT_MOST)
                )
                if (measuredHeight >= size.height) {
                    removeViewAt(childCount - 1)
                    break
                }
                iMess++
            } while (measuredHeight < size.height && iMess < thread.list.size)
            layout(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return iMess
    }
}
