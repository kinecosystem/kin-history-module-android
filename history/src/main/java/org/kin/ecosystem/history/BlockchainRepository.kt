package org.kin.ecosystem.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kin.base.AssetTypeNative
import kin.base.KeyPair
import kin.base.requests.AccountsRequestBuilder
import kin.base.requests.PaymentsRequestBuilder
import kin.base.requests.RequestBuilder
import kin.base.responses.AccountResponse
import kin.base.responses.Page
import kin.base.responses.operations.OperationResponse
import kin.utils.Request
import kin.utils.ResultCallback
import java.math.BigDecimal
import java.net.URI


internal class BlockchainRepository(publicAddress: String): BlockchainSource {

	/**
	 * replace all the request using the {@param Server} of kin-base.
	 */
	private val okHttpClient = Network.Factory.client
	private var currentPage: Page<OperationResponse>? = null
	private var operations = ArrayList<OperationResponse>()
	private val baseUri = URI(Network.URL.BASE_URL)


	private var accountKeyPair = KeyPair.fromAccountId(publicAddress)

	private val _balance = MutableLiveData<Balance>()
	var balance: LiveData<Balance> = _balance

	val payments = SSEPayments(okHttpClient, baseUri, accountKeyPair, this)

	val publicAddress: String = accountKeyPair.accountId

	init {
		getBalance(null)

	}

	override fun getBalance(callback: ResultCallback<Balance>?) {
		Request {
			AccountsRequestBuilder(okHttpClient, baseUri).account(accountKeyPair)
		}.run(object : ResultCallback<AccountResponse> {
			override fun onResult(result: AccountResponse?) {
				val newBalance = retrieveKinBalance(result)
				newBalance?.let {
					_balance.postValue(newBalance)
					callback?.onResult(it)
				} ?: run {
					callback?.onError(Exception(String.format("Could not retrieve account %10s balance", accountKeyPair.accountId)))
				}
			}

			override fun onError(e: java.lang.Exception?) {
				callback?.onError(e)
			}
		})
	}

	private fun retrieveKinBalance(accountResponse: AccountResponse?): Balance? {
		return accountResponse?.let {
			for (assetBalance in it.balances) {
				if (assetBalance.asset is AssetTypeNative) { // The only native asset should be Kin !
					return Balance(BigDecimal(assetBalance.balance))
				}
			}
			return null
		}
	}

	override fun getPaymentsHistory(callback: ResultCallback<ArrayList<OperationResponse>>?) {
		Request {
			PaymentsRequestBuilder(okHttpClient, baseUri)
					.forAccount(accountKeyPair)
					.limit(PAGE_LIMIT)
					.order(RequestBuilder.Order.DESC)
					.execute()
		}.run(object: ResultCallback<Page<OperationResponse>> {
			override fun onResult(result: Page<OperationResponse>?) {
				currentPage = result
				result?.records?.let {
					operations.addAll(it)
				}
				callback?.onResult(operations)
			}

			override fun onError(e: Exception?) {
				callback?.onError(e)
			}
		})
	}

	companion object {
		private const val PAGE_LIMIT = 50
	}
}