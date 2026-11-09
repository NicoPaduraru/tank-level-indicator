package de.tanklevelindicator.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class TankCalculatorTest {
    private val delta = 1e-6

    @Test
    fun cellarTankIsLinear() {
        val r = TankCalculator.calculate(TankSpec(TankType.CELLAR, 5000.0, 150.0), 75.0, 95.0)
        assertEquals(2500.0, r.liters, delta)
        assertEquals(50.0, r.fillPercent, delta)
    }

    @Test
    fun verticalCylinderIsLinear() {
        assertEquals(0.3, TankCalculator.filledFraction(TankType.VERTICAL_CYLINDER, 0.3), delta)
    }

    @Test
    fun horizontalCylinderHalfHeightIsHalfVolume() {
        assertEquals(0.5, TankCalculator.filledFraction(TankType.HORIZONTAL_CYLINDER, 0.5), delta)
    }

    @Test
    fun horizontalCylinderQuarterHeightMatchesCircleSegment() {
        val expected = (2 * PI / 3 - kotlin.math.sqrt(3.0) / 2) / (2 * PI)
        assertEquals(expected, TankCalculator.filledFraction(TankType.HORIZONTAL_CYLINDER, 0.25), delta)
    }

    @Test
    fun sphereKnownPoints() {
        assertEquals(0.5, TankCalculator.filledFraction(TankType.SPHERE, 0.5), delta)
        assertEquals(0.15625, TankCalculator.filledFraction(TankType.SPHERE, 0.25), delta)
    }

    @Test
    fun allShapesAreEmptyAtZeroAndFullAtTop() {
        TankType.entries.forEach {
            assertEquals(0.0, TankCalculator.filledFraction(it, 0.0), delta)
            assertEquals(1.0, TankCalculator.filledFraction(it, 1.0), delta)
        }
    }

    @Test
    fun allShapesRiseWithHeight() {
        TankType.entries.forEach { type ->
            val values = (0..100).map { TankCalculator.filledFraction(type, it / 100.0) }
            assertEquals(values.sorted(), values)
        }
    }

    @Test
    fun sphereDiameterRoundTripsVolume() {
        val d = TankCalculator.sphereDiameterCm(5000.0)
        assertEquals(5000.0, PI * d * d * d / 6 / 1000, delta)
    }

    @Test
    fun volumeFormulas() {
        assertEquals(5250.0, TankCalculator.cuboidVolumeLiters(250.0, 140.0, 150.0), delta)
        assertEquals(PI * 100 * 100 * 100 / 4 / 1000, TankCalculator.cylinderVolumeLiters(100.0, 100.0), delta)
    }

    @Test
    fun refillableUsesConfiguredLimit() {
        val spec = TankSpec(TankType.CELLAR, 1000.0, 100.0)
        assertEquals(450.0, TankCalculator.calculate(spec, 50.0, 95.0).refillableLiters, delta)
        assertEquals(400.0, TankCalculator.calculate(spec, 50.0, 90.0).refillableLiters, delta)
    }

    @Test
    fun refillableIsNeverNegativeAndWarnsAboveLimit() {
        val spec = TankSpec(TankType.CELLAR, 1000.0, 100.0)
        val r = TankCalculator.calculate(spec, 98.0, 95.0)
        assertEquals(0.0, r.refillableLiters, delta)
        assertTrue(r.aboveLimit)
        assertFalse(TankCalculator.calculate(spec, 95.0, 95.0).aboveLimit)
    }
}
