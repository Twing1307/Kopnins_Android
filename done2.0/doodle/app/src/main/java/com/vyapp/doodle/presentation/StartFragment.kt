package com.vyapp.doodle.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.vyapp.doodle.MainActivity
import com.vyapp.doodle.R
import com.vyapp.doodle.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private val binding: FragmentStartBinding by lazy {
        FragmentStartBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        binding.playBtn.setOnClickListener(this::playClick)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.scoreLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.bestScoreTxt.text =
                    requireContext().getString(R.string.score) + " " + it.score
            } else {
                binding.bestScoreTxt.text = requireContext().getString(R.string.score) + " " + "0"
            }
        }

        binding.isA.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAcc = isChecked
        }
    }

    private fun playClick(view: View) {

        val editText = EditText(requireContext())
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(requireContext().getString(R.string.enter_name))
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                if (editText.text.toString().isNotEmpty()) {
                    viewModel.setName(editText.text.toString())

                    (requireActivity() as MainActivity).toGameFragment()

                    dialog.dismiss()
                }
            }
            .create()
        dialog.show()
    }

}