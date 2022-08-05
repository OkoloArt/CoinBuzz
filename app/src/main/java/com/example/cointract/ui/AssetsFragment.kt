package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.databinding.FragmentAssetsBinding
import com.example.cointract.model.AssetResults
import com.example.cointract.model.Assets
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance.retrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [AssetsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssetsFragment : Fragment() {

    private var _binding: FragmentAssetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AssetListAdapter
    var assetsResultList = mutableListOf<AssetResults>(
    )

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
    }

    private fun retrieveAssetListJson() {
        val assetCall: Call<Assets?> = retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getAssetList()
        assetCall.enqueue(object : Callback<Assets?> {
            override fun onResponse(call: Call<Assets?>, response: Response<Assets?>) {
                if (response.isSuccessful && response.body()?.data != null) {

                    assetsResultList.clear()
                    assetsResultList = response.body()?.data as MutableList<AssetResults>

                    adapter = AssetListAdapter()
                    adapter.submitList(assetsResultList)
                    binding.assetsListRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.assetsListRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Assets?>, t: Throwable) {
            }
        })
    }
}