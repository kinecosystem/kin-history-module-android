package org.kin.ecosystem.history

import kin.base.responses.operations.OperationResponse

internal interface SSEListener {

	fun remove(observer: Observer<OperationResponse?>)
}