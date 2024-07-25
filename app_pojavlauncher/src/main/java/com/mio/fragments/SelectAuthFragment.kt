package com.mio.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentSelectAuthMethodBinding
import net.kdt.pojavlaunch.fragments.LocalLoginFragment
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment

class SelectAuthFragment : Fragment(R.layout.fragment_select_auth_method) {
    companion object {
        const val TAG = "SelectAuthFragment"
    }

    private lateinit var binding: FragmentSelectAuthMethodBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectAuthMethodBinding.bind(view)
        binding.microsoft.setOnClickListener {
            Tools.swapFragment(
                requireActivity(),
                MicrosoftLoginFragment::class.java, MicrosoftLoginFragment.TAG, null
            )
        }
        binding.other.setOnClickListener {
            Tools.swapFragment(
                requireActivity(),
                OtherLoginFragment::class.java, OtherLoginFragment.TAG, null
            )
        }
        binding.local.setOnClickListener {
            Tools.swapFragment(
                requireActivity(),
                LocalLoginFragment::class.java, LocalLoginFragment.TAG, null
            )
        }
        binding.close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}