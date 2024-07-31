package com.muratguzel.photosharingapp.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.muratguzel.photosharingapp.R
import com.muratguzel.photosharingapp.databinding.FragmentUploadBinding
import java.util.UUID

class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedImage: Uri? = null
    var selectedBitmap: Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storege: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storege = Firebase.storage
        db = Firebase.firestore
        registerLauncher()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val view = binding.root

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.uploadButton.setOnClickListener { upload(it) }
        binding.imageView.setOnClickListener { selectImage(it) }

    }

    fun upload(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val reference = storege.reference
        val imageReference = reference.child("images").child(imageName)
        selectedImage?.let {
            imageReference.putFile(selectedImage!!).addOnSuccessListener { uploadTask ->
                //url alma
                imageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("userName", auth.currentUser!!.displayName!!)
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", auth.currentUser!!.email!!)
                    postMap.put("comment", binding.explanationText.text.toString())
                    postMap.put("date",Timestamp.now())
                    db.collection("Posts").add(postMap).addOnSuccessListener { dcumetnReference ->
                        val action = UploadFragmentDirections.actionUploadFragmentToFeedFragment()
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.uploadFragment, true)
                            .build()
                        Navigation.findNavController(requireView()).navigate(action, navOptions)
                    }.addOnFailureListener { exception ->
                        Snackbar.make(
                            requireView(),
                            exception.localizedMessage!!,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                }.addOnFailureListener { exception ->
                    Snackbar.make(
                        requireView(),
                        exception.localizedMessage!!,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //read media images
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    // show logic and request permission
                    Snackbar.make(
                        requireView(),
                        "Galeriye gitmek için izin vermeniz gerekiyor",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin Ver", View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                    }).show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // go to gallery
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            //read external storage
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // show logic and request permission
                    Snackbar.make(
                        requireView(),
                        "Galeriye gitmek için izin vermeniz gerekiyor",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin Ver", View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }).show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // go to gallery
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }


    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    intentFromResult?.let {
                        selectedImage = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver, selectedImage!!
                                )
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver, selectedImage
                                )
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }


            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery = Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Snackbar.make(
                        requireView(),
                        "Görsel seçmek için izin vermeniz gerekiyor",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}