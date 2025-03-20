import kotlin.random.Random

class EightTrigrams(player: Player) : Armor(player) {
    override val name: String = "Eight Trigrams"
    override fun beingAttacked() {
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

    fun hasAttackCard(): Boolean {
        return player.hasAttackCard()
    }
}
