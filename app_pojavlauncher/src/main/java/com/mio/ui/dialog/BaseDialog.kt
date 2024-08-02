package com.mio.ui.dialog

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import net.kdt.pojavlaunch.R


open class BaseDialog(context: Context):AppCompatDialog(context,R.style.DialogStyle) {
    init {
        window?.setWindowAnimations(R.style.DialogAnim)
        setFullScreen()
    }

    private fun setFullScreen() {
        val window = window
        if (window != null) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
            setSystemUiVisibility(window.decorView)
        }
    }

    private fun setSystemUiVisibility(decorView: View) {
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        decorView.systemUiVisibility = flags
    }
}