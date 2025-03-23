package Strategy

import General.General
import General.Player

abstract class Strategy {
    abstract fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player?
    abstract fun isFriendly(otherStrategy: Strategy): Boolean
}
open class LordStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        // Step 1: Target Rebels
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        // Step 2: If no Rebels, target Spies
        val spies = allPlayers.filter { it is General && it.strategy is SpyStrategy && it.currentHP > 0 }
        if (spies.isNotEmpty()) {
            return spies.random()
        }
        // No Rebels or Spies left; stop attacking (Loyalists are friendly)
        return null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LoyalistStrategy
    }
}

class LoyalistStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        // Step 1: Target Rebels
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        // Step 2: If no Rebels, target Spies
        val spies = allPlayers.filter { it is General && it.strategy is SpyStrategy && it.currentHP > 0 }
        if (spies.isNotEmpty()) {
            return spies.random()
        }
        // No Rebels or Spies left; stop attacking (Lords and other Loyalists are friendly)
        return null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is LordStrategy || otherStrategy is LoyalistStrategy
    }
}

class RebelStrategy : Strategy() {
    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        // Step 1: Target Lords
        val lords = allPlayers.filter { it is General && it.strategy is LordStrategy && it.currentHP > 0 }
        if (lords.isNotEmpty()) {
            return lords.random()
        }
        // Step 2: If no Lords, target Loyalists
        val loyalists = allPlayers.filter { it is General && it.strategy is LoyalistStrategy && it.currentHP > 0 }
        if (loyalists.isNotEmpty()) {
            return loyalists.random()
        }
        // No Lords or Loyalists left; stop attacking (Rebels don't target Spies in this logic)
        return null
    }
    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return otherStrategy is RebelStrategy
    }
}

class SpyStrategy : Strategy() {
    var riskLevel: Int = 50

    override fun whomToAttack(currentPlayer: Player, allPlayers: List<Player>): Player? {
        // Step 1: Target Rebels
        val rebels = allPlayers.filter { it is General && it.strategy is RebelStrategy && it.currentHP > 0 }
        if (rebels.isNotEmpty()) {
            return rebels.random()
        }
        // Step 2: If no Rebels, target Loyalists
        val loyalists = allPlayers.filter { it is General && it.strategy is LoyalistStrategy && it.currentHP > 0 }
        if (loyalists.isNotEmpty()) {
            return loyalists.random()
        }
        // Step 3: If no Loyalists, target the Lord
        val lords = allPlayers.filter { it is General && it.strategy is LordStrategy && it.currentHP > 0 }
        if (lords.isNotEmpty()) {
            return lords.random()
        }
        // No Rebels, Loyalists, or Lords left; stop attacking
        return null
    }

    override fun isFriendly(otherStrategy: Strategy): Boolean {
        return false
    }
}
class LiuBeiStrategy : LordStrategy() {
}