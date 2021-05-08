package pl.pam.receiptsaver.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateFormatter {

    //Obiekt towarzyszący zawierający statyczne metody wspomagające
    companion object {
        /**
         * Formatuje przekazany timestamp do formatu yyyy-MM-dd HH:mm:ss
         * @property ts timestamp
         * @return ciąg znaków zawierający datę w formacie yyyy-MM-dd HH:mm:ss
         */
        fun getFormattedDateFromTs(ts: String): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ts.toLong()),
                TimeZone.getDefault().toZoneId()
            )
            return dateTime.format(formatter)
        }

        /**
         * Formatuje przekazany timestamp do obiektu LocalDateTime
         * @property ts timestamp
         * @return obiekt LocalDateTime
         */
        fun getLocalDateTimeFromTs(ts: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
        }
    }
}