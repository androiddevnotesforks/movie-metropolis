package movie.core.db.dao

import movie.core.db.PerformanceTracer
import movie.core.db.model.MovieRatingStored
import movie.core.db.trace

class MovieRatingDaoPerformance(
    private val origin: MovieRatingDao,
    private val tracer: PerformanceTracer
) : MovieRatingDao {

    override suspend fun insert(model: MovieRatingStored) = tracer.trace("$Tag.insert") {
        origin.insert(model)
    }

    override suspend fun delete(model: MovieRatingStored) = tracer.trace("$Tag.delete") {
        origin.delete(model)
    }

    override suspend fun update(model: MovieRatingStored) = tracer.trace("$Tag.update") {
        origin.update(model)
    }

    override suspend fun select(id: String) = tracer.trace("$Tag.select") {
        origin.select(id)
    }

    override suspend fun isRecent(id: String) = tracer.trace("$Tag.is-recent") {
        origin.isRecent(id)
    }

    companion object {
        private const val Tag = "movie-rating"
    }

}