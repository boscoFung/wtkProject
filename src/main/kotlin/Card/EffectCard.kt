package Card
import General.*

// TargetedCard
abstract class TargetedCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>)

    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        println("$Name requires a specific target")
    }
}

// GroupCard
abstract class GroupCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    // Declare the effect method as abstract to match EffectCard
    abstract override fun effect(currentPlayer: Player, allPlayers: List<Player>)
}

// SelfCard
abstract class SelfCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    // Declare the effect method as abstract to match EffectCard
    abstract override fun effect(currentPlayer: Player, allPlayers: List<Player>)
}

class SOONCard(Suit: String, Number: String) : SelfCard(Suit, Number, "Something out of nothing") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val cardsDrawn = 2
        for (i in 1..cardsDrawn) {
            val card = CardDeck.drawCard()
            if (card != null) {
                currentPlayer.hand.add(card)
            }
        }
        println("${currentPlayer.name} uses $Name and draws 2 cards")
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)
    }
}

class BumperHarvestCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Bumper Harvest") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val cards = mutableListOf<Card>()
        for (i in 1..allPlayers.size) {
            val card = CardDeck.drawCard() ?: break
            cards.add(card)
        }

        allPlayers.forEachIndexed { index, player ->
            if (cards.isNotEmpty()) {
                val card = cards[index % cards.size]
                player.hand.add(card)
                println("${player.name} receives a card from $Name")
            }
        }
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)
    }
}

class RainingArrowsCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Raining Arrows") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val hostileTargets = (currentPlayer as? General)?.strategy?.whomToAttack(currentPlayer, allPlayers)?.let { listOf(it) } ?: emptyList()
        val safeToUse = allPlayers.all { player ->
            player == currentPlayer || player.currentHP > 1 || hostileTargets.contains(player)
        }

        if (safeToUse) {
            allPlayers.filter { it != currentPlayer }.forEach { target ->
                println("${currentPlayer.name} uses $Name against ${target.name}")
                target.beingAttacked()
            }
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
        }
    }
}

class BarbarianInvasionCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Barbarian Invasion") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val hostileTargets = (currentPlayer as? General)?.strategy?.whomToAttack(currentPlayer, allPlayers)?.let { listOf(it) } ?: emptyList()
        val safeToUse = allPlayers.all { player ->
            player == currentPlayer || player.currentHP > 1 || hostileTargets.contains(player)
        }

        if (safeToUse) {
            allPlayers.filter { it != currentPlayer }.forEach { target ->
                println("${currentPlayer.name} uses $Name against ${target.name}")
                target.beingAttacked()
            }
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
        }
    }
}

class DuelCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Duel") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        println("${currentPlayer.name} initiates $Name with ${target.name}")
        listOf(currentPlayer, target).forEach { player ->
            if (!player.hasAttackCard()) {
                player.currentHP--
                println("${player.name} can't respond in $Name and loses 1 HP")
            } else {
                player.removeCardOfType(AttackCard::class.java)
                println("${player.name} responds in $Name with an attack card")
            }
        }
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)
    }
}

class StealingSheepCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Stealing Sheep") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        val distance = currentPlayer.calculateDistanceTo(target, allPlayers.size)
        if (target.hand.isNotEmpty() && distance <= 1) {
            val stolenCard = target.hand.removeAt(0)
            currentPlayer.hand.add(stolenCard)
            println("${currentPlayer.name} uses $Name to steal a card from ${target.name}")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        } else {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (distance: $distance > 1 or target has no cards)")
        }
    }
}

class BBQCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Burning Bridges") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        val distance = currentPlayer.calculateDistanceTo(target, allPlayers.size)
        if (target.hand.isNotEmpty() && distance <= 1) {
            val removedCard = target.hand.removeAt(0)
            CardDeck.discardCard(removedCard)
            println("${currentPlayer.name} uses $Name to discard a card from ${target.name}")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        } else {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (distance: $distance > 1 or target has no cards)")
        }
    }
}

