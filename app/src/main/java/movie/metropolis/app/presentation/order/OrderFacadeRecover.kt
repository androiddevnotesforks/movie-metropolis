package movie.metropolis.app.presentation.order

import movie.log.flatMapCatching
import movie.log.logSevere

class OrderFacadeRecover(
    private val origin: OrderFacade
) : OrderFacade {

    override suspend fun getRequest() =
        origin.flatMapCatching { getRequest() }.logSevere()

}