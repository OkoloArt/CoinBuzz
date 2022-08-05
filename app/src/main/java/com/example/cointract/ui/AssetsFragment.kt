package com.example.cointract.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.R
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.databinding.FragmentAssetsBinding
import com.example.cointract.model.AssetList
import com.example.cointract.model.AssetSingle
import com.example.cointract.model.AssetsList
import com.example.cointract.model.SingleAsset
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance.retrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 * Use the [AssetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssetsFragment : Fragment() {

    private var _binding: FragmentAssetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AssetListAdapter
    var assetsResultList = mutableListOf<AssetList>(
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAssetsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveAssetListJson()
        retrieveAssetSingleJson(BITCOIN)
        retrieveAssetSingleJson(ETHEREUM)
    }

    private fun retrieveAssetListJson() {
        val assetCall: Call<AssetsList?> = retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getAssetList()
        assetCall.enqueue(object : Callback<AssetsList?> {
            override fun onResponse(call: Call<AssetsList?>, response: Response<AssetsList?>) {
                if (response.isSuccessful && response.body()?.data != null) {

                    assetsResultList.clear()
                    assetsResultList = response.body()?.data as MutableList<AssetList>

                    adapter = AssetListAdapter()
                    adapter.submitList(assetsResultList)
                    binding.assetsListRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.assetsListRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<AssetsList?>, t: Throwable) {
            }
        })
    }

    private fun retrieveAssetSingleJson(assetId: String) {
        val assetCall: Call<AssetSingle?> = retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getAssetSingle(assetId)
        assetCall.enqueue(object : Callback<AssetSingle?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<AssetSingle?>, response: Response<AssetSingle?>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    when (assetId) {
                        "bitcoin" -> {
                            binding.leftAssetIcon.setImageResource(R.drawable.bitcoin)
                            binding.leftAssetName.text = response.body()!!.data.assetName
                            binding.leftAssetSymbol.text = response.body()!!.data.assetSymbol
                            binding.leftAssetChange24Hr.text =
                                "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            binding.leftAssetPriceUsd.text =
                                roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                        }
                        "ethereum" -> {
                            binding.rightAssetName.text = response.body()!!.data.assetName
                            binding.rightAssetSymbol.text = response.body()!!.data.assetSymbol
                            binding.rightAssetChange24Hr.text =  "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            binding.rightAssetPriceUsd.text = roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                            binding.rightAssetIcon.setImageResource(R.drawable.ethereum)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AssetSingle?>, t: Throwable) {
            }
        })
    }

    private fun roundOffPriceUsd(num: String): String {
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        return pattern.format(number)
    }

    private fun roundOffChange24Hr(num: String): Double {
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        return pattern.format(number).toDouble()
    }

    companion object {
        private const val BITCOIN = "bitcoin"
        private const val ETHEREUM = "ethereum"
    }
}