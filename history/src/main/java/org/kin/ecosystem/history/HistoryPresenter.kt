package org.kin.ecosystem.history

import kin.base.responses.operations.OperationResponse
import kin.base.responses.operations.PaymentOperationResponse
import kin.utils.ResultCallback

internal class HistoryPresenter(private val blockchainRepository: BlockchainRepository) :
		HistoryContract.Presenter {

	private var view: HistoryContract.View? = null
	private var earnList: MutableList<PaymentOperationResponse> = ArrayList()
	private var spendList: MutableList<PaymentOperationResponse> = ArrayList()

	private var paymentRegistration: SSERegistration? = null

	override fun onAttach(view: HistoryContract.View) {
		this.view = view
		getCachedHistory()
		getOrderHistoryList()
		getBalance()
		listenToPayments()
	}

	private fun listenToPayments() {
		paymentRegistration = blockchainRepository.payments.observe(observer {
			it?.let {
				syncNewPayments(arrayListOf(it))
				updateRestOfTheList(it)
			}
		})
	}

	private fun getBalance() {
		view?.let { view ->
			blockchainRepository.balance.observe(view.getLifeCycleOwner(), android.arch.lifecycle.Observer { balance ->
				balance?.apply { view.setBalance(amount.toBigInteger().toString()) }
			})
		}
	}

	override fun onDetach() {
		paymentRegistration?.close()
		view = null
	}

	private fun getCachedHistory() {
		//TODO get cached history
		view?.setEarnList(earnList)
		view?.setSpendList(spendList)
	}

	private fun getOrderHistoryList() {
		blockchainRepository.getPaymentsHistory(object :
				                                        ResultCallback<ArrayList<OperationResponse>> {
			override fun onResult(operations: ArrayList<OperationResponse>?) {
				operations?.let { syncNewPayments(it) }
			}

			override fun onError(e: Exception?) {

			}

		})
	}

	private fun syncNewPayments(newOperations: ArrayList<OperationResponse>) {
		val (newEarnList, newSpendList) = splitByType(newOperations)
		addOrders(newEarnList, earnList)
		addOrders(newSpendList, spendList)
	}

	private fun addOrders(operations: List<OperationResponse>,
	                      currentOperations: MutableList<PaymentOperationResponse>) {
		if (operations.isNotEmpty()) {
			//the oldest order is the last one, so we'll go from the last and add the top
			//we will end with newest order at the top.
			for (i in operations.indices.reversed()) {
				try {
					val paymentOperation = operations[i] as PaymentOperationResponse
					val index = currentOperations.indexOf(paymentOperation)
					if (index == NOT_FOUND) {
						//add at top (ui orientation)
						addOrder(paymentOperation = paymentOperation)
					}
				}
				catch (e: Exception) {
				}
			}
		}
	}

	private fun updateRestOfTheList(operationResponse: OperationResponse) {
		try {
			operationResponse as PaymentOperationResponse
			if (operationResponse.isSpend()) {
				if(spendList.size > 1) {
					view?.notifySpendDataChanged(IntRange(1, spendList.size - 1))
				}

			} else {
				if(earnList.size > 1) {
					view?.notifyEarnDataChanged(IntRange(1, earnList.size - 1))
				}
			}
		} catch (e: Exception) {
			// Not a payment, do nothing
		}

	}

	private fun splitByType(
			list: List<OperationResponse>): Pair<List<OperationResponse>, List<OperationResponse>> {
		return list.partition { it.sourceAccount.accountId != blockchainRepository.publicAddress }
	}

	override fun onTabSelected(tab: KinEcosystemTabs.Tab) {
		when (tab) {
			KinEcosystemTabs.Tab.LEFT -> view?.showEarnList()
			KinEcosystemTabs.Tab.RIGHT -> view?.showSpendList()
		}
	}

	private fun addOrder(index: Int = 0, paymentOperation: PaymentOperationResponse) {
		if (paymentOperation.isSpend()) {
			spendList.add(index, paymentOperation)
			view?.onSpendItemInserted()
		} else {
			earnList.add(index, paymentOperation)
			view?.onEarnItemInserted()
		}
	}

	companion object {
		private const val NOT_FOUND = -1
	}
}
