package com.mio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mio.utils.FragmentUtil

open class BaseFragment(layout: Int) : Fragment(layout) {
    var childID = 0
    var parentID = 0

    fun swapChildFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
        FragmentUtil.swapFragment(childFragmentManager, childID, clazz, tag, bundle)
    }

    fun swapParentFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
        FragmentUtil.swapFragment(parentFragmentManager, parentID, clazz, tag, bundle)
    }
}