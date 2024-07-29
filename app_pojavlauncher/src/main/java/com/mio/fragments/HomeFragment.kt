package com.mio.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentHomeBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore

class HomeFragment() : Fragment(R.layout.fragment_home), OnClickListener {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        binding.userIcon.setOnClickListener(this)
        binding.gameSetting.setOnClickListener(this)
        binding.pathSetting.setOnClickListener(this)
        binding.start.setOnClickListener(this)
        binding.edit.setOnClickListener(this)

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
            binding.start -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true)
            binding.edit -> editAccount()
        }
    }

    private fun editAccount() {
        if (binding.accountSpinner.selectedAccount == null) {
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
            if (Build.VERSION.SDK_INT > 28){
                popupMenu.setForceShowIcon(true)
            }
            popupMenu.show()
        }
    }

    private fun swapFragment(clazz: Class<out Fragment>, tag: String, bundle: Bundle? = null) {
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