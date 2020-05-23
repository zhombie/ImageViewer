@file:Suppress("unused")

package q19.imageviewer.photo

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector.OnDoubleTapListener
import androidx.appcompat.widget.AppCompatImageView

/**
 * A zoomable ImageView. See [PhotoViewAttacher] for most of the details on how the zooming
 * is accomplished
 */
internal class PhotoView @JvmOverloads constructor(
    context: Context?,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attr, defStyle) {

    /**
     * Get the current [PhotoViewAttacher] for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    private var attacher: PhotoViewAttacher? = null

    private var pendingScaleType: ScaleType? = null

    init {
        attacher = PhotoViewAttacher(this)
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX)
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            scaleType = pendingScaleType
            pendingScaleType = null
        }
    }

    override fun getScaleType(): ScaleType? {
        return attacher?.scaleType
    }

    override fun getImageMatrix(): Matrix? {
        return attacher?.imageMatrix
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        l?.let { attacher?.setOnLongClickListener(it) }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        l?.let { attacher?.setOnClickListener(it) }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (attacher == null) {
            pendingScaleType = scaleType
        } else {
            attacher?.scaleType = scaleType
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method
        attacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        attacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        attacher?.update()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            attacher?.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        attacher?.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        attacher?.setRotationBy(rotationDegree)
    }

    var isZoomable: Boolean
        get() = attacher?.isZoomable ?: false
        set(value) {
            attacher?.isZoomable = value
        }

    val displayRect: RectF?
        get() = attacher?.displayRect

    fun getDisplayMatrix(matrix: Matrix) {
        attacher?.getDisplayMatrix(matrix)
    }

    fun setDisplayMatrix(finalRectangle: Matrix?): Boolean {
        return attacher?.setDisplayMatrix(finalRectangle) ?: false
    }

    fun getSuppMatrix(matrix: Matrix) {
        attacher?.getSuppMatrix(matrix)
    }

    fun setSuppMatrix(matrix: Matrix?): Boolean {
        return attacher?.setDisplayMatrix(matrix) ?: false
    }

    var minimumScale: Float
        get() = attacher?.minimumScale ?: 0F
        set(value) {
            attacher?.minimumScale = value
        }

    var mediumScale: Float
        get() = attacher?.mediumScale ?: 0F
        set(value) {
            attacher?.mediumScale = value
        }

    var maximumScale: Float
        get() = attacher?.maximumScale ?: 0F
        set(value) {
            attacher?.maximumScale = value
        }

    var scale: Float
        get() = attacher?.scale ?: 0F
        set(value) {
            attacher?.scale = value
        }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        attacher?.setAllowParentInterceptOnEdge(allow)
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        attacher?.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
        attacher?.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
        attacher?.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener) {
        attacher?.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener) {
        attacher?.setOnViewTapListener(listener)
    }

    fun setOnViewDoubleTapListener(listener: ViewDoubleTapListener) {
        attacher?.setOnViewDoubleTapListener(listener)
    }

    fun setOnViewDragListener(listener: ViewDragListener) {
        attacher?.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float, animate: Boolean) {
        attacher?.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        attacher?.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        attacher?.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: OnDoubleTapListener) {
        attacher?.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener) {
        attacher?.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener) {
        attacher?.setOnSingleFlingListener(onSingleFlingListener)
    }

}