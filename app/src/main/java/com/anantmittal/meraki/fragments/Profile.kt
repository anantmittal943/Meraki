package com.anantmittal.meraki.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.FragmentProfileBinding
import com.anantmittal.meraki.data_modals.refUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    val TAG = "xyz"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.uploads.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_uploads2)
        }
        binding.downloads.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_downloads2)
        }
        binding.favourite.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favourites)
        }
        binding.termsCondition.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_termsAndCondition)
        }
        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_privacyPolicy)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        database =
            FirebaseDatabase.getInstance(refUrl)
        if (currentUser != null) {
            databaseReference = database.getReference("users").child(currentUser.uid).child("user_creds")
        }
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.first.text = snapshot.child("firstName").getValue(String::class.java)
                    binding.last.text = snapshot.child("lastName").getValue(String::class.java)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: $error")
            }
        })
    }

}
