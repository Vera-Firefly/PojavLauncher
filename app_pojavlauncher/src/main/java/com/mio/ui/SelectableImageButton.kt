package com.mio.ui

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import net.kdt.pojavlaunch.R

class SelectableImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    interface OnSelectListener {
        fun onSelected(v: SelectableImageButton)
    }

    var onSelectListener: OnSelectListener? = null

    init {
        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.xml.animate_scale_large)
        setOnClickListener() {
            if (!isSelected) {
                isSelected = true
                onSelectListener?.onSelected(it as SelectableImageButton)
                refreshTintColor()
            }
        }
    }

    fun refreshTintColor() {
        drawable.setTint(
            if (isSelected) context.getColor(R.color.theme_color_2) else
                context.getColor(R.color.theme_color)
        )
    }
}