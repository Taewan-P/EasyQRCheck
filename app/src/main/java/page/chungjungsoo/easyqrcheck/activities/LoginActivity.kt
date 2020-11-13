package page.chungjungsoo.easyqrcheck.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import page.chungjungsoo.easyqrcheck.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title = "Login to NAVER"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var cookies: String

        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        loginWebView.settings.javaScriptEnabled = true
        loginWebView.settings.domStorageEnabled = true

        val loginType = intent.getStringExtra("LOGIN_TYPE")

        if (loginType == "L") {
            Toast.makeText(this, "로그인 상태 유지를 꼭 체크해 주세요!", Toast.LENGTH_LONG).show()
            authBtn.visibility = View.GONE
            loginWebView.loadUrl("https://nid.naver.com/nidlogin.login")
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
        }
        else if (loginType == "P") {
            // Phone Verification
            supportActionBar?.title = "NAVER Authentication Page"
            authBtn.visibility = View.VISIBLE
            Toast.makeText(this, "개인정보 제공 동의 및 전화번호 인증을 완료후 인증 완료 버튼을 눌러주세요.", Toast.LENGTH_LONG).show()
            loginWebView.loadUrl("https://nid.naver.com/login/privacyQR")
            loginWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            authBtn.setOnClickListener {
                val intent = Intent(this, PopupActivity::class.java)
                startActivity(intent)
                finishAndRemoveTask()
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