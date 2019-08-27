package org.kin.ecosystem.history

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal object DateUtil {
	private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.US)
	private val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
	private val utcTimeZone = TimeZone.getTimeZone("UTC")

	init {
		utcDateFormat.timeZone = utcTimeZone
	}

	fun getDateFormatted(dateStr: String): String? {
		val date = getDateFromUTCString(dateStr)
		return date?.let { dateFormat.format(it) } ?: run { null }
	}

	private fun getDateFromUTCString(dateStr: String): Date? {
		return try {
			utcDateFormat.parse(dateStr)
		}
		catch (e: ParseException) {
			null
		}
	}
}