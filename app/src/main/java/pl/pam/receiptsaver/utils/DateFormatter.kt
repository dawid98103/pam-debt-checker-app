package pl.pam.receiptsaver.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateFormatter {

    companion object {
        fun getFormattedDateFromTs(ts: String): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ts.toLong()),
                TimeZone.getDefault().toZoneId()
            )
            return dateTime.format(formatter)
        }

        fun getLocalDateTimeFromTs(ts: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
        }
    }
}