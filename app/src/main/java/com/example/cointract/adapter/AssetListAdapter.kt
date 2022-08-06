package com.example.cointract.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cointract.R
import com.example.cointract.databinding.AssetsListDetailBinding
import com.example.cointract.model.AssetList
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class AssetListAdapter :
    ListAdapter<AssetList, AssetListAdapter.AssetListViewHolder>(DiffCallback) {

    class AssetListViewHolder(private val binding: AssetsListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val symbol = numberFormat.currency?.symbol

        @SuppressLint("SetTextI18n")
        fun bind(assetList: AssetList) {
            setChange24HrImage(binding.assetIndicator, assetList.assetChange24Hr)
            binding.assetName.text = assetList.assetName
            binding.assetPriceUsd.text = "$symbol${roundOffPriceUsd(assetList.assetPriceUsd)}"
            binding.assetRank.text = assetList.assetRank
            binding.assetChange24hr.text =
                "${roundOffChange24Hr(assetList.assetChange24Hr)}%"
            binding.assetMarketCap.text = "$symbol${roundOffMCap(assetList.assetMCap)}"
        }

        private fun roundOffPriceUsd(num: String): String {
            val number = num.toFloat()
            val pattern = DecimalFormat("###.##")
            val nNoPattern = DecimalFormat("###.######")
            return when {
                number > 0 && number < 1 -> {
                    nNoPattern.format(number)
                }
                else -> pattern.format(number).toDouble().toString()
            }
        }

        private fun roundOffChange24Hr(num: String): Double {
            val number = num.toFloat()
            val pattern = DecimalFormat("###.##")
            return pattern.format(number).toDouble()
        }

        private fun setChange24HrImage(imageView: ImageView, num: String) {
            when {
                roundOffChange24Hr(num) < 0 -> {
                    imageView.setImageResource(R.drawable.arrow_down)
                }
                roundOffChange24Hr(num) > 0 -> {
                    imageView.setImageResource(R.drawable.arrow_up)
                }
            }
        }

        private fun roundOffMCap(num: String): String {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetListViewHolder {
        return AssetListViewHolder(
            AssetsListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: AssetListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    //   override fun getItemCount() = dataSet.size

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AssetList>() {
            override fun areItemsTheSame(oldItem: AssetList, newItem: AssetList): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: AssetList, newItem: AssetList): Boolean {
                return oldItem.assetName == newItem.assetName
            }
        }
    }
}