package movie.metropolis.app.screen.listing.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import movie.metropolis.app.R
import movie.metropolis.app.model.MovieView
import movie.metropolis.app.presentation.Loadable
import movie.metropolis.app.presentation.onEmpty
import movie.metropolis.app.presentation.onFailure
import movie.metropolis.app.presentation.onLoading
import movie.metropolis.app.presentation.onSuccess
import movie.style.haptic.TickOnChange
import movie.style.layout.EmptyShapeLayout
import movie.style.theme.Theme
import kotlin.math.absoluteValue
import kotlin.math.sign

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MoviePager(
    items: Loadable<List<MovieView>>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
) {
    TickOnChange(value = state.currentPage)
    items.onLoading {
        HorizontalPager(
            modifier = modifier,
            count = 3,
            userScrollEnabled = false,
            state = rememberPagerState(1),
            contentPadding = PaddingValues(start = 64.dp, end = 64.dp, top = 32.dp, bottom = 64.dp)
        ) { index ->
            MoviePoster(
                url = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(DefaultPosterAspectRatio)
                    .interpolateSize(this, index, Theme.container.poster)
            )
        }
    }.onSuccess {
        val list = items.getOrNull().orEmpty()
        HorizontalPager(
            modifier = modifier,
            count = list.size,
            key = { list[it].id },
            contentPadding = PaddingValues(horizontal = 64.dp, vertical = 32.dp),
            state = state
        ) { index ->
            val item = it[index]
            var showPopup by remember { mutableStateOf(false) }
            MoviePoster(
                url = item.posterLarge?.url,
                rating = item.rating,
                modifier = Modifier
                    .detectLongPress { showPopup = true }
                    .fillMaxWidth()
                    .aspectRatio(item.poster?.aspectRatio ?: DefaultPosterAspectRatio)
                    .interpolateSize(
                        scope = this,
                        page = index,
                        shape = Theme.container.poster,
                        shadowColor = animateColorAsState(
                            item.poster?.spotColor ?: Color.Black
                        ).value
                    ),
                onClick = { onClick(item.id) },
                onLongPress = { showPopup = it }
            )
            MoviePopup(
                isVisible = showPopup,
                onVisibilityChanged = { showPopup = false },
                url = item.posterLarge?.url.orEmpty(),
                year = item.releasedAt,
                director = item.directors.joinToString(),
                name = item.name,
                aspectRatio = item.posterLarge?.aspectRatio ?: DefaultPosterAspectRatio
            )
        }
    }.onEmpty {
        HorizontalPager(
            modifier = modifier,
            count = 1,
            userScrollEnabled = false,
            state = rememberPagerState(),
            contentPadding = PaddingValues(start = 64.dp, end = 64.dp, top = 32.dp, bottom = 64.dp)
        ) { _ ->
            EmptyShapeLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(DefaultPosterAspectRatio),
                shape = Theme.container.poster
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text("🤫", style = Theme.textStyle.title.copy(fontSize = 48.sp))
                    Text(
                        stringResource(R.string.empty_movie_main_title),
                        style = Theme.textStyle.title,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.empty_movie_main),
                        style = Theme.textStyle.body,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }.onFailure {
        HorizontalPager(
            modifier = modifier,
            count = 1,
            userScrollEnabled = false,
            state = rememberPagerState(),
            contentPadding = PaddingValues(start = 64.dp, end = 64.dp, top = 32.dp, bottom = 64.dp)
        ) { _ ->
            EmptyShapeLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(DefaultPosterAspectRatio),
                shape = Theme.container.poster,
                color = Theme.color.container.error
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text("😢", style = Theme.textStyle.title.copy(fontSize = 48.sp))
                    Text(
                        stringResource(R.string.error_movie_main_title),
                        style = Theme.textStyle.title,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.error_movie_main),
                        style = Theme.textStyle.body,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
private fun Modifier.interpolateSize(
    scope: PagerScope,
    page: Int,
    shape: Shape,
    rotationDegrees: Float = 5f,
    translation: Dp = 32.dp,
    shadowColor: Color = Color.Black,
) = run {
    val offset = scope.calculateCurrentOffsetForPage(page)
    val pageOffset = offset.absoluteValue
    val fraction = 1f - pageOffset.coerceIn(0f, 1f)
    val scale = lerp(
        start = 0.85f,
        stop = 1f,
        fraction = fraction
    )
    val alpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = fraction
    )
    val shadow = lerp(
        start = 0.dp,
        stop = 32.dp,
        fraction = fraction
    )
    val rotation = lerp(
        start = if (offset.sign == -1f) rotationDegrees.absoluteValue else 0f,
        stop = if (offset.sign == -1f) 0f else -rotationDegrees.absoluteValue,
        fraction = when (offset.sign) {
            -1f -> fraction
            0f -> 0f
            else -> 1f - fraction
        }
    )
    val translation = lerp(
        start = translation,
        stop = 0.dp,
        fraction = fraction
    )
    graphicsLayer {
        this.alpha = alpha
        this.rotationZ = rotation
        this.translationY = translation.toPx()
        this.scaleX = scale
        this.scaleY = scale
        this.shadowElevation = shadow.toPx()
        this.shape = shape
        this.clip = true
        this.ambientShadowColor = shadowColor
        this.spotShadowColor = shadowColor
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    Theme {
        MoviePager(
            items = Loadable.loading(),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun PreviewEmpty() {
    Theme {
        MoviePager(
            items = Loadable.success(emptyList()),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun PreviewError() {
    Theme {
        MoviePager(
            items = Loadable.failure(Throwable()),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}