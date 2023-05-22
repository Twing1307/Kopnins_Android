package com.vyapp.doodle.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.vyapp.doodle.MainActivity
import com.vyapp.doodle.databinding.FragmentGameOverBinding

class GameOverFragment : Fragment() {


    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private val binding: FragmentGameOverBinding by lazy {
        FragmentGameOverBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.overBtn.setOnClickListener {
            (requireActivity() as MainActivity).toGameFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}