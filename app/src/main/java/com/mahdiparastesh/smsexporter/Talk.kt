package com.mahdiparastesh.smsexporter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.mahdiparastesh.smsexporter.Fun.Companion.c
import com.mahdiparastesh.smsexporter.Fun.Companion.drawable
import com.mahdiparastesh.smsexporter.Fun.Companion.filter
import com.mahdiparastesh.smsexporter.Fun.Companion.night
import com.mahdiparastesh.smsexporter.Fun.Companion.vish
import com.mahdiparastesh.smsexporter.adap.SmsAdap
import com.mahdiparastesh.smsexporter.data.*
import com.mahdiparastesh.smsexporter.databinding.TalkBinding
import java.util.*

class Talk : AppCompatActivity() {
    lateinit var b: TalkBinding
    lateinit var m: Model
    lateinit var thread: SMS.Thread
    lateinit var exporter: ActivityResultLauncher<Intent>
    var contact: Contact? = null
    var dialogue: AlertDialog? = null

    companion object {
        lateinit var handler: Handler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = TalkBinding.inflate(layoutInflater)
        m = ViewModelProvider(this, Model.Factory()).get("Model", Model::class.java)
        setContentView(b.root)
        Fun.init(this)


        // Initialization
        if (m.threads.value == null || m.viewThread.value == null) {
            onBackPressed(); return; }
        SMS.findThreadById(m.threads.value!!, m.viewThread.value!!)
            ?.let { thread = it }
        if (!::thread.isInitialized) {
            onBackPressed(); return; }
        if (m.contacts.value != null)
            contact = Contact.findContactByPhone(m.contacts.value!!, thread.address)
        b.tbTitle.text = if (contact != null) contact!!.name else thread.address
        Collections.sort(thread.list, SMS.Sort())
        b.list.adapter = SmsAdap(thread.list)
        b.list.scrollToPosition(thread.list.size - 1)
        b.list.viewTreeObserver.addOnScrollChangedListener {
            vish(b.tbShadow, b.list.computeVerticalScrollOffset() > 0)
        }

        // Handler
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Work.EXPORTED.ordinal -> Toast.makeText(
                        c, if (msg.obj as Int == 1) R.string.exportDone else R.string.exportFailed,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Toolbar
        b.tbNav.setOnClickListener { onBackPressed() }
        if (night) b.tbAction.colorFilter = filter(R.color.CP)
        b.tbAction.setOnClickListener { howExport() }

        // ActivityResultLaunchers
        exporter = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            dialogue?.cancel()
            when (this.how) {
                Type.HTML -> HtmlExporter(thread, contact, it.data!!.data!!).start()
                Type.PDF -> PdfExporter(thread, contact, it.data!!.data!!).start()
                Type.JSON -> JsonExporter(thread, contact, it.data!!.data!!).start()
            }
        }
    }


    @SuppressLint("InflateParams")
    fun howExport() = AlertDialog.Builder(this).apply {
        if (!night) setIcon(R.drawable.icon_1)
        else setIcon(drawable(R.drawable.icon_1)!!.apply { colorFilter = filter(R.color.CP) })
        setTitle(R.string.export)
        setMessage(R.string.howExport)
        setView(
            (LayoutInflater.from(c).inflate(R.layout.export_methods, null) as LinearLayout)
                .apply { for (i in 0 until childCount) this[i].setOnClickListener { export(i) } })
        dialogue = create()
        dialogue!!.show()
    }

    var how = Type.HTML
    fun export(how: Int) {
        this.how = Type.values()[how]
        exporter.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType(this@Talk.how)
            val name = "Exported ${contact?.name ?: thread.address}.${fileType(this@Talk.how)}"
            putExtra(Intent.EXTRA_TITLE, name)
        })
    }

    fun fileType(how: Type) = when (how) {
        Type.HTML -> "html"
        Type.PDF -> "pdf"
        Type.JSON -> "json"
    }

    fun mimeType(how: Type) = when (how) {
        Type.HTML -> "text/html"
        Type.PDF -> "application/pdf"
        Type.JSON -> "application/json"
    }


    enum class Work { EXPORTED }

    enum class Type { HTML, PDF, JSON }
}
