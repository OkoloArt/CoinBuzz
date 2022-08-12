package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.cointract.R
import com.example.cointract.adapter.HomePagerAdapter
import com.example.cointract.databinding.FragmentHomeBinding
import com.example.cointract.datastore.SettingsManager
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess

class HomeFragment : Fragment() {

    private val categoryArray = listOf(
        "Cryptoassets",
        "Exchanges"
    )
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: HomePagerAdapter
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        pagerAdapter = HomePagerAdapter(requireActivity())
        settingsManager = SettingsManager(requireContext())
        binding.apply {
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            //tabs.setSelectedTabIndicator(null)
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = categoryArray[position]
        }.attach()

        handleOnBackPressed()
        observeData()
    }

    private fun handleOnBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   activity!!.finish()
                    exitProcess(0)
                }
            })
    }

    private fun observeData() {

        // Updates LauchScreen selection
        // every time user changes it, it will be observed by preferenceLaunchScreenFlow
//        settingsManager.preferenceLaunchScreenFlow.asLiveData().observe(viewLifecycleOwner) {
//          Toast.makeText(requireContext(),"screen: $it",Toast.LENGTH_SHORT).show()
//        }

        // Updates DayNight selection
        // every time user changes it, it will be observed by preferenceDayNightFlow
        settingsManager.preferenceDayNightFlow.asLiveData().observe(viewLifecycleOwner) {

        }
    }
}