package com.bk.signer_tool.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bk.signer_tool.databinding.ComBkSignerSignatureItemBinding
import com.bk.signer_tool.utils.PDSSignatureUtils
import java.io.File


class SignatureRecycleViewAdapter(
    private val signatures: ArrayList<File> = arrayListOf(),
    private val onClickListener: OnItemClickListener
) : RecyclerView.Adapter<SignatureRecycleViewAdapter.MyViewHolder>() {


    fun submitFiles(signatures: List<File>) {
        this.signatures.addAll(signatures)
        notifyDataSetChanged()
    }

    fun addNewFile(newSignature: File) {
        signatures.add(newSignature)
        notifyItemInserted(signatures.size - 1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            ComBkSignerSignatureItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, pos: Int) {

        myViewHolder.bindViews(pos)

    }

    override fun getItemCount(): Int {
        return signatures.size
    }

    fun removeItem(obj: File): Boolean {
        val positionOfObj = signatures.indexOf(obj)
        signatures.remove(obj)
        notifyItemRemoved(positionOfObj)

        return signatures.isEmpty()
    }

    inner class MyViewHolder(val itemViewAdapter: ComBkSignerSignatureItemBinding) :
        RecyclerView.ViewHolder(itemViewAdapter.root) {


        fun bindViews(position: Int) {

            val signatureView = PDSSignatureUtils.showFreeHandView(
                itemViewAdapter.root.context,
                signatures[position]
            )
            itemViewAdapter.root.addView(signatureView)

            itemViewAdapter.root.setOnClickListener { v ->

                onClickListener.onItemClick(v, signatures[position], position)
            }

            signatureView!!.setOnClickListener { v ->
                onClickListener.onItemClick(v, signatures[position], position)
            }

            itemViewAdapter.deleteSignature.setOnClickListener { v ->
                onClickListener.onDeleteItemClick(
                    v,
                    signatures[position],
                    position
                )
            }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: File, pos: Int)
        fun onDeleteItemClick(view: View?, obj: File, pos: Int)
    }
}