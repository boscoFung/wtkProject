import Card.CardDeck
import Card.EightTrigramsCard
import Factory.*
import Strategy.*
import General.*


fun main() {
    val totalParticipants = 6
    val lordFactory = LordFactory()
    val nonLordFactory = NonLordFactory()

    val lord = lordFactory.createRandomGeneral()
    if (lord != null) {
        GeneralManager.addPlayer(lord.apply { strategy = LordStrategy() })
    }

    val nonLordPlayers = nonLordFactory.createNonLordPlayers(totalParticipants)
    nonLordPlayers.forEach { GeneralManager.addPlayer(it) }

    val caoCaoPlayer = GeneralManager.getPlayerList().find { it is CaoCao } as? CaoCao
    if (caoCaoPlayer != null) {
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

    println("\n=== 測試玩家之間的距離和攻擊範圍 ===")
    val players = GeneralManager.getPlayerList()
    players.forEach { attacker ->
        players.forEach { target ->
            if (attacker != target) {
                val distance = attacker.calculateDistanceTo(target, players.size)
                val range = attacker.calculateAttackRange()
                println("${attacker.name} (座位 ${attacker.seat}) 到 ${target.name} (座位 ${target.seat}) 的距離為 $distance")
                println("${attacker.name} 的攻擊範圍為 $range")
                println(if (distance <= range) "${attacker.name} 可以攻擊 ${target.name}！" else "${attacker.name} 無法攻擊 ${target.name}，距離超出範圍。")
                println()
            }
        }
    }
}
