package com.mio.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McVersionSpinner
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
    }

    override fun onResume() {
        super.onResume()
        binding.mcVersionSpinner.reloadProfiles();
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.userIcon -> ExtraCore.setValue(ExtraConstants.SELECT_AUTH_METHOD, true)
            binding.gameSetting -> binding.mcVersionSpinner.openProfileEditor(requireActivity())
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
            AlertDialog.Builder(requireActivity())
                .setMessage(R.string.warning_remove_account)
                .setPositiveButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.global_delete) { _, _ ->
                    binding.accountSpinner.removeCurrentAccount()
                }
                .show();
        }

    }
}