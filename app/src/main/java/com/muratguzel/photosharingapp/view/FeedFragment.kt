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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.muratguzel.photosharingapp.R
import com.muratguzel.photosharingapp.adapter.PostAdapter
import com.muratguzel.photosharingapp.databinding.FragmentFeedBinding
import com.muratguzel.photosharingapp.model.Post


class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private lateinit var popup: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    val postList: ArrayList<Post> = arrayListOf()
    private var adapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
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
        fireStoreGetData()
        adapter = PostAdapter(postList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun fireStoreGetData() {
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Snackbar.make(requireView(), error.localizedMessage!!, Snackbar.LENGTH_SHORT).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        postList.clear()
                        val documetns = value.documents
                        for (document in documetns) {
                            val comment = document.get("comment") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val userName = document.get("userName") as String
                            val email = document.get("userEmail") as String
                            val post = Post(comment, downloadUrl, email, userName)
                            postList.add(post)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
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
            auth.signOut()
            val action = FeedFragmentDirections.actionFeedFragmentToRegisterAndLoginFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }
}