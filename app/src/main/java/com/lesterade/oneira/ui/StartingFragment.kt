package com.lesterade.oneira.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lesterade.oneira.R
import com.lesterade.oneira.databinding.FragmentStartingBinding
import com.lesterade.oneira.ui.characterLayout.CharacterChoice
import com.lesterade.oneira.gameHandling.player


class StartingFragment : Fragment() {

    private var _binding: FragmentStartingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val chars = listOf("player" to "The main character")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartingBinding.inflate(inflater, container, false)

        chars.forEach {
            val ch = inflater.inflate(R.layout.characterchoice, null)
            binding.list.addView(ch)
            ch.findViewById<CharacterChoice>(R.id.main).loadChar(it.first, it.second)
        }

        return binding.root
    }
}