package com.example.cointract.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.ExchangeListAdapter
import com.example.cointract.databinding.FragmentExchangeBinding
import com.example.cointract.model.CoinViewModel
import com.example.cointract.model.ExchangeList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ExchangeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExchangeListAdapter
    var exchangeResultList = mutableListOf<ExchangeList>(
    )

    private val coinViewModel by sharedViewModel<CoinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExchangeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                Handler(Looper.getMainLooper()).postDelayed({
                    retrieveExchangeListJson()
                }, 6000)
            }
        }
    }

    private fun retrieveExchangeListJson() {
        coinViewModel.responseExchange.observe(viewLifecycleOwner) { exchanges ->
            exchanges?.let {
                binding.loading.visibility = View.INVISIBLE
                binding.exchangeListRecyclerview.visibility = View.VISIBLE
                exchangeResultList = exchanges.data as MutableList<ExchangeList>
                adapter = ExchangeListAdapter()
                adapter.submitList(exchangeResultList)
                binding.exchangeListRecyclerview.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
                binding.exchangeListRecyclerview.adapter = adapter
            }
        }
    }
}