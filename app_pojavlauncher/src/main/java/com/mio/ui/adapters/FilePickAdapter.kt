package com.mio.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.mio.databinding.model.FileInfo
import com.mio.utils.AnimUtil
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.ItemFilePickBinding
import org.apache.commons.io.comparator.DirectoryFileComparator
import org.apache.commons.io.comparator.NameFileComparator
import java.io.File


class FilePickAdapter(val context: Context, private val startPath: String) :
    RecyclerView.Adapter<FilePickAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    private val fileList: MutableList<FileInfo> = mutableListOf()
    var currentPath = ObservableField(startPath)
    var listener: ((File) -> Unit)? = null

    init {
        fileList.addAll(getFileInfo(startPath))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemFilePickBinding>(
                LayoutInflater.from(context),
                R.layout.item_file_pick,
                parent,
                false
            ).root
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        DataBindingUtil.getBinding<ItemFilePickBinding>(holder.itemView)?.apply {
            fileInfo = fileList[position]
            executePendingBindings()
        }
        holder.itemView.setOnClickListener { _ ->
            fileList[position].file.get()?.apply {
                gotoDir(this)
            }
        }
        AnimUtil.playTranslationX(holder.itemView, 200, null, -100f, 0f).start()
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun gotoDir(path: File) {
        path.apply {
            if (isDirectory) {
                currentPath.set(absolutePath)
                fileList.clear()
                currentPath.get()?.let {
                    if (it != startPath) {
                        parentFile?.let { par ->
                            fileList.add(getBackItem(par))
                        }
                    }
                    fileList.addAll(getFileInfo(it))
                }
                notifyDataSetChanged()
            } else {
                listener?.invoke(this)
            }
        }
    }

    fun gotoDir(path: String) {
        gotoDir(File(path))
    }

    private fun getBackItem(file: File): FileInfo {
        return FileInfo(ObservableField("..."), ObservableField(file))
    }

    private fun getFileInfo(path: String): List<FileInfo> {
        return getFiles(path).map {
            FileInfo(ObservableField(it.name), ObservableField(it))
        }
    }

    private fun getFiles(path: String): MutableList<File> {
        return getFiles(File(path))
    }

    private fun getFiles(path: File): MutableList<File> {
        val list = mutableListOf<File>()
        if (path.exists() && path.isDirectory) {
            list.apply {
                path.listFiles()?.let { list.addAll(it) }
                sortWith(NameFileComparator.NAME_INSENSITIVE_COMPARATOR)
                sortWith(DirectoryFileComparator.DIRECTORY_COMPARATOR)
            }
        }
        return list
    }
}