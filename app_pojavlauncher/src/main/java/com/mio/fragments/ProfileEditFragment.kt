package com.mio.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.mio.databinding.model.ObservableMinecraftProfile
import com.mio.managers.PathManager
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentProfileEditBinding
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.multirt.MultiRTUtils
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter
import net.kdt.pojavlaunch.multirt.Runtime
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.profiles.ProfileIconCache
import net.kdt.pojavlaunch.profiles.VersionSelectorDialog
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles
import java.util.Arrays

class ProfileEditFragment : BaseFragment(R.layout.fragment_profile_edit), OnClickListener {
    companion object {
        const val TAG = "ProfileEditFragment"
        const val DELETED_PROFILE = "deleted_profile"
    }

    private lateinit var bind: FragmentProfileEditBinding
    private lateinit var tempProfile: ObservableMinecraftProfile
    private lateinit var profileKey: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentID = if (arguments == null) R.id.container_fragment_home else R.id.container_fragment_download
        load()
        bind = FragmentProfileEditBinding.bind(view).apply {
            profile = tempProfile
            fragment = this@ProfileEditFragment
            icon.setImageDrawable(
                ProfileIconCache.fetchIcon(
                    resources,
                    profileKey,
                    tempProfile.icon.get()
                )
            )
        }
        initEdit()
        initSpinner()
    }

    private fun load() {
        if (arguments == null) {
            LauncherPreferences.DEFAULT_PREF.getString(
                LauncherPreferences.PREF_KEY_CURRENT_PROFILE, ""
            )!!.let {
                profileKey = it
            }
            LauncherProfiles.mainProfileJson.profiles[profileKey]?.let {
                tempProfile = ObservableMinecraftProfile(it)
            }
        } else {
            tempProfile = ObservableMinecraftProfile.createNew()
            profileKey = LauncherProfiles.getFreeProfileKey()
        }
    }

    private fun save() {
        LauncherProfiles.mainProfileJson.profiles[profileKey] = tempProfile.convert()
        LauncherProfiles.write()
        ExtraCore.setValue(ExtraConstants.REFRESH_VERSION_SPINNER, profileKey)
    }

    override fun onClick(v: View) {
        bind.apply {
            when (v.id) {
                R.id.isolate -> tempProfile.gameDir.set("./.minecraft/game/" + name.text.toString())
                R.id.version -> {
                    VersionSelectorDialog.open(v.context, false ){ id: String, snapshot: Boolean ->
                        tempProfile.lastVersionId.set(id)
                    }
                }

                R.id.ctrl -> {
                    FilePickFragment.setResultListener(
                        this@ProfileEditFragment,
                        FilePickFragment.REQUEST_PICK_FILE
                    ) {
                        val file = it.getString("file", "").replace(Tools.CTRLMAP_PATH, ".")
                        tempProfile.controlFile.set(file)
                        ctrlText.text = file
                    }
                    FilePickFragment.openPicker(parentFragmentManager, parentID, Tools.CTRLMAP_PATH)
                }

                R.id.path_button -> {
                    FilePickFragment.setResultListener(
                        this@ProfileEditFragment,
                        FilePickFragment.REQUEST_PICK_FILE
                    ) {
                        val file = it.getString("file", "").replace(PathManager.getCurrentPath(), ".")
                        tempProfile.gameDir.set(file)
                    }
                    FilePickFragment.openPicker(
                        parentFragmentManager,
                        parentID,
                        PathManager.getCurrentPath(),
                        false
                    )
                }

                R.id.save -> {
                    ProfileIconCache.dropIcon(profileKey)
                    save()
                    if (parentFragment is HomeFragment) {
                        val fragment = parentFragment as HomeFragment?
                        val versionSpinner = fragment!!.binding.mcVersionSpinner
                        versionSpinner.profileAdapter.notifyDataSetChanged()
                        versionSpinner.setSelection(versionSpinner.mSelectedIndex)
                    }
                    parentFragmentManager.popBackStack()
                }

                R.id.delete -> {
                    if (LauncherProfiles.mainProfileJson.profiles.size > 1) {
                        ProfileIconCache.dropIcon(profileKey)
                        LauncherProfiles.mainProfileJson.profiles.remove(profileKey)
                        LauncherProfiles.write()
                        ExtraCore.setValue(
                            ExtraConstants.REFRESH_VERSION_SPINNER,
                            DELETED_PROFILE
                        )
                        if (parentFragment is HomeFragment) {
                            val fragment = parentFragment as HomeFragment?
                            val versionSpinner = fragment!!.binding.mcVersionSpinner
                            versionSpinner.profileAdapter.notifyDataSetChanged()
                            versionSpinner.setSelection(versionSpinner.mSelectedIndex - 1)
                        }
                    }

                    parentFragmentManager.popBackStack()
                }

                R.id.cancel -> parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initEdit() {
        bind.apply {
            name.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    tempProfile.name.set(name.text.toString())
                }

            })
            jreArgs.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (jreArgs.text.toString().isEmpty()) {
                        tempProfile.javaArgs.set(null)
                    } else {
                        tempProfile.javaArgs.set(jreArgs.text.toString())
                    }
                }
            })
        }
    }

    private fun initSpinner() {
        bind.apply {
            val runtimes = MultiRTUtils.getRuntimes()
            var runtimeIndex = -1
            if (tempProfile.javaDir.get() != null) {
                val selectedRuntime =
                    tempProfile.javaDir.get()!!.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length)
                val nIndex = runtimes.indexOf(Runtime(selectedRuntime))
                if (nIndex != -1) runtimeIndex = nIndex
            }
            runtimeSpinner.setAdapter(RTSpinnerAdapter(requireContext(), runtimes))
            if (runtimeIndex == -1) runtimeIndex = runtimes.size - 1
            runtimeSpinner.setSelection(runtimeIndex)
            runtimeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = runtimeSpinner.selectedItem as Runtime
                    tempProfile.javaDir.set(
                        if ((selected.name == "<Default>" || selected.versionString == null)
                        ) null else Tools.LAUNCHERPROFILES_RTPREFIX + selected.name
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            val renderersList = Tools.getCompatibleRenderers(requireContext())
            val rendererNameList = renderersList.rendererIds
            val renderList: MutableList<String> =
                ArrayList(renderersList.rendererDisplayNames.size + 1)
            renderList.addAll(Arrays.asList(*renderersList.rendererDisplayNames))
            renderList.add(requireContext().getString(R.string.global_default))
            rendererSpinner.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.item_simple_list_1,
                    renderList
                )
            )
            var rendererIndex: Int = rendererSpinner.adapter.count - 1
            if (tempProfile.pojavRendererName.get() != null) {
                val index: Int = rendererNameList.indexOf(tempProfile.pojavRendererName.get())
                if (index != -1) rendererIndex = index
            }
            rendererSpinner.setSelection(rendererIndex)
            rendererSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == rendererNameList.size) {
                        tempProfile.pojavRendererName.set(null)
                    } else {
                        tempProfile.pojavRendererName.set(rendererNameList[position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
}