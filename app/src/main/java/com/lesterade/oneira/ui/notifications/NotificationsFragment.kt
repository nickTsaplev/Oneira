package com.lesterade.oneira.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lesterade.oneira.MainActivity
import com.lesterade.oneira.databinding.FragmentNotificationsBinding
import com.lesterade.oneira.ui.StartingActivity
import com.lesterade.oneira.ui.gameHandling.loadGame
import com.lesterade.oneira.ui.gameHandling.saveGame

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.button2.setOnClickListener {
            loadGame(requireContext())
        }
        binding.button3.setOnClickListener {
            saveGame(requireContext())
        }
        binding.button4.setOnClickListener {
            activity?.finish()
        }
        binding.button5.setOnClickListener{
            activity?.finish()
            val int = Intent("android.intent.action.VIEW")
            int.setClass(requireContext(), StartingActivity::class.java)
            requireContext().startActivity(int)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}