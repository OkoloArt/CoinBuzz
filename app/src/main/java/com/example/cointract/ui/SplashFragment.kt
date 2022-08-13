package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.cointract.R
import com.example.cointract.databinding.FragmentSplashBinding
import com.example.cointract.datastore.SettingsManager

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var settingsManager: SettingsManager
    private var dayNightMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsManager = SettingsManager(requireContext())
        binding.apply {
            splashFragment = this@SplashFragment
        }

        checkDayNightSettings()
        binding.nextScreen.setOnClickListener {
            goToNextScreen()
        }
    }

    private fun goToNextScreen() {
//        if (dayNightMode){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        findNavController().navigate(R.id.action_splashFragment_to_nav_home)
    }

    private fun checkDayNightSettings() {
        settingsManager.preferenceDayNightFlow.asLiveData().observe(viewLifecycleOwner) {
            dayNightMode = it
        }
    }
}