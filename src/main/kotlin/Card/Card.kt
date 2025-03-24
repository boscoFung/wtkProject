package Card

import General.*
enum class Suit {
    Spades, Hearts, Diamonds, Clubs
}

abstract class Card(open val Suit: String, open val Number: String, open val Name: String) {
    override fun toString() : String {
        return "Suit = $Suit, Number = $Number, Name = $Name"
    }

    fun getNumericValue(): Int {
        return when (Number) {
            "A" -> 1
            "J" -> 11
            "Q" -> 12
            "K" -> 13
            else -> Number.toIntOrNull() ?: 0
        }
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
        if (currentPlayer.currentHP <= 0 || !allPlayers.contains(currentPlayer)) {
            println("${currentPlayer.name} is defeated or not in the player list, skipping Impeccable check.")
            return false
        }
        if (targetPlayer != null && targetPlayer.currentHP <= 0) {
            println("${targetPlayer.name} is defeated, skipping Impeccable check.")
            return false
        }

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
                if (general?.strategy == null || (targetPlayer != null && targetGeneral?.strategy == null)) continue

                val shouldUseImpeccable = if (isBenefit) {
                    !general.strategy!!.isFriendly(targetGeneral?.strategy!!)
                } else {
                    general.strategy!!.isFriendly(targetGeneral?.strategy!!)
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
                if (general?.strategy == null) continue

                val shouldUseImpeccable = if (isBenefit) {
                    !general.strategy!!.isFriendly((currentPlayer as General).strategy!!)
                } else {
                    general.strategy!!.isFriendly((currentPlayer as General).strategy!!)
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

        return if (isCanceled) emptyList() else affectedPlayers
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
