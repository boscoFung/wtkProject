import kotlin.random.Random

class GuanYuAdapter(private val guanYu: GuanYu) : Player {
    override val name: String = "Guan Yu"

    override val maxHP: Int
        get() = guanYu.maximumHP

    override var currentHP: Int = maxHP

    override var numOfCards: Int = 4

    override var skipPlayPhase: Boolean = false

    override val judgementCommands: MutableList<Command> = mutableListOf()

    override fun beingAttacked() {
        println("$name is being attacked.")
        currentHP--
        if (currentHP < 0) currentHP = 0
        println("$name can't dodge the attack, current HP is $currentHP")
    }

    override fun dodgeAttack() {
        println("$name dodges the attack!")
    }
    override fun hasAttackCard(): Boolean {
        val attackChance = 0.20
        return (1..numOfCards).any { Random.nextDouble() < attackChance }
    }
}