package com.bcaf.inovative.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.fragment.ReplyActivity
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.GetAllPostId
import com.bcaf.inovative.data.api.request.User2

class RecyclerViewAdapter2(private val dataItem: DataItem) :
    RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.arraylistreply, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItem.listReply?.get(position)

        // Mengambil komentar (comment) dari ListReplyItem pertama (asumsi ada lebih dari 1 reply)


        // Set nama user ke TextView
        holder.namaTextView.text = dataItem.user.name ?: ""
        holder.komenTextView.text = item!!.comment ?: ""
        holder.tglTextView.text = item!!.tanggalReply ?: ""

    }

    override fun getItemCount(): Int {
        return dataItem.listReply!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaTextView: TextView = itemView.findViewById(R.id.txtNama)
        val komenTextView: TextView = itemView.findViewById(R.id.txtKomen)
        val tglTextView: TextView = itemView.findViewById(R.id.txtTgl)


    }
}
