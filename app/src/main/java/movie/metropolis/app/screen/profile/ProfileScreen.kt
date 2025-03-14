@file:OptIn(ExperimentalFoundationApi::class)

package movie.metropolis.app.screen.profile

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.text.htmlEncode
import movie.metropolis.app.R
import movie.metropolis.app.model.UserView
import movie.metropolis.app.screen.card.UserViewParameter
import movie.metropolis.app.screen.profile.component.ProfileIcon
import movie.metropolis.app.screen.profile.component.ProfileItem
import movie.metropolis.app.screen.profile.component.VersionColumn
import movie.metropolis.app.screen.profile.component.rememberUserImage
import movie.style.BackgroundImage
import movie.style.action.actionView
import movie.style.layout.PreviewLayout
import movie.style.layout.alignForLargeScreen
import movie.style.modifier.surface
import movie.style.rememberImageState
import movie.style.textPlaceholder
import movie.style.theme.Theme

@Composable
fun ProfileScreen(
    user: UserView?,
    onClickSettings: () -> Unit,
    onClickEdit: () -> Unit,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) = Box(
    modifier = modifier.fillMaxSize(),
    propagateMinConstraints = true
) {
    val imageUrl by rememberUserImage(email = user?.email.orEmpty())
    val background = rememberImageState(url = imageUrl)
    BackgroundImage(state = background)
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .alignForLargeScreen()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(top = contentPadding.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileIcon(
                modifier = Modifier
                    .padding(24.dp)
                    .size(128.dp),
                email = user?.email.orEmpty(),
                onClick = actionView { "https://gravatar.com/connect/?user_email=${user?.email?.htmlEncode()}" }
            )
            Text(
                modifier = Modifier.textPlaceholder(user == null),
                text = user?.let { "%s %s".format(user.firstName, user.lastName) }
                    ?: "#".repeat(15),
                style = Theme.textStyle.title,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .alpha(.5f)
                    .textPlaceholder(user == null),
                text = user?.email ?: "#".repeat(25),
                style = Theme.textStyle.caption,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .alpha(.5f)
                    .textPlaceholder(user == null),
                text = user?.phone ?: "#".repeat(10),
                style = Theme.textStyle.caption,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .surface(
                    color = Theme.color.container.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(24.dp)
                .padding(bottom = contentPadding.calculateBottomPadding())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileItem(
                icon = { Icon(painterResource(R.drawable.ic_settings), null) },
                title = { Text(stringResource(id = R.string.settings)) },
                onClick = onClickSettings
            )
            ProfileItem(
                icon = { Icon(painterResource(R.drawable.ic_edit), null) },
                title = { Text(stringResource(R.string.edit_profile)) },
                onClick = onClickEdit
            )
            ProfileItem(
                enabled = user != null,
                icon = { Icon(painterResource(id = R.drawable.ic_card), null) },
                title = { Text(stringResource(R.string.loyalty_card)) },
                onClick = onClickCard
            )
            Spacer(modifier = Modifier.weight(1f))
            VersionColumn(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ProfileScreenPreview() = PreviewLayout {
    val user = remember { UserViewParameter().values.first() }
    ProfileScreen(user, {}, {}, {})
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun ProfileScreenEmptyPreview() = PreviewLayout {
    ProfileScreen(null, {}, {}, {})
}
