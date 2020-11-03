package page.chungjungsoo.easyqr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var tmp: String? = null
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


//            override fun onPageFinished(view: WebView, url: String) {
//                if (url == "https://m.naver.com" || url == "https://www.naver.com") {
//                    // Login Successful?
//                    if (cookies != null) {
//                        // Received cookie successfully
//                        Log.e("WEBVIEW_RESULT", cookies.toString()
//                        )
//                    }
//                    else {
//                        Log.e("WEBVIEW_RESULT", "NULL NULL")
//                    }
//
//                }
//            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
//                Log.d("REQUEST_HEADERS",request?.requestHeaders.toString())
                if (request != null) {
                    if (request.url.toString() == "https://m.naver.com/" ||  request.url.toString() == "https://www.naver.com/") {
                        tmp = request.requestHeaders.toString()

                    }
                }
                return super.shouldInterceptRequest(view, request)
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