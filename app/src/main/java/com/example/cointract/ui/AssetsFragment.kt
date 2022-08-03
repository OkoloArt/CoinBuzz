package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cointract.databinding.FragmentAssetsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AssetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssetsFragment : Fragment() {

    private var _binding: FragmentAssetsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAssetsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}