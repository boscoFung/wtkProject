import kotlin.random.Random

abstract class Equipment(protected val player: Player) : Player {
    override val name: String
        get() = player.name
    override val maxHP: Int
        get() = player.maxHP

    override var currentHP: Int
        get() = player.currentHP
        set(value) { player.currentHP = value }

    override var numOfCards: Int
        get() = player.numOfCards
        set(value) {
            player.numOfCards = value
        }

    override var skipPlayPhase: Boolean
        get() = player.skipPlayPhase
        set(value) {
            player.skipPlayPhase = value
        }

    override val judgementCommands: MutableList<Command>
        get() = player.judgementCommands

    override fun beingAttacked() {
        println("$name is being attacked.")
        dodgeAttack()
    }

    override fun dodgeAttack() {
        player.dodgeAttack()
    }


}

class EightTrigrams(player: Player) : Equipment(player) {
    override fun dodgeAttack() {
        println("Triggering the Eight Trigrams")
        val success = Random.nextBoolean()
        if (success) {
            println("Judgement is true")
            println("$name dodged the attack with the Eight Trigrams.")
        } else {
            println("Judgement is false")
            player.beingAttacked()
        }
    }
    override fun hasAttackCard(): Boolean {
        return player.hasAttackCard()
    }
}