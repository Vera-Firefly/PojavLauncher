package com.mio.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import net.kdt.pojavlaunch.R

class EditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    init {
        context.obtainStyledAttributes(intArrayOf(R.attr.themeColorPrimary)).apply {
            setTextColor(getColor(0, getContext().getColor(R.color.theme_color_primary)))
            recycle()
        }
    }
}