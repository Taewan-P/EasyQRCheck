package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import page.chungjungsoo.easyqr.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title = "Login to NAVER"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var cookies: String

        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        loginWebView.loadUrl("https://nid.naver.com/nidlogin.login")
        loginWebView.settings.javaScriptEnabled = true
        loginWebView.settings.domStorageEnabled = true
        loginWebView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                if (url.toString() == "https://m.naver.com/" || url.toString() == "https://www.naver.com/") {
                    cookies = cookieManager.getCookie(view?.url).toString()
                    val intent = Intent()
                    intent.putExtra("cookie", cookies)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

        loginWebView.webChromeClient = object: WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage?): Boolean {
                Log.d("WEBVIEW_LOG", cm?.message().toString())
                return true
            }
        }
    }
}