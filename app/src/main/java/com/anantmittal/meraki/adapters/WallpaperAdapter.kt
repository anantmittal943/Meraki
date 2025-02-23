package com.anantmittal.meraki.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.R
import com.anantmittal.meraki.WallpaperDataItem
import com.bumptech.glide.Glide

class WallpaperAdapter(
    private val context: Context,
    private val wallpaperList: List<WallpaperDataItem>,
    private val onPhotoClick: (WallpaperDataItem) -> Unit
) :
    RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.wallpaper_layout,parent,false)
        return WallpaperViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallPos = wallpaperList[position]
//        Log.d(TAG, "onBindViewHolder: $position")
        Glide.with(context)
            .load(wallPos.urls.raw)
            .into(holder.img)
//        Log.d(TAG, "onBindViewHolder: ${wallPos.urls.small}")

        holder.itemView.setOnClickListener{
            onPhotoClick(wallPos)
        }

    }

    override fun getItemCount(): Int {
        return wallpaperList.size
    }

    inner class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.wall1)
    }

}