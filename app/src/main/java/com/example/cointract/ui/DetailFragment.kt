package com.example.cointract.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cointract.databinding.FragmentDetailBinding
import com.example.cointract.model.*
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
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

    private val coinViewModel: CoinViewModel by activityViewModels()

    private lateinit var candleStickChart: CandleStickChart
    private var entriesData = mutableListOf<CandleEntry>()
    var candleListResult = mutableListOf<CandlesData>(
    )

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

        candleStickChart = binding.candleData
        coinViewModel.assetId.observe(viewLifecycleOwner) {
            it?.let {
                retrieveAssetSingleJson(it)
                retrieveCandleListJson(EXCHANGE, INTERVAL, it, QUOTEID)
            }
        }
        loadCandleStickChartData()

        setUpCandleStickChart()

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
                        assetMarketCap.text =
                            "$symbol${roundOffLargeNum(response.body()!!.data.assetMCap)}"
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

    private fun retrieveCandleListJson(
        exchange: String,
        interval: String,
        baseId: String,
        quoteId: String,
    ) {
        val assetCall: Call<Candles?> = RetrofitInstance.retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getCandleList(exchange, interval, baseId, quoteId)
        assetCall.enqueue(object : Callback<Candles?> {
            override fun onResponse(call: Call<Candles?>, response: Response<Candles?>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    candleListResult.clear()
                    candleListResult = response.body()!!.data.takeLast(60) as MutableList<CandlesData>

                    for (i in candleListResult.indices) {
                        candleListResult.takeLast(30).reversed()
                        entriesData.add(CandleEntry(
                            i.toFloat(),
                            candleListResult[i].candleHigh.toFloat(),
                            candleListResult[i].candleLow.toFloat(),
                            candleListResult[i].candleOpen.toFloat(),
                            candleListResult[i].candleClose.toFloat(),
                        ))
                        candleStickChart.notifyDataSetChanged()
                        loadCandleStickChartData()
                        setUpCandleStickChart()
                    }
                }
            }

            override fun onFailure(call: Call<Candles?>, t: Throwable) {
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

    private fun setUpCandleStickChart() {

        candleStickChart.apply {
            isHighlightPerDragEnabled = true
            setDrawBorders(false)
            setBackgroundColor(Color.BLACK)
            setBorderColor(Color.GRAY)
            setVisibleXRange(7f,7f)
            description = null
            setMaxVisibleValueCount(50)
            requestDisallowInterceptTouchEvent(true)
        }

        val yAxis = candleStickChart.axisLeft
        val rightAxis = candleStickChart.axisRight
        yAxis.setDrawGridLines(true)
        rightAxis.setDrawGridLines(false)

        val xAxis = candleStickChart.xAxis

        xAxis.setDrawGridLines(false) // disable x axis grid lines

        xAxis.setDrawLabels(false)
        rightAxis.textColor = Color.WHITE
        yAxis.setDrawLabels(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val l = candleStickChart.legend
        l.isEnabled = false
    }

    private fun loadCandleStickChartData() {
        val dummyData: ArrayList<CandleEntry> = ArrayList<CandleEntry>()
        dummyData.add(CandleEntry (0f,0f,0f,0f,0f))

        val dataSet: CandleDataSet = if (entriesData.isEmpty()) {
            CandleDataSet(dummyData, "")
        } else {
            CandleDataSet(entriesData, "")
        }
        dataSet.apply {
            color = Color.rgb(80, 80, 80)
            shadowColor = Color.GRAY
            shadowWidth = 0.8f
            decreasingColor = Color.RED
            decreasingPaintStyle = Paint.Style.FILL
            increasingColor = Color.GREEN
            increasingPaintStyle = Paint.Style.FILL
            neutralColor = Color.LTGRAY
            barSpace=0.4f
            setDrawValues(true)
        }

        // create a data object with the datasets
        val data = CandleData(dataSet)
        // set data
        candleStickChart.data = data
        candleStickChart.invalidate()
    }

    companion object {
        private const val EXCHANGE = "binance"
        private const val INTERVAL = "h8"
        private const val QUOTEID = "tether"
    }
}