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


    open fun onEquip() {
        if (attackLimitModifier == -1) {
            player.modifyAttackLimit(Int.MAX_VALUE)
        } else {
            player.modifyAttackLimit(player.baseAttackLimit + attackLimitModifier)
        }
        player.modifyAttackRange(player.baseAttackRange + attackRangeModifier)
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

    open fun unequip() {
        player.eArmor = null
        println("${player.name} unequipped $name")
    }
}

interface ArmorEffect {
    fun applyEffect(player: Player, onBeingAttacked: () -> Unit)
}

abstract class HorsePlus(player: Player) : Equipment(player) {
    abstract override val name: String
    init {
        player.horsePlus += 1
    }

    open fun unequip() {
        player.horsePlus -= 1
        player.eHorsePlus = null
        println("${player.name} unequipped $name (+1 Horse)")
    }
}

abstract class HorseMinus(player: Player) : Equipment(player) {
    abstract override val name: String
    init {
        player.horseMinus += 1
    }

    open fun unequip() {
        player.horseMinus -= 1
        player.eHorseMinus = null
        println("${player.name} unequipped $name (-1 Horse)")
    }
}
