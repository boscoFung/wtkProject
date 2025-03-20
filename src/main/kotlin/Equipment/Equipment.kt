import kotlin.random.Random

abstract class Equipment(protected val player: Player) {
    abstract val name: String
}

abstract class Weapon(player: Player) : Equipment(player) {
    abstract override val name: String
}

abstract class Armor(player: Player) : Equipment(player) {
    abstract override val name: String
    open fun beingAttacked() {}
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