package com.mio.fragments

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McVersionSpinner
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentHomeBinding

class HomeFragment() : Fragment(R.layout.fragment_home), OnClickListener {
    companion object {
        const val TAG = "HomeFragment"
    }
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
    }

    override fun onResume() {
        super.onResume()
        binding.mcVersionSpinner.reloadProfiles();
    }

    override fun onClick(v: View?) {
        if (v == binding.gameSetting) {
            binding.mcVersionSpinner.openProfileEditor(requireActivity())
        } else if (v == binding.pathSetting) {

        }
    }
}