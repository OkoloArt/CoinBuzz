package com.example.cointract.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cointract.R
import com.example.cointract.databinding.ExchangeListDetailBinding
import com.example.cointract.model.ExchangeList
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ExchangeListAdapter :
    ListAdapter<ExchangeList, ExchangeListAdapter.ExchangeListViewHolder>(DiffCallback) {

    class ExchangeListViewHolder(private val binding: ExchangeListDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        private val symbol = numberFormat.currency?.symbol

        @SuppressLint("SetTextI18n")
        fun bind(exchangeList: ExchangeList) {
            setChange24HrImage(binding.exchangeIndicator, exchangeList.exchangePercentVolume)
            binding.exchangeName.text = exchangeList.exchangeName
            binding.exchangePriceUsd.text = "$symbol${roundOffVUsd(exchangeList.exchangeVolumeUsd)}"
            binding.exchangeRank.text = exchangeList.exchangeRank
            binding.exchangeVolume.text =
                "${roundOffChange24Hr(exchangeList.exchangePercentVolume)}%"
            binding.exchangePairs.text = "${exchangeList.exchangePairs} Pr"
        }

        private fun roundOffChange24Hr(num: String): Double {
            if (num.isNullOrEmpty()) return 0.0
            val number = num.toFloat()
            val pattern = DecimalFormat("###.##")
            return pattern.format(number).toDouble()
        }

        private fun setChange24HrImage(imageView: ImageView, num: String) {
            when {
                roundOffChange24Hr(num) < 0 -> {
                    imageView.setImageResource(R.mipmap.arrow_down)
                }
                roundOffChange24Hr(num) > 0 -> {
                    imageView.setImageResource(R.mipmap.arrow_up)
                }
                roundOffChange24Hr(num).equals(0.0) -> {
                    imageView.setImageResource(0)
                }
            }
        }

        private fun roundOffVUsd(num: String): String {
            if (num.isNullOrEmpty()) return "0.0"
            val number = num.toFloat()
            val pattern = DecimalFormat("###.##")
            when (pattern.format(number).toDouble()) {
                in 1000001.0..999999999.0 -> {
                    val newNumber = number / 1000000
                    return "${pattern.format(newNumber)}M"
                }
            }
            val newNumber = number / 1000000000
            return "${pattern.format(newNumber)}Bn"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeListViewHolder {
        return ExchangeListViewHolder(
            ExchangeListDetailBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ExchangeListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ExchangeList>() {
            override fun areItemsTheSame(oldItem: ExchangeList, newItem: ExchangeList): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ExchangeList, newItem: ExchangeList): Boolean {
                return oldItem.exchangeName == newItem.exchangeName
            }
        }
    }
}