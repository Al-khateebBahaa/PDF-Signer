package com.bk.signer_tool.Document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bk.signer_tool.R
import com.bk.signer_tool.digital_signer.DigitalSignatureActivity


class PDSFragment : Fragment() {
    var mPageViewer: PDSPageViewer? = null
    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val inflate = layoutInflater.inflate(R.layout.com_bk_signer_fragment_layout, viewGroup, false)
        val linearLayout = inflate.findViewById<View>(R.id.fragment) as LinearLayout
        try {
            val fASPageViewer = PDSPageViewer(
                requireContext(),
                requireActivity() as DigitalSignatureActivity?,
                (requireActivity() as DigitalSignatureActivity?)?.document?.getPage(
                    requireArguments().getInt("pageNum")
                )
            )
            mPageViewer = fASPageViewer
            linearLayout.addView(fASPageViewer as View)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inflate
    }

    override fun onDestroyView() {
        if (mPageViewer != null) {
            mPageViewer?.cancelRendering()
            mPageViewer = null
        }
        super.onDestroyView()
    }

    companion object {
        fun newInstance(i: Int): PDSFragment {
            val fASFragment = PDSFragment()
            val bundle = Bundle()
            bundle.putInt("pageNum", i)
            fASFragment.arguments = bundle
            return fASFragment
        }
    }
}