package com.lesterade.oneira.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.lesterade.oneira.databinding.ActivityEndingBinding
import com.lesterade.oneira.databinding.FragmentEndingBinding
import com.lesterade.oneira.R

@Composable
fun EndingScreen(imageName: String, desc: String, onGameStart: () -> Unit) {
    val context = LocalContext.current

    val id = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    Column(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = id),
             imageName)
        Text(text = desc)
        Button(onGameStart) {Text(text = stringResource(R.string.start_again) )}
    }
}

class EndingFragment: Fragment() {
    private var _binding: FragmentEndingBinding? = null

    private val binding get() = _binding!!

    var desc = ""
    var imageName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEndingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textView2.text = desc

        val id = requireContext().resources.getIdentifier(imageName, "drawable", requireContext().packageName)

        binding.imageView2.setImageResource(id)

        binding.buttonAgain.setOnClickListener{
            activity?.finish()
            val int = Intent("android.intent.action.VIEW")
            int.setClass(requireContext(), StartingActivity::class.java)
            requireContext().startActivity(int)
        }

        return root
    }
}

class EndingActivity : AppCompatActivity() {

    private var _binding: ActivityEndingBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ending)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding = ActivityEndingBinding.inflate(layoutInflater)

        val str = intent.getStringExtra("com.lesterade.oneira.ending")
        val img = intent.getStringExtra("com.lesterade.oneira.ending_img")

        binding.fragmentContainerView.getFragment<EndingFragment>().desc = str ?: "NONAME"
        binding.fragmentContainerView.getFragment<EndingFragment>().imageName = img ?: "ending_1"
    }
}