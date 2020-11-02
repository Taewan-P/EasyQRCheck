package page.chungjungsoo.easyqr

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import kotlinx.android.synthetic.main.activity_main.*


private val CLIENT_SECRET = BuildConfig.SECRET_KEY


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val CLIENT_ID = getString(R.string.naver_client_id)
        val CLIENT_NAME = getString(R.string.naver_client_name)

        val loginInstance = OAuthLogin.getInstance()
        loginInstance.init(this, CLIENT_ID, CLIENT_SECRET, CLIENT_NAME)
        val loginBtn: OAuthLoginButton = findViewById(R.id.loginBtn)
        loginBtn.setOAuthLoginHandler(mOAuthLoginHandler)

//        var loginHandler = OAuthLoginHandler() {
//            override fun run()
//        }
        logoutBtn.setOnClickListener {
            loginInstance.logout(this)
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }
}