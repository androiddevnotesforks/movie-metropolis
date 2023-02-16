package movie.metropolis.app.presentation.booking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import movie.core.ResultCallback
import movie.metropolis.app.model.BookingView
import movie.metropolis.app.model.facade.Image
import movie.metropolis.app.presentation.Loadable
import movie.metropolis.app.presentation.asLoadable
import movie.metropolis.app.util.throttleWithTimeout
import kotlin.time.Duration.Companion.seconds

interface BookingFacade {

    suspend fun getBookings(callback: ResultCallback<List<BookingView>>)
    suspend fun getImage(view: BookingView): Image?

    fun refresh()

    companion object {

        fun BookingFacade.bookingsFlow(refresh: Flow<suspend () -> Unit>) = channelFlow {
            getBookings {
                send(it.asLoadable())
            }
            refresh.collect {
                send(Loadable.loading())
                it()
                getBookings {
                    send(it.asLoadable())
                }
            }
        }.throttleWithTimeout(1.seconds)

    }

}