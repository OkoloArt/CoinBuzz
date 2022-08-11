package com.example.cointract.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.cointract.R
import com.example.cointract.databinding.FragmentSettingsBinding
import com.example.cointract.datastore.SettingsManager
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsManager: SettingsManager

    private var dayNightMode = false
    private var biometricClicked = false

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

        settingsManager = SettingsManager(requireContext())
        binding.apply {
            settingsFragment = this@SettingsFragment
        }

        // Updates DayNight selection
        // every time user changes it, it will be observed by preferenceDayNightFlow
        settingsManager.preferenceDayNightFlow.asLiveData().observe(viewLifecycleOwner) {
            dayNightMode = it
        }

        // Updates DayNight selection
        // every time user changes it, it will be observed by preferenceDayNightFlow
        settingsManager.preferenceBiometricFlow.asLiveData().observe(viewLifecycleOwner) {
            binding.biometricSwitch.isChecked = it
        }

        setBiometricSettings()
    }


    fun textPopUp() {
        showMenu()
    }

    private fun showMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.launch)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener { item ->
            Toast.makeText(requireContext(), " Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                settingsManager.storeUserLaunchScreen(item.title.toString(), requireContext())
            }
            true
        }
        popupMenu.show()
    }

    fun setDayNightMode() {
        dayNightMode = !dayNightMode
        if (dayNightMode) {
            Toast.makeText(requireContext(), " Clicked: $dayNightMode", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                settingsManager.storeUserDayNightTheme(dayNightMode, requireContext())
            }
        } else {
            Toast.makeText(requireContext(), " Clicked: $dayNightMode", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                settingsManager.storeUserDayNightTheme(dayNightMode, requireContext())
            }
        }
    }

    private fun setBiometricSettings() {
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), " Clicked: $isChecked", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    settingsManager.storeUserBiometricSettings(isChecked, requireContext())
                }
            } else {
                Toast.makeText(requireContext(), " Clicked: $isChecked", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    settingsManager.storeUserBiometricSettings(isChecked, requireContext())
                }
            }
        }
    }
}