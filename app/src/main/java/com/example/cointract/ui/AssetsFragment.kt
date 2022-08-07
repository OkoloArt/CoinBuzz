package com.example.cointract.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.R
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.databinding.FragmentAssetsBinding
import com.example.cointract.model.AssetList
import com.example.cointract.model.AssetSingle
import com.example.cointract.model.AssetsList
import com.example.cointract.model.CoinViewModel
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance.retrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

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

    private val coinViewModel: CoinViewModel by activityViewModels()

    private var priceUsd = ""
    private var marketCap = ""
    private var change24H = ""
    private var volume24H = ""

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val symbol = numberFormat.currency?.symbol


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
        retrieveAssetSingleJson(TETHER)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                retrieveAssetListJson()
            }
        }, 0, 5000)
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
                    assetsResultList.subList(0, 3).clear()
                    adapter = AssetListAdapter{
                        coinViewModel.setAssetId(it.assetId)
                        findNavController().navigate(R.id.action_nav_home_to_detailFragment)
                    }
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
                            priceUsd =
                                "Price ${roundOffPriceUsd(response.body()!!.data.assetPriceUsd)}"
                            marketCap =
                                "MCap ${roundOffMCap(response.body()!!.data.assetMCap)}"
                            change24H =
                                "Change24H ${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            volume24H =
                                "Volume24h ${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            setUpTextSwitcher()
                        }
                        "ethereum" -> {
                            binding.leftAssetIcon.setImageResource(R.drawable.ethereum)
                            binding.leftAssetName.text = response.body()!!.data.assetName
                            binding.leftAssetSymbol.text = response.body()!!.data.assetSymbol
                            binding.leftAssetChange24Hr.text =
                                "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            binding.leftAssetPriceUsd.text =
                                roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                        }
                        "tether" -> {
                            binding.rightAssetName.text = response.body()!!.data.assetName
                            binding.rightAssetSymbol.text = response.body()!!.data.assetSymbol
                            binding.rightAssetChange24Hr.text =
                                "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            binding.rightAssetPriceUsd.text =
                                roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                            binding.rightAssetIcon.setImageResource(R.drawable.tether)
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
        return "$symbol${pattern.format(number).toDouble()}"
    }

    private fun roundOffChange24Hr(num: String): Double {
        val number = num.toFloat()
        val pattern = DecimalFormat("###.##")
        return pattern.format(number).toDouble()
    }

    private fun setUpTextSwitcher() {
        val bitcoinData = arrayOf(priceUsd, marketCap, change24H, volume24H)
        var index = 0
        binding.textSwitcher.setFactory {
            val textView = TextView(requireContext())
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.textSize = 20f
            textView.setTextColor(Color.WHITE)
            textView
        }
        val textIn = AnimationUtils.loadAnimation(
            requireContext(), android.R.anim.slide_in_left)

        val textOut = AnimationUtils.loadAnimation(
            requireContext(), android.R.anim.slide_out_right)

        binding.textSwitcher.apply {
            setText(bitcoinData[index])
            inAnimation = textIn
            outAnimation = textOut
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    index = if (index + 1 < bitcoinData.size) index + 1 else 0
                    binding.textSwitcher.setText(bitcoinData[index])
                }
            }
        }, 0, 5000)
    }

    private fun roundOffMCap(num: String): String {
        val number = num.toFloat()
        val pattern = DecimalFormat("###,###,###,###")
        return "$symbol${pattern.format(number)}"
    }

    companion object {
        private const val BITCOIN = "bitcoin"
        private const val ETHEREUM = "ethereum"
        private const val TETHER = "tether"
    }
}