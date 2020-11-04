package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.webkit.CookieManager
import kotlinx.android.synthetic.main.activity_popup.*
import page.chungjungsoo.easyqr.R
import page.chungjungsoo.easyqr.database.MyCookieDatabaseHelper

class PopupActivity : Activity() {
    var cookieDBHandler : MyCookieDatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_popup)
        super.onCreate(savedInstanceState)

        cookieDBHandler = MyCookieDatabaseHelper(this)

        val cookies : String = cookieDBHandler!!.getCookies()
        qrWebView.settings.javaScriptEnabled = true
        qrWebView.settings.domStorageEnabled = true

        val cm: CookieManager = CookieManager.getInstance()
        cm.setCookie("https://naver.com", cookies)
        qrWebView.loadUrl("https://nid.naver.com/login/privacyQR")

        okBtn.setOnClickListener {
            finish()
        }

    }
}