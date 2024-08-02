package com.mio.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import net.kdt.pojavlaunch.R

class EditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    init {
        context.obtainStyledAttributes(
            intArrayOf(
                R.attr.themeColorPrimary,
                R.attr.themeColorSecondary
            )
        ).apply {
            setTextColor(getColor(0, getContext().getColor(R.color.theme_color_primary)))
            setHintTextColor(getColor(0, getContext().getColor(R.color.theme_color_secondary)))
            recycle()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textCursorDrawable = AppCompatResources.getDrawable(context, R.drawable.edit_cursor)
        }
    }
}