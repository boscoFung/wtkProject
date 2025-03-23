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
        return true
    }
}

abstract class EffectCard(Suit: String, Number: String, Name: String) : Card(Suit, Number, Name) {
    protected fun checkImpeccable(
        currentPlayer: Player,
        targetPlayer: Player?,
        allPlayers: List<Player>,
        isBenefit: Boolean
    ): Boolean {
        var isCanceled = false
        var currentImpeccable: ImpeccableCard? = null

        val startIndex = allPlayers.indexOf(currentPlayer)
        val orderedPlayers = (allPlayers.drop(startIndex) + allPlayers.take(startIndex)).filter { it.currentHP > 0 }

        while (true) {
            var nextImpeccable: ImpeccableCard? = null
            var impeccableUser: Player? = null

            for (player in orderedPlayers) {
                if (player == currentPlayer && currentImpeccable == null) continue

                val general = player as? General
                val targetGeneral = targetPlayer as? General
                if (general?.strategy == null || targetGeneral?.strategy == null) continue

                val shouldUseImpeccable = if (isBenefit) {
                    !general.strategy!!.isFriendly(targetGeneral.strategy!!)
                } else {
                    general.strategy!!.isFriendly(targetGeneral.strategy!!)
                }

                val hasImpeccable = player.hand.any { it is ImpeccableCard }
                if (hasImpeccable && shouldUseImpeccable) {
                    val impeccable = player.hand.first { it is ImpeccableCard } as ImpeccableCard
                    nextImpeccable = impeccable
                    impeccableUser = player
                    break
                }
            }

            if (nextImpeccable == null) break

            val targetCard = currentImpeccable ?: this
            if (impeccableUser != null && nextImpeccable.apply(impeccableUser, targetCard, allPlayers)) {
                isCanceled = !isCanceled
            }
            currentImpeccable = nextImpeccable
        }

        return isCanceled
    }

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
                iterator.remove()
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
