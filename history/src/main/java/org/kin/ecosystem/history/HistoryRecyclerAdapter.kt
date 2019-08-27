package org.kin.ecosystem.history

import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.kin.history.R
import org.kin.ecosystem.history.DateUtil.getDateFormatted
import kin.base.responses.operations.PaymentOperationResponse
import java.math.BigDecimal


internal open class HistoryRecyclerAdapter :
		BaseRecyclerAdapter<PaymentOperationResponse, HistoryRecyclerAdapter.ViewHolder>(
				R.layout.kinecosystem_order_history_recycler_item) {

	private fun initSizes(context: Context) {
		val resources = context.resources
		if (itemHeight == NOT_INITIALIZED) {
			itemHeight =
					resources.getDimensionPixelOffset(R.dimen.kinecosystem_order_history_item_height)
			itemHalfHeight = itemHeight / 2
		}
	}

	override fun convert(holder: ViewHolder, item: PaymentOperationResponse?) {
		holder.bindObject(item)
	}

	override fun createBaseViewHolder(view: View): ViewHolder {
		return ViewHolder(view)
	}

	internal inner class ViewHolder(item_root: View) :
			AbstractBaseViewHolder<PaymentOperationResponse>(item_root) {

		init {
			getView(R.id.dash_line) as View
			getView(R.id.kin_logo) as View
			getView(R.id.title) as View
			getView(R.id.sub_title) as View
			getView(R.id.amount_text) as View
		}

		override fun init(context: Context) {
			initSizes(context)
		}

		public override fun bindObject(item: PaymentOperationResponse?) {
			item?.let {
				setOrderTitle(it)
				setDate(it)
				setAmount(it)
				updateTimeLine(it)
			}
		}

		private fun setAmount(item: PaymentOperationResponse) {
			setText(R.id.amount_text,
			        (if (item.isSpend()) MINUS_SIGN else PLUS_SIGN) + BigDecimal(item.amount).toBigInteger().toString())
		}

		private fun setDate(item: PaymentOperationResponse) {
			if (item.createdAt.isNotEmpty()) {
				getDateFormatted(item.createdAt)?.let {
					setText(R.id.date, it)
				}
			}
		}

		private fun setOrderTitle(item: PaymentOperationResponse) {
			setText(R.id.title, getTitle(item))
			setMaxEMs(R.id.title,
			          MAX_EMS)
		}

		private fun getTitle(item: PaymentOperationResponse): String {
			return if (item.isSpend()) {
				"Send Kin to - ${item.to.accountId}"
			} else {
				"Received Kin from - ${item.from.accountId}"
			}
		}

		private fun updateTimeLine(item: PaymentOperationResponse) {
			// Timeline dot color
			val itemIndex = layoutPosition
			val lastIndex = dataCount - 1
			setVectorDrawable(R.id.kin_logo,
			                  if (item.isSpend()) R.drawable.kinecosystem_kin_spend_icon_active_small
			                  else R.drawable.kinecosystem_kin_earn_icon_active_small)


			// Timeline path size
			if (itemIndex == 0 || itemIndex == lastIndex) {
				if (dataCount > 1) {
					setVisibility(R.id.dash_line, VISIBLE)
					setViewHeight(R.id.dash_line,
					              itemHalfHeight)
					if (itemIndex == 0) {
						setViewTopMargin(R.id.dash_line,
						                 itemHalfHeight)
					} else {
						setViewTopMargin(R.id.dash_line, 0)
					}
				} else {
					setVisibility(R.id.dash_line, GONE)
				}

			} else {
				setVisibility(R.id.dash_line, VISIBLE)
				setViewHeight(R.id.dash_line,
				              itemHeight)
				setViewTopMargin(R.id.dash_line, 0)
			}
		}
	}

	companion object {
		private const val PLUS_SIGN = "+"
		private const val MINUS_SIGN = "-"

		private const val NOT_INITIALIZED = -1
		private const val MAX_EMS = 12

		private var itemHeight = NOT_INITIALIZED
		private var itemHalfHeight =
				NOT_INITIALIZED
	}
}