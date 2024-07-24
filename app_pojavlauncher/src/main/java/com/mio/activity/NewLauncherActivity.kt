package com.mio.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mio.fragments.HomeFragment
import net.kdt.pojavlaunch.BaseActivity
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.fragments.MainMenuFragment
import kotlin.system.exitProcess

class NewLauncherActivity : BaseActivity(), OnClickListener {
    private lateinit var toolbar: Toolbar
    private lateinit var navMain: BottomNavigationView
    private lateinit var exit: Button
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

        initUI()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, HomeFragment::class.java, null, HomeFragment.TAG)
            .commit()
    }

    private fun initUI() {
        toolbar = findViewById(R.id.toolbar)
        navMain = findViewById(R.id.nav_main)
        exit = findViewById(R.id.exit)
        fragmentContainer = findViewById(R.id.fragment_container)

        setSupportActionBar(toolbar)
        navMain.setOnItemSelectedListener {
            val id = it.itemId
            if (id == R.id.home) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment::class.java, null, HomeFragment.TAG)
                    .commit()
            } else if (id == R.id.download) {

            } else if (id == R.id.setting) {

            }
            return@setOnItemSelectedListener true;
        }
        exit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == exit) {
            finish()
            exitProcess(0)
        }
    }

}