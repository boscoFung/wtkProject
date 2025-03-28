package Card
import Equipment.*
import General.*

// TargetedCard
abstract class TargetedCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>)

    enum class ActionType {
        STEAL, DISCARD
    }

    open fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        println("$Name requires a specific target")
    }

    protected fun executeWithImpeccableCheck(
        currentPlayer: Player,
        target: Player,
        allPlayers: List<Player>,
        action: () -> Unit
    ) {
        if (!checkImpeccable(currentPlayer, target, allPlayers, isBenefit = false)) {
            action()
        } else {
            println("${currentPlayer.name}'s $Name was canceled by Impeccable")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }

    protected fun handlePriorityAction(
        currentPlayer: Player,
        target: Player,
        actionType: ActionType,
        onStealEquipment: (Equipment) -> Unit,
        onStealCard: (Card) -> Unit,
        onDiscardEquipment: (Equipment) -> Unit,
        onDiscardCard: (Card) -> Unit
    ) {
        when {
            target.eWeapon != null -> {
                val weapon = target.eWeapon as Weapon
                when (actionType) {
                    ActionType.STEAL -> onStealEquipment(weapon)
                    ActionType.DISCARD -> onDiscardEquipment(weapon)
                }
                printActionMessage(currentPlayer, target, actionType, weapon)
            }
            target.eArmor != null -> {
                val armor = target.eArmor as Armor
                when (actionType) {
                    ActionType.STEAL -> onStealEquipment(armor)
                    ActionType.DISCARD -> onDiscardEquipment(armor)
                }
                printActionMessage(currentPlayer, target, actionType, armor)
            }
            target.eHorsePlus != null -> {
                val horsePlus = target.eHorsePlus as HorsePlus
                when (actionType) {
                    ActionType.STEAL -> onStealEquipment(horsePlus)
                    ActionType.DISCARD -> onDiscardEquipment(horsePlus)
                }
                printActionMessage(currentPlayer, target, actionType, horsePlus)
            }
            target.eHorseMinus != null -> {
                val horseMinus = target.eHorseMinus as HorseMinus
                when (actionType) {
                    ActionType.STEAL -> onStealEquipment(horseMinus)
                    ActionType.DISCARD -> onDiscardEquipment(horseMinus)
                }
                printActionMessage(currentPlayer, target, actionType, horseMinus)
            }
            target.hand.isNotEmpty() -> {
                val card = target.hand.removeAt(0)
                when (actionType) {
                    ActionType.STEAL -> onStealCard(card)
                    ActionType.DISCARD -> onDiscardCard(card)
                }
                printActionMessage(currentPlayer, target, actionType, card)
            }
        }
    }

    private fun printActionMessage(currentPlayer: Player, target: Player, actionType: ActionType, item: Any) {
        val actionName = when (actionType) {
            ActionType.STEAL -> "Stealing Sheep"
            ActionType.DISCARD -> "Burning Bridges"
        }
        val (itemType, itemName) = when (item) {
            is Weapon -> "Weapon" to item.name
            is Armor -> "Armor" to item.name
            is HorsePlus -> "+1 Horse" to item.name
            is HorseMinus -> "-1 Horse" to item.name
            is Card -> "card (${item.Suit} ${item.Number} - ${item.Name})" to "${item.Suit} ${item.Number} - ${item.Name}"
            else -> "unknown" to "unknown"
        }

        println("${currentPlayer.name} uses $actionName to ${if (actionType == ActionType.STEAL) "steal" else "discard"} a $itemType from ${target.name}")
    }
}

