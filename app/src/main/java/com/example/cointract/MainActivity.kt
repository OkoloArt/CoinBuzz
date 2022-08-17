package com.example.cointract

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.TypedValue
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

    private var dayNightMode = false

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
            R.id.nav_home), drawerLayout)
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
                val action = HomeFragmentDirections.actionNavHomeToNavSettings()
                navController.safeNavigate(action)

            }
            drawerLayout.close()
            true
        }
    }

    private fun updateData() {

        settingsManager.preferenceDayNightFlow.asLiveData().observe(this) {
            dayNightMode = it
        }
        settingsManager.preferenceProfileImageFlow.asLiveData().observe(this) {
            if (it.equals("")) {
                when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        profileImage.setImageResource(R.drawable.ic_indicator_person)
                        profileImage.background = imageViewBorder(
                            borderColor = Color.parseColor(R.color.md_white_1000.toString()),
                            borderWidthInDp = 2
                        )
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

    // extension function to make a border for image view
    fun Context.imageViewBorder(
        borderColor: Int = Color.BLACK,
        borderWidthInDp: Int = 5,
    ): ShapeDrawable {
        // convert dp to equivalent pixels value for border
        val borderWidthInPixels = borderWidthInDp.dpToPixels(this)

        val shapeDrawable = ShapeDrawable(RectShape())

        // specify the border properties
        shapeDrawable.paint.apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidthInPixels
            isAntiAlias = true
            flags = Paint.ANTI_ALIAS_FLAG
        }

        // set padding for drawable
        val padding = (borderWidthInPixels * 2).toInt()
        shapeDrawable.setPadding(padding, padding, padding, padding)

        // return image view border as shape drawable
        return shapeDrawable
    }


    // extension function to convert dp to equivalent pixels
    private fun Int.dpToPixels(context: Context): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    )
}