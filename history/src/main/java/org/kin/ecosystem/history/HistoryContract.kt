package org.kin.ecosystem.history

import android.arch.lifecycle.LifecycleOwner
import kin.base.responses.operations.PaymentOperationResponse

internal interface HistoryContract {

	interface View {

		fun getLifeCycleOwner(): LifecycleOwner

		fun showEarnList()

		fun showSpendList()

		fun setEarnList(earnList: List<PaymentOperationResponse>)

		fun setSpendList(spendList: List<PaymentOperationResponse>)

		fun onEarnItemInserted()

		fun onSpendItemInserted()

		fun notifyEarnDataChanged(range: IntRange)

		fun notifySpendDataChanged(range: IntRange)

		fun setBalance(balance: String)
	}

	interface Presenter {

		fun onAttach(view: View)

		fun onTabSelected(tab: KinEcosystemTabs.Tab)

		fun onDetach()
	}
}