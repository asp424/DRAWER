package com.asp424.drawer


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.asp424.drawer.database.AUTH
import com.asp424.drawer.database.initUser
import com.asp424.drawer.database.updateStateExit
import com.asp424.drawer.databinding.ActivityMainBinding
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private val mViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        APP_ACTIVITY = this
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding!!.root)
        toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        initDrawer()
        intents()
    }

    override fun onResume() {
        super.onResume()
        initFirebase()
    }

    private fun initFirebase() {
        if (AUTH.currentUser?.uid != null) {
            initUser {
                CoroutineScope(Dispatchers.IO).launch {
                    initContacts(this@MainActivity)
                    AppStates.updateState(AppStates.ONLINE)
                }
                initHeaderDrawer(this)

            }
        } else {
            navController.navigate(R.id.nav_enter_phone)
        }
    }

    private fun initDrawer() {
        drawerLayout = mBinding!!.drawerLayout
        navView = mBinding!!.navView
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_main), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_SEND) {
            when {
                intent.type?.startsWith("image/") == true -> {
                    handleSendImage(intent)
                }
                intent.type?.startsWith("text/") == true -> {
                    handleSendText(intent)
                }
            }
        }
        if (intent?.action == "CLICK_ACTION") {
            navController.navigateUp()
            navController.navigateUp()
            navController.navigateUp()
            navController.navigateUp()
            val id = intent.extras!!["userUid"].toString()
            mViewModel.getContaktForUfterNotif(id) {
                navController.navigate(R.id.nav_single)
            }
        }
    }

    private fun intents() {
        if (intent?.action == Intent.ACTION_SEND) {
            when {
                intent.type?.startsWith("image/") == true -> {
                    handleSendImage(intent)
                }
                intent.type?.startsWith("text/") == true -> {
                    handleSendText(intent)
                }
            }
        }
        if (intent?.action == "CLICK_ACTION") {
            val id = intent.extras!!["userUid"].toString()
            mViewModel.getContaktForUfterNotif(id) {
                navController.navigate(R.id.action_nav_main_to_nav_single)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            val bundle = Bundle()
            bundle.putString("shi", it)
            bundle.putInt("Image", 0)
            navController.navigate(R.id.nav_intent, bundle)
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val bundle = Bundle()
            bundle.putString("shi", it.toString())
            bundle.putInt("Image", 1)
            navController.navigate(R.id.nav_intent, bundle)
        }
    }

    override fun onStop() {
        super.onStop()
        updateStateExit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this,
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts(this)
        }
    }
}

