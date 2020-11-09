package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.webkit.CookieManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Lifecycle
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_popup.*
import page.chungjungsoo.easyqr.R
import page.chungjungsoo.easyqr.database.MyCookieDatabaseHelper
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.ByteArrayInputStream
import java.net.UnknownHostException
import java.util.*
import kotlin.concurrent.timer

class PopupActivity : Activity() {
    private var time: Int = 15
    private var pressed: Boolean = false
    var cookieDBHandler : MyCookieDatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_popup)
        super.onCreate(savedInstanceState)
        val scope = CoroutineScope(Dispatchers.Main)
        cookieDBHandler = MyCookieDatabaseHelper(this)

        val cookies : String = cookieDBHandler!!.getCookies()
        val cookieArr = cookies.split("; ").toMutableList()

        val cm: CookieManager = CookieManager.getInstance()
        cm.setCookie("https://naver.com", cookies)

        val url = "https://nid.naver.com/login/privacyQR"

        qrImageView.visibility = View.INVISIBLE
        timeText.visibility = View.INVISIBLE
        if (cookies != "") {
            scope.launch(Dispatchers.Default) {
                val (request, response, result) = Fuel.get(url)
                    .appendHeader(
                        Headers.COOKIE to cookieArr[0],
                        Headers.COOKIE to cookieArr[1],
                        Headers.COOKIE to cookieArr[2]
                    )
                    .responseString()

                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        if (ex.exception is UnknownHostException) {
                            runOnUiThread {
                                Toast.makeText(
                                    baseContext,
                                    "오류! 인터넷에 연결되어있지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finishAndRemoveTask()
                            }
                        }
                        println(ex.exception)
                    }
                    is Result.Success -> {
                        val doc: Document = Jsoup.parse(result.get())
                        try {
                            val src = doc.getElementById("qrImage").attr("src")
                            val base64val = src.split(", ")[1]
                            val decoded = Base64.getDecoder().decode(base64val)
                            val decodedByte: Bitmap =
                                BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            runOnUiThread {
                                loading.visibility = View.GONE
                                qrImageView.visibility = View.VISIBLE
                                qrImageView.setImageBitmap(decodedByte)
                                timeText.visibility = View.VISIBLE
                                startTimer()
                            }

                        } catch (e: NullPointerException) {
                            runOnUiThread {
                                Toast.makeText(
                                    baseContext,
                                    "개인정보 제공 동의 및 전화번호 인증이 필요합니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(baseContext, LoginActivity::class.java)
                                intent.putExtra("LOGIN_TYPE", "P")
                                startActivity(intent)
                                finishAndRemoveTask()
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "로그인을 먼저 해주세요.", Toast.LENGTH_LONG).show()
            finishAndRemoveTask()
        }

        okBtn.setOnClickListener {
            pressed = true
            finishAndRemoveTask()
        }

    }
    private fun startTimer() {
        time = 15
        val timerTask = timer(period = 1000) {
            time--
            if (time == 0) {
                this.cancel()
                if (isAppInForeground(this@PopupActivity)) {
                    runOnUiThread {
                        finish()
                        overridePendingTransition(0,0)
                        startActivity(intent)
                        overridePendingTransition(0,0)

                    }
                }
                else {
                    finishAndRemoveTask()
                }
            }
            else {
                runOnUiThread {
                    timeText.text = "남은시간 : $time 초"
                }
            }
        }
    }
    private fun isAppInForeground(ctx: Context): Boolean {
        val activityManager = ctx.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services : List<ActivityManager.AppTask> = activityManager.appTasks
        if (services.isEmpty()) return false
        if(services.isNotEmpty() && "displayId=-1" !in services[0].taskInfo.toString() && services[0].taskInfo.baseIntent.component.toString() == "page.chungjungsoo.easyqr/.activities.PopupActivity") {
            return true
        }
        return false
    }
}