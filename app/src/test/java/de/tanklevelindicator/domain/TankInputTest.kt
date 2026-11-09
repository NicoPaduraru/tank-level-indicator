package de.tanklevelindicator.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TankInputTest {
    private val delta = 1e-6

    private fun spec(form: TankForm) = (form.toResult() as FormResult.Valid).spec

    @Test
    fun parserAcceptsCommaAndDot() {
        assertEquals(12.5, InputParser.positive("12,5", 100.0)!!, delta)
        assertEquals(12.5, InputParser.positive(" 12.5 ", 100.0)!!, delta)
    }

    @Test
    fun parserRejectsBadInput() {
        listOf("", "abc", "-1", "0", "1e3", "NaN", "Infinity", "1.000", "1.000.000", "1,2,3").forEach {
            assertNull(it, InputParser.positive(it, 100_000.0))
        }
    }

    @Test
    fun positiveRespectsMaximum() {
        assertNull(InputParser.positive("101", 100.0))
    }

    @Test
    fun levelAllowsBoundsOnly() {
        assertEquals(0.0, InputParser.level("0", 100.0)!!, delta)
        assertEquals(100.0, InputParser.level("100", 100.0)!!, delta)
        assertNull(InputParser.level("100,01", 100.0))
    }

    @Test
    fun fillLimitRange() {
        assertEquals(95.0, InputParser.fillLimit("95")!!, delta)
        assertNull(InputParser.fillLimit("49"))
        assertNull(InputParser.fillLimit("101"))
    }

    @Test
    fun dinTableIsComplete() {
        assertEquals(25, DinTanks.all.size)
        assertEquals(16, DinTanks.volumes.size)
        assertEquals(listOf(160, 200), DinTanks.diameters(10_000))
        assertEquals(listOf(290), DinTanks.diameters(100_000))
    }

    @Test
    fun dinDiameterIsOptionalWhenOnlyOneExists() {
        val s = spec(TankForm(TankType.HORIZONTAL_CYLINDER, dinVolume = 5_000))
        assertEquals(5000.0, s.volumeLiters, delta)
        assertEquals(160.0, s.heightCm, delta)
    }

    @Test
    fun dinDiameterIsRequiredWhenSeveralExist() {
        val form = TankForm(TankType.HORIZONTAL_CYLINDER, dinVolume = 10_000)
        assertEquals(FormResult.Invalid(FormError.DIAMETER_REQUIRED), form.toResult())
        assertEquals(200.0, spec(form.copy(dinDiameter = 200)).heightCm, delta)
    }

    @Test
    fun cellarKnownAndCalculated() {
        assertEquals(5000.0, spec(TankForm(TankType.CELLAR, volume = "5000", height = "150")).volumeLiters, delta)
        val calc = TankForm(TankType.CELLAR, InputMode.CALCULATED, length = "250", width = "140", height = "150")
        assertEquals(5250.0, spec(calc).volumeLiters, delta)
    }

    @Test
    fun verticalAndHorizontalCalculatedKeepTheRightHeight() {
        val v = TankForm(TankType.VERTICAL_CYLINDER, InputMode.CALCULATED, diameter = "100", height = "200")
        assertEquals(200.0, spec(v).heightCm, delta)
        val h = TankForm(TankType.HORIZONTAL_CYLINDER, InputMode.CALCULATED, diameter = "160", length = "275")
        assertEquals(160.0, spec(h).heightCm, delta)
    }

    @Test
    fun sphereHeightIsCalculated() {
        val s = spec(TankForm(TankType.SPHERE, volume = "5000"))
        assertEquals(TankCalculator.sphereDiameterCm(5000.0), s.heightCm, delta)
    }

    @Test
    fun missingFieldsAreInvalid() {
        assertTrue(TankForm(TankType.CELLAR, volume = "5000").toResult() is FormResult.Invalid)
        assertTrue(TankForm(TankType.SPHERE).toResult() is FormResult.Invalid)
    }
}
