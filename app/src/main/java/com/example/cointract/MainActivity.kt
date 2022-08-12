package com.example.cointract

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cointract.databinding.ActivityMainBinding
import com.example.cointract.datastore.SettingsManager
import com.example.cointract.ui.HomeFragmentDirections
import com.example.cointract.ui.NewsFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val pickImage = 100

    private var imageUri: Uri? = null
    private lateinit var profileImage: ImageView
    private lateinit var displayName: TextView

    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        settingsManager = SettingsManager(this)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            if (menuItem.itemId == R.id.nav_settings) {
                navController.navigate(R.id.action_nav_home_to_nav_settings)
            }
            drawerLayout.close()
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        //doSomethingHere()
                        supportActionBar?.show()
                        binding.appBarMain.bottomNavigation.visibility = View.VISIBLE
                        binding.appBarMain.bottomNavigation.selectedItemId
                        supportActionBar?.title = null
                    }, 0)
                }
                R.id.newsFragment -> {
                    binding.appBarMain.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarMain.bottomNavigation.selectedItemId
                }
                else -> {
                    supportActionBar?.hide()
                    binding.appBarMain.bottomNavigation.visibility = View.GONE
                }
            }
        }

        binding.appBarMain.bottomNavigation.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.home -> {
                    // Respond to navigation item 1 click
                    navController.safeNavigate(NewsFragmentDirections.actionNewsFragmentToNavHome())
                }
                R.id.news -> {
                    // Respond to navigation item 2 click
                    navController.safeNavigate(HomeFragmentDirections.actionNavHomeToNewsFragment())
                }
            }
            true
        }

        val header = navView.getHeaderView(0)
        profileImage = header.findViewById(R.id.profile_image)
        profileImage.setOnClickListener {
            setProfileImage()
        }

        displayName = header.findViewById(R.id.display_name)
        displayName.setOnClickListener {
            showDialog()
        }

        settingsManager.preferenceProfileImageFlow.asLiveData().observe(this) {
            if (it.equals("")) {
                profileImage.setImageResource(R.drawable.ic_indicator_person)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_indicator_person)
            } else {
                profileImage.setImageURI(Uri.parse(it))
            }
        }

        settingsManager.preferenceDisplayNameFlow.asLiveData().observe(this) {
            displayName.text = it
        }


    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setProfileImage() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "Permission failed", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?,
                ) {
                    TODO("Not yet implemented")
                }

            }).check()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            lifecycleScope.launch {
                settingsManager.storeUserProfileImage(imageUri.toString(), this@MainActivity)
            }
            profileImage.setImageURI(imageUri)
        }
    }

    private fun showDialog() {
        val inflater = this.layoutInflater;
        val view = inflater.inflate(R.layout.display_dialog_layout, null)
        val inputLayout = view.findViewById<TextInputLayout>(R.id.outlinedTextField)
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setView(view)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                // Respond to negative button press
                val input = inputLayout.editText?.text.toString()
                saveDisplayName(input)
            }
            .show()
    }

    private fun saveDisplayName(displayName: String) {
        if (displayName.isNotBlank()) {
            // Launch a coroutine and write the layout setting in the preference Datastore
            lifecycleScope.launch {
                settingsManager.storeUserDisplayName(
                    displayName,
                    this@MainActivity
                )
            }
        }
    }
}