package movie.metropolis.app.feature.global

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import movie.metropolis.app.feature.global.model.BodyResponse
import movie.metropolis.app.feature.global.model.ExtendedMovieResponse
import movie.metropolis.app.feature.global.model.MovieDetailsResponse
import movie.metropolis.app.feature.global.model.MovieEventResponse
import movie.metropolis.app.feature.global.model.NearbyCinemaResponse
import movie.metropolis.app.feature.global.model.ShowingType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class EventServiceImpl(
    private val client: HttpClient
) : EventService {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    override suspend fun getEventsInCinema(
        cinema: String,
        date: Date
    ) = kotlin.runCatching {
        client.get {
            url("/quickbook/10101/film-events/in-cinema/$cinema/at-date/${formatter.format(date)}")
        }.body<BodyResponse<MovieEventResponse>>()
    }

    override suspend fun getNearbyCinemas(
        lat: Double,
        lng: Double
    ) = kotlin.runCatching {
        client.get {
            url("/10101/cinema/bylocation")
            parameter("lat", lat)
            parameter("long", lng)
            parameter("unit", "KILOMETERS")
        }.body<BodyResponse<NearbyCinemaResponse>>()
    }

    override suspend fun getDetail(id: String) = kotlin.runCatching {
        client.get {
            url("/10101/films/byDistributorCode/$id")
        }.body<BodyResponse<MovieDetailsResponse>>()
    }

    override suspend fun getMoviesByType(type: ShowingType) = kotlin.runCatching {
        client.get {
            url("/10101/films/by-showing-type/${type.value}")
            parameter("ordering", "asc")
        }.body<BodyResponse<ExtendedMovieResponse>>()
    }

}