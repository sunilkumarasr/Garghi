package com.royalit.garghi.AdaptersAndModels.Categorys

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class ZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val zoomMatrix = Matrix()
    private val matrixValues = FloatArray(9)

    private var zoom = 1f
    private var baseScale = 1f
    private var lastX = 0f
    private var lastY = 0f

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScaleBegin(
                detector: ScaleGestureDetector
            ): Boolean {
                parent?.requestDisallowInterceptTouchEvent(true)
                return true
            }

            override fun onScale(
                detector: ScaleGestureDetector
            ): Boolean {
                val newZoom =
                    (zoom * detector.scaleFactor).coerceIn(1f, 5f)

                val factor = newZoom / zoom
                zoom = newZoom

                zoomMatrix.postScale(
                    factor,
                    factor,
                    detector.focusX,
                    detector.focusY
                )

                fixTranslation()
                imageMatrix = zoomMatrix

                return true
            }
        }
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(
                event: MotionEvent
            ): Boolean {
                performClick()
                return true
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                if (zoom > 1.01f) {
                    resetZoom()
                } else {
                    zoom = 3f

                    zoomMatrix.postScale(
                        zoom,
                        zoom,
                        event.x,
                        event.y
                    )

                    fixTranslation()
                    imageMatrix = zoomMatrix
                }

                return true
            }
        }
    )

    init {
        scaleType = ScaleType.MATRIX
        isClickable = true
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        // Run after initialization and after Glide sets the image.
        post { resetZoom() }
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetZoom()
    }

    fun resetZoom() {
        val image = drawable ?: return

        val imageWidth = image.intrinsicWidth
        val imageHeight = image.intrinsicHeight

        if (
            width <= 0 ||
            height <= 0 ||
            imageWidth <= 0 ||
            imageHeight <= 0
        ) {
            return
        }

        zoom = 1f

        baseScale = min(
            width.toFloat() / imageWidth,
            height.toFloat() / imageHeight
        )

        val offsetX = (width - imageWidth * baseScale) / 2f
        val offsetY = (height - imageHeight * baseScale) / 2f

        zoomMatrix.reset()
        zoomMatrix.postScale(baseScale, baseScale)
        zoomMatrix.postTranslate(offsetX, offsetY)

        imageMatrix = zoomMatrix
    }

    private fun fixTranslation() {
        val image = drawable ?: return

        zoomMatrix.getValues(matrixValues)

        val scaledWidth = image.intrinsicWidth * baseScale * zoom
        val scaledHeight = image.intrinsicHeight * baseScale * zoom

        val currentX = matrixValues[Matrix.MTRANS_X]
        val currentY = matrixValues[Matrix.MTRANS_Y]

        val targetX = if (scaledWidth <= width) {
            (width - scaledWidth) / 2f
        } else {
            currentX.coerceIn(width - scaledWidth, 0f)
        }

        val targetY = if (scaledHeight <= height) {
            (height - scaledHeight) / 2f
        } else {
            currentY.coerceIn(height - scaledHeight, 0f)
        }

        zoomMatrix.postTranslate(
            targetX - currentX,
            targetY - currentY
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(
            zoom > 1.01f || event.pointerCount > 1
        )

        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                if (
                    zoom > 1.01f &&
                    event.pointerCount == 1 &&
                    !scaleDetector.isInProgress
                ) {
                    zoomMatrix.postTranslate(
                        event.x - lastX,
                        event.y - lastY
                    )

                    fixTranslation()
                    imageMatrix = zoomMatrix
                }

                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val remainingIndex =
                    if (event.actionIndex == 0) 1 else 0

                if (remainingIndex < event.pointerCount) {
                    lastX = event.getX(remainingIndex)
                    lastY = event.getY(remainingIndex)
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}