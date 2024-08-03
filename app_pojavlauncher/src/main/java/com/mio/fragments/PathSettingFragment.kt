package com.mio.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import com.mio.databinding.model.Path
import com.mio.managers.PathManager
import com.mio.ui.adapters.PathSettingAdapter
import com.mio.ui.dialog.RenameDialog
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentPathSettingBinding

class PathSettingFragment : BaseFragment(R.layout.fragment_path_setting) {
    companion object {
        const val TAG = "PathSettingFragment"
    }

    private lateinit var binding: FragmentPathSettingBinding
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentID = R.id.container_fragment_home
        binding = FragmentPathSettingBinding.bind(view).apply {
            adapter = PathSettingAdapter(requireContext(), PathManager.pathList)
            recyclerView.layoutManager = LinearLayoutManager(context)
            add.setOnClickListener {
                FilePickFragment.setResultListener(
                    this@PathSettingFragment,
                    FilePickFragment.REQUEST_PICK_FOLDER
                ) {
                    RenameDialog(
                        requireContext(),
                        requireContext().getString(R.string.rename)
                    ).apply {
                        onConfirm = { str ->
                            PathManager.pathList.add(
                                Path(
                                    ObservableField(str), ObservableField(it.getString("file", "")),
                                    ObservableBoolean(false)
                                )
                            )
                            PathManager.save()
                            adapter?.notifyDataSetChanged()
                        }
                        show()
                    }
                }
                FilePickFragment.openPicker(
                    parentFragmentManager, parentID,
                    Environment.getExternalStorageDirectory().absolutePath, false
                )
            }
            adapter?.folderClickListener = { pos ->
                PathManager.pathList[pos].path.let {
                    FilePickFragment.setResultListener(
                        this@PathSettingFragment,
                        FilePickFragment.REQUEST_PICK_FOLDER
                    ) { bundle ->
                        it.set(bundle.getString("file", ""))
                    }
                    it.get()?.let { value ->
                        FilePickFragment.openPicker(
                            parentFragmentManager, parentID,
                            value, false
                        )
                    }
                }
            }
            refresh.setOnClickListener {
                PathManager.load()
                adapter?.notifyDataSetChanged()
            }
        }
    }

}