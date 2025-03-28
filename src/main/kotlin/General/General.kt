package General
import Card.*
import Command.Command
import EightTrigrams
import Equipment.Armor
import Equipment.Equipment
import Equipment.HorsePlus
import Equipment.HorseMinus
import Equipment.Weapon
import Factory.HorseFactory
import Factory.WeaponFactory
import Strategy.*

import kotlin.random.Random

abstract class General(override val name: String, override val maxHP: Int, override val gender: String) : Player, Subject {
    override var currentHP: Int = maxHP
    override var defeated: Boolean = false
    override var numOfCards: Int = 4
    override val hand: MutableList<Card> = mutableListOf() //手牌
    override var isAttackLimitUnlimited: Boolean = false
    override var skipPlayPhase: Boolean = false
    override val judgementCommands: MutableList<Command> = mutableListOf()

    override var horsePlus: Int = 0 //＋1馬
    override var horseMinus: Int = 0 //-1馬
    override var seat: Int = -1 // 座號
    var attacksThisTurn: Int = 0
    //裝備
    override var eWeapon: Equipment? = null
    override var eArmor: Equipment? = null
    override var eHorsePlus: Equipment? = null
    override var eHorseMinus: Equipment? = null

    override var baseAttackLimit: Int = 1
    override var baseAttackRange: Int = 1
    override var currentAttackLimit: Int = baseAttackLimit
    override var currentAttackRange: Int = baseAttackRange


    var strategy: Strategy? = null

    private val observers: MutableList<Observer> = mutableListOf()

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers(dodged: Boolean) {
        for (observer in observers) {
            observer.update(dodged)
        }
    }

    override fun resetAttacks() {
        attacksThisTurn = 0
    }

    fun unequipByType(equipment: Equipment) {
        when (equipment) {
            is Weapon -> equipment.unequip()
            is Armor -> equipment.unequip()
            is HorsePlus -> equipment.unequip()
            is HorseMinus -> equipment.unequip()
        }
    }
    //target.unequipByType(target.eWeapon!!)

    override fun attack(attacker: Player) {
        println("$name is being attacked by ${attacker.name}.")
        if (currentHP <= 0) {
            println("$name is already defeated and cannot be attacked.")
            return
        }
        println("$name is being attacked.")
        if (eArmor != null) {
            eArmor!!.beingAttacked()
        } else {
            val dodged = hasDodgeCard()
            if (dodged) {
                println("$name dodged attack by spending a dodge card.")
                removeCardOfType(DodgeCard::class.java)
            } else {
                applyDamage(1, attacker, null)
            }
            if (strategy is LordStrategy) {
                notifyObservers(dodged)
            }
        }
    }

    //統一傷害流程
    open fun applyDamage(amount: Int, source: Player?, damageCard: Card? = null) {
        if (currentHP <= 0) {
            println("$name is already defeated and cannot take damage.")
            return
        }
        currentHP -= amount
        println("$name takes $amount damage from ${source?.name ?: "an effect"}, current HP is $currentHP.")

        checkAndUsePeach(source)
        if (currentHP <= 0 && !defeated) {
            handleDefeat(source)
        }
    }

//    override fun beingAttacked() {
//        if (currentHP <= 0) {
//            println("$name is already defeated and cannot be attacked.")
//            return
//        }
//        println("$name is being attacked.")
//        if (eArmor != null) {
//            eArmor!!.beingAttacked()  // Calls EightTrigrams.beingAttacked()
//        } else {
//            dodgeAttack()  // Default behavior if no armor
//        }
//        if (currentHP <= 0) {
//            // The killer will be set in playPhase() when calling beingAttacked()
//            handleDefeat()
//        }
//    }
override fun beingAttacked() {
    if (currentHP <= 0) {
        println("$name is already defeated and cannot be attacked.")
        return
    }
    println("$name is being attacked.")
    if (eArmor != null) {
        eArmor!!.beingAttacked()
    } else {
        dodgeAttack()
    }
    if (currentHP <= 0) {
        handleDefeat(null)
    }
}

