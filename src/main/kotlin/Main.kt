import Card.CardDeck
import Card.EightTrigramsCard

//
//fun main() {
//    val totalParticipants = 4
//    val lordFactory = LordFactory()
//    val nonLordFactory = NonLordFactory()
//
//    val lord = lordFactory.createRandomGeneral()
//    if (lord != null) {
//        GeneralManager.addPlayer(lord.apply { strategy = LordStrategy() })
//    }
//
//    val nonLordPlayers = nonLordFactory.createNonLordPlayers(totalParticipants)
//    nonLordPlayers.forEach { GeneralManager.addPlayer(it) }
//
//    val caoCaoPlayer = GeneralManager.getPlayerList().find { it is CaoCao } as? CaoCao
//    if (caoCaoPlayer != null) {
//        println("曹操在場")
//        val factory = NonLordFactory()
//        val weiChainHead = factory.buildWeiChain(null, GeneralManager.getPlayerList())
//        caoCaoPlayer.next = weiChainHead
//        factory.printWeiChainMessages()
//    }
//
//    val lordPlayer = GeneralManager.getPlayerList().find { it is General && it.strategy is LordStrategy } as? General
//    if (lordPlayer != null) {
//        val spies = GeneralManager.getPlayerList().filter { it is General && it.strategy is SpyStrategy }
//        for (spy in spies) {
//            lordPlayer.registerObserver(SpyObserver(spy as General))
//        }
//    }
//
//    GeneralManager.gameStart()
//
//    // 可選：測試距離（如果你還想保留）
//    println("\n=== 測試玩家之間的距離和攻擊範圍 ===")
//    val players = GeneralManager.getPlayerList()
//    players.forEach { attacker ->
//        players.forEach { target ->
//            if (attacker != target) {
//                val distance = attacker.calculateDistanceTo(target, players.size)
//                val range = attacker.calculateAttackRange()
//                println("${attacker.name} (座位 ${attacker.seat}) 到 ${target.name} (座位 ${target.seat}) 的距離為 $distance")
//                println("${attacker.name} 的攻擊範圍為 $range")
//                println(if (distance <= range) "${attacker.name} 可以攻擊 ${target.name}！" else "${attacker.name} 無法攻擊 ${target.name}，距離超出範圍。")
//                println()
//            }
//        }
//    }
//}
fun main() {
    // Step 1: Set up the game with 4 players
    val totalParticipants = 4
    val lordFactory = LordFactory()
    val nonLordFactory = NonLordFactory()

    // Create a lord player
    val lord = lordFactory.createRandomGeneral()
    if (lord != null) {
        GeneralManager.addPlayer(lord.apply { strategy = LordStrategy() })
    }

    // Create non-lord players
    val nonLordPlayers = nonLordFactory.createNonLordPlayers(totalParticipants)
    nonLordPlayers.forEach { GeneralManager.addPlayer(it) }

    // Step 2: Set up spies to observe the lord (if any)
    val lordPlayer = GeneralManager.getPlayerList().find { it is General && it.strategy is LordStrategy } as? General
    if (lordPlayer != null) {
        val spies = GeneralManager.getPlayerList().filter { it is General && it.strategy is SpyStrategy }
        for (spy in spies) {
            lordPlayer.registerObserver(SpyObserver(spy as General))
        }
    }

    // Step 3: Give the first player an Eight Trigrams card and equip it
    val firstPlayer = GeneralManager.getPlayerList().first() as General
    println("Giving ${firstPlayer.name} an Eight Trigrams card for testing...")
    firstPlayer.hand.add(EightTrigramsCard("Spades", "2"))  // Add Eight Trigrams card to hand
    firstPlayer.playCard(firstPlayer.hand.first { it is EightTrigramsCard })  // Equip the card

    // Step 4: Start the game (this will also trigger playPhase where equipment is equipped)
    GeneralManager.gameStart()

    // Step 5: Simulate an attack on the first player to test Eight Trigrams
    println("\n=== Testing Eight Trigrams ===")
    println("Before attack: ${firstPlayer.name} has ${firstPlayer.currentHP} HP and ${firstPlayer.hand.size} cards.")
    firstPlayer.beingAttacked()  // Trigger the Eight Trigrams judgment
    println("After attack: ${firstPlayer.name} has ${firstPlayer.currentHP} HP and ${firstPlayer.hand.size} cards.")

    // Step 6: Print the discard pile to see the judgment card
    CardDeck.printDiscardPile()

}