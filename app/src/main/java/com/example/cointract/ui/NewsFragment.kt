package com.example.cointract.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.NewsAdapter
import com.example.cointract.databinding.FragmentNewsBinding
import com.example.cointract.model.CoinViewModel
import com.example.cointract.model.NewsList
import com.example.cointract.network.CoinApiInterface
import com.example.cointract.utils.ConnectivityObserver
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

        lifecycleScope.launch(Dispatchers.IO) {
            Handler(Looper.getMainLooper()).post {
                retrieveNewsListJson()
            }
        }
    }

    private fun retrieveNewsListJson() {
        coinViewModel.responseNews.observe(viewLifecycleOwner) { news ->
            news?.let {
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

        connectivityObserver.observeNetworkStatus().asLiveData()
            .observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), "Status: $it", Toast.LENGTH_SHORT).show()
            }
    }
}