    override fun dodgeAttack() {
        val dodged = hasDodgeCard()
        if (dodged) {
            println("$name dodged attack by spending a dodge card.")
            removeCardOfType(DodgeCard::class.java)
        } else {
            reduceHP(1)
        }
        if (strategy is LordStrategy) {
            notifyObservers(dodged)
        }
    }

    fun reduceHP(amount: Int, killer: Player? = null) {
        currentHP -= amount
        println("$name loses $amount HP, current HP is $currentHP.")
        checkAndUsePeach()
        if (currentHP <= 0 && !defeated) {
            handleDefeat(killer)
        }
    }

    open fun checkAndUsePeach(killer: Player? = null) {
        if (defeated) {
            println("$name is already defeated and cannot use Peach cards.")
            return
        }

        // 先檢查自己的桃
        while (currentHP < maxHP && currentHP > 0 && hasPeachCard()) {
            val peachCard = hand.firstOrNull { it is PeachCard } as? PeachCard
            if (peachCard != null) {
                peachCard.use(this)
            }
        }

        // 如果瀕死，觸發救援技能 (only for SunQuan, handled in override)
        if (currentHP <= 0 && !defeated) {
            println("$name is in a dying state (HP: $currentHP).")
            if (currentHP <= 0 && !defeated) {
                println("$name could not be rescued and remains in a dying state.")
                handleDefeat(killer)
            }
        }
    }

