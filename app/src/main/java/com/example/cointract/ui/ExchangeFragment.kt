package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.adapter.ExchangeListAdapter
import com.example.cointract.databinding.FragmentExchangeBinding
import com.example.cointract.model.AssetList
import com.example.cointract.model.AssetsList
import com.example.cointract.model.ExchangeList
import com.example.cointract.model.Exchanges
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [ExchangeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExchangeListAdapter
    var exchangeResultList = mutableListOf<ExchangeList>(
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExchangeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveExchangeListJson()
    }

    private fun retrieveExchangeListJson() {
        val assetCall: Call<Exchanges?> = RetrofitInstance.retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getExchangeList()
        assetCall.enqueue(object : Callback<Exchanges?> {
            override fun onResponse(call: Call<Exchanges?>, response: Response<Exchanges?>) {
                if (response.isSuccessful && response.body()?.data != null) {

                    exchangeResultList.clear()
                    exchangeResultList = response.body()?.data as MutableList<ExchangeList>

                    adapter = ExchangeListAdapter()
                    adapter.submitList(exchangeResultList)
                    binding.exchangeListRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.exchangeListRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Exchanges?>, t: Throwable) {
            }
        })
    }
}