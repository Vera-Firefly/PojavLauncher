package com.mio.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.core.view.children
import com.mio.ui.SelectableImageButton
import com.mio.utils.AnimUtil
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentSettingBinding
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceControlFragment
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceExperimentalFragment
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceJavaFragment
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceMiscellaneousFragment
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceVideoFragment

class SettingFragment : BaseFragment(R.layout.fragment_setting),
    SelectableImageButton.OnSelectListener {
    companion object {
        const val TAG = "SettingFragment"
    }

    private lateinit var binding: FragmentSettingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childID = R.id.container_fragment_setting
        binding = FragmentSettingBinding.bind(view)
        binding.apply {
            startAnimation()
            var height = -200f
            nav.children.forEach {
                (it as? SelectableImageButton)?.onSelectListener = this@SettingFragment
                height -= 200
                AnimUtil.playTranslationY(it, 800, BounceInterpolator(), height, 0f).start()
            }
            video.performClick()
        }
    }

    override fun onSelected(v: SelectableImageButton) {
        binding.apply {
            nav.children.forEach {
                if (it != v)
                    (it as? SelectableImageButton)?.apply {
                        isSelected = false
                        refreshTintColor()
                    }
            }
            when (v) {
                video -> swapChildFragment(
                    LauncherPreferenceVideoFragment::class.java,
                    "LauncherPreferenceVideoFragment"
                )

                control -> swapChildFragment(
                    LauncherPreferenceControlFragment::class.java,
                    "LauncherPreferenceControlFragment"
                )

                java -> swapChildFragment(
                    LauncherPreferenceJavaFragment::class.java,
                    "LauncherPreferenceJavaFragment"
                )

                launcher -> swapChildFragment(
                    LauncherPreferenceVideoFragment::class.java,
                    "LauncherPreferenceVideoFragment"
                )

                other -> swapChildFragment(
                    LauncherPreferenceMiscellaneousFragment::class.java,
                    "LauncherPreferenceMiscellaneousFragment"
                )

                test -> swapChildFragment(
                    LauncherPreferenceExperimentalFragment::class.java,
                    "LauncherPreferenceExperimentalFragment"
                )
            }
        }
    }

    private fun startAnimation() {
        binding.apply {
            AnimUtil.playTranslationX(nav, 1000, BounceInterpolator(), -100f, 0f).start()
            AnimUtil.playTranslationY(
                containerFragmentSetting,
                1000,
                BounceInterpolator(),
                -500f,
                0f
            ).start()
            AnimUtil.playRotation(
                containerFragmentSetting,
                1000,
                BounceInterpolator(),
                90f,
                0f
            ).start()
        }
    }
}