package com.mio.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McAccountSpinner
import com.mio.utils.AnimUtil
import net.kdt.pojavlaunch.PojavApplication
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentHomeBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore

class HomeFragment() : BaseFragment(R.layout.fragment_home), OnClickListener {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private val runnable = Runnable { closeAuthMenu() };

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        childID = R.id.container_fragment_home
        binding.userIcon.setOnClickListener(this)
        binding.gameSetting.setOnClickListener(this)
        binding.pathSetting.setOnClickListener(this)
        binding.edit.setOnClickListener(this)
        binding.add.setOnClickListener(this)
        binding.delete.setOnClickListener(this)

        binding.mcVersionSpinner.profileAdapter.setOnClick {
            binding.mcVersionSpinner.setProfileSelection(it)
            binding.mcVersionSpinner.hidePopup(true)
            binding.mcVersionSpinner.openProfileEditor(this)
        }

        binding.accountSpinner.setUserIcon(binding.userIcon)
        startAnimation()
        binding.accountSpinner.post {
            binding.accountSpinner.refreshUserIcon()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mcVersionSpinner.reloadProfiles();
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.userIcon -> ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
            binding.gameSetting -> binding.mcVersionSpinner.openProfileEditor(this)
//            binding.pathSetting->
            binding.edit -> openAuthMenu()
            binding.add -> {
                ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
                closeAuthMenu()
            }

            binding.delete -> {
                AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.warning_remove_account)
                    .setPositiveButton(android.R.string.cancel, null)
                    .setNegativeButton(R.string.global_delete) { _, _ ->
                        binding.accountSpinner.removeCurrentAccount()
                    }
                    .show()
                closeAuthMenu()
            }
        }
    }

    private fun editAccount() {
        if (McAccountSpinner.getSelectedAccount() == null) {
            ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
            return
        } else {
            val popupMenu = PopupMenu(requireActivity(), binding.edit, Gravity.START);
            popupMenu.inflate(R.menu.menu_edit)
            popupMenu.setOnMenuItemClickListener { it ->
                when (it.itemId) {
                    R.id.add -> ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
                    R.id.remove -> AlertDialog.Builder(requireActivity())
                        .setMessage(R.string.warning_remove_account)
                        .setPositiveButton(android.R.string.cancel, null)
                        .setNegativeButton(R.string.global_delete) { _, _ ->
                            binding.accountSpinner.removeCurrentAccount()
                        }
                        .show();
                }
                false
            }
            if (Build.VERSION.SDK_INT > 28) {
                popupMenu.setForceShowIcon(true)
            }
            popupMenu.show()
        }
    }

    private fun openAuthMenu() {
        binding.add.removeCallbacks(runnable)
        if (binding.add.visibility == GONE) {
            binding.add.visibility = VISIBLE
            binding.delete.visibility = VISIBLE
            var offsetY = binding.edit.height * -1.5f;
            val objectAnimator = ObjectAnimator.ofFloat(binding.add, "translationY", 0f, offsetY)
                .setDuration(500)
            objectAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.add.postDelayed(runnable, 4000)
                }
            })
            objectAnimator.start()
            startAuthMenuAnimation(binding.add)
            offsetY += binding.edit.height * -1.5f
            ObjectAnimator.ofFloat(binding.delete, "translationY", 0f, offsetY)
                .setDuration(500)
                .start();
            startAuthMenuAnimation(binding.delete)
        } else {
            closeAuthMenu()
        }
    }

    private fun closeAuthMenu() {
        var offsetY = binding.edit.height * -1.5f
        val objectAnimator =
            ObjectAnimator.ofFloat(binding.add, "translationY", offsetY, 0f)
                .setDuration(500)
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.add.visibility = GONE
                binding.delete.visibility = GONE
            }
        })
        objectAnimator.start()
        endAuthMenuAnimation(binding.add)

        offsetY += binding.edit.height * -1.5f
        ObjectAnimator.ofFloat(binding.delete, "translationY", offsetY, 0f)
            .setDuration(500).start()
        endAuthMenuAnimation(binding.delete)
    }

    private fun startAuthMenuAnimation(view: View) {
        AnimUtil.playScaleX(view, 1000, BounceInterpolator(), 0f, 1f, 1.5f).start()
        AnimUtil.playScaleY(view, 1000, BounceInterpolator(), 0f, 1f, 1.5f).start()
        AnimUtil.playAlpha(view, 500, null, 0f, 1f).start()
    }

    private fun endAuthMenuAnimation(view: View) {
        AnimUtil.playScaleX(view, 500, BounceInterpolator(), 1f, 0f).start()
        AnimUtil.playScaleY(view, 500, BounceInterpolator(), 1f, 0f).start()
        AnimUtil.playAlpha(view, 500, null, 1f, 0f).start()
    }

    private fun startAnimation() {
        AnimUtil.playTranslationX(binding.left, 500, BounceInterpolator(), -400f, 0f)
            .start()
        AnimUtil.playTranslationY(binding.userIcon, 2000, BounceInterpolator(), -400f, 0f)
            .start()
        AnimUtil.playTranslationX(binding.userIcon, 2000, BounceInterpolator(), -200f, 0f)
            .start()
        AnimUtil.playRotation(binding.userIcon, 2000, BounceInterpolator(), -160f, 0f)
            .start()
        AnimUtil.playTranslationX(binding.accountSpinner, 1500, BounceInterpolator(), -400f, 0f)
            .start()
        AnimUtil.playTranslationY(binding.edit, 1000, BounceInterpolator(), -400f, 0f)
            .start()
        AnimUtil.playTranslationY(
            binding.frameLayout,
            500,
            AnticipateOvershootInterpolator(),
            300f,
            0f
        )
            .start()
        AnimUtil.playTranslationY(
            binding.gameSetting,
            800,
            AnticipateOvershootInterpolator(),
            300f,
            0f
        )
            .start()
        AnimUtil.playTranslationY(
            binding.pathSetting,
            800,
            AnticipateOvershootInterpolator(),
            300f,
            0f
        )
            .start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                    return
                }
                if (parentFragmentManager.backStackEntryCount > 1) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}