package q19.imageviewer.photo

import android.view.View

internal typealias ViewDoubleTapListener = (view: View, x: Float, y: Float) -> Unit

internal typealias ViewDragListener = (dx: Float, dy: Float) -> Unit