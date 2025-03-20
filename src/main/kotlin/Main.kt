fun main() {
    val totalParticipants = 4 // Example: can be adjusted
    val lordFactory = LordFactory()
    val nonLordFactory = NonLordFactory()

    val lord = lordFactory.createRandomGeneral()
    if (lord != null) {
        GeneralManager.addPlayer(LiuBei().apply { strategy = LordStrategy() })
    }

    val nonLordPlayers = nonLordFactory.createNonLordPlayers(totalParticipants)
    nonLordPlayers.forEach { GeneralManager.addPlayer(it) }
    val caoCaoPlayer = GeneralManager.getPlayerList().find { it is CaoCao } as? CaoCao
    if (caoCaoPlayer != null) {
        println("caocao is here")
        val factory = NonLordFactory()
        val weiChainHead = factory.buildWeiChain(null, GeneralManager.getPlayerList())
        caoCaoPlayer.next = weiChainHead
        factory.printWeiChainMessages()
    }

    val lordPlayer = GeneralManager.getPlayerList().find { it is General && it.strategy is LordStrategy } as? General
    if (lordPlayer != null) {
        val spies = GeneralManager.getPlayerList().filter { it is General && it.strategy is SpyStrategy }
        for (spy in spies) {
            lordPlayer.registerObserver(SpyObserver(spy as General))
        }
    }
    GeneralManager.gameStart()
}