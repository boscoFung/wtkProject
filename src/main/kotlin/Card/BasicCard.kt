package Card

import General.Player

class AttackCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Attack")
class DodgeCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Dodge")
class PeachCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Peach"){
    fun use(player: Player) {
        if (player.currentHP < player.maxHP && player.currentHP > 0) {
            player.currentHP++
            println("${player.name} uses $Name to recover 1 HP. Current HP: ${player.currentHP}/${player.maxHP}")
            player.hand.remove(this)
            CardDeck.discardCard(this)
        } else if (player.currentHP <= 0) {
            println("${player.name} cannot use $Name because they are at or below 0 HP.")
        } else {
            println("${player.name} cannot use $Name because they are already at max HP (${player.maxHP}).")
        }
    }
}