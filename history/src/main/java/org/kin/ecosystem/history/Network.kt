package org.kin.ecosystem.history

import com.kin.history.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal object Network {

	object Factory {

		val client = OkHttpClient.Builder().apply {
			if (BuildConfig.DEBUG) {
				val logging = HttpLoggingInterceptor().apply {
					level = HttpLoggingInterceptor.Level.BODY
				}
				addInterceptor(logging).build()
			}
		}.build()
	}

	object URL {
		const val BASE_URL = "https://horizon-block-explorer.kininfrastructure.com"
	}
}