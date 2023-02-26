package com.bk.signer_tool.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.bk.signer_tool.R
import com.bk.signer_tool.Signature.SignatureUtils
import com.bk.signer_tool.Signature.SignatureView
import java.io.File


@SuppressLint("StaticFieldLeak")
object PDSSignatureUtils {
    private val sSignaturePopUpMenu: PopupWindow? = null
    private var mSignatureLayout: View? = null
    fun showFreeHandView(mCtx: Context, file: File?): SignatureView? {
        val createFreeHandView = SignatureUtils.createFreeHandView(
            mCtx.resources.getDimension(R.dimen.sign_menu_width)
                .toInt() - mCtx.resources.getDimension(R.dimen.sign_left_offset)
                .toInt() - mCtx.resources.getDimension(R.dimen.sign_right_offset).toInt() * 3,
            mCtx.resources.getDimension(R.dimen.sign_button_height)
                .toInt() - mCtx.resources.getDimension(R.dimen.sign_top_offset).toInt(),
            file,
            mCtx
        )
        val layoutParams = RelativeLayout.LayoutParams(-2, -2)
        layoutParams.addRule(9)
        layoutParams.setMargins(
            mCtx.resources.getDimension(R.dimen.sign_left_offset).toInt(),
            mCtx.resources.getDimension(R.dimen.sign_top_offset).toInt(),
            0,
            0
        )
        createFreeHandView!!.layoutParams = layoutParams
        return createFreeHandView

    }

    val isSignatureMenuOpen: Boolean
        get() = sSignaturePopUpMenu != null && sSignaturePopUpMenu.isShowing

    fun dismissSignatureMenu() {
        if (sSignaturePopUpMenu != null && sSignaturePopUpMenu.isShowing) {
            sSignaturePopUpMenu.dismiss()
            mSignatureLayout = null
        }
    }
}