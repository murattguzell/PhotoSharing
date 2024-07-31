package com.muratguzel.photosharingapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.muratguzel.photosharingapp.R
import com.muratguzel.photosharingapp.databinding.FragmentFeedBinding
import com.muratguzel.photosharingapp.databinding.FragmentRegisterAndLoginBinding

class RegisterAndLoginFragment : Fragment() {
    private var _binding: FragmentRegisterAndLoginBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
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

        val currentUser = auth.currentUser
        if (currentUser!=null){

            val action = RegisterAndLoginFragmentDirections.actionRegisterAndLoginFragmentToFeedFragment()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registerAndLoginFragment,true)
                .build()
            Navigation.findNavController(view).navigate(action,navOptions)
        }
    }

    fun signUp(view: View) {

        val userName = binding.userNameText.text.toString()
        val email = binding.EmailText.text.toString()
        val password = binding.PasswordText.text.toString()
        if (userName.equals("") || email.equals("") || password.equals("")) {
            Snackbar.make(requireView(), "Boş alanları doldurunuz", Snackbar.LENGTH_SHORT).show()
        } else {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterAndLoginFragment", "createUserWithEmailAndPassword success")
                    // displayName güncelleme
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Profil güncelleme başarılı
                               Log.d("RegisterAndLoginFragment", "displayName update")
                            }
                        }?.addOnFailureListener { exception ->
                            Log.w("RegisterAndLoginFragment", "displayName update failure")
                        }

                    val action =
                        RegisterAndLoginFragmentDirections.actionRegisterAndLoginFragmentToFeedFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }.addOnFailureListener { exception ->
                Snackbar.make(requireView(), exception.localizedMessage!!, Snackbar.LENGTH_SHORT)
                    .show()
                Log.w("RegisterAndLoginFragment", "createUserWithEmailAndPassword failure")
            }
        }

    }

    fun signIn(view: View) {
        val userName = binding.userNameText.text.toString()
        val email = binding.EmailText.text.toString()
        val password = binding.PasswordText.text.toString()
        if (userName.equals("") || email.equals("") || password.equals("")) {
            Snackbar.make(requireView(), "Boş alanları doldurunuz", Snackbar.LENGTH_SHORT).show()
        } else {

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterAndLoginFragment", "createUserWithEmailAndPassword success")
                    val action =
                        RegisterAndLoginFragmentDirections.actionRegisterAndLoginFragmentToFeedFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }.addOnFailureListener { exception ->
                Snackbar.make(requireView(), exception.localizedMessage!!, Snackbar.LENGTH_SHORT)
                    .show()
                Log.w("RegisterAndLoginFragment", exception.localizedMessage!!)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}