    override fun playCard(card: Card) {
        when (card) {

            is EightTrigramsCard -> {
                val armor = EightTrigrams(this, card) // 傳遞 card
                equipArmor(armor)
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
            is HorseCard -> {
                val horse = HorseFactory.createHorse(this, card.Name, card.type, card)
                when (card.type) {
                    HorseType.PLUS -> {
                        eHorsePlus?.unequip()
                        eHorsePlus = horse
                        horsePlus = 1
                        println("$name equipped ${horse.name} (+1 Horse)")
                    }
                    HorseType.MINUS -> {
                        eHorseMinus?.unequip()
                        eHorseMinus = horse
                        horseMinus = 1
                        println("$name equipped ${horse.name} (-1 Horse)")
                    }
                }
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
            is WeaponCard -> {
                val weapon = WeaponFactory.createWeapon(this, card.Name, card) // 傳遞 card
                if (eWeapon != null) {
                    (eWeapon as Weapon).unequip()
                }
                eWeapon = weapon
                weapon.onEquip()
                println("$name equipped ${weapon.name}")
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
        }
    }

    fun playEffectCards() {
        val processedCards = mutableSetOf<Card>()
        while (hand.any { it is EffectCard } && !defeated && !GeneralManager.isGameOver()) {
            val effectCard = hand.firstOrNull { it is EffectCard && it !in processedCards } as? EffectCard ?: break
            println("${name} is attempting to play effect card: ${effectCard.Suit} ${effectCard.Number} - ${effectCard.Name}")
            when (effectCard) {
                is TargetedCard -> {
                    val maxDistance = when (effectCard) {
                        is StealingSheepCard -> 1
                        is DuelCard -> null
                        is BBQCard -> null
                        else -> null
                    }
                    val target = strategy?.whomToAttack(this, GeneralManager.getAlivePlayerList(), maxDistance)
                    if (target != null) {
                        effectCard.effect(this, target, GeneralManager.getAlivePlayerList())
                    } else {
                        effectCard.effect(this, GeneralManager.getAlivePlayerList())
                    }
                    if (hand.contains(effectCard)) {
                        hand.remove(effectCard)
                        CardDeck.discardCard(effectCard)
                        println("${name} discarded invalid effect card: ${effectCard.Suit} ${effectCard.Number} - ${effectCard.Name}")
                    }
                }
                is GroupCard -> {
                    effectCard.effect(this, GeneralManager.getAlivePlayerList())
                    if (hand.contains(effectCard)) {
                        hand.remove(effectCard)
                        CardDeck.discardCard(effectCard)
                        println("${name} discarded group effect card: ${effectCard.Suit} ${effectCard.Number} - ${effectCard.Name}")
                    }
                }
                is SelfCard -> {
                    effectCard.effect(this, GeneralManager.getAlivePlayerList())
                    if (hand.contains(effectCard)) {
                        hand.remove(effectCard)
                        CardDeck.discardCard(effectCard)
                        println("${name} discarded self effect card: ${effectCard.Suit} ${effectCard.Number} - ${effectCard.Name}")
                    }
                }
            }
            processedCards.add(effectCard)
            println("${name} has ${hand.size} card(s) remaining after playing effect card.")
        }
    }

    override fun calculateDistanceTo(target: Player, totalPlayers: Int): Int {
        val seat1 = this.seat
        val seat2 = target.seat
        val clockwise = Math.abs(seat1 - seat2)
        val counterclockwise = totalPlayers - clockwise
        val baseDistance = minOf(clockwise, counterclockwise)
        val adjustedDistance = maxOf(1, baseDistance - this.horseMinus + target.horsePlus)
        println("Distance from ${this.name} to ${target.name}: base=$baseDistance, adjusted=$adjustedDistance (my -1=${this.horseMinus}, their +1=${target.horsePlus})")
        return adjustedDistance
    }

    fun equipWeapon(weapon: Weapon) {
        if (eWeapon != null) {
            eWeapon?.unequip()
        }
        eWeapon = weapon
        weapon.onEquip()
        println("${name} equipped ${weapon.name}")
    }

    override fun calculateAttackRange(): Int {
        return currentAttackRange
    }

    override fun canBeTargeted(source: Player, card: Card): Boolean {
        if (currentHP <= 0) return false
        if (source is General && this is General && strategy != null && source.strategy != null) {
            return !strategy!!.isFriendly(source.strategy!!)
        }
        return true
    }

    override fun performAttack() {
        if (!hasAttackCard()) {
            println("$name has no Attack card to use.")
            return
        }
        val attackCard = removeCardOfType(AttackCard::class.java, discard = false)
        if (attackCard == null) {
            println("$name failed to retrieve an Attack card.")
            return
        }
        val range = calculateAttackRange()
        val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }
        var target = strategy?.whomToAttack(this, alivePlayers, range)
        var attemptedTargets = mutableSetOf<Player>()

        while (target != null) {
            val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
            val targetIdentity = (target as General).strategy?.javaClass?.simpleName?.replace("Strategy", "")?.toLowerCase() ?: "unknown"
            if (distance <= range && target.canBeTargeted(this, attackCard)) {
                if (eWeapon != null) {
                    println("$name uses ${eWeapon!!.name} with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name} (距離: $distance / 攻擊範圍: $range)")
                    attacksThisTurn++
                    (eWeapon as Weapon).attackTarget(this, target, attackCard)
                } else {
                    println("$name spends ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name}")
                    attacksThisTurn++
                    target.attack(this)
                    (target as General).applyDamage(1, this, attackCard) // 直接調用 applyDamage，傳入 attackCard
                    return // 成功攻擊後結束
                }
                return
            } else {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                attemptedTargets.add(target)
                val remainingTargets = alivePlayers.filter { it !in attemptedTargets }
                target = strategy?.whomToAttack(this, remainingTargets, range)
                if (target == null || attemptedTargets.size >= alivePlayers.size) {
                    println("$name has no valid target to attack within range $range.")
                    CardDeck.discardCard(attackCard)
                    attacksThisTurn++
                    return
                }
            }
        }
        println("$name has no valid target to attack.")
        CardDeck.discardCard(attackCard)
        attacksThisTurn++
    }

    override fun playPhase() {
        if (defeated) {
            println("$name is already defeated and skips their play phase.")
            return
        }
        if (skipPlayPhase) {
            println("$name is skipping the Play Phase.")
            skipPlayPhase = false
            return
        }
        println("$name is in the Play Phase.")

        println("Checking for Peach cards: HP = $currentHP, Max HP = $maxHP, Has Peach = ${hasPeachCard()}")
        while (currentHP < maxHP && hasPeachCard() && !defeated && !GeneralManager.isGameOver()) {
            val peachCard = hand.firstOrNull { it is PeachCard } as? PeachCard
            if (peachCard != null) {
                peachCard.use(this)
            } else {
                break
            }
        }

        hand.filterIsInstance<EquipmentCard>().forEach { card ->
            if (defeated) return
            playCard(card)
        }
        playEffectCards()

        var attemptedTargets = mutableSetOf<Player>()
        while (attacksThisTurn < currentAttackLimit && hasAttackCard() && !defeated && !GeneralManager.isGameOver()) {
            val range = calculateAttackRange()
            val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this && it !in attemptedTargets }
            val target = strategy?.whomToAttack(this, alivePlayers, range)
            if (target == null || alivePlayers.isEmpty()) {
                println("$name has no valid target to attack within range $range.")
                break
            }
            val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
            if (distance <= range && target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                performAttack()
                attemptedTargets.clear()
            } else {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                attemptedTargets.add(target)
                if (attemptedTargets.size >= alivePlayers.size) {
                    println("$name has no remaining targets within range $range.")
                    break
                }
            }
        }
        if (hasJudgementCard("Acedia")) {
            val acediaTarget = strategy?.whomToAttack(this, GeneralManager.getAlivePlayerList())
            if (acediaTarget != null) {
                playJudgementCard(acediaTarget, "Acedia")
            }
        }
        if (hasJudgementCard("Lightning")) {
            val lightningTarget = strategy?.whomToAttack(this, GeneralManager.getAlivePlayerList())
            if (lightningTarget != null) {
                playJudgementCard(lightningTarget, "Lightning")
            }
        }
    }

    override fun drawPhase() {
        if (currentHP <= 0) {
            println("$name is defeated and skips the Draw Phase.")
            return
        }
        val cardsDrawn = 2
        var actualCardsDrawn = 0
        for (i in 1..cardsDrawn) {
            val card = CardDeck.drawCard() // 從牌庫抽牌
            if (card != null) {
                hand.add(card) // 加入手牌
                actualCardsDrawn++
            } else {
                println("The deck is empty. No more cards can be drawn.")
                break
            }
        }
        println("$name draws $actualCardsDrawn card(s) and now has ${hand.size} card(s).")
        println("Deck Size: ${CardDeck.getDeckSize()}")
    }

    override fun discardPhase() {
        if (currentHP <= 0) {
            println("$name is defeated and skips the Discard Phase.")
            return
        }
        println("$name has ${hand.size} card(s), current HP is $currentHP")
        val cardsToDiscard = maxOf(0, hand.size - maxOf(0, currentHP)) // Ensure cardsToDiscard is non-negative
        if (cardsToDiscard > 0) {
            var remainingToDiscard = cardsToDiscard
            while (remainingToDiscard > 0 && hand.isNotEmpty()) {
                val discardedCard = hand.removeAt(0) // 棄掉最左邊的卡
                CardDeck.discardCard(discardedCard) // 加入棄牌堆
                remainingToDiscard--
            }
            println("$name discards ${cardsToDiscard - remainingToDiscard} card(s), now has ${hand.size} card(s).")
        } else {
            println("$name does not need to discard any cards.")
            println("$name discards 0 card(s), now has ${hand.size} card(s).")
        }
    }
}

interface Player {
    val name: String
    val maxHP: Int
    var defeated: Boolean
    var currentHP: Int
    val hand: MutableList<Card>
    var numOfCards: Int
    var skipPlayPhase: Boolean
    val judgementCommands: MutableList<Command>
    var seat: Int
    var horsePlus: Int
    var horseMinus: Int
    var isAttackLimitUnlimited: Boolean

