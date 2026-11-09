package de.tanklevelindicator.ui

import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tanklevelindicator.R
import de.tanklevelindicator.data.SavedTank
import de.tanklevelindicator.domain.DinTanks
import de.tanklevelindicator.domain.FormError
import de.tanklevelindicator.domain.FormResult
import de.tanklevelindicator.domain.InputMode
import de.tanklevelindicator.domain.InputParser
import de.tanklevelindicator.domain.TankCalculator
import de.tanklevelindicator.domain.TankType

@Composable
fun TankApp(vm: TankViewModel = viewModel()) {
    BackHandler(enabled = vm.canGoBack) { vm.back() }
    when (vm.screen) {
        Screen.HOME -> HomeScreen(vm)
        Screen.SETTINGS -> SettingsScreen(vm)
        Screen.TYPE_SELECT -> TypeSelectScreen(vm)
        Screen.TANK_DATA -> TankDataScreen(vm)
        Screen.LEVEL -> LevelScreen(vm)
        Screen.RESULT -> ResultScreen(vm)
    }
}

@Composable
private fun ErrorText(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text(text) }
}

@Composable
private fun HomeScreen(vm: TankViewModel) {
    val tanks by vm.tanks.collectAsStateWithLifecycle()
    var toDelete by remember { mutableStateOf<SavedTank?>(null) }
    val locale = currentLocale()

    AppScreen(
        title = stringResource(R.string.my_tanks),
        actions = {
            IconButton(onClick = vm::openSettings) {
                Icon(Icons.Filled.Settings, stringResource(R.string.settings))
            }
        },
    ) {
        if (tanks.isEmpty()) {
            Text(stringResource(R.string.no_tanks), Modifier.padding(vertical = 8.dp))
        }
        tanks.forEach { tank ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { vm.openTank(tank) },
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    TankDrawing(tank.spec.type, 0.5f, 40.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tank.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(
                                R.string.tank_summary,
                                stringResource(tank.spec.type.labelRes()),
                                formatWhole(tank.spec.volumeLiters, locale),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    IconButton(onClick = { toDelete = tank }) {
                        Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                    }
                }
            }
        }
        PrimaryButton(stringResource(R.string.new_tank), vm::startNewTank)
    }

    toDelete?.let { tank ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_text, tank.name)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteTank(tank.id)
                    toDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun SettingsScreen(vm: TankViewModel) {
    val locale = currentLocale()
    var limitText by rememberSaveable { mutableStateOf(formatOneDecimal(vm.fillLimit.value, locale)) }
    var limitError by rememberSaveable { mutableStateOf(false) }

    AppScreen(title = stringResource(R.string.settings), onBack = vm::back) {
        NumberField(
            value = limitText,
            onValueChange = { text ->
                limitText = text
                val value = InputParser.fillLimit(text)
                limitError = value == null
                if (value != null) vm.setFillLimit(value)
            },
            label = stringResource(R.string.fill_limit),
            unit = "%",
        )
        if (limitError) ErrorText(stringResource(R.string.error_limit))
        Text(
            stringResource(R.string.language),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        ModeSwitch(
            firstLabel = "Deutsch",
            secondLabel = "English",
            firstSelected = locale.language == "de",
            onSelect = { german ->
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(if (german) "de" else "en"),
                )
            },
        )
    }
}

@Composable
private fun TypeSelectScreen(vm: TankViewModel) {
    AppScreen(title = stringResource(R.string.which_tank), onBack = vm::back) {
        TankType.entries.forEach { type ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { vm.selectType(type) },
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    TankDrawing(type, 0.5f, 48.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(type.labelRes()), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(type.descriptionRes()), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun TankDataScreen(vm: TankViewModel) {
    val form = vm.form
    val locale = currentLocale()
    val liters = "L"
    val cm = "cm"

    @Composable
    fun volumeField() = NumberField(form.volume, { v -> vm.updateForm { it.copy(volume = v) } }, stringResource(R.string.tank_volume), liters)

    @Composable
    fun heightField() = NumberField(form.height, { v -> vm.updateForm { it.copy(height = v) } }, stringResource(R.string.height), cm)

    @Composable
    fun lengthField() = NumberField(form.length, { v -> vm.updateForm { it.copy(length = v) } }, stringResource(R.string.length), cm)

    @Composable
    fun diameterField() = NumberField(form.diameter, { v -> vm.updateForm { it.copy(diameter = v) } }, stringResource(R.string.diameter), cm)

    @Composable
    fun modeSwitch(knownLabel: String) = ModeSwitch(
        firstLabel = knownLabel,
        secondLabel = stringResource(R.string.calculate_volume),
        firstSelected = form.mode == InputMode.KNOWN,
        onSelect = { known ->
            vm.updateForm { it.copy(mode = if (known) InputMode.KNOWN else InputMode.CALCULATED) }
        },
    )

    AppScreen(title = stringResource(form.type.labelRes()), onBack = vm::back) {
        when (form.type) {
            TankType.CELLAR -> {
                modeSwitch(stringResource(R.string.volume_known))
                if (form.mode == InputMode.KNOWN) {
                    volumeField()
                } else {
                    lengthField()
                    NumberField(form.width, { v -> vm.updateForm { it.copy(width = v) } }, stringResource(R.string.width), cm)
                }
                heightField()
            }
            TankType.VERTICAL_CYLINDER -> {
                modeSwitch(stringResource(R.string.volume_known))
                if (form.mode == InputMode.KNOWN) volumeField() else diameterField()
                heightField()
            }
            TankType.HORIZONTAL_CYLINDER -> {
                modeSwitch(stringResource(R.string.choose_din))
                if (form.mode == InputMode.KNOWN) {
                    DinSelection(vm)
                } else {
                    diameterField()
                    lengthField()
                }
            }
            TankType.SPHERE -> volumeField()
        }

        val valid = (form.toResult() as? FormResult.Valid)?.spec
        if (valid != null && form.type == TankType.SPHERE) {
            Hint(stringResource(R.string.height_calculated, formatOneDecimal(valid.heightCm, locale)))
        } else if (valid != null && form.mode == InputMode.CALCULATED) {
            Hint(stringResource(R.string.volume_calculated, formatWhole(valid.volumeLiters, locale)))
        }

        when (vm.formError) {
            FormError.INVALID_NUMBER -> ErrorText(stringResource(R.string.error_number))
            FormError.DIAMETER_REQUIRED -> ErrorText(stringResource(R.string.error_diameter))
            null -> Unit
        }
        PrimaryButton(stringResource(R.string.next), vm::submitForm)
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun DinSelection(vm: TankViewModel) {
    val form = vm.form
    val locale = currentLocale()
    val diameters = DinTanks.diameters(form.dinVolume)
    val single = diameters.size == 1

    Dropdown(
        label = stringResource(R.string.tank_volume),
        selectedText = "${formatWhole(form.dinVolume.toDouble(), locale)} L",
        options = DinTanks.volumes.map { it to "${formatWhole(it.toDouble(), locale)} L" },
        onSelect = { volume -> vm.updateForm { it.copy(dinVolume = volume, dinDiameter = null) } },
    )
    Dropdown(
        label = stringResource(if (single) R.string.diameter_optional else R.string.diameter_required),
        selectedText = form.selectedDinDiameter?.let { "$it cm" } ?: stringResource(R.string.choose_diameter),
        options = diameters.map { it to "$it cm" },
        onSelect = { d -> vm.updateForm { it.copy(dinDiameter = d) } },
        enabled = !single,
    )
    Hint(stringResource(if (single) R.string.one_diameter else R.string.several_diameters))
}

@Composable
private fun LevelScreen(vm: TankViewModel) {
    val spec = vm.activeSpec ?: return
    val locale = currentLocale()
    val maxText = formatOneDecimal(spec.heightCm, locale, roundDown = true)

    AppScreen(title = stringResource(R.string.current_level), onBack = vm::back) {
        Text(
            stringResource(
                R.string.tank_summary,
                stringResource(spec.type.labelRes()),
                formatWhole(spec.volumeLiters, locale),
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
        NumberField(vm.levelText, vm::updateLevel, stringResource(R.string.measured_height), "cm")
        Hint(stringResource(R.string.allowed_range, maxText))
        if (vm.levelError) ErrorText(stringResource(R.string.error_level, maxText))
        PrimaryButton(stringResource(R.string.calculate), vm::submitLevel)
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun ResultScreen(vm: TankViewModel) {
    val spec = vm.activeSpec ?: return
    val limit by vm.fillLimit.collectAsStateWithLifecycle()
    val locale = currentLocale()
    val result = TankCalculator.calculate(spec, vm.levelCm, limit)
    val percent = formatOneDecimal(result.fillPercent, locale)

    AppScreen(title = stringResource(R.string.result), onBack = vm::back) {
        TankDrawing(
            type = spec.type,
            heightRatio = (vm.levelCm / spec.heightCm).toFloat(),
            size = 150.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            description = stringResource(R.string.tank_drawing, percent),
        )
        Spacer(Modifier.height(12.dp))
        Metric(
            stringResource(R.string.contents),
            "${formatWhole(result.liters, locale)} L",
            Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metric(stringResource(R.string.fill_level), "$percent %", Modifier.weight(1f))
            Metric(
                stringResource(R.string.refillable_to, formatOneDecimal(limit, locale)),
                "${formatWhole(result.refillableLiters, locale)} L",
                Modifier.weight(1f),
            )
        }
        if (result.aboveLimit) ErrorText(stringResource(R.string.above_limit))
        Hint(stringResource(R.string.disclaimer))

        if (vm.isSaved) {
            if (vm.tankName.isNotBlank()) Hint(stringResource(R.string.tank_saved))
        } else {
            OutlinedTextField(
                value = vm.tankName,
                onValueChange = vm::updateName,
                label = { Text(stringResource(R.string.tank_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
            if (vm.nameError) ErrorText(stringResource(R.string.error_name))
            OutlinedButton(onClick = vm::saveTank, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(stringResource(R.string.save_tank))
            }
        }
        OutlinedButton(onClick = vm::goHome, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(R.string.back_to_tanks))
        }
    }
}
