package com.lesterade.oneira.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.lesterade.oneira.GameMasterViewModel
import com.lesterade.oneira.R
import com.lesterade.oneira.databinding.FragmentMenuBinding
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.ui.StartingActivity
import com.lesterade.oneira.gameHandling.loadGame
import com.lesterade.oneira.gameHandling.saveGame

@Composable
fun MenuScreen(handler: GameHandler, onGameRestart: () -> Unit) {
    val context = LocalContext.current
    val buttonColors = ButtonColors(Color(0xFF704300), Color(0xFFBDBDBD), Color(0xFF363636), Color(0xFFBDBDBD))

    Column() {
        Button({loadGame(context, handler.master)}, shape = RectangleShape, colors = buttonColors) {Text(text = stringResource(R.string.load_game))}
        Button({saveGame(context, handler.master) }, shape = RectangleShape, colors = buttonColors) {Text(text = stringResource(R.string.save_game))}
        Button(onGameRestart, shape = RectangleShape, colors = buttonColors) {Text(text = stringResource(R.string.new_game))}
    }
}

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModel: GameMasterViewModel by activityViewModels<GameMasterViewModel>()
        val master = viewModel.master.value!!

        binding.button2.setOnClickListener {
            loadGame(requireContext(), master)
        }
        binding.button3.setOnClickListener {
            saveGame(requireContext(), master)
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