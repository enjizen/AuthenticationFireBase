package cockatoo.enjizen.authenticationfirebase

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_email_password.*


import java.net.URL

class EmailPasswordActivity : BaseActivity(), View.OnClickListener {


    private val TAG = "EmailPasswordActivity"

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)

        bindWidgets()

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }

            updateUI(user)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (user.photoUrl != null) {
                DownloadImageTask().execute(user.photoUrl.toString())
            }
            profile.text = "DisplayName: ${user.displayName}"
            profile.append("\n\n")
            profile.append("Email: ${user.email}")
            profile.append("\n\n")
            profile.append("Firebase ID: ${user.uid}")
            profile.append("\n\n")
            profile.append("Email Verification: ${user.isEmailVerified}")

            if (user.isEmailVerified) {
                verify_button.visibility = View.GONE
            } else {
                verify_button.visibility = View.VISIBLE
            }

            email_password_buttons.visibility = View.GONE
            email_password_fields.visibility = View.GONE
            signout_zone.visibility = View.VISIBLE
        } else {
            profile.text = null

            email_password_buttons.visibility = View.VISIBLE
            email_password_fields.visibility = View.VISIBLE
            signout_zone.visibility = View.GONE
        }
        hideProgressDialog()
    }

    private fun bindWidgets() {


        email_sign_in_button.setOnClickListener(this)
        email_create_account_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        verify_button.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.email_sign_in_button ->

                signIn(edt_email.text.toString(), edt_password.text.toString())

            R.id.sign_out_button ->

                signOut()

            R.id.email_create_account_button ->

                createAccount(edt_email.text.toString(), edt_password.text.toString())
        }
    }


    private fun validateForm(): Boolean {
        return when {
            TextUtils.isEmpty(edt_email.text.toString()) -> {
                layout_email.error = "Required."
                false
            }
            TextUtils.isEmpty(edt_password.text.toString()) -> {
                layout_password.error = "Required."
                false
            }
            else -> {
                layout_email.error = null
                layout_password?.error = null
                true
            }
        }
    }


    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
            if (!task.isSuccessful) {
                profile?.setTextColor(Color.RED)
                profile?.text = task.exception?.message
            } else {
                profile?.setTextColor(Color.DKGRAY)
            }
            hideProgressDialog()
        }
    }

    private fun signOut() {
        val alert = AlertDialog.Builder(this)
        alert.setMessage(R.string.logout)
        alert.setCancelable(false)
        alert.setPositiveButton(android.R.string.yes) { dialogInterface, i ->
            mAuth!!.signOut()
            updateUI(null)
        }
        alert.setNegativeButton(android.R.string.no) { dialogInterface, i -> dialogInterface.dismiss() }
        alert.show()
    }

    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                profile.setTextColor(Color.RED)
                profile.text = task.exception!!.message
            } else {
                profile.setTextColor(Color.DKGRAY)
            }

            hideProgressDialog()
        }


    }


    private inner class DownloadImageTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            var mIcon: Bitmap? = null
            try {
                val `in` = URL(urls[0]).openStream()
                mIcon = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mIcon
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                profile.layoutParams.width = resources.displayMetrics.widthPixels / 100 * 24
                logo.setImageBitmap(result)
            }
        }
    }
}
