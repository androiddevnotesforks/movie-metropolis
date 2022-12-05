package movie.metropolis.app.screen.detail

import java.util.Date

class MovieFacadeRecover(
    private val origin: MovieFacade
) : MovieFacade {

    override suspend fun getAvailableFrom() =
        kotlin.runCatching { origin.getAvailableFrom().getOrThrow() }

    override suspend fun getMovie() =
        kotlin.runCatching { origin.getMovie().getOrThrow() }

    override suspend fun getPoster() =
        kotlin.runCatching { origin.getPoster().getOrThrow() }

    override suspend fun getTrailer() =
        kotlin.runCatching { origin.getTrailer().getOrThrow() }

    override suspend fun getShowings(
        date: Date,
        latitude: Double,
        longitude: Double
    ) = kotlin.runCatching { origin.getShowings(date, latitude, longitude).getOrThrow() }

}