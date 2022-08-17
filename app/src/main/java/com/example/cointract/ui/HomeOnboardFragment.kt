package com.example.cointract.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cointract.R
import com.example.cointract.adapter.OnBoardPagerAdapter
import com.example.cointract.databinding.FragmentHomeOnboardBinding
import com.example.cointract.datastore.SettingsManager
import com.google.android.material.tabs.TabLayoutMediator
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 * Use the [HomeOnboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeOnboardFragment : Fragment() {

    private var _binding: FragmentHomeOnboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: OnBoardPagerAdapter
    private val settingsManager by inject<SettingsManager>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeOnboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = OnBoardPagerAdapter(requireActivity(),requireContext())

         binding.viewPager.adapter = pagerAdapter
        binding.indicatorView.apply {
            setSliderWidth(resources.getDimension(R.dimen.dp_10))
            setSliderHeight(resources.getDimension(R.dimen.dp_5))
            setSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setupWithViewPager(binding.viewPager)
        }
        binding.viewPager.offscreenPageLimit = 1
        binding.btnGotToNextScreen.setOnClickListener {
            lifecycleScope.launch{
                settingsManager.storeUserIsFirstTimeLaunch(false,requireContext())
            }
            val action = HomeOnboardFragmentDirections.actionHomeOnboardFragmentToNavHome()
            findNavController().navigate(action)
        }

        handleOnBackPressed()
    }

    private fun handleOnBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.finish()
                    exitProcess(0)
                }
            })
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch{
            settingsManager.storeUserIsFirstTimeLaunch(false,requireContext())
        }
    }
}