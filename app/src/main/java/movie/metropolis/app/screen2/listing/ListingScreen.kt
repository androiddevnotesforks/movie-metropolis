@file:OptIn(ExperimentalFoundationApi::class)

package movie.metropolis.app.screen2.listing

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import movie.metropolis.app.model.MovieView
import movie.metropolis.app.screen.listing.MovieViewProvider
import movie.metropolis.app.screen2.listing.component.PosterColumn
import movie.metropolis.app.screen2.listing.component.PromotionColumn
import movie.metropolis.app.screen2.listing.component.PromotionHorizontalPager
import movie.metropolis.app.screen2.listing.component.RatingBox
import movie.metropolis.app.util.rememberStoreable
import movie.metropolis.app.util.rememberVisibleItemAsState
import movie.style.BackgroundImage
import movie.style.Image
import movie.style.layout.PreviewLayout
import movie.style.layout.plus
import movie.style.rememberImageState
import movie.style.rememberPaletteImageState

@Composable
fun ListingScreen(
    promotions: ImmutableList<MovieView>,
    movies: ImmutableList<MovieView>,
    state: LazyStaggeredGridState,
    onClick: (MovieView) -> Unit,
    onFavoriteClick: (MovieView) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) = Box(
    modifier = modifier,
    propagateMinConstraints = true
) {
    val selectedItem by state.rememberVisibleItemAsState()
    BackgroundImage(
        state = rememberImageState(movies.getOrNull(selectedItem)?.posterLarge?.url)
    )
    var zoom by rememberStoreable(key = "listing-zoom", default = 100f)
    LazyVerticalStaggeredGrid(
        modifier = Modifier.pointerInput(Unit) {
            detectTransformGestures { _, _, zoomFactor, _ ->
                zoom = (zoom * zoomFactor).coerceIn(75f, 200f)
            }
        },
        state = state,
        contentPadding = contentPadding + PaddingValues(24.dp),
        columns = StaggeredGridCells.Adaptive(zoom.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            val state = rememberPagerState { promotions.size }
            PromotionHorizontalPager(state = state) {
                val it = promotions[it]
                val state = rememberPaletteImageState(it.poster?.url)
                PromotionColumn(
                    color = state.palette.color,
                    contentColor = state.palette.textColor,
                    name = { Text(it.name) },
                    rating = {
                        val rating = it.rating
                        if (rating != null) RatingBox(
                            color = state.palette.color,
                            contentColor = state.palette.textColor,
                            rating = { Text(rating) },
                            offset = PaddingValues(start = 4.dp, bottom = 4.dp)
                        )
                    },
                    poster = { Image(state) },
                    action = { Icon(Icons.Rounded.FavoriteBorder, null) },
                    onClick = { onClick(it) },
                    onActionClick = { onFavoriteClick(it) },
                    onLongClick = { /* todo show overlay */ }
                )
            }
        }
        items(movies, key = { it.id }) {
            val state = rememberPaletteImageState(url = it.poster?.url ?: it.posterLarge?.url)
            PosterColumn(
                modifier = Modifier.animateItemPlacement(),
                color = state.palette.color,
                contentColor = state.palette.textColor,
                poster = { Image(state) },
                favorite = {
                    val icon = when (it.favorite) {
                        true -> Icons.Rounded.Favorite
                        else -> Icons.Rounded.FavoriteBorder
                    }
                    Icon(icon, null)
                },
                name = { Text(it.name) },
                rating = {
                    val rating = it.rating
                    if (rating != null) RatingBox(
                        color = state.palette.color,
                        contentColor = state.palette.textColor,
                        rating = { Text(rating) }
                    )
                },
                onClick = { onClick(it) },
                onActionClick = { onFavoriteClick(it) },
                onLongClick = { /* todo show overlay */ }
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ListingScreenPreview() = PreviewLayout {
    val content = MovieViewProvider().values
    val movies = content.toImmutableList()
    val promotions = content.shuffled().take(3).toImmutableList()
    ListingScreen(
        promotions = promotions,
        movies = movies,
        state = rememberLazyStaggeredGridState(),
        onClick = {},
        onFavoriteClick = {}
    )
}

private class ListingScreenParameter : PreviewParameterProvider<ListingScreenParameter.Data> {
    override val values = sequence { yield(Data()) }

    class Data
}