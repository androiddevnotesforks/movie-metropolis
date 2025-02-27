package movie.metropolis.app.presentation.booking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import movie.cinema.city.CinemaCity
import movie.metropolis.app.model.BookingView
import movie.metropolis.app.model.adapter.BookingViewFromTicket
import movie.metropolis.app.util.retryOnNetworkError

class BookingFacadeCinemaCity(
    private val cinemaCity: CinemaCity
) : BookingFacade {

    override val bookings: Flow<List<BookingView>> = flow {
        emit(cinemaCity.customers.getTickets().map(::BookingViewFromTicket))
    }.retryOnNetworkError()

}