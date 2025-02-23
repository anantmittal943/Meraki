package com.anantmittal.meraki

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.databinding.WallpaperLayoutBinding
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class ImageAdapter(
//    private val imageUris: List<Uri>,
//    private val ownerNames: List<String>,
//    private val profileImages: List<String>,
    private val list: List<OwnerData>,
//    private val onClick: (Uri, String, String) -> Unit
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

//        val ownerName = ownerNames[position]
//        val profileImage = profileImages[position]
        /*val profileImage = if (profileImages.isNotEmpty() && position < profileImages.size) {
            profileImages[position]
        } else {
            ""  // or a default URL/image resource
        }*/
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