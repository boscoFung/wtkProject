package Card

import General.General
import General.Player

class AttackCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Attack")
class DodgeCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Dodge")
class PeachCard(Suit: String, Number: String) : BasicCard(Suit, Number, "Peach") {
    fun use(currentPlayer: Player) {
        if (currentPlayer is General && currentPlayer.defeated) {
            println("${currentPlayer.name} is already defeated and cannot use $Name.")
            return
        }
        if (currentPlayer.currentHP <= 0) {
            println("${currentPlayer.name} is in a dying state but will attempt to use $Name.")
        }
        if (currentPlayer.currentHP >= currentPlayer.maxHP) {
            println("${currentPlayer.name} is already at full HP (${currentPlayer.currentHP}/${currentPlayer.maxHP}) and cannot use $Name.")
            return
        }
        currentPlayer.currentHP++
        println("${currentPlayer.name} uses $Name to recover 1 HP. Current HP is now ${currentPlayer.currentHP}.")
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)
    }
}