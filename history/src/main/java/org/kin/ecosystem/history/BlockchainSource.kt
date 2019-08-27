package org.kin.ecosystem.history

import kin.base.responses.operations.OperationResponse
import kin.utils.ResultCallback

internal interface BlockchainSource {

	fun getBalance(callback: ResultCallback<Balance>?)

	fun getPaymentsHistory(callback: ResultCallback<ArrayList<OperationResponse>>?)
}