    var eWeapon: Equipment?
    var eArmor: Equipment?
    var eHorsePlus: Equipment?
    var eHorseMinus: Equipment?
    val gender: String

    var baseAttackLimit: Int
    var baseAttackRange: Int

    var currentAttackLimit: Int
    var currentAttackRange: Int

    fun modifyAttackLimit(newLimit: Int) {
        currentAttackLimit = newLimit
        if (newLimit == Int.MAX_VALUE) {
            println("$name's attack limit modified to unlimited")
        } else {
            println("$name's attack limit modified to $currentAttackLimit")
        }
    }

    fun modifyAttackRange(newRange: Int) {
        currentAttackRange = newRange
        println("$name's attack range modified to $currentAttackRange")
    }

    fun restoreAttackLimit() {
        currentAttackLimit = baseAttackLimit
        println("$name's attack limit restored to $currentAttackLimit")
    }

    fun restoreAttackRange() {
        currentAttackRange = baseAttackRange
        println("$name's attack range restored to $currentAttackRange")
    }
    fun performAttack()
    fun calculateDistanceTo(target: Player, totalPlayers: Int): Int
    fun calculateAttackRange(): Int
    fun equipArmor(armor: Armor) {
        eArmor = armor
        println("$name equipped ${armor.name}")
    }
    fun playCard(card: Card)

