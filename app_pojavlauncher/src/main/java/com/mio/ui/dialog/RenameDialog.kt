package com.mio.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.databinding.DialogRenameBinding

class RenameDialog(context: Context, var title: String) : BaseDialog(context) {
    var onConfirm: ((String) -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null)
        val bind = DataBindingUtil.bind<DialogRenameBinding>(view)
        setContentView(view)
        bind?.apply {
            title.setText(this@RenameDialog.title)
            confirm.setOnClickListener {
                if (edit.text.toString().trim() != "") {
                    dismiss()
                    onConfirm?.invoke(edit.text.toString())
                }
            }
            cancle.setOnClickListener {
                dismiss()
                onCancel?.invoke()
            }
        }
    }
}