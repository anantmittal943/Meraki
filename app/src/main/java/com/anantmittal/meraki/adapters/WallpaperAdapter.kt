package com.anantmittal.meraki.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.R
import com.anantmittal.meraki.api.api_data_modals.WallpaperDataItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class WallpaperAdapter(
    private val context: Context,
    private val wallpaperList: List<WallpaperDataItem>,
    private val onPhotoClick: (WallpaperDataItem) -> Unit
) :
    RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    private val requestOptions = RequestOptions()
        .placeholder(R.drawable.image)
        .error(R.drawable.image)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val inflater = LayoutInflater.from(context).inflate(R.layout.wallpaper_layout,parent,false)
        return WallpaperViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallPos = wallpaperList[position]
        Glide.with(holder.img)
            .load(wallPos.urls.small)
            .apply(requestOptions)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onPhotoClick(wallpaperList[adapterPosition])
            }
        }

    }

    override fun getItemCount(): Int {
        return wallpaperList.size
    }

    class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.wall1)
    }

}