package com.mio.fragments

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentDownloadGameBinding
import net.kdt.pojavlaunch.fragments.FabricInstallFragment
import net.kdt.pojavlaunch.fragments.ForgeInstallFragment
import net.kdt.pojavlaunch.fragments.OptiFineInstallFragment
import net.kdt.pojavlaunch.fragments.ProfileEditorFragment
import net.kdt.pojavlaunch.fragments.QuiltInstallFragment

class GameDownloadFragment() : BaseFragment(R.layout.fragment_download_game), OnClickListener {
    companion object {
        const val TAG = "GameDownloadFragment"
    }

    private lateinit var binding: FragmentDownloadGameBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDownloadGameBinding.bind(view)
        parentID = R.id.container_fragment_download
        binding.vanillaProfile.setOnClickListener(this)
        binding.optifineProfile.setOnClickListener(this)
        binding.moddedProfileFabric.setOnClickListener(this)
        binding.moddedProfileForge.setOnClickListener(this)
        binding.moddedProfileQuilt.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.vanillaProfile -> {
                swapParentFragment(
                    ProfileEditorFragment::class.java,
                    ProfileEditorFragment.TAG,
                    Bundle(1)
                )
            }

            binding.optifineProfile -> {
                swapParentFragment(
                    OptiFineInstallFragment::class.java,
                    OptiFineInstallFragment.TAG
                )
            }

            binding.moddedProfileFabric -> {
                swapParentFragment(
                    FabricInstallFragment::class.java, FabricInstallFragment.TAG
                )
            }

            binding.moddedProfileForge -> {
                swapParentFragment(
                    ForgeInstallFragment::class.java, ForgeInstallFragment.TAG
                )
            }

            binding.moddedProfileQuilt -> {
                swapParentFragment(
                    QuiltInstallFragment::class.java, QuiltInstallFragment.TAG
                )
            }
        }
    }
}