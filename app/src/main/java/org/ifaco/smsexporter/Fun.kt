package org.ifaco.smsexporter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.Exception
import java.util.*

@Suppress("unused")
class Fun {
    companion object {
        lateinit var c: Context
        val calendar = CalendarType.SOLAR_HIJRI


        fun init(that: AppCompatActivity) {
            c = that.applicationContext
        }

        fun vis(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.GONE
        }

        fun vish(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.INVISIBLE
        }

        fun color(res: Int) = ContextCompat.getColor(c, res)

        fun calendar(unix: Long): Calendar = Calendar.getInstance().apply { timeInMillis = unix }

        fun replaceAll(s: String, ol: String, nw: String): String {
            if (ol == nw) return s
            var ss = s
            try {
                while (ss.contains(ol)) ss = ss.replace(ol, nw)
            } catch (ignored: Exception) {
                return s
            }
            return ss
        }

        fun checkPerm(perm: String) =
            ActivityCompat.checkSelfPermission(c, perm) == PackageManager.PERMISSION_GRANTED

        fun bmpRound(bmp: Bitmap): Bitmap =
            Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888).apply {
                var canvas = Canvas(this)
                canvas.drawRoundRect(
                    RectF(Rect(0, 0, bmp.width, bmp.height)),
                    bmp.width / 2f, bmp.height / 2f,
                    Paint().apply { flags = Paint.ANTI_ALIAS_FLAG })
                var paintImage =
                    Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP) }
                canvas.drawBitmap(bmp, 0f, 0f, paintImage)
            }

        fun loadAvatar(uri: Uri, iv: ImageView) = Glide.with(c).asBitmap().load(uri).into(
            object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    iv.setImageResource(R.drawable.default_contact_1)
                }

                override fun onResourceReady(res: Bitmap, trans: Transition<in Bitmap>?) {
                    iv.setImageBitmap(bmpRound(res))
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    iv.setImageResource(R.drawable.default_contact_1)
                }
            })
    }


    enum class CalendarType { GREGORIAN, SOLAR_HIJRI }
}
