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
    }
}