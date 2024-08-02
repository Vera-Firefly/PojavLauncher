package com.mio.managers

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mio.databinding.model.Path
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import java.io.File
import java.io.FileReader

object PathManager {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val path = Tools.DIR_DATA + "/path_list.json"
    val pathList: MutableList<Path> = mutableListOf()
    @JvmStatic
    val observablePath = ObservableField<String>()

    @JvmStatic
    fun load() {
        pathList.clear()
        File(path).apply {
            if (exists()) {
                val fromJson =
                    GSON.fromJson<List<Path>>(
                        FileReader(path),
                        object : TypeToken<List<Path>>() {}.type
                    )
                pathList.addAll(fromJson)
            } else {
                val json = GSON.toJson(
                    listOf(
                        Path(
                            ObservableField("PojavInfinity"),
                            ObservableField(Tools.DIR_GAME_HOME),
                            ObservableBoolean(true)
                        )
                    )
                )
                Tools.write(absolutePath, json)
                load()
            }
        }
        refreshPath()
    }

    @JvmStatic
    fun save() {
        File(path).apply {
            val json = GSON.toJson(pathList)
            Tools.write(absolutePath, json)
        }
    }

    @JvmStatic
    fun refreshPath() {
        Tools.DIR_GAME_NEW = getCurrentPath() + "/.minecraft"
        Tools.DIR_HOME_VERSION = Tools.DIR_GAME_NEW + "/versions"
        Tools.DIR_HOME_LIBRARY = Tools.DIR_GAME_NEW + "/libraries"
        Tools.DIR_HOME_CRASH = Tools.DIR_GAME_NEW + "/crash-reports"
        Tools.ASSETS_PATH = Tools.DIR_GAME_NEW + "/assets"
        Tools.OBSOLETE_RESOURCES_PATH = Tools.DIR_GAME_NEW + "/resources"
        LauncherProfiles.launcherProfilesFile = File(Tools.DIR_GAME_NEW, "launcher_profiles.json");
        observablePath.set(getCurrentPath())
    }

    @JvmStatic
    fun getCurrentPath():String {
        return PrefManager.getCurrentPath()
    }
}