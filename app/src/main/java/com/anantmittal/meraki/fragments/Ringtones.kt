package com.anantmittal.meraki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anantmittal.meraki.databinding.FragmentRingtonesBinding

class Ringtones : Fragment() {

    private lateinit var binding: FragmentRingtonesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRingtonesBinding.inflate(inflater, container, false)
        return binding.root
    }
}

