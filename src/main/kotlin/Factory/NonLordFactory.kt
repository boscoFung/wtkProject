package Factory
import General.*
import Strategy.*
import kotlin.random.Random

open class NonLordFactory : GeneralFactory() {
    private val nonLords = listOf(ZhenJi(), ZhugeLiang(), SimaYi(), ZhangFei(), ZhouYu(), XuChu(), XiahouDun(), DiaoChan(), ZhaoYun(),LuBu())
    protected val createdNonLords = mutableSetOf<String>()
    private val weiChainMessages = mutableListOf<String>()

    override fun createRandomGeneral(): General? {
        val availableGenerals = nonLords.filter { it.name !in createdNonLords }
        return if (availableGenerals.isNotEmpty()) {
            val selectedGeneral = availableGenerals[Random.nextInt(availableGenerals.size)]
            createdNonLords.add(selectedGeneral.name)
            selectedGeneral
        } else {
            null
        }
    }
    fun createNonLordPlayers(totalParticipants: Int): List<Player> {
        val numNonLords = totalParticipants - 1
        val (numLoyalists, numRebels, numSpies) = when (totalParticipants) {
            4 -> Triple(1, 1, 1)
            5 -> Triple(1, 2, 1)
            6 -> Triple(1, 3, 1)
            7 -> Triple(2, 3, 1)
            8 -> Triple(2, 4, 1)
            9 -> Triple(3, 4, 1)
            10 -> Triple(3, 4, 2)
            else -> throw IllegalArgumentException("Unsupported number of participants: $totalParticipants")
        }

        val strategies = mutableListOf<Strategy>()
        repeat(numLoyalists) { strategies.add(LoyalistStrategy()) }
        repeat(numRebels) { strategies.add(RebelStrategy()) }
        repeat(numSpies) { strategies.add(SpyStrategy()) }
        strategies.shuffle()

        val players = mutableListOf<Player>()
        for (i in 0 until numNonLords) {
            val general = createRandomGeneral()
            if (general != null) {
                general.strategy = strategies[i]
                players.add(general)
            }
        }
        return players
    }

//    fun createGuanYu(): GuanYuAdapter? {
//        val guanYu = GuanYuAdapter(GuanYu())
//        return if (guanYu.name !in createdNonLords) {
//            createdNonLords.add(guanYu.name)
//            guanYu
//        } else {
//            null
//        }
//    }

    fun buildWeiChain(start: WeiGeneral? = null, activeGenerals: List<Player>): WeiGeneral? {
        val weiGenerals = activeGenerals.filterIsInstance<WeiGeneral>()
            .filter { it.name != "Cao Cao" } // Exclude Cao Cao

        var current: WeiGeneral? = start
        for (general in weiGenerals) {
            if (current == null) {
                current = general
            } else {
                current.next = general
                //println("${current.name} is linked to ${general.name}")
                current = general
            }
            weiChainMessages.add("${general.name} added to the Wei chain.")
        }

        return weiGenerals.firstOrNull()
    }
    fun printWeiChainMessages() {
        weiChainMessages.forEach { println(it) }
    }
}
