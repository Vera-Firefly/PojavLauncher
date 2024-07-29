package com.mio.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.animation.BounceInterpolator
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McAccountSpinner
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentHomeBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore

class HomeFragment() : Fragment(R.layout.fragment_home), OnClickListener {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private val runnable = Runnable { closeAuthMenu() };

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
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
            binding.add -> ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
            binding.delete -> AlertDialog.Builder(requireActivity())
                .setMessage(R.string.warning_remove_account)
                .setPositiveButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.global_delete) { _, _ ->
                    binding.accountSpinner.removeCurrentAccount()
                }
                .show()
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
            startAnimation(binding.add)
            offsetY += binding.edit.height * -1.5f
            ObjectAnimator.ofFloat(binding.delete, "translationY", 0f, offsetY)
                .setDuration(500)
                .start();
            startAnimation(binding.delete)
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
        endAnimation(binding.add)

        offsetY += binding.edit.height * -1.5f
        ObjectAnimator.ofFloat(binding.delete, "translationY", offsetY, 0f)
            .setDuration(500).start()
        endAnimation(binding.delete)
    }

    private fun startAnimation(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f, 1.5f).setDuration(1000)
        scaleX.interpolator = BounceInterpolator()
        scaleX.start()
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f, 1.5f).setDuration(1000)
        scaleY.interpolator = BounceInterpolator()
        scaleY.start()
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(500).start()
    }

    private fun endAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f).setDuration(500).start()
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f).setDuration(500).start()
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(500).start()
    }

    fun swapFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .setReorderingAllowed(true)
            .addToBackStack(tag)
            .replace(R.id.container_fragment_home, clazz, bundle, tag)
            .commit()
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