package movie.metropolis.app.feature.global.serializer

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal class YearSerializer : KDateSerializer() {

    override val type: String = "year"
    override val formatter = SimpleDateFormat("yyyy", Locale.ROOT).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

}