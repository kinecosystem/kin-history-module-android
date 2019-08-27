package org.kin.ecosystem.history

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.arch.lifecycle.LifecycleOwner
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import com.kin.history.R
import kin.base.responses.operations.PaymentOperationResponse
import java.lang.IllegalStateException

open class HistoryFragment: Fragment(), HistoryContract.View {

	private lateinit var earnRecyclerAdapter: HistoryRecyclerAdapter
	private lateinit var spendRecyclerAdapter: HistoryRecyclerAdapter

	private lateinit var earnOrderRecyclerView: RecyclerView
	private lateinit var spendOrderRecyclerView: RecyclerView
	private lateinit var balanceText: TextSwitcher

	private lateinit var presenter: HistoryPresenter

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.kinecosystem_fragment_order_history, container, false)
		initViews(root)
		arguments?.getString(KEY_PUBLIC_ADDRESS)?.let {
			presenter = HistoryPresenter(BlockchainRepository(it))
			presenter.onAttach(this)
		} ?: run { throw IllegalStateException("public address not provided, could not initiate HistoryFragment") }

		return root
	}

	override fun onDestroyView() {
		presenter.onDetach()
		super.onDestroyView()
	}

	private fun initViews(root: View) {
		balanceText = root.findViewById<TextSwitcher>(R.id.balance_text).apply {
			setFactory {
				val balanceText = TextView(context)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					balanceText.setTextAppearance(R.style.KinHistoryBalanceTextSmall)
				} else {
					balanceText.setTextAppearance(context,
					                              R.style.KinHistoryBalanceTextSmall)
				}
				balanceText.typeface = com.kin.ecosystem.base.FontUtil.SAILEC_MEDIUM
				balanceText
			}
		}

		//Earn Recycler
		earnOrderRecyclerView = root.findViewById(R.id.earn_order_recycler)
		earnRecyclerAdapter = HistoryRecyclerAdapter().apply {
			bindToRecyclerView(earnOrderRecyclerView)
		}

		// Spend Recycler
		spendOrderRecyclerView = root.findViewById<RecyclerView>(R.id.spend_order_recycler).apply {
			x = screenWidth.toFloat()
			visibility = GONE
		}

		spendRecyclerAdapter = HistoryRecyclerAdapter().apply {
			bindToRecyclerView(spendOrderRecyclerView)
		}

		root.findViewById<KinEcosystemTabs>(R.id.order_history_tabs).apply {
			setOnTabClickedListener { presenter.onTabSelected(it) }
		}
	}

	override fun getLifeCycleOwner(): LifecycleOwner {
		return viewLifecycleOwner
	}

	override fun setBalance(balance: String) {
		balanceText.setText(balance)
	}

	override fun showEarnList() {
		AnimatorSet().apply {
			val spendSlide = ValueAnimator.ofFloat(0F, screenWidth.toFloat()).apply {
				addUpdateListener {
					spendOrderRecyclerView.x = it.animatedValue as Float
				}
				withActions(startAction = {
					earnOrderRecyclerView.visibility = VISIBLE
				}, endAction = {
					spendOrderRecyclerView.visibility = GONE
				})
			}
			val earnAnimSlide = ValueAnimator.ofFloat(-screenWidth.toFloat(), 0F).apply {
				addUpdateListener {
					earnOrderRecyclerView.x = it.animatedValue as Float
				}
			}
			duration = DURATION_SLIDE_ANIM
			playTogether(spendSlide, earnAnimSlide)
			start()
		}
	}


	override fun showSpendList() {
		AnimatorSet().apply {
			val spendSlide = ValueAnimator.ofFloat(screenWidth.toFloat(), 0F ).apply {
				addUpdateListener {
					spendOrderRecyclerView.x = it.animatedValue as Float
				}
				withActions(startAction = {
					spendOrderRecyclerView.visibility = VISIBLE
				}, endAction = {
					earnOrderRecyclerView.visibility = GONE
				})
			}
			val earnAnimSlide = ValueAnimator.ofFloat(0F, -screenWidth.toFloat()).apply {
				addUpdateListener {
					earnOrderRecyclerView.x = it.animatedValue as Float
				}
			}
			duration = DURATION_SLIDE_ANIM
			playTogether(spendSlide, earnAnimSlide)
			start()
		}
	}

	override fun setEarnList(earnList: List<PaymentOperationResponse>) {
		earnRecyclerAdapter.setNewData(earnList)
	}

	override fun setSpendList(spendList: List<PaymentOperationResponse>) {
		spendRecyclerAdapter.setNewData(spendList)
	}

	override fun onEarnItemInserted() {
		earnRecyclerAdapter.notifyItemInserted(0)
	}

	override fun onSpendItemInserted() {
		spendRecyclerAdapter.notifyItemInserted(0)
	}

	override fun notifyEarnDataChanged(range: IntRange) {
		earnRecyclerAdapter.notifyItemRangeChanged(range.first, range.last)
	}

	override fun notifySpendDataChanged(range: IntRange) {
		spendRecyclerAdapter.notifyItemRangeChanged(range.first, range.last)
	}

	companion object {
		private const val KEY_PUBLIC_ADDRESS = "kin_history_pub_address"

		private const val DURATION_SLIDE_ANIM = 300L

		fun newInstance(publicAddress: String) = HistoryFragment().apply {
			arguments = Bundle().apply {
				putString(KEY_PUBLIC_ADDRESS, publicAddress)
			}
		}
	}
}