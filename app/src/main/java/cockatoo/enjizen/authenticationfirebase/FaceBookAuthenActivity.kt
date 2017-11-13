package cockatoo.enjizen.authenticationfirebase

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_face_book_authen.*

class FaceBookAuthenActivity : BaseActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(this)

        setContentView(R.layout.activity_face_book_authen)

        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if(user != null){
                Log.d("facebook","onAuthStateChanged:signed_in: ${user.uid}" )
            }
            else{
                Log.d("facebook","onAuthStateChanged:signed_out")
            }

            updateUI(user)
        }

        mCallbackManager = CallbackManager.Factory.create()
        button_facebook_login.setReadPermissions("email","public_profile")
        button_facebook_login.registerCallback(mCallbackManager,object : FacebookCallback<LoginResult>{

            override fun onSuccess(result: LoginResult?) {
                Log.d("facebook","facebook:onSuccess: $result")
                handleFacebookAccessToken(result!!.accessToken)

            }

            override fun onCancel() {
                Log.d("facebook","facebook:onCancel")
                updateUI(null)
            }
            override fun onError(error: FacebookException?) {
                Log.d("facebook","facebook:onError")
                updateUI(null)
            }



        })
    }

    private fun handleFacebookAccessToken(token: AccessToken){
        Log.d("facebook","handleFacebookAccessTokent: $token")

        showProgressDialog()

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this@FaceBookAuthenActivity){task ->
            Log.d("facebook","signInWithCredential:onComplete: ${task.isSuccessful}")
            if(!task.isSuccessful){
                profile.setTextColor(Color.RED)
                profile.text = task.exception?.message
            }
            else{
                profile.setTextColor(Color.DKGRAY)
            }

            hideProgressDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: FirebaseUser?){
        if(user != null){
            if(user.photoUrl != null){

            }

            profile.text = "Display Name: ${user.displayName}"
            profile.append("\n\n")
            profile.append("Email: ${user.email}")
            profile.append("\n\n")
            profile.append("Firebase ID: ${user.uid}")

            button_facebook_login.visibility = View.GONE
            button_facebook_signout.visibility = View.VISIBLE

        }

        else{
            logo.layoutParams.width = (resources.displayMetrics.widthPixels / 100) * 64
            logo.setImageResource(R.mipmap.authentication)
            profile.text = null

            button_facebook_login.visibility = View.VISIBLE
            button_facebook_signout.visibility = View.GONE
        }

        hideProgressDialog()


    }


    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()

        if(mAuthListener != null){
            mAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }
}
