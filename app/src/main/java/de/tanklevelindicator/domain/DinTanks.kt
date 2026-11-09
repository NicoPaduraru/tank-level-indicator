package de.tanklevelindicator.domain

data class DinTank(val volumeLiters: Int, val diameterCm: Int)

/** Genormte Groessen liegender Tanks nach DIN 6608 / 6616: nur Volumen und Durchmesser. */
object DinTanks {
    val all: List<DinTank> = listOf(
        DinTank(1_000, 100),
        DinTank(3_000, 125),
        DinTank(4_000, 160),
        DinTank(5_000, 160),
        DinTank(7_000, 160),
        DinTank(10_000, 160),
        DinTank(10_000, 200),
        DinTank(13_000, 160),
        DinTank(13_000, 200),
        DinTank(16_000, 160),
        DinTank(16_000, 200),
        DinTank(20_000, 200),
        DinTank(20_000, 250),
        DinTank(25_000, 200),
        DinTank(25_000, 250),
        DinTank(30_000, 200),
        DinTank(30_000, 250),
        DinTank(40_000, 250),
        DinTank(40_000, 290),
        DinTank(50_000, 250),
        DinTank(50_000, 290),
        DinTank(60_000, 250),
        DinTank(60_000, 290),
        DinTank(80_000, 290),
        DinTank(100_000, 290),
    )

    val volumes: List<Int> = all.map { it.volumeLiters }.distinct()

    fun diameters(volumeLiters: Int): List<Int> =
        all.filter { it.volumeLiters == volumeLiters }.map { it.diameterCm }
}
