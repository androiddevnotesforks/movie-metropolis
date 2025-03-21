package movie.cinema.city.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import movie.cinema.city.serializer.ExtendedMediaSerializer
import movie.cinema.city.serializer.LocalTimestampSerializer
import movie.cinema.city.serializer.LocaleSerializer
import movie.cinema.city.serializer.MinutesDurationSerializer
import movie.cinema.city.serializer.StringAsIntSerializer
import movie.cinema.city.serializer.YearSerializer
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

@Serializable
internal data class ExtendedMovieResponse(
    @SerialName("key") val id: Key,
    @SerialName("link") val url: String,
    @Serializable(YearSerializer::class)
    @SerialName("releaseYear") val releasedAt: Date?,
    @Serializable(LocalTimestampSerializer::class)
    @SerialName("dateStarted") val screeningFrom: Date,
    @Serializable(MinutesDurationSerializer::class)
    @SerialName("length") val duration: Duration,
    @SerialName("media") val media: List<@Serializable(ExtendedMediaSerializer::class) Media>,
    @SerialName("i18nFieldsMap") val metadata: Map<@Serializable(LocaleSerializer::class) Locale, Metadata>,
    @SerialName("allAttributes") val attributes: List<Key>
) {

    @Serializable
    data class Key(
        @SerialName("key") val key: String
    )

    sealed class Media {

        @Serializable
        data class Image(
            @Serializable(StringAsIntSerializer::class)
            @SerialName("dimensionWidth") val width: Int,
            @Serializable(StringAsIntSerializer::class)
            @SerialName("dimensionHeight") val height: Int,
            @SerialName("url") val url: String
        ) : Media()

        @Serializable
        data class Video(
            @SerialName("url") val url: String,
            @SerialName("subType") val type: String
        ) : Media()

        @Serializable
        object Noop : Media()

    }

    @Serializable
    data class Metadata(
        @SerialName("name") val name: String,
        @SerialName("synopsis") val synopsis: String? = null,
        @SerialName("directors") val directors: String? = null,
        @SerialName("cast") val cast: String? = null,
        @SerialName("production") val countryOfOrigin: String? = null
    )

}