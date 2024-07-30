package com.mio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R

open class BaseFragment(layout: Int) : Fragment(layout) {
    var childID = 0
    var parentID = 0

    fun swapChildFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .setReorderingAllowed(true)
            .addToBackStack(tag)
            .replace(childID, clazz, bundle, tag)
            .commit()
    }

    fun swapParentFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .setReorderingAllowed(true)
            .addToBackStack(tag)
            .replace(parentID, clazz, bundle, tag)
            .commit()
    }
}