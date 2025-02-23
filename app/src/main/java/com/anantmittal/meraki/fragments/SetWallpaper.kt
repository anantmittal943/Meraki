package com.anantmittal.meraki.fragments

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.anantmittal.meraki.OwnerData
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.FragmentSetWallpaperBinding
import com.anantmittal.meraki.refUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SetWallpaper : Fragment() {

    private lateinit var binding: FragmentSetWallpaperBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var downDatabaseReference: DatabaseReference
    private lateinit var favDatabaseReference: DatabaseReference
    /*private var photoUrl: String? = null
    private var ownerUsername: String? = null
    private var ownerProfileUrl: String? = null*/
    val TAG = "xyz"
    private lateinit var ownerData: OwnerData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetWallpaperBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(refUrl)


        ownerData = arguments?.getSerializable("data") as OwnerData
//        ownerUsername = arguments?.getString("ownerUsername")
//        val ownerName = arguments?.getString("ownerName", "Unknown")
//        ownerProfileUrl = arguments?.getString("ownerProfileUrl")

        binding.ownerName.text = ownerData.ownerUserName

        ownerData.ownerProfileUrl?.let {
            Glide.with(requireContext()).load(it).placeholder(R.drawable.profile)
                .error(R.drawable.profile).into(binding.ownerProfilePicture)
        }

        ownerData.uri?.let {
            Glide.with(requireContext()).load(it).into(binding.photoPreview)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.photoPreview.setOnClickListener {
            showBottomSheet()
        }

        binding.setAsButton.setOnClickListener {
            showBottomSheet()
        }

        binding.shareButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Please wait while your wallpaper is being shared",
                Toast.LENGTH_SHORT
            ).show()
            shareWallpaper()
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            favDatabaseReference =
                database.getReference("users").child(currentUser.uid).child("favourites")
        }

        var isFav = false
        var favKey: String? = null
        favDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    if (ownerData.uri.toString() == data.child("link").getValue(String::class.java)) {
                        isFav = data.child("isFav").getValue(Boolean::class.java) ?: false
                        favKey = data.key
                        break
                    }
                }
                if (isFav) {
                    binding.favButton.setImageResource(R.drawable.favorite)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: $error")
            }
        })

        binding.favButton.setOnClickListener {
            isFav = !isFav
            if (isFav) {
                binding.favButton.setImageResource(R.drawable.favorite)
                Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
                if (favKey == null) {
                    favKey = favDatabaseReference.push().key
                }
                favKey?.let {
                    favDatabaseReference.child(it).child("link").setValue(ownerData.uri.toString())
                    favDatabaseReference.child(it).child("ownerUsername").setValue(ownerData.ownerUserName)
                    favDatabaseReference.child(it).child("ownerProfileUrl").setValue(ownerData.ownerProfileUrl)
                    favDatabaseReference.child(it).child("isFav").setValue(isFav)
                }
            } else {
                Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT)
                    .show()
                favKey?.let {
                    favDatabaseReference.child(it).removeValue()
                }
                binding.favButton.setImageResource(R.drawable.favorite_border)
            }
        }

        return binding.root
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val homeOption = bottomSheetView.findViewById<TextView>(R.id.home_option)
        val lockOption = bottomSheetView.findViewById<TextView>(R.id.lock_option)
        val bothOption = bottomSheetView.findViewById<TextView>(R.id.both_option)
        val downloadsOption = bottomSheetView.findViewById<TextView>(R.id.download_option)

        homeOption.setOnClickListener {
            setWallpaper(WallpaperManager.FLAG_SYSTEM)
            bottomSheetDialog.dismiss()
        }
        lockOption.setOnClickListener {
            setWallpaper(WallpaperManager.FLAG_LOCK)
            bottomSheetDialog.dismiss()
        }
        bothOption.setOnClickListener {
            setWallpaper(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
            bottomSheetDialog.dismiss()
        }
        downloadsOption.setOnClickListener {
            downloadWallpaper()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun downloadWallpaper() {
        ownerData.uri.toString()?.let {
            Glide.with(requireContext()).asBitmap().load(it).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        val fileName = "Wallpaper_${System.currentTimeMillis()}.png"
                        val albumName = "Meraki"
                        val picturesDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val directory = File(picturesDir, albumName)
                        if (!directory.exists()) directory.mkdirs()
                        val file = File(directory, fileName)
                        val outputStream = FileOutputStream(file)
                        resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        Toast.makeText(
                            requireContext(),
                            "Wallpaper downloaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, file.absolutePath)

                        MediaScannerConnection.scanFile(
                            requireContext(), arrayOf(file.absolutePath), arrayOf("image/png")
                        ) { path, _ ->
                            Log.d(TAG, "File scanned $path")
                        }
                    } catch (e: IOException) {
                        Toast.makeText(
                            requireContext(),
                            "error downloading wallpaper ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                downDatabaseReference =
                    database.getReference("users").child(currentUser.uid).child("downloads")
            }
            val wallpaperDownloadKey = downDatabaseReference.push().key
            if (wallpaperDownloadKey != null) {
                downDatabaseReference.child(wallpaperDownloadKey).child("link").setValue(it)
                downDatabaseReference.child(wallpaperDownloadKey).child("ownerUsername").setValue(ownerData.ownerUserName)
                downDatabaseReference.child(wallpaperDownloadKey).child("ownerProfileUrl").setValue(ownerData.ownerProfileUrl)
            }
        }
    }

    private fun setWallpaper(flag: Int) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            ownerData.uri.toString()?.let {
                Glide.with(requireContext()).asBitmap().load(ownerData.uri.toString())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap, transition: Transition<in Bitmap>?
                        ) {
                            wallpaperManager.setBitmap(resource, null, true, flag)
                            // TODO: put loader
                            Toast.makeText(
                                requireContext(), "Wallpaper Set Successfully", Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), "Error Setting Wallpaper : ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareWallpaper() {
        ownerData.uri.toString()?.let {
            Glide.with(requireContext()).asBitmap().load(it).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        val file = File(requireContext().cacheDir, "shared_image.png")
                        val outputStream = FileOutputStream(file)
                        resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()

                        val fileUri: Uri = FileProvider.getUriForFile(
                            requireContext(), "${requireContext().packageName}.provider", file
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, fileUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        startActivity(Intent.createChooser(shareIntent, "Share Wallpaper"))
                    } catch (e: IOException) {
                        Toast.makeText(
                            requireContext(),
                            "Error sharing image: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })
        }
    }

}