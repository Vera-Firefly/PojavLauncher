package com.mio.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View

class AnimUtil {

    companion object {
        fun playTranslationY(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "translationY", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }

        fun playTranslationX(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "translationX", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }

        fun playRotation(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "rotation", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }

        fun playScaleX(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "scaleX", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }

        fun playScaleY(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "scaleY", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }

        fun playAlpha(
            view: View,
            duration: Long,
            interpolator: TimeInterpolator? = null,
            vararg values: Float
        ): Animator {
            val animator = ObjectAnimator.ofFloat(view, "alpha", *values)
                .setDuration(duration)
            if (interpolator != null) {
                animator.interpolator = interpolator
            }
            return animator
        }
    }
}