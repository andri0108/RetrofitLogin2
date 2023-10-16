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
import com.bcaf.inovative.data.api.request.User2

class RecyclerViewAdapter(private val dataItems: List<DataItem>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.arraylist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItems[position]

        holder.titleTextView.text = item.judulPost
        holder.descriptionTextView.text = item.deskripsi
        holder.emailTextView.text = item.user.name
        holder.tglTextView.text = item.tanggalPost
        holder.idTextView.text = item.idPost.toString()
        holder.likeTextView.text = item.upvote.toString()
        holder.likeCountTextView.text = item.likeCount.toString()

        // Handle Like button click
        holder.likeButton.isEnabled = !item.isLiked
        holder.imgkomen.setOnClickListener {
            // If you want to navigate to ReplyFragment, replace with appropriate fragment class
            val replyFragment = ReplyActivity()
            val bundle = Bundle()
            bundle.putSerializable("postItem", item)
            bundle.putInt("postId", item.idPost ?: -1)
            bundle.putString("deskripsi", item.deskripsi ?:"")


            replyFragment.arguments = bundle
            // You need to use the activity's FragmentManager to initiate the fragment transaction
            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager

            // Start a fragment transaction to replace the current fragment with ReplyFragment
            fragmentManager.beginTransaction()
                .replace(R.id.frmFragmentRoot, replyFragment)
                .addToBackStack(null)  // Optional: Add to back stack if needed
                .commit()
        }

        // Handle Like button click
        holder.likeButton.setOnClickListener {
            if (item.isLiked) {
                // If already liked, decrement the like count and set isLiked to false
                item.likeCount--
                item.isLiked = false
            } else {
                // If not liked, increment the like count and set isLiked to true
                item.likeCount++
                item.isLiked = true
            }


            holder.likeCountTextView.text = item.likeCount.toString()

        }

    }
    override fun getItemCount(): Int {
        return dataItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtJudulPost)
        val descriptionTextView: TextView = itemView.findViewById(R.id.txtDeskripsi)
        val emailTextView: TextView = itemView.findViewById(R.id.txtNama)
        val idTextView: TextView = itemView.findViewById(R.id.txtId)
        val tglTextView: TextView = itemView.findViewById(R.id.txtTanggalPost)
        val likeTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
        val likeButton: ImageView = itemView.findViewById(R.id.likeIcon)
        val imgkomen: ImageView = itemView.findViewById(R.id.imgkomen)
        val likeCountTextView: TextView = itemView.findViewById(R.id.likeCountTextView)
    }
}
