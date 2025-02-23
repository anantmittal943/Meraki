package com.anantmittal.meraki

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.databinding.WallpaperLayoutBinding
import com.bumptech.glide.Glide

class ImageAdapter(
    private val list: List<OwnerData>,
    private val onClick: (OwnerData) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            WallpaperLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
//        Log.d(TAG, "onBindViewHolder: $imageUris $ownerNames $profileImages")
        val listData = list[position]
//        Log.d(TAG, "onBindViewHolder: $uri $ownerName $profileImage")
        holder.bind(listData.uri, listData.ownerUserName, listData.ownerProfileUrl)
    }

    override fun getItemCount(): Int = list.size

    inner class ImageViewHolder(private val binding: WallpaperLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri, ownerName: String, profileImageUrl: String) {
            Log.d(TAG, "bind: $uri $ownerName $profileImageUrl")
            Glide.with(binding.wall1.context).load(uri).into(binding.wall1)
            binding.wall1.setOnClickListener { onClick(OwnerData(uri, ownerName, profileImageUrl)) }
        }
    }
}