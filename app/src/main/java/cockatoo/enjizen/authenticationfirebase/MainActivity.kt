package cockatoo.enjizen.authenticationfirebase

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_authen_email.setOnClickListener {
            startActivity(Intent(this@MainActivity, EmailPasswordActivity::class.java)) }

        btn_authen_facebook.setOnClickListener {

            startActivity(Intent(this@MainActivity, FacebookLoginActivity::class.java))
        }


    }
}
