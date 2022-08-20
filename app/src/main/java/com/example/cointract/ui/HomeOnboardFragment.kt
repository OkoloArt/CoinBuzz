package com.example.cointract.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.cointract.R
import com.example.cointract.adapter.OnBoardPagerAdapter
import com.example.cointract.databinding.FragmentHomeOnboardBinding
import com.example.cointract.datastore.SettingsManager
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

        pagerAdapter = OnBoardPagerAdapter(requireActivity(), requireContext())

        binding.viewPager.adapter = pagerAdapter
        binding.indicatorView.apply {
            setSliderWidth(resources.getDimension(R.dimen.dp_10))
            setSliderHeight(resources.getDimension(R.dimen.dp_5))
            setSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setupWithViewPager(binding.viewPager)
        }
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.autoScroll(5500)
        binding.btnGotToNextScreen.setOnClickListener {
            lifecycleScope.launch {
                settingsManager.storeUserIsFirstTimeLaunch(false, requireContext())
            }
            val action = HomeOnboardFragmentDirections.actionHomeOnboardFragmentToNavHome()
            findNavController().navigate(action)
        }

        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
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
        lifecycleScope.launch {
            settingsManager.storeUserIsFirstTimeLaunch(false, requireContext())
        }
    }

    private fun ViewPager2.autoScroll(interval: Long) {

        val handler = Handler(Looper.getMainLooper())
        var scrollPosition = 0

        val runnable = object : Runnable {
            override fun run() {

                /**
                 * Calculate "scroll position" with
                 * adapter pages count and current
                 * value of scrollPosition.
                 */
                val count = pagerAdapter?.itemCount ?: 0
                setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, interval)
            }
        }

        binding.viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Updating "scroll position" when user scrolls manually
                scrollPosition = position + 1
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })

        handler.post(runnable)
    }
}
