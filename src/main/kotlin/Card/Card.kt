package Card

import General.*

abstract class Card(open val Suit: String, open val Number: String, open val Name: String) {
    override fun toString() : String {
        return "Suit = $Suit, Number = $Number, Name = $Name"
    }
}

abstract class BasicCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name)


abstract class ImpeccableCard(Suit: String, Number: String, Name: String) : Card(Suit, Number, Name) {
    // 執行無懈可擊的效果，返回是否成功取消目標卡
    abstract fun apply(currentPlayer: Player, targetCard: Card, allPlayers: List<Player>): Boolean
}

class NegationCard(Suit: String, Number: String) : ImpeccableCard(Suit, Number, "Negation") {
    override fun apply(currentPlayer: Player, targetCard: Card, allPlayers: List<Player>): Boolean {

        println("${currentPlayer.name} uses $Name to cancel ${targetCard.Name}")
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)

        println("")
        println("${currentPlayer.name} 使用了無懈可擊，取消了 ${targetCard.Name} 的效果。")
        return true
    }
}

abstract class EffectCard(Suit: String, Number: String, Name: String) : Card(Suit, Number, Name) {
    // 檢查是否被無懈可擊取消，返回 true 表示被取消
    protected fun checkImpeccable(
        currentPlayer: Player,
        targetPlayer: Player?, // 針對卡和自我卡的目標，群體卡為 null
        allPlayers: List<Player>,
        isBenefit: Boolean // 是否為收益效果（決定使用無懈可擊的條件）
    ): Boolean {
        var isCanceled = false
        var currentImpeccable: ImpeccableCard? = null

        // 從當前玩家開始，按順序詢問所有玩家
        val startIndex = allPlayers.indexOf(currentPlayer)
        val orderedPlayers = (allPlayers.drop(startIndex) + allPlayers.take(startIndex)).filter { it.currentHP > 0 }

        // 處理連鎖無懈可擊
        while (true) {
            var nextImpeccable: ImpeccableCard? = null
            var impeccableUser: Player? = null // 記錄使用無懈可擊的玩家

            for (player in orderedPlayers) {
                if (player == currentPlayer && currentImpeccable == null) continue // 跳過第一輪的使用者

                val general = player as? General
                val targetGeneral = targetPlayer as? General
                if (general?.strategy == null || targetGeneral?.strategy == null) continue

                // 根據卡片類型和是否為收益效果決定是否使用無懈可擊
                val shouldUseImpeccable = if (isBenefit) {
                    // 收益效果：如果目標是敵方，則使用無懈可擊
                    !general.strategy!!.isFriendly(targetGeneral.strategy!!)
                } else {
                    // 傷害效果或針對/自我效果：如果目標是友方，則使用無懈可擊
                    general.strategy!!.isFriendly(targetGeneral.strategy!!)
                }

                val hasImpeccable = player.hand.any { it is ImpeccableCard }
                if (hasImpeccable && shouldUseImpeccable) {
                    val impeccable = player.hand.first { it is ImpeccableCard } as ImpeccableCard
                    nextImpeccable = impeccable
                    impeccableUser = player // 記錄使用無懈可擊的玩家
                    break
                }
            }

            if (nextImpeccable == null) break // 沒有玩家使用無懈可擊，結束連鎖

            // 執行無懈可擊
            val targetCard = currentImpeccable ?: this
            if (impeccableUser != null && nextImpeccable.apply(impeccableUser, targetCard, allPlayers)) {
                isCanceled = !isCanceled // 翻轉取消狀態（無懈可擊的連鎖）
            }
            currentImpeccable = nextImpeccable
        }

        return isCanceled
    }

    // 群體卡專用的無懈可擊檢查（針對每個受影響的玩家）
    protected fun checkImpeccableForGroup(
        currentPlayer: Player,
        affectedPlayers: List<Player>,
        allPlayers: List<Player>,
        isBenefit: Boolean
    ): List<Player> {
        val playersToAffect = affectedPlayers.toMutableList()
        val iterator = playersToAffect.iterator()
        while (iterator.hasNext()) {
            val affectedPlayer = iterator.next()
            if (checkImpeccable(currentPlayer, affectedPlayer, allPlayers, isBenefit)) {
                iterator.remove() // 如果被無懈可擊取消，則移除該玩家
            }
        }
        return playersToAffect
    }
}

abstract class EquipmentCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
}

abstract class JudgementCard(
    override val Suit: String,
    override val Number: String,
    override val Name: String
) : Card(Suit, Number, Name) {
}
