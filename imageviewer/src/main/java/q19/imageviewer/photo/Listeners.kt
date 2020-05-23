package q19.imageviewer.photo

import android.view.View

typealias ViewDoubleTapListener = (view: View, x: Float, y: Float) -> Unit

typealias ViewDragListener = (dx: Float, dy: Float) -> Unit