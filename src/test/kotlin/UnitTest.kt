import Factory.*
import General.*
import Strategy.*
import kotlin.test.Test
import kotlin.test.assertTrue

class WeiTest {
    @Test
    fun testEntourage() {
        // Step 1: Create Cao Cao directly and add to GeneralManager
        val caoCao = CaoCao()
        caoCao.strategy = LordStrategy()
        GeneralManager.addPlayer(caoCao)

        // Step 2: Use WeiOnlyNonLordFactory to generate 3 Wei generals
        val factory = WeiOnlyNonLordFactory()
        val activeGenerals = mutableListOf<Player>(caoCao)
        val weiGenerals = mutableListOf<WeiGeneral>()
        repeat(3) {
            val general = factory.createRandomGeneral()
            if (general != null) {
                GeneralManager.addPlayer(general)
                activeGenerals.add(general)
                if (general is WeiGeneral) {
                    weiGenerals.add(general) // Add to list instead of setting firstWeiGeneral
                }
//                println("General ${general.name} created.")
                val strategies = listOf(LoyalistStrategy(), RebelStrategy(), SpyStrategy())
                general.strategy = strategies.random()
                val strategyName = general.strategy?.javaClass?.simpleName?.removeSuffix("Strategy")?.toLowerCase()
//                println("${general.name}, a $strategyName, has ${general.maxHP} health point(s).")
//                if (strategyName == "spy") {
//                    println("${general.name} is observing lord.")
//                }
            }
        }

        // Step 3: Build the Wei chain
        val weiChainStart = factory.buildWeiChain(null, activeGenerals)
        if (!weiGenerals.isEmpty()) {
            weiGenerals[0].forceDodgeForTesting = false // Set on first Wei general after loop
        }
        caoCao.next = weiChainStart
//        factory.printWeiChainMessages()

        // Step 4: Test Cao Cao's entourage skill
        val result = caoCao.entourage()
        assertTrue(result, "Cao Cao's entourage should help him dodge the attack.")
    }
}

class WeiOnlyNonLordFactory : NonLordFactory() {
    override fun createRandomGeneral(): General? {
        while (true) {
            val general = super.createRandomGeneral() ?: return null // No more generals available
            // Print general creation info (required for output)
            println("General ${general.name} created.")
            val strategies = listOf(LoyalistStrategy(), RebelStrategy(), SpyStrategy())
            general.strategy = strategies.random()
            val strategyName = general.strategy?.javaClass?.simpleName?.removeSuffix("Strategy")?.toLowerCase()
            println("${general.name}, a $strategyName, has ${general.maxHP} health point(s).")
            if (strategyName == "spy") {
                println("${general.name} is observing lord.")
            }

            // Check if the general is a WeiGeneral
            if (general is WeiGeneral) {
                println("${general.name} added to the Wei chain.")
                return general
            } else {
                println("${general.name} is discarded as he/she is not a Wei.")
                createdNonLords.remove(general.name) // Remove from createdNonLords to allow re-creation
            }
        }
    }
}