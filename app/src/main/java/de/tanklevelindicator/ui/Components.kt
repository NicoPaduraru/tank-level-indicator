package de.tanklevelindicator.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.tanklevelindicator.R
import de.tanklevelindicator.domain.TankType
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

@Composable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

fun formatWhole(value: Double, locale: Locale): String =
    NumberFormat.getIntegerInstance(locale).format(value)

/** Rundet wahlweise ab, damit die angezeigte Obergrenze als Eingabe gueltig bleibt. */
fun formatOneDecimal(value: Double, locale: Locale, roundDown: Boolean = false): String =
    NumberFormat.getInstance(locale).apply {
        maximumFractionDigits = 1
        roundingMode = if (roundDown) RoundingMode.DOWN else RoundingMode.HALF_UP
    }.format(value)

@StringRes
fun TankType.labelRes(): Int = when (this) {
    TankType.CELLAR -> R.string.type_cellar
    TankType.VERTICAL_CYLINDER -> R.string.type_vertical
    TankType.HORIZONTAL_CYLINDER -> R.string.type_horizontal
    TankType.SPHERE -> R.string.type_sphere
}

@StringRes
fun TankType.descriptionRes(): Int = when (this) {
    TankType.CELLAR -> R.string.desc_cellar
    TankType.VERTICAL_CYLINDER -> R.string.desc_vertical
    TankType.HORIZONTAL_CYLINDER -> R.string.desc_horizontal
    TankType.SPHERE -> R.string.desc_sphere
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                },
                actions = { actions() },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            content = content,
        )
    }
}

@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(MAX_NUMBER_LENGTH)) },
        label = { Text(label) },
        suffix = { Text(unit) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    )
}

private const val MAX_NUMBER_LENGTH = 12

@Composable
fun ModeSwitch(
    firstLabel: String,
    secondLabel: String,
    firstSelected: Boolean,
    onSelect: (first: Boolean) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        listOf(firstLabel to true, secondLabel to false).forEachIndexed { index, (label, isFirst) ->
            SegmentedButton(
                selected = firstSelected == isFirst,
                onClick = { onSelect(isFirst) },
                shape = SegmentedButtonDefaults.itemShape(index, 2),
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Dropdown(
    label: String,
    selectedText: String,
    options: List<Pair<T, String>>,
    onSelect: (T) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = Modifier.padding(top = 8.dp),
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun TankDrawing(
    type: TankType,
    heightRatio: Float,
    size: Dp,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val liquid = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { if (description != null) contentDescription = description },
    ) {
        val inset = this.size.minDimension * 0.05f
        val box = Rect(inset, inset, this.size.width - inset, this.size.height - inset)
        val shape = Path().apply {
            when (type) {
                TankType.CELLAR -> addRoundRect(RoundRect(box, CornerRadius(inset, inset)))
                TankType.VERTICAL_CYLINDER -> {
                    val side = box.width * 0.18f
                    addRoundRect(
                        RoundRect(
                            Rect(box.left + side, box.top, box.right - side, box.bottom),
                            CornerRadius(inset * 3, inset * 3),
                        ),
                    )
                }
                TankType.HORIZONTAL_CYLINDER, TankType.SPHERE -> {
                    val radius = min(box.width, box.height) / 2
                    addOval(Rect(box.center, radius))
                }
            }
        }
        drawPath(shape, empty)
        clipPath(shape) {
            val top = box.bottom - box.height * heightRatio.coerceIn(0f, 1f)
            drawRect(liquid, Offset(0f, top), Size(this.size.width, this.size.height - top))
        }
        drawPath(shape, outline, style = Stroke(width = inset / 2))
    }
}
