package com.muratguzel.photosharingapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import com.muratguzel.photosharingapp.R
import com.muratguzel.photosharingapp.databinding.FragmentFeedBinding


class FeedFragment : Fragment(),PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var popup: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { floatingButtonClick(it) }

        popup = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
    }

    fun floatingButtonClick(view: View) {

        popup.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.uploadItem) {
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item?.itemId == R.id.signOut) {
            //cıkış
            val action = FeedFragmentDirections.actionFeedFragmentToRegisterAndLoginFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }
}