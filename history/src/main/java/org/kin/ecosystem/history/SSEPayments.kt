package org.kin.ecosystem.history

import com.here.oksse.ServerSentEvent
import kin.base.KeyPair
import kin.base.requests.EventListener
import kin.base.requests.PaymentsRequestBuilder
import kin.base.responses.operations.OperationResponse
import okhttp3.OkHttpClient
import java.net.URI

internal class SSEPayments(private val httpClient: OkHttpClient, private val uri: URI,
                  private val accountKeyPair: KeyPair, private val blockchainSource: BlockchainSource) : EventListener<OperationResponse?>,
                                                                                                                         SSEListener {

	private var sse: ServerSentEvent? = null
	private val _payment = ObservableData.create<OperationResponse?>()

	override fun onEvent(operationResponse: OperationResponse?) {
		_payment.postValue(operationResponse)
		blockchainSource.getBalance(null)
	}

	fun observe(observer: Observer<OperationResponse?>): SSERegistration {
		if (!_payment.hasObservers()) {
			startSSE()
		}
		_payment.addObserver(observer)
		return SSERegistration(this, observer)
	}

	private fun startSSE() {
		sse = PaymentsRequestBuilder(httpClient, uri)
				.cursor("now")
				.forAccount(accountKeyPair)
				.stream(this)
	}

	override fun remove(observer: Observer<OperationResponse?>) {
		_payment.removeObserver(observer)
		if (!_payment.hasObservers()) {
			sse?.close()
		}
	}
}