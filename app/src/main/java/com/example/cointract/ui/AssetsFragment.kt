package com.example.cointract.ui

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.R
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.databinding.FragmentAssetsBinding
import com.example.cointract.model.*
import com.example.cointract.network.CoinApiInterface
import com.example.cointract.network.CoinCapRetrofitInstance.coinCapRetrofitInstance
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
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
    private var assetsResultList = mutableListOf<AssetList>(
    )

    private val coinViewModel by sharedViewModel<CoinViewModel>()
    private lateinit var textView: TextView

    private var priceUsd = ""
    private var marketCap = ""
    private var change24H = ""
    private var volume24H = ""

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val symbol = numberFormat.currency?.symbol

    private var bitcoinData = arrayListOf<String>()
    private var index = 0

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

        CoroutineScope(IO).launch() {
            Handler(Looper.getMainLooper()).postDelayed({
                showData()
            }, 6000)

            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    retrieveAssetSingleJson(BITCOIN)
                    retrieveAssetSingleJson(ETHEREUM)
                    retrieveAssetSingleJson(TETHER)
                }
            }, 0, 10000)
        }
    }

    private fun showData() {
        if (view != null) {
            retrieveAssetListJson()
            retrieveAssetSingleJson(BITCOIN)
            retrieveAssetSingleJson(ETHEREUM)
            retrieveAssetSingleJson(TETHER)
        }
    }

    private fun retrieveAssetListJson() {
        coinViewModel.responseAssetList.observe(viewLifecycleOwner) { assetList ->
            assetList?.let {
                binding.loading.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
                adapter = AssetListAdapter {
                    coinViewModel.setAssetId(it.assetId)
                    val action = HomeFragmentDirections.actionNavHomeToDetailFragment()
                    findNavController().safeNavigate(action)
                }

                val toIndex = assetList.data.size
                assetsResultList = assetList.data.subList(3, toIndex) as MutableList<AssetList>
                adapter.submitList(assetsResultList)
                binding.assetsListRecyclerview.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
            //    binding.assetsListRecyclerview.setHasFixedSize(true)
                binding.assetsListRecyclerview.adapter = adapter
            }
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    private fun retrieveAssetSingleJson(assetId: String) {
        val assetCall: Call<AssetSingle?> = coinCapRetrofitInstance!!.create(
            CoinApiInterface::class.java
        ).getAssetSingle(assetId)
        assetCall.enqueue(object : Callback<AssetSingle?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<AssetSingle?>,
                response: Response<AssetSingle?>,
            ) {
                if (response.isSuccessful && response.body()?.data != null) {
                    when (assetId) {
                        BITCOIN -> {
                            priceUsd =
                                "Price  ${roundOffPriceUsd(response.body()!!.data.assetPriceUsd)}"
                            marketCap =
                                "MCap  ${roundOffMCap(response.body()!!.data.assetMCap)}"
                            change24H =
                                "C24H  ${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            volume24H =
                                "V24h  ${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                            setUpTextSwitcher()
                        }
                        ETHEREUM -> {
                            binding.apply {
                                leftAssetIcon.setImageResource(R.mipmap.ethereum)
                                leftAssetName.text = response.body()!!.data.assetName
                                leftAssetSymbol.text = response.body()!!.data.assetSymbol
                                leftAssetChange24Hr.text =
                                    "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                                leftAssetPriceUsd.text =
                                    roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                                leftCardView.setOnClickListener {
                                    coinViewModel.setAssetId(ETHEREUM)
                                    val action= HomeFragmentDirections.actionNavHomeToDetailFragment()
                                    findNavController().navigate(action)
                                }
                            }
                        }
                        TETHER -> {
                            binding.apply {
                                rightAssetName.text = response.body()!!.data.assetName
                                rightAssetSymbol.text = response.body()!!.data.assetSymbol
                                rightAssetChange24Hr.text =
                                    "${roundOffChange24Hr(response.body()!!.data.assetChange24Hr)}%"
                                rightAssetPriceUsd.text =
                                    roundOffPriceUsd(response.body()!!.data.assetPriceUsd)
                                rightAssetIcon.setImageResource(R.mipmap.tether)
                                rightCardView.setOnClickListener {
                                    coinViewModel.setAssetId(TETHER)
                                    val action= HomeFragmentDirections.actionNavHomeToDetailFragment()
                                    findNavController().navigate(action)
                                }
                            }
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
        bitcoinData.clear()
        bitcoinData = arrayListOf(priceUsd, marketCap, change24H, volume24H)
        val context: Context = this.context ?: return
        val typeface = ResourcesCompat.getFont(context, R.font.oswald_regular)

        binding.textSwitcher.removeAllViews()
        binding.textSwitcher.setFactory {
            textView = TextView(context)
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.textSize = 20f
            textView.typeface = typeface
            textView.setTextAppearance(android.R.style.TextAppearance_Material_SearchResult_Subtitle)
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

        index = if (index + 1 < bitcoinData.size) index + 1 else 0
        binding.textSwitcher.setText(bitcoinData[index])

        binding.textSwitcher.setOnClickListener{
            coinViewModel.setAssetId(BITCOIN)
            val action= HomeFragmentDirections.actionNavHomeToDetailFragment()
            findNavController().navigate(action)
        }
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