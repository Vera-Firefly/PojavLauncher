package com.mio.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import com.mio.utils.AnimUtil
import com.mio.utils.AnimUtil.Companion.delay
import com.mio.utils.AnimUtil.Companion.interpolator
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentSelectAuthMethodBinding
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment

class SelectAuthFragment : BaseFragment(R.layout.fragment_select_auth_method) {
    companion object {
        const val TAG = "SelectAuthFragment"
    }

    private lateinit var binding: FragmentSelectAuthMethodBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectAuthMethodBinding.bind(view)
        parentID = R.id.container_fragment_home
        binding.microsoft.setOnClickListener {
            parentFragmentManager.popBackStack()
            swapParentFragment(MicrosoftLoginFragment::class.java, MicrosoftLoginFragment.TAG)
        }
        binding.other.setOnClickListener {
            swapParentFragment(OtherLoginFragment::class.java, OtherLoginFragment.TAG)
        }
        binding.local.setOnClickListener {
            swapParentFragment(LocalLoginFragment::class.java, LocalLoginFragment.TAG)
        }
        binding.close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            AnimUtil.playTranslationY(card, 800, -400f, 0f).interpolator(OvershootInterpolator())
                .start()
            AnimUtil.playTranslationY(microsoft, 800, -400f, 0f).delay(100)
                .interpolator(BounceInterpolator())
                .start()
            AnimUtil.playTranslationY(other, 800, -400f, 0f).delay(200)
                .interpolator(BounceInterpolator())
                .start()
            AnimUtil.playTranslationY(local, 800, -400f, 0f).delay(300)
                .interpolator(BounceInterpolator())
                .start()
        }
    }
}
