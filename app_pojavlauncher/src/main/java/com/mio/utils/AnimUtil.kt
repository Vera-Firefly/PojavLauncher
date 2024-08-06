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
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "translationY", *values).apply {
                this.duration = duration
            }
        }

        fun playTranslationX(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "translationX", *values).apply {
                this.duration = duration
            }
        }

        fun playTranslationZ(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "translationZ", *values).apply {
                this.duration = duration
            }
        }

        fun playRotation(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "rotation", *values).apply {
                this.duration = duration
            }
        }

        fun playScaleX(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "scaleX", *values).apply {
                this.duration = duration
            }
        }

        fun playScaleY(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "scaleY", *values).apply {
                this.duration = duration
            }
        }

        fun playAlpha(
            view: View,
            duration: Long,
            vararg values: Float
        ): ObjectAnimator {
            return ObjectAnimator.ofFloat(view, "alpha", *values).apply {
                this.duration = duration
            }
        }

        fun ObjectAnimator.delay(delayTime: Long): ObjectAnimator {
            this.startDelay = delayTime
            return this
        }

        fun ObjectAnimator.interpolator(interpolator: TimeInterpolator): ObjectAnimator {
            this.interpolator = interpolator
            return this
        }
    }
}