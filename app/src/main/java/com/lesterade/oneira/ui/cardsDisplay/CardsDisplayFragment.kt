package com.lesterade.oneira.ui.cardsDisplay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lesterade.oneira.GameMasterViewModel
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay
import com.lesterade.oneira.R
import com.lesterade.oneira.databinding.FragmentCardsDisplayBinding
import com.lesterade.oneira.gameHandling.biomes.SimpleBiome
import com.lesterade.oneira.gameHandling.dispTool
import com.lesterade.oneira.ui.EndingActivity
import com.lesterade.oneira.ui.game.GameScreen
import kotlin.math.ceil

class CardsDisplayFragment : Fragment() {

    private var _binding: FragmentCardsDisplayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardsDisplayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModel: GameMasterViewModel by activityViewModels<GameMasterViewModel>()

        binding.composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent {
            val configuration = LocalConfiguration.current
            val scaleVal = configuration.densityDpi / 240f

            val scale = if (scaleVal > 2f) ceil(scaleVal) else null

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.master.value?.cards?.forEachIndexed { i: Int, it: Instrument ->
                    ToolDisplay(
                        dispTool(
                            it.imageId,
                            it.header,
                            it.description,
                            it.transmutable && viewModel.master.value?.scene is SimpleBiome
                        ), i, {}, scale)
                }
            }
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}