package org.ifaco.smsexporter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.Fun.Companion.drawable
import org.ifaco.smsexporter.Fun.Companion.filter
import org.ifaco.smsexporter.Fun.Companion.night
import org.ifaco.smsexporter.Fun.Companion.vish
import org.ifaco.smsexporter.adap.SmsAdap
import org.ifaco.smsexporter.data.*
import org.ifaco.smsexporter.databinding.TalkBinding
import java.util.*

class Talk : AppCompatActivity() {
    lateinit var b: TalkBinding
    lateinit var m: Model
    lateinit var thread: SMS.Thread
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
                    Work.EXPORTED.ordinal -> {
                        Toast.makeText(c, "${msg.obj}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Toolbar
        b.tbNav.setOnClickListener { onBackPressed() }
        if (night) b.tbAction.colorFilter = filter(R.color.CP)
        b.tbAction.setOnClickListener { howExport() }
    }

    override fun onActivityResult(req: Int, res: Int, intent: Intent?) {
        super.onActivityResult(req, res, intent)
        when (req) {
            reqFolder -> {
                dialogue?.cancel()
                if (res == RESULT_OK && intent != null && intent.data != null)
                    export(where = intent.data!!) else dialogue?.cancel()
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
                .apply { for (i in 0 until childCount) this[i].setOnClickListener { whereExport(i) } })
        dialogue = create()
        dialogue!!.show()
    }

    val reqFolder = 666
    var how = Type.HTML
    fun whereExport(how: Int) {
        this.how = Type.values()[how]
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType(this@Talk.how)
            val name = "Exported ${contact?.name ?: thread.address}.${fileType(this@Talk.how)}"
            putExtra(Intent.EXTRA_TITLE, name)
        }
        startActivityForResult(intent, reqFolder)
    }

    fun export(how: Type = this.how, where: Uri) {
        when (how) {
            Type.HTML -> HtmlExporter(thread, contact, where).start()
            Type.PDF -> PdfExporter(thread, contact, where).start()
            Type.JSON -> JsonExporter(thread, contact, where).start()
        }
    }

    fun fileType(how: Type) = when (how) {
        Type.HTML -> "html"
        Type.PDF -> "pdf"
        Type.JSON -> "json"
    }

    fun mimeType(how: Type) = when (how) {
        Type.HTML -> "text/html"
        Type.PDF -> "application/pdf"
        Type.JSON -> "json"///
    }


    enum class Work { EXPORTED }

    enum class Type { HTML, PDF, JSON }
}
