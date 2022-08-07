package com.example.cointract.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cointract.databinding.FragmentDetailBinding
import com.example.cointract.model.AssetSingle
import com.example.cointract.model.CoinViewModel
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val symbol = numberFormat.currency?.symbol

    private val coinViewModel : CoinViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinViewModel.assetId.observe(viewLifecycleOwner){
            it?.let {
                retrieveAssetSingleJson(it)
            }
        }
    }

    private fun retrieveAssetSingleJson(assetId: String) {
        val assetCall: Call<AssetSingle?> = RetrofitInstance.retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getAssetSingle(assetId)
        assetCall.enqueue(object : Callback<AssetSingle?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<AssetSingle?>, response: Response<AssetSingle?>) {
                if (response.isSuccessful && response.body()?.data != null) {

                    binding.apply {
                        assetName.text = response.body()!!.data.assetName
                        assetPriceUsd.text =
                            roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                        assetChange24hr.text =
                            "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                        assetMarketCap.text = "$symbol${roundOffLargeNum(response.body()!!.data.assetMCap)}"
                        assetCirculatingSupply.text =
                            roundOffLargeNum(response.body()!!.data.assetCirculatingSupply)
                        assetVolume24h.text =
                            "$symbol${roundOffLargeNum(response.body()!!.data.assetVolumeUsd24Hr)}"
                        assetStatisticsChange24hr.text =
                            "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                        assetMaxSupply.text =
                            roundOffLargeNum(response.body()!!.data.assetMaxSupply)
                        assetRank.text = "#${response.body()!!.data.assetRank}"
                    }
                }
            }

            override fun onFailure(call: Call<AssetSingle?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun roundOffPriceUsd(num: String): String {
        if (num.isNullOrEmpty()) return "0.0"
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        return "$symbol${pattern.format(number).toDouble()}"
    }

    private fun roundOffChange24Hr(num: String): Double {
        if (num.isNullOrEmpty()) return 0.0
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        return pattern.format(number).toDouble()
    }

    private fun roundOffLargeNum(num: String): String {
        if (num.isNullOrEmpty()) return "0.0"
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        when (pattern.format(number).toLong()) {
            in 1000001..999999999 -> {
                val newNumber = number / 1000000
                return "${pattern.format(newNumber)}Mn"
            }
        }
        val newNumber = number / 1000000000
        return "${pattern.format(newNumber)}Bn"
    }

}