package com.example.cointract.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.cointract.utils.NetworkConnectivityObserver
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject
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
    private val connectivityObserver by inject<NetworkConnectivityObserver>()

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

        if (checkForInternet(requireContext())) {
            binding.viewPager.visibility = View.VISIBLE
            binding.noInternetConnection.visibility = View.INVISIBLE
        } else {
            binding.viewPager.visibility = View.INVISIBLE
            binding.noInternetConnection.visibility = View.VISIBLE
        }

        connectivityObserver.observeNetworkStatus().asLiveData()
            .observe(viewLifecycleOwner) {
                it?.let {
                    if (it.name == "Lost") {
                        binding.viewPager.visibility = View.INVISIBLE
                        binding.noInternetConnection.visibility = View.VISIBLE
                    } else {
                        binding.viewPager.visibility = View.VISIBLE
                        binding.noInternetConnection.visibility = View.INVISIBLE
                    }
                }
            }

        binding.apply {
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            //tabs.setSelectedTabIndicator(null)
        }


        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = categoryArray[position]
        }.attach()

        handleOnBackPressed()
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

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}