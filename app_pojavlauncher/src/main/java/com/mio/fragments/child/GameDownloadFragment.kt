package com.mio.fragments.child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentDownloadGameBinding
import net.kdt.pojavlaunch.fragments.FabricInstallFragment
import net.kdt.pojavlaunch.fragments.ForgeInstallFragment
import net.kdt.pojavlaunch.fragments.OptiFineInstallFragment
import net.kdt.pojavlaunch.fragments.ProfileEditorFragment
import net.kdt.pojavlaunch.fragments.QuiltInstallFragment

class GameDownloadFragment() : Fragment(R.layout.fragment_download_game), OnClickListener {
    companion object {
        const val TAG = "GameDownloadFragment"
    }

    private lateinit var binding: FragmentDownloadGameBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDownloadGameBinding.bind(view)
        binding.vanillaProfile.setOnClickListener(this)
        binding.optifineProfile.setOnClickListener(this)
        binding.moddedProfileFabric.setOnClickListener(this)
        binding.moddedProfileForge.setOnClickListener(this)
        binding.moddedProfileQuilt.setOnClickListener(this)
    }

    private fun swapFragment(clazz: Class<out Fragment>, tag: String) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .setReorderingAllowed(true)
            .addToBackStack(tag)
            .replace(R.id.container_fragment_download, clazz, null, GameDownloadFragment.TAG)
            .commit()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.vanillaProfile -> {
                swapFragment(
                    ProfileEditorFragment::class.java,
                    ProfileEditorFragment.TAG
                )
            }

            binding.optifineProfile -> {
                swapFragment(
                    OptiFineInstallFragment::class.java,
                    OptiFineInstallFragment.TAG
                )
            }

            binding.moddedProfileFabric -> {
                swapFragment(
                    FabricInstallFragment::class.java, FabricInstallFragment.TAG
                )
            }

            binding.moddedProfileForge -> {
                swapFragment(
                    ForgeInstallFragment::class.java, ForgeInstallFragment.TAG
                )
            }

            binding.moddedProfileQuilt -> {
                swapFragment(
                    QuiltInstallFragment::class.java, QuiltInstallFragment.TAG
                )
            }
        }
    }
}