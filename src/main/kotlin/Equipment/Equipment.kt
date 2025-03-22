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

    // 根據當前攻擊次數決定是否允許攻擊
    abstract fun canAttack(attacksThisTurn: Int): Boolean

    open fun applyEffect(attacker: Player, target: Player, attackCard: Card?) {
        target.beingAttacked() // 默認效果
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