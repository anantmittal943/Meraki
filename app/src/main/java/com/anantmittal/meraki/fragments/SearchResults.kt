package com.anantmittal.meraki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anantmittal.meraki.R
import com.anantmittal.meraki.databinding.FragmentSearchResultsBinding

class SearchResults : Fragment() {

    private lateinit var binding: FragmentSearchResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

}