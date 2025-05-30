package movie.metropolis.app.ui.booking.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import movie.style.layout.PreviewLayout
import movie.style.util.pc
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

private const val multiplier = 1f
private fun Modifier.widthOfDay() = width(24.hours.inWholeMinutes.times(multiplier).dp)

@Composable
fun BookingTable(
    modifier: Modifier = Modifier,
    haze: HazeState = remember { HazeState() },
    contentPadding: PaddingValues = PaddingValues(),
    state: ScrollState = rememberScrollState(),
    tableHeadPadding: PaddingValues = PaddingValues(1.pc),
    tablePadding: PaddingValues = PaddingValues(1.pc),
    rows: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(1.pc)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .hazeEffect(haze)
                .padding(tableHeadPadding)
                .widthOfDay(),
        ) {
            repeat(24) {
                BookingTableHead(it)
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.large)
                .hazeEffect(state = haze)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithCache {
                    onDrawWithContent {
                        val colors = listOf(Color.Transparent, Color.Black)
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors,
                                startY = 0f,
                                endY = tablePadding.calculateTopPadding().toPx()
                            ),
                            blendMode = BlendMode.DstIn
                        )
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors.reversed(),
                                startY = size.height - tablePadding.calculateBottomPadding().toPx(),
                                endY = size.height
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                }
                .verticalScroll(rememberScrollState())
                .padding(tablePadding)
                .widthOfDay(),
            verticalArrangement = Arrangement.spacedBy(1.pc)
        ) {
            rows()
        }
    }
}

@Composable
fun ColumnScope.BookingTableRow(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) = Column(
    modifier = modifier.widthOfDay(),
    verticalArrangement = Arrangement.spacedBy(1.pc)
) {
    Box(modifier = Modifier.padding(horizontal = 1.pc)) {
        ProvideTextStyle(MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) {
            title()
        }
    }
    Box {
        content()
    }
}

@Composable
fun ColumnScope.BookingTableSection(
    backdrop: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit,
) = Box(
    modifier = modifier
        .widthOfDay()
        .border(
            2.dp,
            borderColor.copy(.5f).compositeOver(MaterialTheme.colorScheme.background),
            MaterialTheme.shapes.medium
        )
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(MaterialTheme.shapes.medium)
            .drawWithCache {
                val color = backgroundColor.copy(.75f)
                val mode =
                    if (backgroundColor.luminance() > .5) BlendMode.Lighten else BlendMode.Darken
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        color = color,
                        blendMode = mode
                    )
                }
            },
        propagateMinConstraints = true
    ) {
        backdrop()
    }
    Column(
        modifier = Modifier.padding(vertical = 1.pc),
        verticalArrangement = Arrangement.spacedBy(1.pc),
        content = content
    )
}

@Composable
fun BoxScope.BookingBox(
    start: Duration,
    duration: Duration,
    onClick: () -> Unit,
    time: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BookingTableCell(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = .5.pc, horizontal = .5.pc)
            .basicMarquee(),
        start = start,
        duration = duration
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer) {
            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                time()
            }
        }
    }
}

@Composable
fun BoxScope.BookingTableHead(
    hour: Int,
    modifier: Modifier = Modifier
) {
    BookingTableCell(
        modifier = modifier,
        start = hour.hours,
        duration = 1.hours
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 1.pc, vertical = .25.pc),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("${hour}h")
        }
    }
}

@Composable
private fun BoxScope.BookingTableCell(
    start: Duration,
    duration: Duration,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(duration.inWholeMinutes.times(multiplier).dp)
            .offset(start.inWholeMinutes.times(multiplier).dp)
            .then(modifier),
        propagateMinConstraints = true
    ) {
        content()
    }
}

@PreviewLightDark
@Composable
private fun BookingTablePreview() = PreviewLayout {
    val state = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        with(density) {
            state.scrollTo(6.hours.inWholeMinutes.times(multiplier).dp.toPx().toInt())
        }
    }
    BookingTable(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        state = state
    ) {
        BookingTableRow(
            title = { Text("Title", Modifier.offset { IntOffset(x = state.value, y = 0) }) }
        ) {
            BookingBox(6.hours, 1.5.hours, {}, { Text("06:00") })
        }
    }
}