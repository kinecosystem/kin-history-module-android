package org.kin.ecosystem.history

internal interface Observer<T> {

	fun onChanged(value: T)
}

internal inline fun <T> observer(crossinline onChange: (T) -> Unit): Observer<T> = object :
		Observer<T> {
	override fun onChanged(value: T) = onChange(value)
}
