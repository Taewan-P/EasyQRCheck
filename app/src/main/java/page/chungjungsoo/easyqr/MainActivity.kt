package page.chungjungsoo.easyqr

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


private var cookie : String = ""

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logoutBtn.visibility = View.GONE
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, 101)
        }

        logoutBtn.setOnClickListener {
            cookie = ""
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            loginBtn.visibility = View.VISIBLE
            logoutBtn.visibility = View.GONE
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                101 -> {
                    cookie = data!!.getStringExtra("cookie").toString()
                    Toast.makeText(applicationContext, "Logged in successfully.", Toast.LENGTH_SHORT).show()
                    Log.d("Loaded Cookie", cookie)
                    loginBtn.visibility = View.GONE
                    logoutBtn.visibility = View.VISIBLE

                }
            }
        }
    }
}