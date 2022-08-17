package com.example.cointract.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.NewsAdapter
import com.example.cointract.databinding.FragmentNewsBinding
import com.example.cointract.model.CoinViewModel
import com.example.cointract.model.NewsList
import com.example.cointract.utils.NetworkConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NewsAdapter
    private var newsListResult = mutableListOf<NewsList>()

    private val connectivityObserver by inject<NetworkConnectivityObserver>()
    private val coinViewModel by sharedViewModel<CoinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkForInternet(requireContext())) {
            binding.loading.visibility=View.VISIBLE
            binding.noInternetConnection.visibility = View.INVISIBLE
        } else {
            binding.loading.visibility=View.INVISIBLE
            binding.newsListRecyclerview.visibility = View.INVISIBLE
            binding.noInternetConnection.visibility = View.VISIBLE
        }

        connectivityObserver.observeNetworkStatus().asLiveData()
            .observe(viewLifecycleOwner) {
                it?.let {
                    if (it.name == "Lost") {
                        binding.loading.visibility=View.INVISIBLE
                        binding.newsListRecyclerview.visibility = View.INVISIBLE
                        binding.noInternetConnection.visibility = View.VISIBLE
                    } else {
                     //   binding.newsListRecyclerview.visibility = View.VISIBLE
                        binding.loading.visibility=View.VISIBLE
                        binding.noInternetConnection.visibility = View.INVISIBLE
                        lifecycleScope.launch(Dispatchers.IO) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                retrieveNewsListJson()
                            }, 6000)
                        }
                    }
                }
            }
    }

    private fun retrieveNewsListJson() {
        if (view != null) {
            coinViewModel.responseNews.observe(viewLifecycleOwner) { news ->
                news?.let {
                    binding.loading.visibility = View.INVISIBLE
                    binding.newsListRecyclerview.visibility = View.VISIBLE
                    newsListResult = news.news as MutableList<NewsList>
                    adapter = NewsAdapter()
                    adapter.submitList(newsListResult)
                    binding.newsListRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.newsListRecyclerview.adapter = adapter
                }
            }
        }
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
