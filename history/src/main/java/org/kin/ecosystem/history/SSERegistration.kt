package org.kin.ecosystem.history


import kin.base.responses.operations.OperationResponse

internal class SSERegistration(private val sseListener: SSEListener, private val observer: Observer<OperationResponse?>) {

	fun close() {
		sseListener.remove(observer)
	}
}