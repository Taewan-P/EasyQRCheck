package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.CookieManager
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
import java.util.*

class PopupActivity : Activity() {
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
        scope.launch(Dispatchers.Default) {
            val (request, response, result) = Fuel.get(url)
                .appendHeader(Headers.COOKIE to cookieArr[0], Headers.COOKIE to cookieArr[1], Headers.COOKIE to cookieArr[2])
                .responseString()

            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    println(ex)
                }
                is Result.Success -> {
                    val doc: Document = Jsoup.parse(result.get())
                    val src = doc.getElementById("qrImage").attr("src")
                    val base64val = src.split(", ")[1]
                    val decoded = Base64.getDecoder().decode(base64val)
                    val decodedByte : Bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                    runOnUiThread {
                        loading.visibility = View.GONE
                        qrImageView.visibility = View.VISIBLE
                        qrImageView.setImageBitmap(decodedByte)
                    }
                }
            }
        }

        okBtn.setOnClickListener {
            finish()
        }

    }
}