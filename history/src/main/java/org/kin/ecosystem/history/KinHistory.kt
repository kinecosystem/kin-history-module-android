package org.kin.ecosystem.history

import android.content.Context
import com.kin.ecosystem.base.FontUtil
import java.lang.IllegalStateException

class KinHistory private constructor(context: Context, internal val publicAddress: String) {

	init {
		FontUtil.init(context.assets)
	}

	companion object {

		@Volatile
		private var instance: KinHistory? = null

		internal val publicAddress
			get() = instance?.publicAddress

		fun init(context: Context, publicAddress: String) {
			when {
				instance != null -> instance!!
				else -> synchronized(this) {
					if (instance == null) instance =
							KinHistory(context, publicAddress)
				}
			}
		}

		fun getHistoryFragment(): HistoryFragment? {
			publicAddress?.let { publicAddress ->
				return instance?.let {
					HistoryFragment.newInstance(publicAddress)
				} ?: run { null }
			}
			throw IllegalStateException("Kin.init(...) must be called fist")
		}
	}
}