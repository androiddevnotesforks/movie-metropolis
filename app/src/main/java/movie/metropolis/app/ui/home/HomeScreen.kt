@file:OptIn(ExperimentalMaterial3Api::class)

package movie.metropolis.app.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material3.carousel.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import dev.chrisbanes.haze.HazeState
import movie.metropolis.app.screen.cinema.component.CinemaViewProvider
import movie.metropolis.app.screen.movie.component.MovieViewProvider
import movie.metropolis.app.screen.profile.component.rememberUserImage
import movie.metropolis.app.ui.home.component.CinemaBox
import movie.metropolis.app.ui.home.component.Flippable
import movie.metropolis.app.ui.home.component.LoyaltyCardBack
import movie.metropolis.app.ui.home.component.LoyaltyCardFront
import movie.metropolis.app.ui.home.component.MovieBox
import movie.metropolis.app.ui.home.component.RatingBox
import movie.metropolis.app.ui.home.component.TicketBox
import movie.metropolis.app.ui.home.component.UserTopBar
import movie.metropolis.app.ui.home.component.rememberTimeOfDayString
import movie.style.Image
import movie.style.layout.DefaultPosterAspectRatio
import movie.style.layout.PreviewLayout
import movie.style.modifier.animateItemAppearance
import movie.style.rememberImageState
import movie.style.rememberPaletteImageState
import movie.style.util.pc

val CardSize = 10.pc
val CarouselItemInfo.fraction get() = (size - minSize) / (maxSize - minSize)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    animationScope: AnimatedContentScope,
    state: HomeScreenState,
    onMovieClick: (id: String, upcoming: Boolean) -> Unit,
    onCinemaClick: (id: String) -> Unit,
    onTicketClick: (id: String) -> Unit,
    onProfileClick: () -> Unit,
    onTicketsClick: () -> Unit,
    modifier: Modifier = Modifier,
    haze: HazeState = remember { HazeState() }
) = HomeScreenScaffold(
    modifier = modifier,
    state = state,
    userAccount = { user, membership ->
        UserTopBar(
            icon = {
                val image by rememberUserImage(user.email)
                val state = rememberImageState(image)
                IconButton(onClick = onProfileClick) {
                    Image(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState("profile"),
                            animatedVisibilityScope = animationScope,
                            clipInOverlayDuringTransition = OverlayClip(CircleShape),
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                        ),
                        state = state
                    )
                }
            },
            title = { Text(rememberTimeOfDayString(user.firstName)) },
            subtitle = {
                if (membership == null || membership.isExpired) Text("Free account")
                else Text("Premium account")
            },
            card = {
                Flippable(
                    modifier = Modifier
                        .aspectRatio(3.37f / 2.125f)
                        .fillMaxWidth(),
                    front = {
                        LoyaltyCardFront(
                            title = {
                                if (membership?.isExpired == false) Text("Premium") else Text(
                                    "Expired"
                                )
                            },
                            name = { Text("${user.firstName} ${user.lastName}") },
                            number = { Text(membership?.cardNumber ?: "XXXX XXXX XXXX") },
                            expiration = { Text(membership?.memberUntil ?: "n/a") }
                        )
                    },
                    back = {
                        LoyaltyCardBack(
                            id = membership?.cardNumber?.replace(" ", "").orEmpty(),
                            name = { Text("${user.firstName} ${user.lastName}") }
                        )
                    }
                )
            }
        )
    },
    ticket = {
        val image = rememberPaletteImageState(it.movie.poster?.url)
        TicketBox(
            modifier = Modifier
                .animateItemAppearance()
                .sharedElement(
                    rememberSharedContentState("ticket-${it.id}"), animationScope
                ),
            expired = it.expired,
            onClick = { onTicketClick(it.id) },
            date = { Text(it.date) },
            time = { Text(it.time) },
            poster = { Image(image) },
            contentColor = image.palette.textColor,
            color = image.palette.color
        )
    },
    onShowAllTicketsClick = onTicketsClick,
    cinema = {
        CinemaBox(
            modifier = Modifier.animateItemAppearance(),
            onClick = { onCinemaClick(it.id) },
            name = { Text(it.name) },
            city = { Text(it.city) },
            distance = {
                val d = it.distance
                if (d != null) Text(d)
            },
            image = { Image(rememberImageState(it.image)) })
    },
    movie = { movie, upcoming ->
        val image = rememberPaletteImageState(movie.poster?.url)
        MovieBox(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("movie-${movie.id}"),
                    animatedVisibilityScope = animationScope,
                    clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium),
                    renderInOverlayDuringTransition = false,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
                .animateItemAppearance(),
            onClick = { onMovieClick(movie.id, upcoming) },
            haze = haze,
            shape = MaterialTheme.shapes.medium,//mask,
            aspectRatio = movie.poster?.aspectRatio ?: DefaultPosterAspectRatio,
            rating = {
                val r = movie.rating
                if (r != null) RatingBox(
                    color = image.palette.color,
                    haze = haze
                ) {
                    Text(r)
                }
            },
            poster = { Image(image) },
            name = {
                Text(
                    text = movie.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            category = {
                Text(
                    text = movie.genre.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            })
    },
    userError = {},
    ticketError = {},
    movieError = {},
    cinemaError = {}
)

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun HomeScreenMoviesPreview() = PreviewLayout {
    val state = remember {
        HomeScreenState().apply {
            recommended.addAll(MovieViewProvider().values)
            comingSoon.addAll(MovieViewProvider().values)
        }
    }
    SharedTransitionLayout {
        AnimatedContent(state) { state ->
            HomeScreen(
                animationScope = this,
                state = state,
                onMovieClick = { _, _ -> },
                onCinemaClick = {},
                onProfileClick = {},
                onTicketClick = {},
                onTicketsClick = {})
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun HomeScreenCinemasPreview() = PreviewLayout {
    val state = remember {
        HomeScreenState().apply {
            cinemas.addAll(CinemaViewProvider().values)
        }
    }
    SharedTransitionLayout {
        AnimatedContent(state) { state ->
            HomeScreen(
                animationScope = this,
                state = state,
                onMovieClick = { _, _ -> },
                onProfileClick = {},
                onCinemaClick = {},
                onTicketClick = {},
                onTicketsClick = {})
        }
    }
}