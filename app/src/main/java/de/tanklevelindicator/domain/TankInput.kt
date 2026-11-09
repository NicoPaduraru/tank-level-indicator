package de.tanklevelindicator.domain

object InputLimits {
    const val MAX_VOLUME_LITERS = 10_000_000.0
    const val MAX_DIMENSION_CM = 10_000.0
    const val MIN_FILL_LIMIT = 50.0
    const val MAX_FILL_LIMIT = 100.0
    const val DEFAULT_FILL_LIMIT = 95.0
    const val MAX_NAME_LENGTH = 40
}

object InputParser {
    // Hoechstens zwei Nachkommastellen: "1.000" wird so nicht still als 1 Liter gelesen.
    private val number = Regex("""^\d+([.,]\d{1,2})?$""")

    private fun parse(text: String): Double? {
        val trimmed = text.trim()
        if (!number.matches(trimmed)) return null
        return trimmed.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }
    }

    /** Wert groesser 0 und hoechstens [max], sonst null. */
    fun positive(text: String, max: Double): Double? =
        parse(text)?.takeIf { it > 0 && it <= max }

    /** Fuellhoehe von 0 bis [heightCm], sonst null. */
    fun level(text: String, heightCm: Double): Double? =
        parse(text)?.takeIf { it in 0.0..heightCm }

    fun fillLimit(text: String): Double? =
        parse(text)?.takeIf { it in InputLimits.MIN_FILL_LIMIT..InputLimits.MAX_FILL_LIMIT }
}

enum class InputMode { KNOWN, CALCULATED }

enum class FormError { INVALID_NUMBER, DIAMETER_REQUIRED }

sealed interface FormResult {
    data class Valid(val spec: TankSpec) : FormResult
    data class Invalid(val error: FormError) : FormResult
}

/** Eingaben des Tankformulars, unveraendert als Text. */
data class TankForm(
    val type: TankType,
    val mode: InputMode = InputMode.KNOWN,
    val volume: String = "",
    val height: String = "",
    val length: String = "",
    val width: String = "",
    val diameter: String = "",
    val dinVolume: Int = DinTanks.volumes.first(),
    val dinDiameter: Int? = null,
) {
    /** Bei nur einem Durchmesser ist die Auswahl optional und wird automatisch gesetzt. */
    val selectedDinDiameter: Int?
        get() = DinTanks.diameters(dinVolume).singleOrNull() ?: dinDiameter

    fun toResult(): FormResult = when (type) {
        TankType.CELLAR -> cellar()
        TankType.VERTICAL_CYLINDER -> verticalCylinder()
        TankType.HORIZONTAL_CYLINDER -> horizontalCylinder()
        TankType.SPHERE -> sphere()
    }

    private fun dimension(text: String) = InputParser.positive(text, InputLimits.MAX_DIMENSION_CM)
    private fun liters(text: String) = InputParser.positive(text, InputLimits.MAX_VOLUME_LITERS)

    private fun valid(volume: Double?, height: Double?): FormResult {
        if (volume == null || height == null || volume > InputLimits.MAX_VOLUME_LITERS) {
            return FormResult.Invalid(FormError.INVALID_NUMBER)
        }
        return FormResult.Valid(TankSpec(type, volume, height))
    }

    private fun cellar(): FormResult {
        val h = dimension(height)
        if (mode == InputMode.KNOWN) return valid(liters(volume), h)
        val l = dimension(length)
        val w = dimension(width)
        return valid(if (l != null && w != null && h != null) TankCalculator.cuboidVolumeLiters(l, w, h) else null, h)
    }

    private fun verticalCylinder(): FormResult {
        val h = dimension(height)
        if (mode == InputMode.KNOWN) return valid(liters(volume), h)
        val d = dimension(diameter)
        return valid(if (d != null && h != null) TankCalculator.cylinderVolumeLiters(d, h) else null, h)
    }

    private fun horizontalCylinder(): FormResult {
        if (mode == InputMode.KNOWN) {
            val d = selectedDinDiameter ?: return FormResult.Invalid(FormError.DIAMETER_REQUIRED)
            return valid(dinVolume.toDouble(), d.toDouble())
        }
        val d = dimension(diameter)
        val l = dimension(length)
        return valid(if (d != null && l != null) TankCalculator.cylinderVolumeLiters(d, l) else null, d)
    }

    private fun sphere(): FormResult {
        val v = liters(volume)
        return valid(v, v?.let(TankCalculator::sphereDiameterCm))
    }
}
