package com.mio.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mio.managers.PathManager
import com.mio.ui.adapters.PathSettingAdapter
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.FragmentPathSettingBinding

class PathSettingFragment : BaseFragment(R.layout.fragment_path_setting) {
    companion object {
        const val TAG = "PathSettingFragment"
    }

    private lateinit var binding: FragmentPathSettingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentID = R.id.container_fragment_home
        binding = FragmentPathSettingBinding.bind(view).apply {
            adapter = context?.let { PathSettingAdapter(it, PathManager.pathList) }
            recyclerView.layoutManager = LinearLayoutManager(context)
            add.setOnClickListener {
                FilePickFragment.setResultListener(this@PathSettingFragment,FilePickFragment.REQUEST_PICK_FILE) {
                    Log.e("测试",it.getString("file","file"))
                }
                swapParentFragment(FilePickFragment::class.java, FilePickFragment.TAG)
            }
        }
    }

}