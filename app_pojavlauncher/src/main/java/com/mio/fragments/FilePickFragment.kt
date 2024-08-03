package com.mio.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mio.ui.adapters.FilePickAdapter
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.databinding.FragmentFilePickBinding

class FilePickFragment : BaseFragment(R.layout.fragment_file_pick) {
    companion object {
        const val TAG = "FilePickFragment"
        const val REQUEST_PICK_FILE = "REQUEST_PICK_FILE"
        const val BUNDLE_START_PATH = "BUNDLE_START_PATH"
        const val BUNDLE_SELECT_FILE = "BUNDLE_SELECT_FILE"
        const val BUNDLE_SELECT_FOLDER = BUNDLE_SELECT_FILE

        fun setResultListener(
            fragment: Fragment,
            key: String,
            listener: (Bundle) -> Unit
        ) {
            val l: ((requestKey: String, bundle: Bundle) -> Unit) = { requestKey, bundle ->
                if (key == requestKey) {
                    listener.invoke(bundle)
                }
            }
            fragment.setFragmentResultListener(key, l)
        }
    }

    private lateinit var bind: FragmentFilePickBinding
    private var startPath: String = Tools.DIR_GAME_HOME
    private var isSelectFile = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseBundle()
        bind = FragmentFilePickBinding.bind(view).apply {
            adapter = context?.let { FilePickAdapter(it, startPath) }
            recyclerView.layoutManager = LinearLayoutManager(context)
            if (isSelectFile) {
                selectFolder.visibility = View.GONE
            }
            adapter?.listener = {
                Bundle().apply {
                    putString("file", it.absolutePath)
                    setFragmentResult(REQUEST_PICK_FILE, this)
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun parseBundle() {
        arguments?.apply {
            startPath = getString(BUNDLE_START_PATH, Tools.DIR_GAME_HOME)
            isSelectFile = getBoolean(BUNDLE_SELECT_FILE, true)
        }
    }

}