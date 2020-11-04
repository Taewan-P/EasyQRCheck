package page.chungjungsoo.easyqr.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import page.chungjungsoo.easyqr.R
import page.chungjungsoo.easyqr.database.MyCookieDatabaseHelper


class MainActivity : AppCompatActivity() {
    var cookieDBHandler : MyCookieDatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cookieDBHandler = MyCookieDatabaseHelper(this)

        val cookies : String = cookieDBHandler!!.getCookies()

        if (cookies != "") {
            loginBtn.visibility = View.GONE
            Log.d("LOAD SUCCESSFUL", "SUCCESSFULLY LOADED EXISTING COOKIES")
        }
        else { logoutBtn.visibility = View.GONE }



        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, 101)
        }

        logoutBtn.setOnClickListener {
            val del = cookieDBHandler!!.deleteCookies()
            if (del) {
                Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
                Log.d("DELETE SUCCESSFUL", "SUCCESSFULLY DELETED COOKIES")
                loginBtn.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
            }
        }

        testBtn.setOnClickListener{
            val intent = Intent(this, PopupActivity::class.java)
            startActivity(intent)
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
                        Log.d("TEST","$jkl, $aut, $ses")
                        Toast.makeText(applicationContext, "Logged in successfully.", Toast.LENGTH_SHORT).show()
                        Log.d("ADD SUCCESSFUL", "SUCCESSFULLY ADDED COOKIES TO THE DATABASE.")
                        loginBtn.visibility = View.GONE
                        logoutBtn.visibility = View.VISIBLE
                    }
                    else {
                        Log.e("ADDING ERROR", "ERROR ADDING COOKIES TO DB")
                    }
                }
            }
        }
    }
}