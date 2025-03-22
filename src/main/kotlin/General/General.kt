package General
import Card.*
import Command.Command
import EightTrigrams
import Equipment.Armor
import Equipment.Equipment
import Equipment.Weapon
import Factory.HorseFactory
import Strategy.*

import kotlin.random.Random

abstract class General(override val name: String, override val maxHP: Int, override val gender: String) : Player, Subject {
    override var currentHP: Int = maxHP
    override var defeated: Boolean = false
    override var numOfCards: Int = 4
    override val hand: MutableList<Card> = mutableListOf() //手牌

    override var skipPlayPhase: Boolean = false
    override val judgementCommands: MutableList<Command> = mutableListOf()

    override var horsePlus: Int = 0 //＋1馬
    override var horseMinus: Int = 0 //-1馬
    override var seat: Int = -1 // 座號
    override var attacksThisTurn: Int = 0
    //裝備
    override var eWeapon: Equipment? = null
    override var eArmor: Equipment? = null
    override var eHorsePlus: Equipment? = null
    override var eHorseMinus: Equipment? = null

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

    open fun attack(attacker: Player) {
        println("$name is being attacked by ${attacker.name}.")
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
            handleDefeat(attacker) // Pass the attacker as the killer
        }
    }
    override fun beingAttacked() {
        if (currentHP <= 0) {
            println("$name is already defeated and cannot be attacked.")
            return
        }
        println("$name is being attacked.")
        if (eArmor != null) {
            eArmor!!.beingAttacked()  // Calls EightTrigrams.beingAttacked()
        } else {
            dodgeAttack()  // Default behavior if no armor
        }
        if (currentHP <= 0) {
            // The killer will be set in playPhase() when calling beingAttacked()
            handleDefeat()
        }
    }

    override fun dodgeAttack() {
        val dodged = hasDodgeCard()
        if (dodged) {
            println("$name dodged attack by spending a dodge card.")
            removeCardOfType(DodgeCard::class.java)
        } else {
            currentHP--
            println("$name can't dodge the attack, current HP is $currentHP.")
        }
        if (strategy is LordStrategy) {
            notifyObservers(dodged)
        }
    }
    override fun playCard(card: Card) {
        when (card) {
            is EightTrigramsCard -> {
                val armor = EightTrigrams(this)
                equipArmor(armor)
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
            is HorseCard -> {
                val horse = HorseFactory.createHorse(this, card.Name, card.type)
                when (card.type) {
                    HorseType.PLUS -> {
                        eHorsePlus = horse
                        println("$name equipped ${horse.name} (+1 Horse)")
                    }
                    HorseType.MINUS -> {
                        eHorseMinus = horse
                        println("$name equipped ${horse.name} (-1 Horse)")
                    }
                }
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
            is WeaponCard -> {
                val weapon = WeaponFactory.createWeapon(this, card.Name)
                eWeapon = weapon
                println("$name equipped ${weapon.name}")
                removeCardOfType(EquipmentCard::class.java, card.Name, discard = false)
            }
        }
    }

    private fun playEffectCards() {
        val effectCards = hand.filterIsInstance<EffectCard>().toList()
        for (effectCard in effectCards) {
            if (GeneralManager.isGameOver()) break

            when (effectCard) {
                is TargetedCard -> {
                    val target = strategy?.whomToAttack(this, GeneralManager.getAlivePlayerList())
                    if (target != null) {
                        effectCard.effect(this, target, GeneralManager.getAlivePlayerList())
                    } else {
                        effectCard.effect(this, GeneralManager.getAlivePlayerList())
                    }
                }
                is GroupCard -> {
                    effectCard.effect(this, GeneralManager.getAlivePlayerList())
                }
                is SelfCard -> {
                    effectCard.effect(this, GeneralManager.getAlivePlayerList())
                }
            }
        }
    }
    override fun calculateDistanceTo(target: Player, totalPlayers: Int): Int {
        val seat1 = this.seat
        val seat2 = target.seat
        val clockwise = Math.abs(seat1 - seat2)
        val counterclockwise = totalPlayers - clockwise
        val baseDistance = minOf(clockwise, counterclockwise)
        return maxOf(1, baseDistance - this.horseMinus + target.horsePlus)
    }

    override fun calculateAttackRange(): Int {
        return 1
    }
    override fun performAttack() {
        if (!hasAttackCard()) {
            println("$name has no Attack card to use.")
            return
        }

        val target = strategy?.whomToAttack(this, GeneralManager.getAlivePlayerList())
        if (target == null) {
            println("$name has no valid target to attack.")
            return
        }

        val targetIdentity = when ((target as General).strategy) {
            is LordStrategy -> "lord"
            is LoyalistStrategy -> "loyalist"
            is RebelStrategy -> "rebel"
            is SpyStrategy -> "spy"
            else -> "unknown"
        }
        val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
        val range = calculateAttackRange()

        // 檢查武器是否允許攻擊，無武器時限制為 1 次
        val canAttack = eWeapon?.let { (it as Weapon).canAttack(attacksThisTurn) } ?: (attacksThisTurn < 1)
        if (distance <= range && canAttack) {
            val removedCard = removeCardOfType(AttackCard::class.java)
            println("$name spends ${removedCard?.Suit} ${removedCard?.Number} - ${removedCard?.Name} to attack a $targetIdentity, ${target.name}")
            attacksThisTurn++
            eWeapon?.let { (it as Weapon).applyEffect(this, target, removedCard) } ?: target.attack(this)
        } else if (distance > range) {
            println("$name cannot attack ${target.name} (distance: $distance > range: $range)")
        } else {
            println("$name has reached the attack limit this turn (attacks: $attacksThisTurn).")
        }
    }

    override fun playPhase() {
        if (skipPlayPhase) {
            println("$name is skipping the Play Phase.")
            skipPlayPhase = false
            return
        }
        println("$name is in the Play Phase.")

        // 裝備卡處理
        hand.filterIsInstance<EquipmentCard>().forEach { card ->
            playCard(card)
        }

        // 效果卡處理
        playEffectCards()

        // 執行攻擊
        performAttack()

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

    var eWeapon: Equipment?
    var eArmor: Equipment?
    var eHorsePlus: Equipment?
    var eHorseMinus: Equipment?
    val gender: String
    var attacksThisTurn: Int  // 新增：追蹤當回合攻擊次數
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
            currentHP--
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
                    is AcediaCard -> card.applyTo(target, card)
                    is LightningCard -> card.applyTo(target, card)
                    // Add more JudgementCard types here in the future
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
        if (currentHP > 0) {
            println("${name} is not defeated (HP: $currentHP).")
            return
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
        if (GeneralManager.isGameOver()) return
        preparationPhase()
        if (GeneralManager.isGameOver()) return
        judgementPhase()
        if (GeneralManager.isGameOver()) return
        drawPhase()
        if (GeneralManager.isGameOver()) return
        playPhase()
        if (GeneralManager.isGameOver()) return
        discardPhase()
        if (GeneralManager.isGameOver()) return
        finalPhase()
    }

    fun preparationPhase() {
//        println("$name is in the Preparation Phase.")
        resetAttacks()
    }

    fun judgementPhase() {
//        println("$name is in the Judgement Phase.")
        val iterator = judgementCommands.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            command(this)
            iterator.remove()
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
}