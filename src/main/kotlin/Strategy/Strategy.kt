package Strategy

import General.General
import General.Player

abstract class Strategy {
    abstract fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player?
    abstract fun isFriendly(otherStrategy: Strategy): Boolean
}
open class LordStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy }
        return if (rebels.isNotEmpty()) rebels.random() else null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LoyalistStrategy
    }
}

class LoyalistStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy }
        return if (rebels.isNotEmpty()) rebels.random() else null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LordStrategy || otherStrategy is LoyalistStrategy
    }
}

class RebelStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        val lords = allPlayers.filter { it is General && it.strategy is LordStrategy }
        return if (lords.isNotEmpty()) lords.random() else null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LoyalistStrategy
    }
}

class SpyStrategy : Strategy() {
    var riskLevel: Int = 50

    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy }
        return if (rebels.isNotEmpty()) rebels.random() else null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return false
    }
}
class LiuBeiStrategy : LordStrategy() {
}