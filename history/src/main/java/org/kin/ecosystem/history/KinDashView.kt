package org.kin.ecosystem.history

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.kin.history.R


internal class KinDashView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
		View(context, attrs) {
	private val mPaint: Paint
	private var orientation: Int = 0

	init {
		val dashGap: Int
		val dashLength: Int
		val dashThickness: Int
		val color: Int
		val styledAttributes =
				context.theme.obtainStyledAttributes(attrs,
				                                     R.styleable.KinEcosystemDashView, 0, 0)

		try {
			dashGap =
					styledAttributes.getDimensionPixelSize(R.styleable.KinEcosystemDashView_dashGap,
					                                       5)
			dashLength =
					styledAttributes.getDimensionPixelSize(R.styleable.KinEcosystemDashView_dashLength,
					                                       5)
			dashThickness =
					styledAttributes.getDimensionPixelSize(R.styleable.KinEcosystemDashView_dashThickness,
					                                       3)
			color = styledAttributes.getColor(R.styleable.KinEcosystemDashView_color, -0x1000000)
			orientation = styledAttributes.getInt(R.styleable.KinEcosystemDashView_orientation,
			                                      ORIENTATION_HORIZONTAL)
		} finally {
			styledAttributes.recycle()
		}

		mPaint = Paint()
		mPaint.isAntiAlias = true
		mPaint.color = color
		mPaint.style = Paint.Style.STROKE
		mPaint.strokeWidth = dashThickness.toFloat()
		mPaint.pathEffect =
				DashPathEffect(floatArrayOf(dashLength.toFloat(), dashGap.toFloat()), 0f)

		setLayerType(LAYER_TYPE_SOFTWARE, mPaint)
	}

	override fun onDraw(canvas: Canvas) {
		if (orientation == ORIENTATION_HORIZONTAL) {
			val center = height * .5f
			canvas.drawLine(0f, center, width.toFloat(), center, mPaint)
		} else {
			val center = width * .5f
			canvas.drawLine(center, 0f, center, height.toFloat(), mPaint)
		}
	}

	companion object {

		private const val ORIENTATION_HORIZONTAL = 0
		private const val ORIENTATION_VERTICAL = 1
	}
}
