package com.lesterade.oneira

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.lesterade.oneira.databinding.ActivityMainBinding
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.ActorFactory
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import com.lesterade.oneira.gameHandling.GameMaster
import com.lesterade.oneira.gameHandling.biomes.LocationFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val language = "ru"

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: GameMasterViewModel by viewModels()

        viewModel.master.value = GameMaster(msgPatterns(this))

        ToolFactory.launch(applicationContext, language)
        ActorFactory.launch(applicationContext, language)
        LocationFactory.launch(applicationContext, language)

        viewModel.master.value?.startNewGame(intent.getStringExtra("com.lesterade.oneira.charname") ?: "player")

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_game, R.id.navigation_cards_display, R.id.navigation_menu
            )
        )

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()
    }
}