abstract class SelfCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, allPlayers: List<Player>)

    protected fun executeWithImpeccableCheck(
        currentPlayer: Player,
        allPlayers: List<Player>,
        action: () -> Unit
    ) {
        if (!checkImpeccable(currentPlayer, currentPlayer, allPlayers, isBenefit = false)) {
            action()
        } else {
            println("${currentPlayer.name}'s $Name was canceled by Impeccable")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}

abstract class GroupCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, allPlayers: List<Player>)

    protected fun handleDamageWithDodge(
        currentPlayer: Player,
        target: Player,
        allPlayers: List<Player>,
        damageResults: MutableList<String>
    ) {
        if (checkImpeccable(currentPlayer, target, allPlayers, isBenefit = false)) {
            println("${target.name} is protected from $Name by Impeccable")
            damageResults.add("Dealt 0 damage to ${target.name}")
            return
        }

        val canDodge = when (Name) {
            "Barbarian Invasion" -> target.hand.any { it is AttackCard }
            "Raining Arrows" -> target.hand.any { it is DodgeCard }
            else -> target.hand.any { it is AttackCard || it is DodgeCard }
        }

        if (canDodge) {
            val dodgeCard = when (Name) {
                "Barbarian Invasion" -> target.hand.first { it is AttackCard }
                "Raining Arrows" -> target.hand.first { it is DodgeCard }
                else -> target.hand.first { it is AttackCard || it is DodgeCard }
            }
            target.hand.remove(dodgeCard)
            CardDeck.discardCard(dodgeCard)
            println("${target.name} uses ${dodgeCard.Name} (${dodgeCard.Suit} ${dodgeCard.Number}) to dodge $Name")
            damageResults.add("Dealt 0 damage to ${target.name}")
        } else {
            val initialHP = target.currentHP
            (target as General).applyDamage(1, currentPlayer, this)
            val damageDealt = initialHP - target.currentHP
            println("${target.name} can't dodge $Name, current HP is ${target.currentHP}")
            damageResults.add("Dealt $damageDealt damage to ${target.name}")

            if (target.currentHP <= 0) {
                target.handleDefeat(currentPlayer)
            }
        }
    }
}

class BumperHarvestCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Bumper Harvest") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val cards = mutableListOf<Card>()
        for (i in 1..allPlayers.size) {
            val card = CardDeck.drawCard() ?: break
            cards.add(card)
        }

        val drawResults = mutableListOf<String>()
        val affectedPlayers = allPlayers.filter { it.currentHP > 0 }
        val playersToDraw = checkImpeccableForGroup(currentPlayer, affectedPlayers, allPlayers, isBenefit = true)

        if (playersToDraw.isEmpty()) {
            println("${currentPlayer.name}'s $Name was canceled by Impeccable")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
            return
        }

        playersToDraw.forEachIndexed { index, player ->
            if (cards.isNotEmpty()) {
                val card = cards[index % cards.size]
                player.hand.add(card)
                println("${player.name} receives a card from $Name: ${card.Suit} ${card.Number} - ${card.Name}")
                drawResults.add("${player.name} drew 1 card")
            }
        }
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)

        println("")
        if (drawResults.isNotEmpty()) {
            println("${currentPlayer.name} used $Name, ${drawResults.joinToString(", ")}.")
        } else {
            println("${currentPlayer.name} used $Name, but the deck is empty, no cards drawn.")
        }
    }
}

class BrotherhoodCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Brotherhood") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val affectedPlayers = allPlayers.filter { it.currentHP > 0 }
        if (currentPlayer.currentHP != currentPlayer.maxHP) {
            val playersToHeal = checkImpeccableForGroup(currentPlayer, affectedPlayers, allPlayers, isBenefit = true)

            val healResults = mutableListOf<String>()
            playersToHeal.forEach { player ->
                if (player.currentHP < player.maxHP) {
                    player.currentHP++
                    healResults.add("${player.name} recovered 1 HP")
                } else {
                    healResults.add("${player.name}'s HP is already full")
                }
            }

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)

            if (healResults.isNotEmpty()) {
                println("${currentPlayer.name} used $Name, ${healResults.joinToString(", ")}.")
            } else {
                println("${currentPlayer.name} used $Name, but no players need healing.")
            }
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
            val damageResults = mutableListOf<String>()
            val targets = allPlayers.filter { it != currentPlayer && it.currentHP > 0 }
            targets.forEach { target ->
                println("${currentPlayer.name} uses $Name against ${target.name}")
                handleDamageWithDodge(currentPlayer, target, allPlayers, damageResults)
            }
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)

            if (damageResults.isNotEmpty()) {
                println("${currentPlayer.name} used $Name, ${damageResults.joinToString(", ")}.")
            }
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
        }
    }
}

class RainingArrowsCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Raining Arrows") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val hostileTargets = (currentPlayer as? General)?.strategy?.whomToAttack(currentPlayer, allPlayers)?.let { listOf(it) } ?: emptyList()
        val safeToUse = allPlayers.all { player ->
            player == currentPlayer || player.currentHP > 1 || hostileTargets.contains(player)
        }

        if (safeToUse) {
            val damageResults = mutableListOf<String>()
            val targets = allPlayers.filter { it != currentPlayer && it.currentHP > 0 }
            targets.forEach { target ->
                println("${currentPlayer.name} uses $Name against ${target.name}")
                handleDamageWithDodge(currentPlayer, target, allPlayers, damageResults)
            }
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)

            if (damageResults.isNotEmpty()) {
                println("${currentPlayer.name} used $Name, ${damageResults.joinToString(", ")}.")
            }
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
        }
    }
}

class SOONCard(Suit: String, Number: String) : SelfCard(Suit, Number, "Something out of nothing") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        if (currentPlayer.currentHP <= 0) {
            println("${currentPlayer.name} is already defeated and cannot use $Name")
            return
        }
        executeWithImpeccableCheck(currentPlayer, allPlayers) {
            val cardsDrawn = 2
            var actualCardsDrawn = 0
            for (i in 1..cardsDrawn) {
                val card = CardDeck.drawCard()
                if (card != null) {
                    currentPlayer.hand.add(card)
                    actualCardsDrawn++
                }
            }
            println("${currentPlayer.name} uses $Name and draws $actualCardsDrawn cards")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}

class DuelCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Duel") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        if (!target.canBeTargeted(currentPlayer, this)) {
            println("${currentPlayer.name} cannot target ${target.name} with $Name due to restrictions.")
            return
        }

        executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
            println("${currentPlayer.name} initiates $Name with ${target.name}")
            val results = mutableListOf<String>()
            var duelActive = true
            var targetTurn = true // Target (opponent) goes first

            while (duelActive) {
                val currentDuelist = if (targetTurn) target else currentPlayer
                val opponentName = if (targetTurn) target.name else currentPlayer.name
                val isOpponent = currentDuelist == target
                val requiredCards = (currentPlayer as? General)?.getRequiredDuelCards(isOpponent) ?: 1
                val attackCards = currentDuelist.hand.filterIsInstance<AttackCard>()
                if (attackCards.size < requiredCards) {
                    val initialHP = currentDuelist.currentHP
                    (currentDuelist as General).applyDamage(1, currentPlayer, this)
                    val damageDealt = initialHP - currentDuelist.currentHP
                    println("$opponentName can't provide $requiredCards Attack card(s), current HP is ${currentDuelist.currentHP}")
                    results.add("$opponentName took $damageDealt damage")

                    if (currentDuelist.currentHP <= 0) {
                        currentDuelist.handleDefeat(currentPlayer)
                        break
                    }
                    duelActive = false
                } else {
                    val usedCards = mutableListOf<AttackCard>()
                    repeat(requiredCards) {
                        val attackCard = currentDuelist.removeCardOfType(AttackCard::class.java, discard = false)
                        if (attackCard != null) {
                            usedCards.add(attackCard as AttackCard)
                            CardDeck.discardCard(attackCard) // Only discard here
                        }
                    }
                    println("$opponentName responds in $Name with $requiredCards Attack card(s): ${usedCards.joinToString { "${it.Suit} ${it.Number}" }}")
                    results.add("$opponentName used $requiredCards Attack card(s) (${usedCards.joinToString { "${it.Suit} ${it.Number}" }})")
                    targetTurn = !targetTurn
                }
            }

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
            println("${currentPlayer.name} used $Name against ${target.name}, ${results.joinToString(", ")}.")
        }
    }

    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        val alivePlayers = allPlayers.filter { it != currentPlayer && it.currentHP > 0 }
        var target = (currentPlayer as? General)?.strategy?.whomToAttack(currentPlayer, alivePlayers, null)
        var attemptedTargets = mutableSetOf<Player>()

        if (target == null || alivePlayers.isEmpty()) {
            println("$Name requires a specific target, but no valid target was found.")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
            return
        }

        while (target != null) {
            if (target.canBeTargeted(currentPlayer, this)) {
                effect(currentPlayer, target, allPlayers)
                return
            } else {
                println("${currentPlayer.name} cannot target ${target.name} with $Name due to restrictions.")
                attemptedTargets.add(target)
                val remainingTargets = alivePlayers.filter { it !in attemptedTargets }
                target = (currentPlayer as? General)?.strategy?.whomToAttack(currentPlayer, remainingTargets, null)
                if (target == null || attemptedTargets.size >= alivePlayers.size) {
                    println("$Name requires a specific target, but no valid target was found.")
                    currentPlayer.hand.remove(this)
                    CardDeck.discardCard(this)
                    return
                }
            }
        }
    }
}

class StealingSheepCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Stealing Sheep") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        val distance = currentPlayer.calculateDistanceTo(target, allPlayers.size)

        if (distance > 1) {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (distance: $distance > 1)")
            currentPlayer.hand.remove(this) // 移除卡片
            CardDeck.discardCard(this) // 丟棄卡片
            return
        }

        if (target.eWeapon == null && target.eArmor == null && target.eHorsePlus == null && target.eHorseMinus == null && target.hand.isEmpty()) {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (target has no cards or equipment)")
            currentPlayer.hand.remove(this) // 移除卡片
            CardDeck.discardCard(this) // 丟棄卡片
            return
        }

        executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
            handlePriorityAction(
                currentPlayer = currentPlayer,
                target = target,
                actionType = ActionType.STEAL,
                onStealEquipment = { equipment ->
                    val stolenCard = equipment.getCard()
                    equipment.unequip()
                    currentPlayer.hand.add(stolenCard)
                    when (equipment) {
                        is Weapon -> target.eWeapon = null
                        is Armor -> target.eArmor = null
                        is HorsePlus -> target.eHorsePlus = null
                        is HorseMinus -> target.eHorseMinus = null
                    }
                },
                onStealCard = { card ->
                    currentPlayer.hand.add(card)
                },
                onDiscardEquipment = {},
                onDiscardCard = {}
            )

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}

// BBQCard
class BBQCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Burning Bridges") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        if (target.currentHP <= 0) {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (target is already defeated)")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
            return
        }

        if (target.eWeapon == null && target.eArmor == null && target.eHorsePlus == null &&
            target.eHorseMinus == null && target.hand.isEmpty()) {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (target has no cards or equipment)")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
            return
        }

        executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
            handlePriorityAction(
                currentPlayer = currentPlayer,
                target = target,
                actionType = ActionType.DISCARD,
                onStealEquipment = {},
                onStealCard = {},
                onDiscardEquipment = { equipment ->
                    equipment.unequip() // 卸下裝備
                    when (equipment) {
                        is Weapon -> target.eWeapon = null
                        is Armor -> target.eArmor = null
                        is HorsePlus -> target.eHorsePlus = null
                        is HorseMinus -> target.eHorseMinus = null
                    }
                },
                onDiscardCard = { card ->
                    CardDeck.discardCard(card) // 丟棄卡片
                }
            )

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}