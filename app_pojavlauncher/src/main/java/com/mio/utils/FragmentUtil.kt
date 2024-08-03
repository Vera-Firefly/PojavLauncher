package com.mio.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.kdt.pojavlaunch.R

class FragmentUtil {
    companion object {
        @JvmStatic
        fun closeAll(fragmentManager: FragmentManager) {
            for (i in 0 until fragmentManager.backStackEntryCount) {
                fragmentManager.popBackStack()
            }
        }

        @JvmStatic
        fun swapFragment(
            manager: FragmentManager,
            containerID: Int,
            clazz: Class<out Fragment>,
            tag: String,
            bundle: Bundle? = null
        ) {
            manager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .setReorderingAllowed(true)
                .addToBackStack(tag)
                .replace(containerID, clazz, bundle, tag)
                .commit()
        }

        @JvmStatic
        fun addFragment(
            manager: FragmentManager,
            containerID: Int,
            clazz: Class<out Fragment>,
            tag: String,
            bundle: Bundle? = null
        ) {
            manager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .setReorderingAllowed(true)
                .addToBackStack(tag)
                .add(containerID, clazz, bundle, tag)
                .commit()
        }
    }
}