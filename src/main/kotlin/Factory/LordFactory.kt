package Factory

import General.CaoCao
import General.General
import General.LiuBei
import General.SunQuan
import Strategy.LordStrategy
import kotlin.random.Random

class LordFactory : GeneralFactory() {
    private val lords = listOf(LiuBei(), CaoCao(), SunQuan())
    private val createdLords = mutableSetOf<String>()

    override fun createRandomGeneral(): General? {
        val availableLords = lords.filter { it.name !in createdLords }
        return if (availableLords.isNotEmpty()) {
            val selectedLord = availableLords[Random.nextInt(availableLords.size)]
            createdLords.add(selectedLord.name)
            selectedLord.apply { strategy = LordStrategy() }
        } else {
            null
        }
    }
}