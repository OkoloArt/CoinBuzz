package com.example.cointract.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.cointract.R
import com.example.cointract.databinding.FragmentSplashBinding
import com.example.cointract.datastore.SettingsManager
import java.util.concurrent.Executor

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsManager: SettingsManager
    private var dayNightMode = false
    private var biometricMode = false

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsManager = SettingsManager(requireContext())
        binding.apply {
            splashFragment = this@SplashFragment
        }
        biometricAuthentication()
        checkBiometricCapability()
        updateSettingsData()
        binding.nextScreen.setOnClickListener {
            goToNextScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun goToNextScreen() {
//        if (dayNightMode){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        if (biometricMode && isBiometricFeatureAvailable()) {
            // Prompt appears when user chooses Biometric mode .
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo)
        }else{
            Toast.makeText(requireContext(),
                "BiometricMode: ${isBiometricFeatureAvailable()} $biometricMode", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_splashFragment_to_nav_home)
        }
    }

    private fun updateSettingsData() {

        settingsManager.preferenceDayNightFlow.asLiveData().observe(viewLifecycleOwner) {
            dayNightMode = it
        }
        settingsManager.preferenceBiometricFlow.asLiveData().observe(viewLifecycleOwner) {
            biometricMode = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkBiometricCapability(){
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                startActivityForResult(enrollIntent, REQUEST_CODE

                )
            }
        }
    }

    private fun biometricAuthentication(){
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(requireContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_splashFragment_to_nav_home)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

    }

    private fun isBiometricFeatureAvailable() : Boolean{
        val biometricManager = BiometricManager.from(requireContext())
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }
    companion object {
        private const val REQUEST_CODE = 100
    }

}