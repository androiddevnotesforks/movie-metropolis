package movie.metropolis.app.presentation.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import movie.metropolis.app.presentation.Loadable
import movie.metropolis.app.presentation.asLoadable

interface LoginFacade {

    val currentUserEmail: String?
    val domain: String

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ): Result<Unit>

    companion object {

        fun LoginFacade.stateFlow(emitter: Flow<suspend LoginFacade.() -> Result<Unit>>) =
            channelFlow {
                send(Loadable.success(false))
                emitter.collect {
                    send(Loadable.loading())
                    send(it().map { true }.asLoadable())
                }
            }

    }

}