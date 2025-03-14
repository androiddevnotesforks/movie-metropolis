package movie.metropolis.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import movie.metropolis.app.presentation.profile.ProfileFacade
import movie.metropolis.app.ui.profile.ProfileState.SaveState
import movie.metropolis.app.util.updateWith
import movie.style.layout.LayoutState
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val facade: ProfileFacade
) : ViewModel() {

    private val saveQueue = Channel<Any>()
    val state = ProfileState()

    init {
        viewModelScope.launch {
            launch {
                state.user = LayoutState.result(facade.getUser())
            }
            launch {
                state.cinemas.updateWith(facade.getCinemas())
            }
            launch {
                state.membership = LayoutState.result(facade.getMembership())
            }
            saveQueue.consumeAsFlow().debounce(5.seconds)
                .onEach { state.saving = SaveState.Saving }
                .onEach { facade.save(state.user.getOrNull() ?: return@onEach) }
                .onEach { state.saving = SaveState.Idle }
                .catch { state.saving = SaveState.Fail }
                .launchIn(this)
        }
    }

    fun onPhoneChange(value: String) {
        state.user.getOrNull()?.phone = value
        saveQueue.trySend(value)
    }

    fun onFirstNameChange(value: String) {
        state.user.getOrNull()?.firstName = value
        saveQueue.trySend(value)
    }

    fun onLastNameChange(value: String) {
        state.user.getOrNull()?.lastName = value
        saveQueue.trySend(value)
    }

    fun onConsentChange(value: Boolean) {
        state.user.getOrNull()?.consent?.marketing = value
        saveQueue.trySend(value)
    }

}