package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import page.chungjungsoo.easyqr.R
import page.chungjungsoo.easyqr.database.MyCookieDatabaseHelper


class MainActivity : AppCompatActivity() {
    var cookieDBHandler : MyCookieDatabaseHelper? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cookieDBHandler = MyCookieDatabaseHelper(this)

        val cookies : String = cookieDBHandler!!.getCookies()

        if (cookies != "") {
            loginBtn.visibility = View.GONE
            step2_layout.visibility = View.VISIBLE
            step3_layout.visibility = View.VISIBLE
            Log.d("LOAD SUCCESSFUL", "SUCCESSFULLY LOADED EXISTING COOKIES")
        }
        else {
            logoutBtn.visibility = View.GONE
            step2_layout.visibility = View.INVISIBLE
            step3_layout.visibility = View.INVISIBLE
        }



        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("LOGIN_TYPE", "L")
            startActivityForResult(intent, 101)
        }

        logoutBtn.setOnClickListener {
            val del = cookieDBHandler!!.deleteCookies()
            if (del) {
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                Log.d("DELETE SUCCESSFUL", "SUCCESSFULLY DELETED COOKIES")
                loginBtn.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                step2_layout.visibility = View.INVISIBLE
                step3_layout.visibility = View.INVISIBLE
            }
            val tmp: WebView = WebView(this)
            tmp.clearCache(true)
//            val service: ActivityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
//            service.clearApplicationUserData()
        }

        testBtn.setOnClickListener{
            val intent = Intent(this, PopupActivity::class.java)
            startActivity(intent)
        }

        addHomeBtn.setOnClickListener {
            val intent = Intent(this, PopupActivity::class.java)
            intent.action = "QR_SHORTCUT"
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            val shortcutInfo = ShortcutInfo.Builder(this, "QR Check")
                .setIcon(Icon.createWithResource(this, R.drawable.qr_vector))
                .setShortLabel("QR Check")
                .setIntent(intent)
                .build()

            shortcutManager.requestPinShortcut(shortcutInfo, null)
            step3_layout.visibility = View.VISIBLE
        }

        close_btn.setOnClickListener {
            finishAndRemoveTask()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                101 -> {
                    val cookie = data!!.getStringExtra("cookie")?.toString()
                    val cookieArr = cookie!!.split("; ").toList()
                    var jkl = ""
                    var aut = ""
                    var ses = ""
                    for (name in cookieArr) {
                        when (name.substring(0..6)) {
                            "NID_JKL" -> {
                                jkl = name.substring(8 until name.length)
                            }
                            "NID_AUT" -> {
                                aut = name.substring(8 until name.length)
                            }
                            "NID_SES" -> {
                                ses = name.substring(8 until name.length)
                            }
                        }
                    }

                    val added = cookieDBHandler!!.addCookies(jkl, aut, ses)
                    if (added) {
                        Toast.makeText(applicationContext, "Logged in successfully.", Toast.LENGTH_SHORT).show()
                        Log.d("ADD SUCCESSFUL", "SUCCESSFULLY ADDED COOKIES TO THE DATABASE.")
                        loginBtn.visibility = View.GONE
                        logoutBtn.visibility = View.VISIBLE
                        step2_layout.visibility = View.VISIBLE
                    }
                    else {
                        Log.e("ADDING ERROR", "ERROR ADDING COOKIES TO DB")
                    }
                }
            }
        }
    }
}