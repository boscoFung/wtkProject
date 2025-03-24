package Equipment
import Card.Card
import Card.CardDeck
import Card.EquipmentCard
import General.*
import kotlin.random.Random


abstract class Equipment(protected val player: Player, card: EquipmentCard) {
    abstract val name: String
    private val card: EquipmentCard = card
    protected open var isEquipped = true

    open fun beingAttacked() {
        player.beingAttacked()
    }

    open fun unequip() {
        if (!isEquipped) {
            println("${player.name} already unequipped $name, skipping")
            return
        }
        isEquipped = false
        CardDeck.discardCard(card)
        println("${player.name} unequipped $name and discarded it to the discard pile")
    }

    fun getCard(): EquipmentCard = card
}

abstract class Weapon(player: Player, card: EquipmentCard) : Equipment(player, card) {
    abstract override val name: String
    abstract val attackRangeModifier: Int
    abstract val attackLimitModifier: Int

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

    override fun unequip() {
        super.unequip()
        player.restoreAttackLimit()
        player.restoreAttackRange()
        player.eWeapon = null
    }


}

//interface WeaponEffect {
//    fun applyEffect(attacker: Player, target: Player, attackCard: Card?)
//}

abstract class Armor(player: Player, card: EquipmentCard) : Equipment(player, card) {
    abstract override val name: String
    private var storedCard: EquipmentCard? = card
    override fun unequip() {
        super.unequip()
        player.eArmor = null
    }
}

interface ArmorEffect {
    fun applyEffect(player: Player, onBeingAttacked: () -> Unit)
}

abstract class HorsePlus(player: Player, card: EquipmentCard) : Equipment(player, card) {
    abstract override val name: String
    override var isEquipped = true
    init {
        player.horsePlus += 1
    }

    override fun unequip() {
        if (!isEquipped) return
        player.horsePlus += 1
        isEquipped = false
        super.unequip()
        player.eHorsePlus = null
    }
}

abstract class HorseMinus(player: Player, card: EquipmentCard) : Equipment(player, card) {
    abstract override val name: String
    override var isEquipped = true
    init {
        player.horseMinus += 1
    }

    override fun unequip() {
        if (!isEquipped) return
        player.horseMinus -= 1
        isEquipped = false
        super.unequip()
        player.eHorseMinus = null
    }
}