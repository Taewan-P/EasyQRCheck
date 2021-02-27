package page.chungjungsoo.easyqrcheck.activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import page.chungjungsoo.easyqrcheck.R
import page.chungjungsoo.easyqrcheck.database.MyCookieDatabaseHelper


class MainActivity : AppCompatActivity() {
    var cookieDBHandler : MyCookieDatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_frame, PreferenceFragment())
            .commit()
        cookieDBHandler = MyCookieDatabaseHelper(this)

        val cookies : String = cookieDBHandler!!.getCookies()

        if (cookies != "") {
            loadLoginLayout()
            Log.d("LOAD SUCCESSFUL", "SUCCESSFULLY LOADED EXISTING COOKIES")
        }
        else {
            loadLogoutLayout()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_frame, PreferenceFragment())
            .commit()

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
                loadLogoutLayout()
            }
            val tmp: WebView = WebView(this)
            tmp.clearCache(true)
        }

        testBtn.setOnClickListener{
            val intent = Intent(this, PopupActivity::class.java)
            startActivity(intent)
        }

        addHomeBtn.setOnClickListener {
            val intent = Intent(this, PopupActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.addFlags(FLAG_ACTIVITY_NO_HISTORY)
            intent.action = "QR_SHORTCUT"
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            val shortcutInfo = ShortcutInfo.Builder(this, "QR Check")
                .setIcon(Icon.createWithResource(this, R.mipmap.shortcut))
                .setShortLabel("QR Check")
                .setIntent(intent)
                .build()

            shortcutManager.requestPinShortcut(shortcutInfo, null)
            step3_layout.visibility = View.VISIBLE
            step4_layout.visibility = View.VISIBLE
        }

        close_btn.setOnClickListener {
            finishAndRemoveTask()
        }

    }
    private fun loadLoginLayout() {
        loginBtn.visibility = View.GONE
        logoutBtn.visibility = View.VISIBLE
        step2_layout.visibility = View.VISIBLE
        step3_layout.visibility = View.VISIBLE
        step4_layout.visibility = View.VISIBLE
    }

    private fun loadLogoutLayout() {
        loginBtn.visibility = View.VISIBLE
        logoutBtn.visibility = View.GONE
        step2_layout.visibility = View.INVISIBLE
        step3_layout.visibility = View.INVISIBLE
        step4_layout.visibility = View.INVISIBLE
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
                    var success = true
                    for (name in cookieArr) {
                        try {
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
                        } catch (e: StringIndexOutOfBoundsException) {
                            Log.e("STRING ERROR", "STRING INDEX OUT OF BOUND")
                            success = false
                        }
                    }
                    if (success) {
                        val added = cookieDBHandler!!.addCookies(jkl, aut, ses)
                        if (added) {
                            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                            Log.d("ADD SUCCESSFUL", "SUCCESSFULLY ADDED COOKIES TO THE DATABASE.")
                            loginBtn.visibility = View.GONE
                            logoutBtn.visibility = View.VISIBLE
                            step2_layout.visibility = View.VISIBLE
                        } else {
                            Log.e("ADDING ERROR", "ERROR ADDING COOKIES TO DB")
                            Toast.makeText(applicationContext, "로그인 실패!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(applicationContext, "로그인 정보를 받아오는데 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.license -> {
                val opensource = Intent(this, OssLicensesMenuActivity::class.java)
                startActivity(opensource)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}