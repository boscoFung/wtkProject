//import Card.CardDeck
//import Card.EightTrigramsCard
//import Factory.*
//import Strategy.*
//import General.*
//
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

import Card.*
import Equipment.SerpentSpear
import Equipment.Weapon
import Factory.HorseFactory
import General.General
import General.Player
import Strategy.GeneralManager
import Strategy.LordStrategy
import Strategy.RebelStrategy

fun main() {
    CardDeck.initializeDeck()

    // Player 1 (Attacker) with various weapons
    val player1 = object : General("Attacker", 4, "Male") {
        override fun calculateAttackRange(): Int = currentAttackRange
    }.apply {
        strategy = LordStrategy()
        seat = 1
        hand.add(AttackCard("Spades", "7"))
        hand.add(AttackCard("Spades", "8"))
        hand.add(AttackCard("Spades", "9"))
        hand.add(DodgeCard("Hearts", "2")) // For Serpent Spear
    }

    // Player 2 (Target)
    val player2 = object : General("Target", 4, "Female") { // Opposite gender for Gender Double Swords
        override fun calculateAttackRange(): Int = currentAttackRange
    }.apply {
        strategy = RebelStrategy()
        seat = 2
        hand.add(DodgeCard("Diamonds", "2"))
        eHorsePlus = HorseFactory.createHorse(this, "Red Hare", HorseType.PLUS)
    }

    GeneralManager.addPlayer(player1)
    GeneralManager.addPlayer(player2)

    val weapons = listOf(
        "Zhuge Crossbow", "Rock Cleaving Axe", "Heaven Scorcher Halberd", "Gender Double Swords",
        "Green Dragon Crescent Blade", "Blue Steel Blade", "Serpent Spear", "Kirin Bow"
    )

    weapons.forEach { weaponName ->
        println("\n=== Testing $weaponName ===")
        // Properly unequip the previous weapon
        if (player1.eWeapon != null) {
            (player1.eWeapon as Weapon).unequip()
        }
        player1.attacksThisTurn = 0
        player1.currentHP = 4
        player2.currentHP = 4
        player1.hand.clear()
        player1.hand.add(AttackCard("Spades", "7"))
        if (weaponName == "Heaven Scorcher Halberd") player1.hand.clear() // Test last card
        player1.hand.add(AttackCard("Spades", "8"))
        if (weaponName == "Serpent Spear") player1.hand.add(DodgeCard("Hearts", "2")) // For discard
        player2.hand.clear()
        player2.hand.add(DodgeCard("Diamonds", "2"))

        val weaponCard = WeaponCard("Diamonds", "A", weaponName)
        player1.playCard(weaponCard)
        println("${player1.name}'s hand: ${player1.hand.map { "${it.Suit} ${it.Number} - ${it.Name}" }}")
        println("${player2.name}'s HP: ${player2.currentHP}, Hand: ${player2.hand.map { it.Name }}")

        player1.performAttack()
        if (weaponName == "Serpent Spear" && player1.hand.size >= 2) { // Test special ability
            (player1.eWeapon as SerpentSpear).attackTarget(player1, player2, null)
        }
    }
}