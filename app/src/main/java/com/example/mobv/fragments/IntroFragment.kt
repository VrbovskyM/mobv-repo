package com.example.mobv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.databinding.FragmentIntroBinding
import com.example.mobv.databinding.FragmentLoginBinding
import com.example.mobv.viewModels.AuthViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var viewModel: AuthViewModel
    private var introBinding: FragmentIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        introBinding = FragmentIntroBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            introFragment  = this@IntroFragment
        }

        view.findViewById<Button>(R.id.button1).setOnClickListener {
            findNavController().navigate(R.id.toLogin)
        }
        view.findViewById<Button>(R.id.button2).setOnClickListener {
            findNavController().navigate(R.id.toRegister)
        }
    }

}