package com.mio.databinding.model

import androidx.databinding.ObservableField
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftResolution

data class ObservableMinecraftProfile(
    var name: ObservableField<String> = ObservableField(),
    var type: ObservableField<String> = ObservableField(),
    var created: ObservableField<String> = ObservableField(),
    var lastUsed: ObservableField<String> = ObservableField(),
    var icon: ObservableField<String> = ObservableField(),
    var lastVersionId: ObservableField<String> = ObservableField(),
    var gameDir: ObservableField<String> = ObservableField("./.minecraft/game/default"),
    var javaDir: ObservableField<String> = ObservableField(),
    var javaArgs: ObservableField<String> = ObservableField(),
    var logConfig: String? = null,
    var logConfigIsXML: Boolean = false,
    var pojavRendererName: ObservableField<String> = ObservableField(),
    var controlFile: ObservableField<String> = ObservableField(""),
    var resolution: Array<MinecraftResolution>? = null
) {
    companion object {
        fun createNew(): ObservableMinecraftProfile {
            return ObservableMinecraftProfile().apply {
                name.set("New")
                lastVersionId.set("latest-release")
            }
        }

        fun createDefault(): ObservableMinecraftProfile {
            return ObservableMinecraftProfile().apply {
                name.set("Default")
                lastVersionId.set("1.7.10")
            }
        }
    }

    constructor(profile: MinecraftProfile) : this() {
        name.set(profile.name)
        name.set(profile.name)
        type.set(profile.type)
        created.set(profile.created)
        lastUsed.set(profile.lastUsed)
        icon.set(profile.icon)
        lastVersionId.set(profile.lastVersionId)
        gameDir.set(profile.gameDir)
        javaDir.set(profile.javaDir)
        javaArgs.set(profile.javaArgs)
        logConfig = profile.logConfig
        logConfigIsXML = profile.logConfigIsXML
        pojavRendererName.set(profile.pojavRendererName)
        controlFile.set(profile.controlFile)
        resolution = profile.resolution
    }

    fun convert(): MinecraftProfile {
        val profile = MinecraftProfile()
        profile.name = name.get()
        profile.type = type.get()
        profile.created = created.get()
        profile.lastUsed = lastUsed.get()
        profile.icon = icon.get()
        profile.lastVersionId = lastVersionId.get()
        profile.gameDir = gameDir.get()
        profile.javaDir = javaDir.get()
        profile.javaArgs = javaArgs.get()
        profile.logConfig = logConfig
        profile.logConfigIsXML = logConfigIsXML
        profile.pojavRendererName = pojavRendererName.get()
        profile.controlFile = controlFile.get()
        profile.resolution = resolution
        return profile
    }
}
