package com.mio.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.mio.utils.AnimUtil
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentDownloadBinding
import net.kdt.pojavlaunch.fragments.SearchModFragment
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper

class DownloadFragment() : BaseFragment(R.layout.fragment_download), OnClickListener {
    companion object {
        const val TAG = "DownloadFragment"
    }

    private lateinit var binding: FragmentDownloadBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDownloadBinding.bind(view)
        childID = R.id.container_fragment_download
        binding.apply {
            gameDownload.setOnClickListener(this@DownloadFragment)
            modDownload.setOnClickListener(this@DownloadFragment)
            modpackDownload.setOnClickListener(this@DownloadFragment)
            installJar.setOnClickListener(this@DownloadFragment)
            installJar.setOnLongClickListener {
                runInstallerWithConfirmation(true)
                true
            }
        }

        swapChildFragment(
            GameDownloadFragment::class.java,
            GameDownloadFragment.TAG
        )
        startAnimation()
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v) {
                gameDownload -> {
                    swapChildFragment(
                        GameDownloadFragment::class.java,
                        GameDownloadFragment.TAG
                    )
                }

                modDownload -> {

                }

                modpackDownload -> {
                    swapChildFragment(
                        SearchModFragment::class.java,
                        SearchModFragment.TAG
                    )
                }

                installJar -> runInstallerWithConfirmation(false)

            }
        }
    }

    private fun startAnimation() {
        AnimUtil.playTranslationX(binding.scrollview, 1000, BounceInterpolator(), -400f, 0f).start()
        AnimUtil.playTranslationY(binding.gameDownload,500,BounceInterpolator(),-200f,0f).start()
        AnimUtil.playTranslationY(binding.modDownload,400,BounceInterpolator(),400f,0f).start()
        AnimUtil.playTranslationY(binding.modpackDownload,500,BounceInterpolator(),500f,0f).start()
        AnimUtil.playTranslationX(binding.containerFragmentDownload,500,null,500f,0f).start()
        AnimUtil.playScaleX(binding.containerFragmentDownload,1000,BounceInterpolator(),0f,1f,0.5f,1f).start()
        AnimUtil.playScaleY(binding.containerFragmentDownload,1000,BounceInterpolator(),0f,1f,0.5f,1f).start()
    }

    private fun runInstallerWithConfirmation(isCustomArgs: Boolean) {
        if (ProgressKeeper.getTaskCount() == 0) Tools.installMod(requireActivity(), isCustomArgs)
        else Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show()
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