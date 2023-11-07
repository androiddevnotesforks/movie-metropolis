@file:OptIn(ExperimentalComposeUiApi::class)

package movie.metropolis.app.screen2.setup

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import movie.metropolis.app.R
import movie.metropolis.app.screen2.setup.component.rememberRandomItemAsState
import movie.style.BackgroundImage
import movie.style.layout.PreviewLayout
import movie.style.modifier.glow
import movie.style.modifier.surface
import movie.style.rememberImageState
import movie.style.theme.Theme

@Composable
fun SetupLoginContent(
    posters: ImmutableList<String>,
    state: LoginState,
    onStateChange: (LoginState) -> Unit,
    onLoginClick: () -> Unit,
    onLoginSkip: () -> Unit,
    modifier: Modifier = Modifier,
) = Box(
    modifier = modifier,
    propagateMinConstraints = true
) {
    val poster by posters.rememberRandomItemAsState()
    BackgroundImage(state = rememberImageState(url = poster))
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .alignByBaseline()
                        .alpha(.5f),
                    text = "Movie Metropolis",
                    style = Theme.textStyle.title
                )
                Text(
                    modifier = Modifier
                        .alignByBaseline()
                        .alpha(.5f),
                    text = "for",
                    style = Theme.textStyle.caption
                )
            }
            Image(
                modifier = Modifier.height(128.dp),
                painter = painterResource(R.drawable.ic_logo_cinemacity),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .surface(
                    color = Theme.color.container.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .glow(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .navigationBarsPadding()
                .imePadding()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val reqs = remember { List(2) { FocusRequester() } }
            LaunchedEffect(reqs) {
                reqs[0].requestFocus()
            }
            Text(modifier = Modifier.padding(start = 12.dp), text = stringResource(R.string.email))
            TextField(
                modifier = Modifier
                    .focusRequester(reqs[0])
                    .focusProperties {
                        next = reqs[1]
                    }
                    .fillMaxWidth()
                    .glow(Theme.container.button),
                value = state.email,
                onValueChange = { onStateChange(state.copy(email = it)) },
                placeholder = { Text("john.doe@email.com") },
                shape = Theme.container.button,
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Theme.color.container.surface.copy(.2f),
                    errorContainerColor = Theme.color.container.error.copy(.2f),
                    disabledContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                leadingIcon = {
                    Icon(Icons.Rounded.Email, null)
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(R.string.password)
            )
            TextField(
                modifier = Modifier
                    .focusRequester(reqs[1])
                    .focusProperties {
                        previous = reqs[0]
                    }
                    .fillMaxWidth()
                    .glow(Theme.container.button),
                value = state.password,
                onValueChange = { onStateChange(state.copy(password = it)) },
                placeholder = { Text("*****") },
                shape = Theme.container.button,
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onGo = { onLoginClick() }),
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Theme.color.container.surface.copy(.1f),
                    errorContainerColor = Theme.color.container.error.copy(.1f),
                    disabledContainerColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(Icons.Rounded.Lock, null)
                }
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onLoginSkip
            ) {
                Text("Continue without logging in")
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun SetupLoginContentPreview() = PreviewLayout(modifier = Modifier.fillMaxSize()) {
    SetupLoginContent(
        posters = List(10) { "" }.toImmutableList(),
        state = LoginState(),
        onStateChange = {},
        onLoginClick = {},
        onLoginSkip = {}
    )
}

private class SetupLoginContentParameter :
    PreviewParameterProvider<SetupLoginContentParameter.Data> {
    override val values = sequence { yield(Data()) }

    class Data
}