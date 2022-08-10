package com.example.cointract.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import com.example.cointract.R
import com.example.cointract.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


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

        binding.apply {
            settingsFragment = this@SettingsFragment
        }
    }


    fun textPopUp(){
        showMenu()
    }
    private fun showMenu(){
        val popupMenu =  PopupMenu(requireContext(),binding.launch)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.gravity= Gravity.END
        popupMenu.setOnMenuItemClickListener { item ->
            Toast.makeText(requireContext()," Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
            true
        }
        popupMenu.show()
    }
}