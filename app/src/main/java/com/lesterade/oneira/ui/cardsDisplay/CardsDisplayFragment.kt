package com.lesterade.oneira.ui.cardsDisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lesterade.oneira.GameMasterViewModel
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay
import com.lesterade.oneira.R
import com.lesterade.oneira.databinding.FragmentCardsDisplayBinding

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

        val layout = binding.mainList

        val viewModel: GameMasterViewModel by activityViewModels<GameMasterViewModel>()

        viewModel.master.value?.cards?.forEach {
            val curText = inflater.inflate(R.layout.tooldisplay, layout)
        }

        viewModel.master.value?.cards?.forEachIndexed { i: Int, it: Instrument ->
            layout[i].findViewById<ToolDisplay>(R.id.main).loadTool(it)
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}