package com.mio.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import com.mio.utils.AnimUtil
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
            Tools.swapFragment(
                requireActivity(),
                MicrosoftLoginFragment::class.java, MicrosoftLoginFragment.TAG, null
            )
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
}