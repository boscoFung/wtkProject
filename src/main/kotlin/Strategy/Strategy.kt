package Strategy

import Card.AttackCard
import General.General
import General.Player

abstract class Strategy {
    abstract fun whomToAttack(
        currentPlayer: Player,
        allPlayers: List<Player>,
        maxDistance: Int? = null // 可選的距離限制，null 表示不限制距離
    ): Player?

    abstract fun isFriendly(otherStrategy: Strategy): Boolean

    protected fun filterByDistance(
        currentPlayer: Player,
        candidates: List<Player>,
        allPlayers: List<Player>,
        maxDistance: Int?
    ): List<Player> {
        if (maxDistance == null) return candidates
        return candidates.filter {
            val distance = currentPlayer.calculateDistanceTo(it, GeneralManager.getPlayerCount())
            distance <= maxDistance
        }
    }
}

open class LordStrategy : Strategy() {
    override fun whomToAttack(
        currentPlayer: Player,
        allPlayers: List<Player>,
        maxDistance: Int?
    ): Player? {
        var rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        rebels = filterByDistance(currentPlayer, rebels, allPlayers, maxDistance)
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        var spies = allPlayers.filter { it is General && it.strategy is SpyStrategy && it.currentHP > 0 }
        spies = filterByDistance(currentPlayer, spies, allPlayers, maxDistance)
        if (spies.isNotEmpty()) {
            return spies.random()
        }
        return null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LoyalistStrategy
    }
}

class LoyalistStrategy : Strategy() {
    override fun whomToAttack(
        currentPlayer: Player,
        allPlayers: List<Player>,
        maxDistance: Int?
    ): Player? {
        var rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        rebels = filterByDistance(currentPlayer, rebels, allPlayers, maxDistance)
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        var spies = allPlayers.filter { it is General && it.strategy is SpyStrategy && it.currentHP > 0 }
        spies = filterByDistance(currentPlayer, spies, allPlayers, maxDistance)
        if (spies.isNotEmpty()) {
            return spies.random()
        }
        return null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LordStrategy || otherStrategy is LoyalistStrategy
    }
}

class RebelStrategy : Strategy() {
    override fun whomToAttack(
        currentPlayer: Player,
        allPlayers: List<Player>,
        maxDistance: Int?
    ): Player? {
        val range = maxDistance ?: currentPlayer.calculateAttackRange()
        var lords = allPlayers.filter {
            it is General && it.strategy is LordStrategy && it.currentHP > 0 &&
                    currentPlayer.calculateDistanceTo(it, GeneralManager.getPlayerCount()) <= range &&
                    it.canBeTargeted(currentPlayer, AttackCard("", "0"))
        }
        if (lords.isNotEmpty()) {
            return lords.random()
        }
        var loyalists = allPlayers.filter {
            it is General && it.strategy is LoyalistStrategy && it.currentHP > 0 &&
                    currentPlayer.calculateDistanceTo(it, GeneralManager.getPlayerCount()) <= range &&
                    it.canBeTargeted(currentPlayer, AttackCard("", "0"))
        }
        if (loyalists.isNotEmpty()) {
            return loyalists.random()
        }
        return null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is RebelStrategy
    }
}

class SpyStrategy : Strategy() {
    var riskLevel: Int = 50

    override fun whomToAttack(
        currentPlayer: Player,
        allPlayers: List<Player>,
        maxDistance: Int?
    ): Player? {
        var rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        rebels = filterByDistance(currentPlayer, rebels, allPlayers, maxDistance)
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        var loyalists = allPlayers.filter { it is General && it.strategy is LoyalistStrategy && it.currentHP > 0 }
        loyalists = filterByDistance(currentPlayer, loyalists, allPlayers, maxDistance)
        if (loyalists.isNotEmpty()) {
            return loyalists.random()
        }
        var lords = allPlayers.filter { it is General && it.strategy is LordStrategy && it.currentHP > 0 }
        lords = filterByDistance(currentPlayer, lords, allPlayers, maxDistance)
        if (lords.isNotEmpty()) {
            return lords.random()
        }
        return null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return false
    }
}
class LiuBeiStrategy : LordStrategy() {
}