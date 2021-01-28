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
import org.ifaco.smsexporter.Fun.Companion.c
import org.ifaco.smsexporter.adap.ThreadAdap
import org.ifaco.smsexporter.databinding.MainBinding

class Main : AppCompatActivity() {
    lateinit var b: MainBinding
    lateinit var model: Model

    companion object {
        lateinit var handler: Handler
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = MainBinding.inflate(layoutInflater)
        model = ViewModelProvider(this, Model.Factory()).get("Model", Model::class.java)
        setContentView(b.root)
        Fun.init(this)


        // Handlers
        handler = object : Handler(Looper.getMainLooper()) {
            @Suppress("UNCHECKED_CAST")
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Work.THREADS.ordinal -> model.threads.value = msg.obj as List<SMS.Thread>?
                    Work.VIEW_THREAD.ordinal -> {
                        model.viewThread.value = msg.obj as String?
                        startActivity(Intent(this@Main, Talk::class.java))
                    }
                }
            }
        }

        // Permissions
        if (ActivityCompat.checkSelfPermission(c, smsPerm) == PackageManager.PERMISSION_GRANTED
        ) Collector(this).start()
        else ActivityCompat.requestPermissions(this, arrayOf(smsPerm), reqSmsPerm)

        // List
        arrangeList()
        model.threads.observe(this, { threads -> arrangeList(threads) })
    }

    val smsPerm = Manifest.permission.READ_SMS
    val reqSmsPerm = 666
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        when (requestCode) {
            reqSmsPerm -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Collector(this).start()
        }
    }


    fun arrangeList(threads: List<SMS.Thread>? = model.threads.value) {
        if (threads != null) b.list.adapter = ThreadAdap(threads)
    }


    enum class Work { THREADS, VIEW_THREAD }
}
