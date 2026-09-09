package com.vc.composesample.data

import com.vc.androidcore.preferences.DataStoreManager
import com.vc.androidcore.repository.BaseRepository
import com.vc.composecore.base.BaseComposeViewModel
import com.vc.composecore.state.UiAction
import com.vc.composecore.state.UiEffect
import com.vc.composecore.state.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class SampleUser(
    val id: String,
    val name: String,
    val email: String,
    val isVerified: Boolean = true
)

data class SampleFeedState(
    val isLoading: Boolean = false,
    val users: List<SampleUser> = emptyList(),
    val error: String? = null
) : ViewState

sealed interface SampleFeedAction : UiAction {
    data object LoadUsers : SampleFeedAction
    data class RemoveUser(val userId: String) : SampleFeedAction
}

sealed interface SampleFeedEffect : UiEffect {
    data class ShowMessage(val message: String) : SampleFeedEffect
}

class SampleRepository(
    private val dataStoreManager: DataStoreManager? = null
) : BaseRepository() {

    fun getUsersStream(): Flow<List<SampleUser>> = flow {
        emit(
            listOf(
                SampleUser("1", "Viral Kalathiya", "viral@example.com"),
                SampleUser("2", "Alex Mercer", "alex@example.com"),
                SampleUser("3", "Sarah Connor", "sarah@example.com")
            )
        )
    }
}

class SampleFeedViewModel(
    private val repository: SampleRepository = SampleRepository()
) : BaseComposeViewModel<SampleFeedState, SampleFeedAction, SampleFeedEffect>(SampleFeedState()) {

    init {
        dispatch(SampleFeedAction.LoadUsers)
    }

    override fun dispatch(action: SampleFeedAction) {
        when (action) {
            is SampleFeedAction.LoadUsers -> {
                updateState { copy(isLoading = true) }
                launchSafe {
                    repository.getUsersStream().collect { userList ->
                        updateState { copy(isLoading = false, users = userList) }
                    }
                }
            }
            is SampleFeedAction.RemoveUser -> {
                updateState { copy(users = users.filterNot { it.id == action.userId }) }
                sendEffect(SampleFeedEffect.ShowMessage("User removed"))
            }
        }
    }
}
