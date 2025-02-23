package com.anantmittal.meraki.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.anantmittal.meraki.ImageAdapter
import com.anantmittal.meraki.OwnerData
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.FragmentFavouritesBinding
import com.anantmittal.meraki.refUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Favourites : Fragment() {

    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageAdapter: ImageAdapter
    private val list = mutableListOf<OwnerData>()
    val TAG = "xyz"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance(refUrl)
        val currentUser = firebaseAuth.currentUser

        setupRecyclerView()

        if (currentUser != null) {
            databaseReference =
                database.getReference("users").child(currentUser.uid).child("favourites")
            fetchWallpapersFromFirebase()
//            Log.d(TAG, "onCreateView: fetch wala funtion called")
        }

        return binding.root
    }

    private fun fetchWallpapersFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (data in snapshot.children) {
                    val imageUrl = data.child("link").getValue(String::class.java)
                    val ownerUsername = data.child("ownerUsername").getValue(String::class.java)
                    val ownerProfileUrl = data.child("ownerProfileUrl").getValue(String::class.java)
                    Log.d(TAG, "onDataChange:$imageUrl $ownerUsername $ownerProfileUrl ")
                    if (imageUrl != null && ownerUsername != null && ownerProfileUrl != null) {
                        list.add(OwnerData(Uri.parse(imageUrl), ownerUsername, ownerProfileUrl))
                    }
                }

                imageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context, "Failed to load images: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupRecyclerView() {
        imageAdapter =
            ImageAdapter(list) { data ->
                val bundle = Bundle().apply {
                    putSerializable("data", data)
                }
                findNavController().navigate(R.id.action_favourites_to_setWallpaper, bundle)
            }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = imageAdapter
        }
    }
}