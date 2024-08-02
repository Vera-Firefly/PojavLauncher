package com.mio.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.databinding.ObservableField
import net.kdt.pojavlaunch.Tools

object PrefManager {
    private const val PREF_NAME = "infinity_pref"
    private lateinit var pref: SharedPreferences
    private var isInit = false

    @JvmStatic
    fun load(context: Context) {
        if (isInit) return
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        isInit = true
    }

    @JvmStatic
    fun edit(): SharedPreferences.Editor {
        return pref.edit()
    }

    @JvmStatic
    fun getCurrentPath(): String {
        return pref.getString("currentPath", Tools.DIR_GAME_HOME) ?: Tools.DIR_GAME_HOME
    }

    @JvmStatic
    fun setCurrentPath(path: String) {
        edit().apply {
            putString("currentPath", path)
            apply()
        }
    }
}