package com.mio.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.transition.Slide
import android.transition.Transition
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.fragment.app.Fragment
import com.mio.fragments.ProfileEditFragment
import com.mio.managers.PathManager
import com.mio.utils.AnimUtil
import com.mio.utils.AnimUtil.Companion.interpolator
import com.mio.utils.FragmentUtil
import fr.spse.extended_view.ExtendedTextView
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.profiles.ProfileAdapter
import net.kdt.pojavlaunch.profiles.ProfileAdapterExtra

class McVersionSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ExtendedTextView(context, attrs, defStyleAttr) {
    private lateinit var listView: ListView
    private lateinit var popupWindow: PopupWindow
    private var popupAnimation: Transition? = null
    var selectedIndex = 0
    val profileAdapter = ProfileAdapter(
        arrayOf(
            ProfileAdapterExtra(
                VERSION_SPINNER_PROFILE_CREATE,
                R.string.create_profile,
                ResourcesCompat.getDrawable(resources, R.drawable.ic_add, context.theme)
            ),
        )
    )

    init {
        createPopupWindow()
        init()
    }

    fun setProfileSelection(position: Int) {
        setSelection(position)
        LauncherPreferences.DEFAULT_PREF.edit()
            .putString(
                LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                profileAdapter.getItem(position).toString()
            )
            .apply()
    }

    fun setSelection(position: Int) {
        listView.setSelection(position)
        profileAdapter.setView(this, profileAdapter.getItem(position), false)
        selectedIndex = position
    }

    fun openProfileEditor(fragment: Fragment) {
        val currentSelection = profileAdapter.getItem(selectedIndex)
        if (currentSelection is ProfileAdapterExtra) {
            performExtraAction(currentSelection)
        } else {
            FragmentUtil.swapFragment(
                fragment.childFragmentManager,
                R.id.container_fragment_home,
                ProfileEditFragment::class.java,
                ProfileEditFragment.TAG
            )
        }
    }

    fun reloadProfiles() {
        profileAdapter.reloadProfiles()
    }

    private fun init() {
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen._12ssp).toFloat()
        )
        gravity = Gravity.CENTER_VERTICAL
        val startPadding = context.resources.getDimensionPixelOffset(R.dimen._17sdp)
        val endPadding = context.resources.getDimensionPixelOffset(R.dimen._5sdp)
        setPaddingRelative(startPadding, 0, endPadding, 0)
        compoundDrawablePadding = startPadding

        setProfileSelection(
            profileAdapter.resolveProfileIndex(
                LauncherPreferences.DEFAULT_PREF.getString(
                    LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                    ""
                )
            )
        )

        // Popup window behavior
        setOnClickListener(object : OnClickListener {
            override fun onClick(v: View) {
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                    return
                }
                val xOffset = (x + width + 5).toInt()
                val yOffset = (y + height).toInt()
                popupWindow.showAsDropDown(this@McVersionSpinner, xOffset, yOffset)
                AnimUtil.playScaleX(listView, 500, 0f, 1f).interpolator(BounceInterpolator())
                    .start()
                AnimUtil.playScaleY(listView, 500, 0f, 1f).interpolator(BounceInterpolator())
                    .start()
                post { listView.setSelection(selectedIndex) }
            }
        })
        PathManager.observablePath.addOnPropertyChangedCallback(object :
            OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                reloadProfiles()
                setProfileSelection(0)
            }
        })
    }

    private fun performExtraAction(extra: ProfileAdapterExtra) {
        if (extra.id == VERSION_SPINNER_PROFILE_CREATE) {
            ExtraCore.setValue(ExtraConstants.CREATE_NEW_PROFILE, true)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createPopupWindow() {
        listView = inflate(context, R.layout.spinner_mc_version, null) as ListView
        profileAdapter.setListView(listView)
        listView.adapter = profileAdapter
        listView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val item = profileAdapter.getItem(position)
                if (item is String) {
                    hidePopup(true)
                    setProfileSelection(position)
                } else if (item is ProfileAdapterExtra) {
                    hidePopup(false)
                    performExtraAction(item)
                }
            }
        val displayMetrics = resources.displayMetrics
        popupWindow = PopupWindow(
            listView,
            (displayMetrics.widthPixels / 5) * 2,
            (displayMetrics.heightPixels / 5) * 3
        )
        popupWindow.elevation = 5f
        popupWindow.isClippingEnabled = false
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss()
                return@setTouchInterceptor true
            }
            false
        }
        popupAnimation = Slide(Gravity.START)
        popupWindow.enterTransition = popupAnimation as Transition?
        popupWindow.exitTransition = popupAnimation as Transition?
    }

    fun hidePopup(animate: Boolean) {
        if (!animate) {
            popupWindow.enterTransition = null
            popupWindow.exitTransition = null
            popupWindow.dismiss()
            popupWindow.enterTransition = popupAnimation as Transition?
            popupWindow.exitTransition = popupAnimation as Transition?
        } else {
            popupWindow.dismiss()
        }
    }

    companion object {
        private const val VERSION_SPINNER_PROFILE_CREATE = 0
    }
}
