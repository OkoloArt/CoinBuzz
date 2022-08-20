package com.example.cointract.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cointract.R
import com.example.cointract.databinding.FragmentSettingsBinding
import com.example.cointract.datastore.SettingsManager
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsManager by inject<SettingsManager>()

    private var dayNightMode = false
    private var launchScreen = ""
    private var biometric = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateData()
        binding.apply {
            settingsFragment = this@SettingsFragment
        }
        setBiometricSettings()
        handleLaunchScreenAction()

    }


    fun textPopUp() {
        showMenu()
    }

    private fun showMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.launch)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener { item ->
            lifecycleScope.launch {
                launchScreen = item.title.toString()
                settingsManager.storeUserLaunchScreen(launchScreen, requireContext())
                binding.launch.text = "$launchScreen >"
            }
            true
        }
        popupMenu.show()
    }

    fun setDayNightMode() {
        dayNightMode = !dayNightMode
        if (dayNightMode) {
            binding.modeImage.setImageResource(R.drawable.ic_dark_mode)
            lifecycleScope.launch {
                settingsManager.storeUserDayNightTheme(dayNightMode, requireContext())
            }
        } else {
            binding.modeImage.setImageResource(R.drawable.ic_light_mode)
            lifecycleScope.launch {
                settingsManager.storeUserDayNightTheme(dayNightMode, requireContext())
            }
        }
        ProcessPhoenix.triggerRebirth(activity?.applicationContext);
//        findNavController().navigate(R.id.action_nav_settings_to_splashFragment)
    }

    private fun setBiometricSettings() {
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    settingsManager.storeUserBiometricSettings(isChecked, requireContext())
                }
            } else {
                lifecycleScope.launch {
                    settingsManager.storeUserBiometricSettings(isChecked, requireContext())
                }
            }
        }
    }

    private fun updateData() {

        // Updates Launch Screen selection
        // every time user changes it, it will be observed by preferenceLaunchScreenFlow
        settingsManager.preferenceLaunchScreenFlow.asLiveData().observe(viewLifecycleOwner) {
            launchScreen = it
            binding.launch.text = "$launchScreen >"
        }

        // Updates DayNight selection
        // every time user changes it, it will be observed by preferenceDayNightFlow
        settingsManager.preferenceDayNightFlow.asLiveData().observe(viewLifecycleOwner) {
            dayNightMode = it
            if (dayNightMode) {
                binding.modeImage.setImageResource(R.drawable.ic_dark_mode)
            } else {
                binding.modeImage.setImageResource(R.drawable.ic_light_mode)
            }
        }

        // Updates Biometric selection
        // every time user changes it, it will be observed by preferenceBiometricFlow
        settingsManager.preferenceBiometricFlow.asLiveData().observe(viewLifecycleOwner) {
            biometric = it
            binding.biometricSwitch.isChecked=biometric
        }

    }

    private fun handleLaunchScreenAction() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (launchScreen) {
                        "Markets" -> {
                            val action = SettingsFragmentDirections.actionNavSettingsToNavHome()
                            findNavController().navigate(action)
                        }
                        "News" -> {
                            val action =SettingsFragmentDirections.actionNavSettingsToNewsFragment2()
                            findNavController().navigate(action)
                        }
                    }
                }
            })
    }

}