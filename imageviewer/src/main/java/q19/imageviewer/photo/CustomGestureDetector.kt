package q19.imageviewer.photo

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.VelocityTracker
import android.view.ViewConfiguration
import q19.imageviewer.photo.Util.getPointerIndex
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

internal class CustomGestureDetector(
    context: Context,
    listener: OnGestureListener
) {

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    private val mDetector: ScaleGestureDetector
    private var mVelocityTracker: VelocityTracker? = null
    var isDragging = false
        private set
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private val mTouchSlop: Float
    private val mMinimumVelocity: Float
    private val mListener: OnGestureListener

    init {
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()

        mListener = listener
        val mScaleListener: OnScaleGestureListener = object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (isNaN(scaleFactor) || isInfinite(scaleFactor)) return false
                if (scaleFactor >= 0) {
                    mListener.onScale(scaleFactor, detector.focusX, detector.focusY)
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // NO-OP
            }
        }
        mDetector = ScaleGestureDetector(context, mScaleListener)
    }

    private fun getActiveX(motionEvent: MotionEvent): Float {
        return try {
            motionEvent.getX(mActivePointerIndex)
        } catch (e: Exception) {
            motionEvent.x
        }
    }

    private fun getActiveY(motionEvent: MotionEvent): Float {
        return try {
            motionEvent.getY(mActivePointerIndex)
        } catch (e: Exception) {
            motionEvent.y
        }
    }

    val isScaling: Boolean
        get() = mDetector.isInProgress

    fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        return try {
            mDetector.onTouchEvent(motionEvent)
            processTouchEvent(motionEvent)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is called
            true
        }
    }

    private fun processTouchEvent(motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = motionEvent.getPointerId(0)
                mVelocityTracker = VelocityTracker.obtain()
                mVelocityTracker?.addMovement(motionEvent)
                mLastTouchX = getActiveX(motionEvent)
                mLastTouchY = getActiveY(motionEvent)
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(motionEvent)
                val y = getActiveY(motionEvent)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = sqrt((dx * dx) + (dy * dy)) >= mTouchSlop
                }
                if (isDragging) {
                    mListener.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y
                    mVelocityTracker?.addMovement(motionEvent)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                // Recycle Velocity Tracker
                mVelocityTracker?.recycle()
                mVelocityTracker = null
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    mVelocityTracker?.let { velocityTracker ->
                        mLastTouchX = getActiveX(motionEvent)
                        mLastTouchY = getActiveY(motionEvent)

                        // Compute velocity within the last 1000ms
                        velocityTracker.addMovement(motionEvent)
                        velocityTracker.computeCurrentVelocity(1000)
                        val vX = velocityTracker.xVelocity
                        val vY = velocityTracker.yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (max(abs(vX), abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                mVelocityTracker?.recycle()
                mVelocityTracker = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = getPointerIndex(motionEvent.action)
                val pointerId = motionEvent.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = motionEvent.getPointerId(newPointerIndex)
                    mLastTouchX = motionEvent.getX(newPointerIndex)
                    mLastTouchY = motionEvent.getY(newPointerIndex)
                }
            }
        }
        mActivePointerIndex = motionEvent
            .findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        return true
    }

}