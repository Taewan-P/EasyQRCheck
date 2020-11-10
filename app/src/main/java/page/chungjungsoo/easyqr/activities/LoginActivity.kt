package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import page.chungjungsoo.easyqr.R
import page.chungjungsoo.easyqr.database.MyCookieDatabaseHelper

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
            Toast.makeText(this, "자동로그인을 꼭 체크해 주세요!", Toast.LENGTH_LONG).show()
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
                var cookieDBHandler : MyCookieDatabaseHelper? = null
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    if ("https://nid.naver.com/nidlogin.login" in url.toString()) {
                        Toast.makeText(this@LoginActivity,"로그아웃 되었습니다. 다시 로그인해 주세요.", Toast.LENGTH_LONG).show()
                        val del = cookieDBHandler!!.deleteCookies()
                        if (del) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        finishAndRemoveTask()

                    }
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