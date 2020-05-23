/*
 * Copyright 2018 stfalcon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package q19.imageviewer.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup

internal val View?.localVisibleRect: Rect
    get() = Rect().also { this?.getLocalVisibleRect(it) }

internal val View?.globalVisibleRect: Rect
    get() = Rect().also { this?.getGlobalVisibleRect(it) }

internal val View?.hitRect: Rect
    get() = Rect().also { this?.getHitRect(it) }

internal val View?.isRectVisible: Boolean
    get() = this != null && globalVisibleRect != localVisibleRect

internal val View?.isVisible: Boolean
    get() = this != null && visibility == View.VISIBLE

internal fun View.makeVisible() {
    visibility = View.VISIBLE
}

internal fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

internal fun View.makeGone() {
    visibility = View.GONE
}

internal fun View.applyMargin(
    start: Int? = null,
    top: Int? = null,
    end: Int? = null,
    bottom: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            marginStart = start ?: marginStart
            topMargin = top ?: topMargin
            marginEnd = end ?: marginEnd
            bottomMargin = bottom ?: bottomMargin
        }
    }
}

internal fun View.switchVisibilityWithAnimation() {
    val isVisible = visibility == View.VISIBLE
    val from = if (isVisible) 1.0f else 0.0f
    val to = if (isVisible) 0.0f else 1.0f

    ObjectAnimator.ofFloat(this, "alpha", from, to).apply {
        duration = ViewConfiguration.getDoubleTapTimeout().toLong()

        if (isVisible) {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    makeGone()
                }
            })
        } else {
            makeVisible()
        }

        start()
    }
}
