package com.example.artpaletteversion2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.activity.FullImageActivity
import com.example.artpaletteversion2.model.UrlImageModel

class ExploreAdapter(private val list: ArrayList<UrlImageModel>, private val context: Context): RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>() {
    class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exploreImage: ImageView = itemView.findViewById(R.id.exploreImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_explore, parent, false)
        return ExploreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        Glide.with(context).load(list[position].urls.regular).into(holder.exploreImage)

        holder.exploreImage.setOnClickListener {
            val intent = Intent(context, FullImageActivity::class.java)
            intent.putExtra("image", list[position].urls.regular)
            context.startActivity(intent)
        }
    }
}