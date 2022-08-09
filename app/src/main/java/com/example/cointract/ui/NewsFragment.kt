package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.NewsAdapter
import com.example.cointract.databinding.FragmentNewsBinding
import com.example.cointract.model.News
import com.example.cointract.model.NewsList
import com.example.cointract.network.CoinApiInterface
import com.example.cointract.network.CoinStatsRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NewsAdapter
  var newsListResult = mutableListOf<NewsList>()

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

        retrieveNewsListJson("0","10")
    }

    private fun retrieveNewsListJson(skip:String,limit:String) {
        val assetCall: Call<News?> = CoinStatsRetrofitInstance.coinStatsRetrofitInstance!!.create(
            CoinApiInterface::class.java
        ).getNewsList(skip, limit)
        assetCall.enqueue(object : Callback<News?> {
            override fun onResponse(call: Call<News?>, response: Response<News?>) {
                if (response.isSuccessful && response.body()?.news != null) {

                    newsListResult.clear()
                    newsListResult = response.body()?.news as MutableList<NewsList>
                    adapter = NewsAdapter()
                    adapter.submitList(newsListResult)
                    binding.newsListRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.newsListRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<News?>, t: Throwable) {
            }
        })
    }
}