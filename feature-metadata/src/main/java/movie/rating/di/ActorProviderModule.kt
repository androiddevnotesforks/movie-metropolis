package movie.rating.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import movie.rating.ActorProvider
import movie.rating.ActorProviderDatabase
import movie.rating.ActorProviderFold
import movie.rating.ActorProviderStoring
import movie.rating.ActorProviderTMDB
import movie.rating.database.ActorDao
import movie.rating.database.ActorReferenceConnectionDao
import movie.rating.database.ActorReferenceDao
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
internal class ActorProviderModule {

    @Provides
    @Reusable
    fun actor(
        @Rating client: Provider<HttpClient>,
        actor: ActorDao,
        connection: ActorReferenceConnectionDao,
        reference: ActorReferenceDao
    ): ActorProvider {
        var out: ActorProvider
        out = ActorProviderTMDB(client)
        out = ActorProviderStoring(out, actor, connection, reference)
        out = ActorProviderFold(
            ActorProviderDatabase(actor, reference),
            out
        )
        return out
    }

}