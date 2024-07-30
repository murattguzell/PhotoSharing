package com.muratguzel.photosharingapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.muratguzel.photosharingapp.R
import com.muratguzel.photosharingapp.databinding.FragmentFeedBinding
import com.muratguzel.photosharingapp.databinding.FragmentRegisterAndLoginBinding

class RegisterAndLoginFragment : Fragment() {
    private var _binding: FragmentRegisterAndLoginBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterAndLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener { signUp(it) }
        binding.signInButton.setOnClickListener { signIn(it) }

    }

    fun signUp(view: View) {
        val action = RegisterAndLoginFragmentDirections.actionRegisterAndLoginFragmentToFeedFragment()
        Navigation.findNavController(view).navigate(action)
    }
    fun signIn(view: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}