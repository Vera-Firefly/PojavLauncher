package com.kdt.mcgui;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.transition.Slide;
import android.transition.Transition;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.mio.fragments.ProfileEditFragment;
import com.mio.managers.PathManager;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.profiles.ProfileAdapter;
import net.kdt.pojavlaunch.profiles.ProfileAdapterExtra;

import fr.spse.extended_view.ExtendedTextView;

/**
 * A class implementing custom spinner like behavior, notably:
 * dropdown popup view with a custom direction.
 */
public class McVersionSpinner extends ExtendedTextView {
    private static final int VERSION_SPINNER_PROFILE_CREATE = 0;

    public McVersionSpinner(@NonNull Context context) {
        super(context);
        init();
    }

    public McVersionSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public McVersionSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /* The class is in charge of displaying its own list with adapter content being known in advance */
    private ListView mListView = null;
    private PopupWindow mPopupWindow = null;
    private Object mPopupAnimation;
    public int mSelectedIndex;

    private final ProfileAdapter mProfileAdapter = new ProfileAdapter(new ProfileAdapterExtra[]{
            new ProfileAdapterExtra(VERSION_SPINNER_PROFILE_CREATE,
                    R.string.create_profile,
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add, getContext().getTheme())),
    });


    /**
     * Set the selection AND saves it as a shared preference
     */
    public void setProfileSelection(int position) {
        setSelection(position);
        LauncherPreferences.DEFAULT_PREF.edit()
                .putString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                        mProfileAdapter.getItem(position).toString())
                .apply();
    }

    public void setSelection(int position) {
        if (mListView != null) {
            mListView.setSelection(position);
        }
        mProfileAdapter.setView(this, mProfileAdapter.getItem(position), false);
        mSelectedIndex = position;
    }

    public void openProfileEditor(Fragment fragment) {
        Object currentSelection = mProfileAdapter.getItem(mSelectedIndex);
        if (currentSelection instanceof ProfileAdapterExtra) {
            performExtraAction((ProfileAdapterExtra) currentSelection);
        } else {
            fragment.getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .setReorderingAllowed(true)
                    .addToBackStack(ProfileEditFragment.TAG)
                    .replace(R.id.container_fragment_home, ProfileEditFragment.class, null, ProfileEditFragment.TAG)
                    .commit();
        }
    }

    /**
     * Reload profiles from the file, forcing the spinner to consider the new data
     */
    public void reloadProfiles() {
        mProfileAdapter.reloadProfiles();
    }

    /**
     * Initialize various behaviors
     */
    private void init() {
        // Setup various attributes
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._12ssp));
        setGravity(Gravity.CENTER_VERTICAL);
        int startPadding = getContext().getResources().getDimensionPixelOffset(R.dimen._17sdp);
        int endPadding = getContext().getResources().getDimensionPixelOffset(R.dimen._5sdp);
        setPaddingRelative(startPadding, 0, endPadding, 0);
        setCompoundDrawablePadding(startPadding);

        int profileIndex;
        String extra_value = (String) ExtraCore.consumeValue(ExtraConstants.REFRESH_VERSION_SPINNER);
        if (extra_value != null) {
            profileIndex = extra_value.equals(ProfileEditFragment.DELETED_PROFILE) ? 0
                    : getProfileAdapter().resolveProfileIndex(extra_value);
        } else {
            profileIndex = mProfileAdapter.resolveProfileIndex(
                    LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE, ""));
        }

        setProfileSelection(Math.max(0, profileIndex));

        // Popup window behavior
        setOnClickListener(new OnClickListener() {
            final int offset = -getContext().getResources().getDimensionPixelOffset(R.dimen._4sdp);
            @Override
            public void onClick(View v) {
                if (mPopupWindow == null) {
                    getPopupWindow();
                }

                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    return;
                }
                final int xOffset = (int) (getX() + getWidth() + 5);
                final int yOffset = (int) (getY() + getHeight());
                mPopupWindow.showAsDropDown(McVersionSpinner.this,  xOffset, yOffset);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mListView, "scaleX", 0, 1);
                scaleX.setDuration(500);
                scaleX.setInterpolator(new BounceInterpolator());
                scaleX.start();
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(mListView, "scaleY", 0, 1);
                scaleY.setDuration(500);
                scaleY.setInterpolator(new BounceInterpolator());
                scaleY.start();
                // Post() is required for the layout inflation phase
                post(() -> mListView.setSelection(mSelectedIndex));
            }
        });
        PathManager.getObservablePath().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                reloadProfiles();
                setProfileSelection(0);
            }
        });
    }

    private void performExtraAction(ProfileAdapterExtra extra) {
        //Replace with switch-case if you want to add more extra actions
        if (extra.id == VERSION_SPINNER_PROFILE_CREATE) {
            ExtraCore.setValue(ExtraConstants.CREATE_NEW_PROFILE, true);
        }
    }


    /**
     * Create the listView and popup window for the interface, and set up the click behavior
     */
    @SuppressLint("ClickableViewAccessibility")
    private void getPopupWindow() {
        mListView = (ListView) inflate(getContext(), R.layout.spinner_mc_version, null);
        mProfileAdapter.setListView(mListView);
        mListView.setAdapter(mProfileAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = mProfileAdapter.getItem(position);
            if (item instanceof String) {
                hidePopup(true);
                setProfileSelection(position);
            } else if (item instanceof ProfileAdapterExtra) {
                hidePopup(false);
                performExtraAction((ProfileAdapterExtra) item);
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mPopupWindow = new PopupWindow(mListView, (displayMetrics.widthPixels / 5) * 2, (displayMetrics.heightPixels / 5) * 3);
        mPopupWindow.setElevation(5);
        mPopupWindow.setClippingEnabled(false);

        // Block clicking outside of the popup window
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                mPopupWindow.dismiss();
                return true;
            }
            return false;
        });


        // Custom animation, nice slide in
        mPopupAnimation = new Slide(Gravity.START);
        mPopupWindow.setEnterTransition((Transition) mPopupAnimation);
        mPopupWindow.setExitTransition((Transition) mPopupAnimation);
    }

    public void hidePopup(boolean animate) {
        if (mPopupWindow == null) {
            return;
        }
        if (!animate) {
            mPopupWindow.setEnterTransition(null);
            mPopupWindow.setExitTransition(null);
            mPopupWindow.dismiss();
            mPopupWindow.setEnterTransition((Transition) mPopupAnimation);
            mPopupWindow.setExitTransition((Transition) mPopupAnimation);
        } else {
            mPopupWindow.dismiss();
        }
    }

    public ProfileAdapter getProfileAdapter() {
        return mProfileAdapter;
    }
}
