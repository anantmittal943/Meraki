package com.anantmittal.meraki.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.anantmittal.meraki.adapters.ImageAdapter
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.FragmentUploadsBinding
import com.anantmittal.meraki.data_modals.OwnerData
import com.anantmittal.meraki.data_modals.refUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Uploads : Fragment() {

    private lateinit var binding: FragmentUploadsBinding

    private val imageUris: MutableList<Uri> = mutableListOf()
    private val ownerNames = mutableListOf<String>()
    private val profileImages = mutableListOf<String>()
    private var ownerUsername: String = ""
    private var profileImagesUrl: String = "https://i.pinimg.com/736x/c0/74/9b/c0749b7cc401421662ae901ec8f9f660.jpg"
    private val list = mutableListOf<OwnerData>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUploadsBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(refUrl)

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            databaseReference =
                database.getReference("users").child(currentUser.uid).child("uploads")
            fetchWallpapersFromDatabase()
        }

        setupRecyclerView()

        val imagePicker =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
                if (!uris.isNullOrEmpty()) {
                    for (uri in uris) {
                        list.add(OwnerData(uri, "ownerUsername", "profileImagesUrl"))
                        updateWallpaperToDatabase(uri)
                    }
                    imageAdapter.notifyDataSetChanged()
                }
            }

        binding.btnUpload.setOnClickListener {
            imagePicker.launch("image/*")
        }

        return binding.root
    }

    private fun fetchWallpapersFromDatabase() {
        val uid = databaseReference.parent
        uid?.child("user_creds")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstName = snapshot.child("firstName").getValue(String::class.java)
                    val lastName = snapshot.child("lastName").getValue(String::class.java)
                    ownerUsername = "$firstName $lastName"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "failed to get name", Toast.LENGTH_SHORT).show()
            }
        })
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("TAG", "onDataChange: ")
                list.clear()
//                ownerNames.clear()
//                profileImages.clear()
                for (data in snapshot.children) {
                    val imageUrl = data.getValue(String::class.java)
                    if (imageUrl != null) {
                        list.add(OwnerData(Uri.parse(imageUrl), ownerUsername, profileImagesUrl))
                        /*imageUris.add(Uri.parse(imageUrl))
                        ownerNames.add(ownerUsername)
                        profileImages.add(profileImagesUrl)*/
                    }
                }
                Log.d("MyTag", "onDataChange: ${imageUris.toString()}")
                Log.d("MyTag", "onDataChange: ${ownerNames.toString()}")
                Log.d("MyTag", "onDataChange: ${profileImages.toString()}")
                imageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(), "failed to fetch wallpapers :(", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateWallpaperToDatabase(uri: Uri) {

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val wallpaperUploadKey = databaseReference.push().key
            if (wallpaperUploadKey != null) {
                databaseReference.child(wallpaperUploadKey).setValue(uri.toString())
            }
        }
    }

    private fun setupRecyclerView() {

        imageAdapter = ImageAdapter(list) {data ->
            val bundle = Bundle().apply {
                putSerializable("data", data)
                /*putString("ownerUsername", data.ownerName)
                putString("ownerProfileUrl", data.profileImage)*/
            }
            findNavController().navigate(R.id.action_uploads2_to_setWallpaper, bundle)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = imageAdapter
        }
    }

}