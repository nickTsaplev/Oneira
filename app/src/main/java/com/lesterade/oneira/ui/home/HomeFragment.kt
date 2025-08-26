package com.lesterade.oneira.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.lesterade.oneira.databinding.FragmentHomeBinding
import com.lesterade.oneira.ui.gameHandling.GameMaster
import com.lesterade.oneira.ui.gameHandling.instrument
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay
import com.lesterade.oneira.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layout = binding.mainList

        GameMaster.cards.forEach {
            val curText = inflater.inflate(R.layout.tooldisplay, layout)
        }

        GameMaster.cards.forEachIndexed() { i: Int, it: instrument ->
            layout[i].findViewById<ToolDisplay>(R.id.main).loadTool(it)
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}