package com.lesterade.oneira

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lesterade.oneira.databinding.ActivityMainBinding
import com.lesterade.oneira.ui.dashboard.DashboardFragment
import com.lesterade.oneira.ui.gameHandling.ToolFactory
import com.lesterade.oneira.ui.gameHandling.ActorFactory
import com.lesterade.oneira.ui.gameHandling.GameHandler
import com.lesterade.oneira.ui.gameHandling.GameMaster
import com.lesterade.oneira.ui.gameHandling.LocationFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ToolFactory.launch(applicationContext)
        ActorFactory.launch(applicationContext)
        LocationFactory.launch(applicationContext)

        super.onCreate(savedInstanceState)

        GameMaster.startNewGame(intent.getStringExtra("com.lesterade.oneira.charname") ?: "player")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications
            )
        )

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()
    }
}