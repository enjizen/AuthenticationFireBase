package cockatoo.enjizen.authenticationfirebase

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import th.co.yuphasuk.wanchalerm.findpeoplelost.util.ProgressDialogUtil

open class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {

        ProgressDialogUtil.show(this,"")
    }

    fun hideProgressDialog() {
        ProgressDialogUtil.dismiss()
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}