package com.example.cointract.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cointract.databinding.AssetsListDetailBinding
import com.example.cointract.model.AssetResults
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class AssetListAdapter :
    ListAdapter<AssetResults, AssetListAdapter.AssetListViewHolder>(DiffCallback) {

    class AssetListViewHolder(private val binding: AssetsListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val symbol = numberFormat.currency?.symbol

        fun bind(assetResults: AssetResults) {
            binding.assetName.text = assetResults.assetName
            binding.assetPriceUsd.text = "$symbol${roundOff(assetResults.assetPriceUsd)}"
            binding.assetRank.text = assetResults.assetRank
            binding.assetChange24hr.text = roundOff(assetResults.assetChange24Hr)
            binding.assetMarketCap.text = "$symbol${roundOffMCap(assetResults.assetMCap)}"
        }

        private fun roundOff(num: String): String {
            val number = num.toDouble()
            return ((number * 100.0).roundToInt() / 100.0).toString()
        }

        private fun roundOffMCap(num: String): String {
            val number = num.toDouble()
            return ((number * 100.0).roundToInt()/10000000.0).toString()
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
        private val DiffCallback = object : DiffUtil.ItemCallback<AssetResults>() {
            override fun areItemsTheSame(oldItem: AssetResults, newItem: AssetResults): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: AssetResults, newItem: AssetResults): Boolean {
                return oldItem.assetName == newItem.assetName
            }
        }
    }
}