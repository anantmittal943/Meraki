package com.anantmittal.meraki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anantmittal.meraki.databinding.FragmentAiGeneratorBinding

class AiGenerator : Fragment() {

    private lateinit var binding: FragmentAiGeneratorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAiGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }
}

