package com.example.cointract.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cointract.databinding.MarketListDetailBinding
import com.example.cointract.model.MarketList
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MarketAdapter : ListAdapter<MarketList, MarketAdapter.MarketListViewHolder>(DiffCallback) {

    class MarketListViewHolder(private val binding: MarketListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val symbol = numberFormat.currency?.symbol

        @SuppressLint("SetTextI18n")
        fun bind(marketList: MarketList) {
            binding.marketPair.text = marketList.pair
            binding.marketVolume.text = "vol $symbol${roundOffVUsd(marketList.volume)}\n"
            binding.marketExchange.text = marketList.exchange
            binding.marketPrice.text =
                "$symbol${roundOffPriceUsd(marketList.price)}"
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

        private fun roundOffVUsd(num: String): String {
            if (num.isNullOrEmpty()) return "0.0"
            val number = num.toFloat()
            val pattern = DecimalFormat("###,###,###.##")
            return pattern.format(number)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketListViewHolder {
        return MarketListViewHolder(
            MarketListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: MarketListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MarketList>() {
            override fun areItemsTheSame(oldItem: MarketList, newItem: MarketList): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MarketList, newItem: MarketList): Boolean {
                return oldItem.exchange == newItem.exchange
            }
        }
    }
}