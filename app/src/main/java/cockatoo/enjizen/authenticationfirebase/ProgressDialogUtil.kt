package th.co.yuphasuk.wanchalerm.findpeoplelost.util


import android.content.Context
import android.graphics.Color
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower

object ProgressDialogUtil{
    private var dialog: ACProgressFlower? = null

     fun show(context: Context, messageLoading: String) {


         if(dialog == null){
             dialog = ACProgressFlower.Builder(context)
                     .direction(ACProgressConstant.DIRECT_ANTI_CLOCKWISE)
                     .themeColor(Color.WHITE)
                     .text(messageLoading)
                     .fadeColor(Color.DKGRAY).build()
         }

         dialog!!.show()


    }

     fun dismiss() {
        if (dialog == null) {
            return
        }

        dialog!!.dismiss()
    }

}