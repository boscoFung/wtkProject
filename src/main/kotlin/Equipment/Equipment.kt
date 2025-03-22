package Equipment
import Card.Card
import General.*
import kotlin.random.Random


abstract class Equipment(protected val player: Player) {
    abstract val name: String
    open fun beingAttacked() {
        player.beingAttacked()
    }
}

abstract class Weapon(player: Player) : Equipment(player) {
    abstract override val name: String
    abstract val attackRangeModifier: Int  // 攻擊距離增量
    abstract val attackLimitModifier: Int  // 攻擊上限增量（-1 表示無限）

    // 儲存原始屬性
    private val originalAttackLimit: Int
    private val originalAttackRange: Int

    init {
        originalAttackLimit = player.currentAttackLimit
        originalAttackRange = player.currentAttackRange

        // Apply attack limit modifier
        if (attackLimitModifier == -1) {
            player.modifyAttackLimit(Int.MAX_VALUE)
        } else {
            player.modifyAttackLimit(originalAttackLimit + attackLimitModifier)
        }

        // Apply attack range modifier
        player.modifyAttackRange(originalAttackRange + attackRangeModifier)
    }

    abstract fun canAttack(attacksThisTurn: Int): Boolean
    abstract fun attackTarget(attacker: Player, target: Player, attackCard: Card?)

    open fun unequip() {
        player.restoreAttackLimit()
        player.restoreAttackRange()
        player.eWeapon = null
        println("${player.name} unequipped $name")
    }
}
interface WeaponEffect {
    fun applyEffect(attacker: Player, target: Player, attackCard: Card?)
}

abstract class Armor(player: Player) : Equipment(player) {
    abstract override val name: String
}

interface ArmorEffect {
    fun applyEffect(player: Player, onBeingAttacked: () -> Unit)
}

abstract class HorsePlus(player: Player) : Equipment(player) {
    abstract override val name: String
    init {
        player.horsePlus += 1
    }
}

abstract class HorseMinus(player: Player) : Equipment(player) {
    abstract override val name: String
    init {
        player.horseMinus += 1
    }
}