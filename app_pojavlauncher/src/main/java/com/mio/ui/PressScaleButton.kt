package com.mio.ui

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import net.kdt.pojavlaunch.R

class PressScaleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {
    init {
        background = ResourcesCompat.getDrawable(resources, R.drawable.background_button, context.theme)
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.xml.animate_scale)
    }
}