    fun beingAttacked() {
        println("$name is being attacked.")
        dodgeAttack()
    }

    fun dodgeAttack() {
        if (hasDodgeCard()) {
            println("$name dodged attack by spending a dodge card.")
        } else {

            println("$name can't dodge the attack, current HP is $currentHP.")
        }
    }

    fun hasDodgeCard(): Boolean {
        return hand.any { it is DodgeCard }
    }

    fun hasAttackCard(): Boolean {
        return hand.any { it is AttackCard }
    }

    fun hasPeachCard(): Boolean {
        return hand.any { it is PeachCard }
    }
    //how to use for equipment : val removedCard = removeCardOfType(EquipmentCard::class.java, name = equipmentName, discard = false)
    //how to use for basic card : val removedCard = removeCardOfType(AttackCard::class.java)
    fun <T : Card> removeCardOfType(type: Class<T>, name: String? = null, discard: Boolean = true): Card? {
        val cardIndex = if (name != null) {
            hand.indexOfFirst { type.isInstance(it) && it.Name == name }
        } else {
            hand.indexOfFirst { type.isInstance(it) }
        }
        return if (cardIndex != -1) {
            val removedCard = hand.removeAt(cardIndex)
            if (discard && removedCard !is EquipmentCard) {
                CardDeck.discardCard(removedCard)
            } // Send to discard pile
            removedCard
        } else {
            null
        }
    }

    fun hasJudgementCard(name: String): Boolean {
        return hand.any { it is JudgementCard && it.Name == name }
    }

    fun playJudgementCard(target: Player, cardName: String) {
        if (target.currentHP <= 0) {
            println("${target.name} is already defeated and cannot be targeted by $cardName.")
            return
        }
        if (hasJudgementCard(cardName)) {
            val card = removeCardOfType(JudgementCard::class.java, name = cardName, discard = false)
            if (card != null) {
                println("${name} plays $cardName on ${target.name}.")
                when (card) {
                    is AcediaCard -> card.applyTo(target, card) // 立即執行
                    is LightningCard -> card.applyTo(target, card)
                }
            }
        } else {
            println("${name} does not have the judgement card '$cardName' to play.")
        }
    }

    fun handleDefeat(killer: Player? = null) {
        if (defeated) {
            println("${name} has already been defeated. Skipping defeat logic.")
            return
        }
        if (judgementCommands.isNotEmpty()) {
            println("${name} was defeated with pending judgement commands (e.g., Acedia, Lightning). Clearing them.")
            judgementCommands.clear()
        }
        if (currentHP > 0) {
            println("${name} is not defeated (HP: $currentHP).")
            return
        }
        if (eWeapon != null) {
            (eWeapon as Weapon).unequip()
            eWeapon = null
        }
        if (eArmor != null) {
            (eArmor as Armor).unequip()
            eArmor = null
        }
        if (eHorsePlus != null) {
            (eHorsePlus as HorsePlus).unequip()
            eHorsePlus = null
        }
        if (eHorseMinus != null) {
            (eHorseMinus as HorseMinus).unequip()
            eHorseMinus = null
        }
        // Discard all cards in the player's hand to the discard pile
        if (hand.isNotEmpty()) {
            println("${name} discards all cards to the discard pile upon defeat:")
            hand.forEach { card ->
                println("${card.Suit} ${card.Number} - ${card.Name}")
                CardDeck.discardCard(card)
            }
            hand.clear()
            println("${name} now has ${hand.size} card(s) in hand.")
        } else {
            println("${name} has no cards to discard upon defeat.")
        }
        defeated = true
        println("${name} has been defeated and is out of the game (HP: $currentHP) by ${killer?.name ?: "an effect"}.")
        // If the defeated player is a Rebel, the killer draws 3 cards
        if (this is General && this.strategy is RebelStrategy && killer != null && killer.currentHP > 0) {
            println("${killer.name} killed a Rebel (${name}) and draws 3 cards as a reward.")
            for (i in 1..3) {
                val card = CardDeck.drawCard()
                if (card != null) {
                    killer.hand.add(card)
                    println("${killer.name} draws: ${card.Suit} ${card.Number} - ${card.Name}")
                } else {
                    println("The deck is empty. No more cards can be drawn for the reward.")
                    break
                }
            }
        }
        // Notify GeneralManager to check game-over conditions
        GeneralManager.checkGameOver()
    }

