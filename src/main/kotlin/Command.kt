typealias Command = (Player) -> Unit
const val ACEDIA_DODGE_CHANCE = 0.25

val AcediaCommand: Command = { player ->
    println("${player.name} judging the Acedia card.")
    if (Math.random() < ACEDIA_DODGE_CHANCE) {
        println("${player.name} dodged the Acedia card.")
    } else {
        println("${player.name} can't dodge the Acedia card. Skipping one round of Play Phase.")
        player.skipPlayPhase = true
    }
}