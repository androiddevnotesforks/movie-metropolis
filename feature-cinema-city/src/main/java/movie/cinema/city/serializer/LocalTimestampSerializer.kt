package movie.cinema.city.serializer

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal class LocalTimestampSerializer : KDateSerializer() {

    override val type = "timestamp"
    override val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Europe/Prague")
    }

}