    fun takeTurn() {
        if (defeated || GeneralManager.isGameOver()) return
        preparationPhase()
        if (defeated || GeneralManager.isGameOver()) return
        judgementPhase()
        if (defeated || GeneralManager.isGameOver()) return
        drawPhase()
        if (defeated || GeneralManager.isGameOver()) return
        playPhase()
        if (defeated || GeneralManager.isGameOver()) return
        discardPhase()
        if (defeated || GeneralManager.isGameOver()) return
        finalPhase()
    }

    fun preparationPhase() {
//        println("$name is in the Preparation Phase.")
        resetAttacks()
    }

    fun judgementPhase() {
        val commandsToExecute = judgementCommands.toList()
        judgementCommands.clear()
        val iterator = commandsToExecute.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            command(this)
        }
    }

//    fun drawPhase() {
//        val cardsDrawn = 2
//        numOfCards += cardsDrawn
//        println("$name draws $cardsDrawn card(s) and now has $numOfCards card(s).")
//    }

    fun drawPhase() {
        val cardsDrawn = 2
        for (i in 1..cardsDrawn) {
            val card = CardDeck.drawCard() // 從牌庫抽牌
            if (card != null) {
                hand.add(card) // 加入手牌
            } else {
                println("The deck is empty. No more cards can be drawn.")
            }
        }
        println("$name draws $cardsDrawn card(s) and now has ${hand.size} card(s).")
        println("Deck Size:" + CardDeck.getDeckSize())
    }

    fun playPhase() {
//        println("$name is in the Play Phase.")
        if (skipPlayPhase) {
            println("$name is skipping the Play Phase.")
            skipPlayPhase = false
        } else {
            println("$name is in the Play Phase.")
        }
    }
    fun resetAttacks()
//    fun discardPhase() {
//        println("$name has $numOfCards card(s), current HP is $currentHP")
//        val cardsToDiscard = numOfCards - currentHP
//        if (cardsToDiscard > 0) {
//            numOfCards -= cardsToDiscard
//            println("$name discards $cardsToDiscard card(s), now has $numOfCards card(s).")
//        } else {
//            println("$name does not need to discard any cards.")
//        }
//    }

    fun getRequiredDuelCards(isOpponent: Boolean): Int {   //lubu
        return 1 // default behavior
    }

    fun attack(attacker: Player)

    fun discardPhase() {
        println("$name has ${hand.size} card(s), current HP is $currentHP")
        val cardsToDiscard = maxOf(0, hand.size - maxOf(0, currentHP)) // Ensure cardsToDiscard is non-negative
        if (cardsToDiscard > 0) {
            var remainingToDiscard = cardsToDiscard
            while (remainingToDiscard > 0 && hand.isNotEmpty()) {
                val discardedCard = hand.removeAt(0) // 棄掉最左邊的卡
                CardDeck.discardCard(discardedCard) // 加入棄牌堆
                remainingToDiscard--
            }
            println("$name discards ${cardsToDiscard - remainingToDiscard} card(s), now has ${hand.size} card(s).")
        } else {
            println("$name does not need to discard any cards.")
            println("$name discards 0 card(s), now has ${hand.size} card(s).")
        }
        println("$name discards $cardsToDiscard card(s), now has ${hand.size} card(s).")
    }


    fun finalPhase() {
//        println("$name is in the Final Phase.")
    }

    fun canBeTargeted(source: Player, card: Card): Boolean {
        return true
    }
}