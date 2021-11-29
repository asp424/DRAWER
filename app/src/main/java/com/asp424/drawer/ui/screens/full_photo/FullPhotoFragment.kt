package com.asp424.drawer.ui.screens.full_photo

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.asp424.drawer.MainActivity
import com.asp424.drawer.R
import com.asp424.drawer.databinding.FragmentFullPhotoBinding
import com.asp424.drawer.utilites.APP_ACTIVITY
import com.asp424.drawer.utilites.downloadAndSetImageWithoutPlaceholder
import com.asp424.drawer.utilites.hideKeyboard
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.platform.MaterialContainerTransform


class FullPhotoFragment: Fragment(R.layout.fragment_full_photo) {
    private var fullPhotoFragment: FragmentFullPhotoBinding? = null
    private val _fullPhotoFragment get() = fullPhotoFragment
    private lateinit var mToolbar: MaterialToolbar
    /*val args: FullPhotoFragmentArgs by navArgs()*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fullPhotoFragment = FragmentFullPhotoBinding.inflate(inflater, container, false)
        val path = arguments?.getString("ass").toString()

        if (path.startsWith("https")) {
            fullPhotoFragment?.image?.apply {
                fullPhotoFragment?.fullPhoto?.visibility = View.GONE
                visibility = View.VISIBLE
                downloadAndSetImageWithoutPlaceholder(path) {}
            }
        } else {
            fullPhotoFragment?.apply {
                image.visibility = View.GONE
                fullPhoto.visibility = View.VISIBLE
                fullPhoto.setImage(ImageSource.uri(path))
            }

        }
        return _fullPhotoFragment?.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300L
        }

    }

    override fun onStart() {
        super.onStart()
        mToolbar =
            (requireActivity() as MainActivity).findViewById(R.id.topAppBar)
        mToolbar.visibility = View.GONE
    }

    @SuppressLint("ResourceAsColor")
    override fun onResume() {
        super.onResume()
        hideKeyboard()
        APP_ACTIVITY.drawerLayout.closeDrawers()
    }

    @SuppressLint("ResourceAsColor")
    override fun onDestroy() {
        super.onDestroy()
        mToolbar.visibility = View.VISIBLE
    }
}
