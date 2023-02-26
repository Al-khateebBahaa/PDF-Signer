package com.bk.signer_tool.imageviewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bk.signer_tool.Document.PDSFragment
import com.bk.signer_tool.PDF.PDSPDFDocument


class PDSPageAdapter constructor(
    fragmentManager: FragmentManager,
    private val mDocument: PDSPDFDocument
) : FragmentStatePagerAdapter(
    fragmentManager
) {
    override fun getCount(): Int {
        return mDocument.numPages
    }

    override fun getItem(i: Int): Fragment {
        return PDSFragment.newInstance(i)
    }
}