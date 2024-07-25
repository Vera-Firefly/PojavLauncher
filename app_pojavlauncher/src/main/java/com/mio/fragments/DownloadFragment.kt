package com.mio.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentDownloadBinding

class DownloadFragment() : Fragment(R.layout.fragment_download), OnClickListener {
    companion object {
        const val TAG = "DownloadFragment"
    }
    private lateinit var binding: FragmentDownloadBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDownloadBinding.bind(view)
        binding.gameDownload.setOnClickListener(this)
        binding.modDownload.setOnClickListener(this)
        binding.modpackDownload.setOnClickListener(this)

        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .setReorderingAllowed(true)
            .addToBackStack(tag)
            .replace(
                R.id.container_fragment_download,
                GameDownloadFragment::class.java,
                null,
                GameDownloadFragment.TAG
            )
            .commit()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.gameDownload -> {

            }

            binding.modDownload -> {

            }

            binding.modpackDownload -> {

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (childFragmentManager.backStackEntryCount > 1) {
                    childFragmentManager.popBackStack()
                    return
                }
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}