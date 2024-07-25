package com.mio.fragments

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kdt.mcgui.McVersionSpinner
import net.kdt.pojavlaunch.R

class HomeFragment() : Fragment(R.layout.fragment_home), OnClickListener {
    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var versionSpinner: McVersionSpinner
    private lateinit var gameSetting: Button
    private lateinit var pathSetting: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionSpinner = view.findViewById(R.id.mc_version_spinner)
        gameSetting = view.findViewById(R.id.game_setting)
        pathSetting = view.findViewById(R.id.path_setting)
    }

    override fun onResume() {
        super.onResume()
        versionSpinner.reloadProfiles();
    }

    override fun onClick(v: View?) {
        if (v == gameSetting) {
            versionSpinner.openProfileEditor(requireActivity())
        } else if (v == pathSetting) {

        }
    }
}