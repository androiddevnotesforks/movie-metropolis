package movie.metropolis.app.presentation.order

import kotlinx.coroutines.flow.channelFlow
import movie.metropolis.app.presentation.asLoadable
import movie.metropolis.app.util.throttleWithTimeout
import kotlin.time.Duration.Companion.seconds

interface OrderFacade {

    suspend fun getRequest(): Result<RequestView>

    fun interface Factory {
        fun create(url: String): OrderFacade
    }

    companion object {

        val OrderFacade.requestFlow
            get() = channelFlow {
                send(getRequest().asLoadable())
            }.throttleWithTimeout(1.seconds)

    }

}