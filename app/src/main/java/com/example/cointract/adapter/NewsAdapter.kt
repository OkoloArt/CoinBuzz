package com.example.cointract.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cointract.databinding.MarketListDetailBinding
import com.example.cointract.databinding.NewsListDetailsBinding
import com.example.cointract.model.MarketList
import com.example.cointract.model.NewsList
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class NewsAdapter(private val onItemClicked : (NewsList) -> Unit): ListAdapter<NewsList, NewsAdapter.NewsListViewHolder>(DiffCallback) {

    class NewsListViewHolder(private val binding: NewsListDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(newsList: NewsList) {
            binding.newsTitle.text = newsList.news_title
            binding.newsSource.text = newsList.news_source
            Picasso.get().load(newsList.news_image).into(binding.newsImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        return NewsListViewHolder(
            NewsListDetailsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<NewsList>() {
            override fun areItemsTheSame(oldItem: NewsList, newItem: NewsList): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: NewsList, newItem: NewsList): Boolean {
                return oldItem.news_title == newItem.news_title
            }
        }
    }
}