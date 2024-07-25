package com.mio.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mio.fragments.DownloadFragment
import com.mio.fragments.HomeFragment
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.ActivityNewMainBinding
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment
import kotlin.system.exitProcess

class NewLauncherActivity : BaseActivity(), OnClickListener {
    private lateinit var binding: ActivityNewMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_main)
        initUI()
    }

    private fun initUI() {
        setSupportActionBar(binding.toolbar)
        binding.navMain.setOnItemSelectedListener {
            val id = it.itemId
            val fragment = supportFragmentManager.findFragmentById(binding.containerFragment.id)
            if (id == R.id.home) {
                if (fragment !is HomeFragment) {
                    swapFragment(HomeFragment::class.java, HomeFragment.TAG)
                }
            } else if (id == R.id.download) {
                if (fragment !is DownloadFragment) {
                    swapFragment(DownloadFragment::class.java, DownloadFragment.TAG)
                }
            } else if (id == R.id.setting) {
                if (fragment !is LauncherPreferenceFragment) {
                    swapFragment(LauncherPreferenceFragment::class.java, "SETTINGS_FRAGMENT")
                }
            }
            return@setOnItemSelectedListener true;
        }
        binding.exit.setOnClickListener(this)
        binding.navMain.selectedItemId = R.id.home
    }

    override fun onClick(v: View?) {
        if (v == binding.exit) {
            finish()
            exitProcess(0)
        }
    }

    private fun swapFragment(clazz: Class<out Fragment>, tag: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(
                R.id.container_fragment,
                clazz,
                null,
                tag
            )
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragment =
                supportFragmentManager.findFragmentByTag(MicrosoftLoginFragment.TAG) as MicrosoftLoginFragment?
            if (fragment != null && fragment.isVisible) {
                if (fragment.canGoBack()) {
                    fragment.goBack()
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onAttachedToWindow() {
        LauncherPreferences.computeNotchSize(this)
    }

}