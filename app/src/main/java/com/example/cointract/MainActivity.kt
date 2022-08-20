package com.example.cointract

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val pickImage = 100

    private var imageUri: Uri? = null
    private lateinit var profileImage: ImageView
    private lateinit var displayName: TextView

    private val settingsManager by inject<SettingsManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.newsFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
        navigationSelectedListener(navView, navController, drawerLayout)

        destinationChangeListener(navController, drawerLayout)

        bottomNavigationView(navController)

        val header = navView.getHeaderView(0)
        profileImage = header.findViewById(R.id.profile_image)
        profileImage.setOnClickListener {
            setProfileImage()
        }

        displayName = header.findViewById(R.id.display_name)
        displayName.setOnClickListener {
            showDialog()
        }
        updateData()

    }

    private fun destinationChangeListener(
        navController: NavController,
        drawerLayout: DrawerLayout,
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        //doSomethingHere()
                        supportActionBar?.show()
                        binding.appBarMain.bottomNavigation.apply {
                            visibility = View.VISIBLE
                            selectedItemId = R.id.home
                        }
                        supportActionBar?.title = null
                    }, 0)
                }
                R.id.newsFragment -> {
                    supportActionBar?.show()
                    binding.appBarMain.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarMain.bottomNavigation.selectedItemId = R.id.news
                    supportActionBar?.title = getString(R.string.news)
                }
                else -> {
                    supportActionBar?.hide()
                    binding.appBarMain.bottomNavigation.visibility = View.GONE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        }
    }

    private fun bottomNavigationView(navController: NavController) {
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
    }

    private fun navigationSelectedListener(
        navView: NavigationView,
        navController: NavController,
        drawerLayout: DrawerLayout,
    ) {
        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            if (menuItem.itemId == R.id.nav_settings) {
                when (navController.currentDestination?.id) {
                    R.id.nav_home -> {
                        val action = HomeFragmentDirections.actionNavHomeToNavSettings()
                        navController.safeNavigate(action)
                    }
                    R.id.newsFragment -> {
                        val action = NewsFragmentDirections.actionNewsFragmentToNavSettings()
                        navController.safeNavigate(action)
                    }
                }
            } else {
                Toast.makeText(this, "Function not Implemented", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.close()
            true
        }
    }

    private fun updateData() {
        settingsManager.preferenceProfileImageFlow.asLiveData().observe(this) {
            if (it.isBlank()) {
//                profileImage.setImageResource(R.drawable.ic_person)
                when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        profileImage.setImageResource(R.drawable.ic_indicator_person)
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        profileImage.setImageResource(R.drawable.ic_person)
                    }
                }
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
                        Intent(Intent.ACTION_OPEN_DOCUMENT,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    gallery.flags = (FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
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
            if (imageUri == null) return
            contentResolver.takePersistableUriPermission(imageUri!!,
                FLAG_GRANT_READ_URI_PERMISSION);
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