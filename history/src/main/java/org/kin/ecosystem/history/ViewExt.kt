package org.kin.ecosystem.history

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.v4.app.Fragment
import kin.base.responses.operations.PaymentOperationResponse

internal val Fragment.screenWidth: Int get() = resources.displayMetrics.widthPixels

internal inline fun <T : ValueAnimator> T.withActions(crossinline startAction: T.() -> Unit, crossinline endAction: T.() -> Unit) {
	addListener(object : AnimatorListenerAdapter() {
		override fun onAnimationStart(animation: Animator?) {
			super.onAnimationStart(animation)
			startAction()
		}
		override fun onAnimationEnd(animation: Animator?) {
			super.onAnimationEnd(animation)
			endAction()
		}
	})
}

internal fun PaymentOperationResponse.isSpend() = run { KinHistory.publicAddress != to.accountId }
