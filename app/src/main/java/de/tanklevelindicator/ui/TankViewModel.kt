package de.tanklevelindicator.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.tanklevelindicator.data.SavedTank
import de.tanklevelindicator.data.TankStore
import de.tanklevelindicator.domain.FormError
import de.tanklevelindicator.domain.FormResult
import de.tanklevelindicator.domain.InputLimits
import de.tanklevelindicator.domain.InputParser
import de.tanklevelindicator.domain.TankForm
import de.tanklevelindicator.domain.TankSpec
import de.tanklevelindicator.domain.TankType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class Screen { HOME, SETTINGS, TYPE_SELECT, TANK_DATA, LEVEL, RESULT }

class TankViewModel(application: Application) : AndroidViewModel(application) {
    private val store = TankStore(application)

    val tanks: StateFlow<List<SavedTank>> =
        store.tanks.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val fillLimit: StateFlow<Double> =
        store.fillLimit.stateIn(viewModelScope, SharingStarted.Eagerly, InputLimits.DEFAULT_FILL_LIMIT)

    private var stack by mutableStateOf(listOf(Screen.HOME))
    val screen: Screen get() = stack.last()
    val canGoBack: Boolean get() = stack.size > 1

    var form by mutableStateOf(TankForm(TankType.CELLAR))
        private set
    var formError by mutableStateOf<FormError?>(null)
        private set

    var activeSpec by mutableStateOf<TankSpec?>(null)
        private set
    var isSaved by mutableStateOf(false)
        private set
    var levelText by mutableStateOf("")
        private set
    var levelError by mutableStateOf(false)
        private set
    var levelCm by mutableStateOf(0.0)
        private set
    var tankName by mutableStateOf("")
        private set
    var nameError by mutableStateOf(false)
        private set

    private fun push(next: Screen) {
        stack = stack + next
    }

    fun back() {
        if (canGoBack) stack = stack.dropLast(1)
    }

    fun goHome() {
        stack = listOf(Screen.HOME)
    }

    fun openSettings() = push(Screen.SETTINGS)

    fun startNewTank() = push(Screen.TYPE_SELECT)

    fun selectType(type: TankType) {
        form = TankForm(type)
        formError = null
        push(Screen.TANK_DATA)
    }

    fun updateForm(change: (TankForm) -> TankForm) {
        form = change(form)
        formError = null
    }

    private fun startLevel(spec: TankSpec, saved: Boolean) {
        activeSpec = spec
        isSaved = saved
        levelText = ""
        levelError = false
        tankName = ""
        nameError = false
        push(Screen.LEVEL)
    }

    fun submitForm() {
        when (val result = form.toResult()) {
            is FormResult.Invalid -> formError = result.error
            is FormResult.Valid -> startLevel(result.spec, saved = false)
        }
    }

    fun openTank(tank: SavedTank) = startLevel(tank.spec, saved = true)

    fun updateLevel(text: String) {
        levelText = text
        levelError = false
    }

    fun submitLevel() {
        val spec = activeSpec ?: return
        val level = InputParser.level(levelText, spec.heightCm)
        if (level == null) {
            levelError = true
        } else {
            levelCm = level
            push(Screen.RESULT)
        }
    }

    fun updateName(text: String) {
        tankName = text.take(InputLimits.MAX_NAME_LENGTH)
        nameError = false
    }

    fun saveTank() {
        val spec = activeSpec ?: return
        val name = tankName.trim()
        if (name.isEmpty()) {
            nameError = true
            return
        }
        isSaved = true
        viewModelScope.launch { store.add(SavedTank(UUID.randomUUID().toString(), name, spec)) }
    }

    fun deleteTank(id: String) {
        viewModelScope.launch { store.remove(id) }
    }

    fun setFillLimit(value: Double) {
        viewModelScope.launch { store.setFillLimit(value) }
    }
}
