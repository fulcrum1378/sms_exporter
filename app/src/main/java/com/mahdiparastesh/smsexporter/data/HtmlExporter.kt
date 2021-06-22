package com.mahdiparastesh.smsexporter.data

import android.net.Uri
import com.mahdiparastesh.smsexporter.Fun
import com.mahdiparastesh.smsexporter.R
import com.mahdiparastesh.smsexporter.etc.SolarHijri
import java.lang.StringBuilder
import java.util.*

class HtmlExporter(thread: SMS.Thread, contact: Contact?, where: Uri) :
    BaseExporter(thread, contact, where) {
    var ink: StringBuilder = StringBuilder()
    val cp = "#00A375"
    val cpv = "#007755"
    val cs = "#EEE"
    val csv = "#DDD"

    override fun run() {
        val css = """
html, body {
    background-color: white;
    margin: 0;
    padding: 0;
    font-family: Roboto, Arial, Sans-serif;
}
#contact {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background-color: $cp;
    color: #FFF;
    padding: 12px 0;
    font-size: 18px;
    text-align: center;
}
#main {
    width: 950px;
    padding: 50px 10px 35px 10px;
}
.message {
    width: 100%;
    padding: 4px 0;
    text-align: left;
}
.message.fromMe {
}
.date {
    color: #777;
    font-size: 12px;
    margin-top: 23px;
    margin-bottom: 4px;
}
.box {
    width: 70%;
    border-radius: 17px;
    background-color: $cs;
    color: #666;
    padding: 8px 16px;
    text-align: right;
    font-size: 16px;
    cursor: pointer;
}
.box:hover {
    background-color: $csv;
}
.box.fromMe {
    background-color: $cp;
    color: #F0F0F0;
    margin-left: calc(30% - 20px);
}
.box.fromMe:hover {
    background-color: $cpv;
}
.time {
    color: #888;
    font-size: 10px;
    text-align: right;
    margin-top: -21px;
}
.message.fromMe .time {
    text-align: left;
}
#copyright {
    color: #777;
    margin-top: 45px;
    font-size: 14px;
    font-style: italic;
}

@media only screen and (max-width: 950px) {
    #main {
        width: calc(100% - 50px);
        padding: 45px 0 25px 0;
    }
}

@font-face {
    font-family: Roboto, sans-serif;
    src: url('https://fonts.googleapis.com/css2?family=Roboto&display=swap');
}
"""
        ink.append(
            """<!DOCTYPE html>
<html>
  <meta charset="utf-8">
  <title>Exported: ${contact?.name ?: thread.address}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="$cp">
  
  <style type="text/css">
$css
  </style>
<head>
</head>

<body>
  <center>
    <div id="contact">
      ${contact?.name ?: thread.address}${if (contact != null) " (${thread.address})" else ""}
    </div>
    <div id="main">
"""
        )
        for (i in thread.list.indices) {
            val m = thread.list[i]
            val calendar = Fun.calendar(thread.list[i].date!!)
            var showDate = true
            if (i > 0) {
                val previous = Fun.calendar(thread.list[i - 1].date!!)
                if (calendar[Calendar.YEAR] == previous[Calendar.YEAR] &&
                    calendar[Calendar.MONTH] == previous[Calendar.MONTH] &&
                    calendar[Calendar.DAY_OF_MONTH] == previous[Calendar.DAY_OF_MONTH]
                ) showDate = false
            }
            if (showDate) {
                var y = calendar[Calendar.YEAR]
                var mo = calendar[Calendar.MONTH]
                var d = calendar[Calendar.DAY_OF_MONTH]
                var mArray = R.array.grMonth
                if (Fun.calendar == Fun.CalendarType.SOLAR_HIJRI) SolarHijri(calendar).apply {
                    y = Y; mo = M; d = D
                    mArray = R.array.shMonth
                }
                val sDate = Fun.c.getString(
                    R.string.smsDate,
                    Fun.c.resources.getStringArray(R.array.week)[calendar[Calendar.DAY_OF_WEEK] - 1],
                    d, Fun.c.resources.getStringArray(mArray)[mo], y
                )
                ink.append(
                    """<center><p class="date">$sDate</p></center>
"""
                )
            }

            var showTime = true
            if (i < thread.list.size - 1) {
                val next = Fun.calendar(thread.list[i + 1].date!!)
                if (calendar[Calendar.HOUR_OF_DAY] == next[Calendar.HOUR_OF_DAY] &&
                    calendar[Calendar.MINUTE] == next[Calendar.MINUTE] &&
                    thread.list[i].fromMe == thread.list[i + 1].fromMe
                ) showTime = false
            }
            val sTime = if (showTime) Fun.c.getString(
                R.string.smsTime, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]
            ) else ""

            ink.append(
                """      <div class="message${if (m.fromMe) " fromMe" else ""}">
        <div class="box${if (m.fromMe) " fromMe" else ""}">
          ${m.text}
        </div>
        <p class="time">$sTime</p>
      </div>
"""
            )
        }
        val me =
            "<a href=\"https://www.linkedin.com/in/mahdi-parastesh-a72ab51b9/\" target=\"_blank\">Mahdi Parastesh</a>"
        ink.append(
            """<p id="copyright">Created by $me</p>
    </div>
  </center>
</body>
</html>"""
        )
        digital = ink.toString().toByteArray()
        super.run()
        done(1)
    }
}
