package org.kin.ecosystem.history

import android.content.Context
import android.view.View

internal abstract class AbstractBaseViewHolder<T>(view: View) : BaseViewHolder(view) {

	init {
		this.init(view.context)
	}

	protected abstract fun init(context: Context)

	protected abstract fun bindObject(item: T?)
}
