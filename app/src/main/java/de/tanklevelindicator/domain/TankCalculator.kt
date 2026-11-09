package de.tanklevelindicator.domain

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.max
import kotlin.math.sin

enum class TankType { CELLAR, VERTICAL_CYLINDER, HORIZONTAL_CYLINDER, SPHERE }

/** Beschreibt einen Tank. Bei liegendem Zylinder und Kugel ist die Hoehe der Durchmesser. */
data class TankSpec(
    val type: TankType,
    val volumeLiters: Double,
    val heightCm: Double,
)

data class FillResult(
    val liters: Double,
    val fillPercent: Double,
    val refillableLiters: Double,
    val aboveLimit: Boolean,
)

object TankCalculator {
    private const val CM3_PER_LITER = 1000.0

    fun cuboidVolumeLiters(lengthCm: Double, widthCm: Double, heightCm: Double): Double =
        lengthCm * widthCm * heightCm / CM3_PER_LITER

    fun cylinderVolumeLiters(diameterCm: Double, lengthCm: Double): Double =
        PI / 4 * diameterCm * diameterCm * lengthCm / CM3_PER_LITER

    fun sphereDiameterCm(volumeLiters: Double): Double =
        cbrt(6 * volumeLiters * CM3_PER_LITER / PI)

    /** Gefuellter Anteil (0..1) des Tanks bei relativer Fuellhoehe (0..1). */
    fun filledFraction(type: TankType, heightRatio: Double): Double {
        val r = heightRatio.coerceIn(0.0, 1.0)
        return when (type) {
            TankType.CELLAR, TankType.VERTICAL_CYLINDER -> r
            TankType.HORIZONTAL_CYLINDER -> {
                val angle = 2 * acos((1 - 2 * r).coerceIn(-1.0, 1.0))
                (angle - sin(angle)) / (2 * PI)
            }
            TankType.SPHERE -> r * r * (3 - 2 * r)
        }
    }

    fun calculate(spec: TankSpec, levelCm: Double, fillLimitPercent: Double): FillResult {
        val fraction = filledFraction(spec.type, levelCm / spec.heightCm)
        val liters = spec.volumeLiters * fraction
        val fillPercent = fraction * 100
        return FillResult(
            liters = liters,
            fillPercent = fillPercent,
            refillableLiters = max(0.0, spec.volumeLiters * fillLimitPercent / 100 - liters),
            aboveLimit = fillPercent > fillLimitPercent,
        )
    }
}
