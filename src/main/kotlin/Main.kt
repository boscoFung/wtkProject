import Card.CardDeck
import Card.EightTrigramsCard
import Factory.*
import Strategy.*
import General.*


fun main() {
    val totalParticipants = 4
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
//import Card.*
//import Equipment.*
//import General.*
//import Strategy.*
//
//fun main() {
//    // 假玩家類別，用於測試
//    class TestPlayer(name: String) : General(name, 4, "male")
//
//    val player = TestPlayer("Test Warrior")
//    player.seat = 0 // 先設 seat，讓距離邏輯不報錯
//
//    // 測試用目標
//    val target = TestPlayer("Target Dummy")
//    target.seat = 1
//
//    // 放入 GeneralManager 裡，避免 null
//
//
//    // 所有武器測試清單
//    val weapons = listOf(
//        ZhugeCrossbow(player),
//        RockCleavingAxe(player),
//        SkyPiercingHalberd(player),
//        YinYangSwords(player),
//        GreenDragonBlade(player),
//        BlueSteelBlade(player),
//        SerpentSpear(player),
//        KirinBow(player)
//    )
//
//    println("=========== 武器測試開始 ===========")
//
//    for (weapon in weapons) {
//        println("\n>>> 測試裝備武器：${weapon.name}")
//
//        // 卸下原本的武器（如有）
//        if (player.eWeapon != null) {
//            (player.eWeapon as Weapon).unequip()
//        }
//
//        // 設定玩家初始狀態
//        player.currentAttackLimit = player.baseAttackLimit
//        player.currentAttackRange = player.baseAttackRange
//
//        // 裝備新武器
//        player.eWeapon = weapon
//        weapon.onEquip()
//        // 印出裝備後效果
//        println("${player.name} 裝備 ${weapon.name}")
//        println("  攻擊上限應為: ${if (weapon.attackLimitModifier == -1) "無限" else player.baseAttackLimit + weapon.attackLimitModifier}")
//        println("  實際攻擊上限: ${player.currentAttackLimit}")
//        println("  攻擊距離應為: ${player.baseAttackRange + weapon.attackRangeModifier}")
//        println("  實際攻擊距離: ${player.currentAttackRange}")
//    }
//
//    println("\n=========== 武器測試結束 ===========")
//}
