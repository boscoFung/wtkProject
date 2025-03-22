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
        // 儲存原始屬性
        originalAttackLimit = player.baseAttackLimit
        originalAttackRange = player.baseAttackRange

        // 修改攻擊上限
        if (attackLimitModifier == -1) {
            player.modifyAttackLimit(Int.MAX_VALUE)  // 無限攻擊
        } else {
            player.modifyAttackLimit(player.baseAttackLimit + attackLimitModifier)
        }

        // 修改攻擊距離
        player.modifyAttackRange(player.baseAttackRange + attackRangeModifier)
    }

    // 檢查是否可以攻擊（根據攻擊次數限制）
    abstract fun canAttack(attacksThisTurn: Int): Boolean

    // 攻擊目標
    abstract fun attackTarget(attacker: Player, target: Player, attackCard: Card?)

    // 解除武器時恢復原始屬性
    open fun unequip() {
        player.modifyAttackLimit(originalAttackLimit)
        player.modifyAttackRange(originalAttackRange)
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