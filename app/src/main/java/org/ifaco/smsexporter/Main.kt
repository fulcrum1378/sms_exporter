package org.ifaco.smsexporter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import org.ifaco.smsexporter.Fun.Companion.night
import org.ifaco.smsexporter.Fun.Companion.vish
import org.ifaco.smsexporter.adap.ThreadAdap
import org.ifaco.smsexporter.data.Collector
import org.ifaco.smsexporter.data.Contact
import org.ifaco.smsexporter.data.SMS
import org.ifaco.smsexporter.databinding.MainBinding
import java.util.*

// adb connect 192.168.1.8:

class Main : AppCompatActivity() {
    lateinit var b: MainBinding
    lateinit var m: Model

    companion object {
        lateinit var handler: Handler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = MainBinding.inflate(layoutInflater)
        m = ViewModelProvider(this, Model.Factory()).get("Model", Model::class.java)
        setContentView(b.root)
        Fun.init(this)

        // Handlers
        handler = object : Handler(Looper.getMainLooper()) {
            @Suppress("UNCHECKED_CAST")
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Work.THREADS.ordinal -> m.threads.value = msg.obj as List<SMS.Thread>?
                    Work.VIEW_THREAD.ordinal -> {
                        m.viewThread.value = msg.obj as String?
                        startActivity(Intent(this@Main, Talk::class.java))
                    }
                    Work.CONTACTS.ordinal -> m.contacts.value = msg.obj as List<Contact>?
                }
            }
        }

        // Permissions
        if (Fun.checkPerm(smsPerm) && Fun.checkPerm(conPerm))
            Collector(this).start()
        else ActivityCompat.requestPermissions(this, arrayOf(smsPerm, conPerm), reqSmsPerm)

        // Toolbar
        if (night) b.tbNav.colorFilter = Fun.filter(R.color.CP)

        // List
        arrangeList()
        m.threads.observe(this, { threads -> arrangeList(threads) })
        m.contacts.observe(this, { contacts -> arrangeList(contacts = contacts) })
        b.list.viewTreeObserver.addOnScrollChangedListener {
            vish(b.tbShadow, b.list.computeVerticalScrollOffset() > 0)
        }
    }

    val smsPerm = Manifest.permission.READ_SMS
    val conPerm = Manifest.permission.READ_CONTACTS
    val reqSmsPerm = 666
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            reqSmsPerm -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Collector(this).start() else onBackPressed()
        }
    }


    fun arrangeList(
        threads: List<SMS.Thread>? = m.threads.value, contacts: List<Contact>? = m.contacts.value
    ) {
        if (threads != null) {
            Collections.sort(threads, SMS.Thread.Sort())
            b.list.adapter = ThreadAdap(threads, contacts)
        }
    }


    enum class Work { THREADS, VIEW_THREAD, CONTACTS }
}
