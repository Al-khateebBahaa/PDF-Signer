package com.bk.signer_tool.demo.Adapter

import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bk.signer_tool.demo.R
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainRecycleViewAdapter(
    private val items: ArrayList<File> = arrayListOf(),
    private val mOnItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val selected_items: SparseBooleanArray = SparseBooleanArray()
    private var current_selected_idx = -1


     fun addNewFile(item: File) {
        items.add(item)
        notifyItemInserted(items.size - 1)

    }


    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView
        var name: TextView
        var brief: TextView
        var size: TextView
        var lyt_parent: View

        init {
            image = v.findViewById(R.id.fileImageView)
            name = v.findViewById(R.id.fileItemTextview)
            brief = v.findViewById(R.id.dateItemTimeTextView)
            size = v.findViewById(R.id.sizeItemTimeTextView)
            lyt_parent = v.findViewById(R.id.listItemLinearLayout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.com_bk_signer_mainitemgrid, parent, false)
        vh = OriginalViewHolder(v)
        return vh
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = items[position]
        if (holder is OriginalViewHolder) {
            val view = holder
            view.name.text = obj.name
            val lastModDate = Date(obj.lastModified())
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
            val strDate = formatter.format(lastModDate)
            view.brief.text = strDate
            view.size.text = GetSize(obj.length())
            view.lyt_parent.setOnClickListener { v ->
                mOnItemClickListener.onItemClick(v, obj, position)
            }
            toggleCheckedIcon(holder, position)
            view.image.setImageResource(R.drawable.ic_adobe)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun toggleCheckedIcon(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder as OriginalViewHolder
        if (selected_items[position, false]) {
            view.lyt_parent.setBackgroundColor(Color.parseColor("#4A32740A"))
            if (current_selected_idx == position) resetCurrentIndex()
        } else {
            view.lyt_parent.setBackgroundColor(Color.parseColor("#ffffff"))
            if (current_selected_idx == position) resetCurrentIndex()
        }
    }

    fun GetSize(size: Long): String {
        val dictionary = arrayOf("bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
        var index = 0
        var m = size.toDouble()
        val dec = DecimalFormat("0.00")
        index = 0
        while (index < dictionary.size) {
            if (m < 1024) {
                break
            }
            m = m / 1024
            index++
        }
        return dec.format(m) + " " + dictionary[index]
    }

    private fun resetCurrentIndex() {
        current_selected_idx = -1
    }


    interface OnItemClickListener {
        fun onItemClick(view: View?, value: File, position: Int)
    }
}