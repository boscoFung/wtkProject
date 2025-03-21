package General
import Card.*
import Command.Command
import EightTrigrams
import Equipment.Armor
import Equipment.Equipment
import Factory.HorseFactory
import Strategy.*

import kotlin.random.Random

abstract class General(override val name: String, override val maxHP: Int) :Player, Subject {
    override var currentHP: Int = maxHP
    override var numOfCards: Int = 4
    override val hand: MutableList<Card> = mutableListOf() //手牌

    override var skipPlayPhase: Boolean = false
    override val judgementCommands: MutableList<Command> = mutableListOf()

    override var horsePlus: Int = 0 //＋1馬
    override var horseMinus: Int = 0 //-1馬
    override var seat: Int = -1 // 座號

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

    override fun beingAttacked() {
        println("$name is being attacked.")
        if (eArmor != null) {
            eArmor!!.beingAttacked()  // Calls EightTrigrams.beingAttacked()
        } else {
            dodgeAttack()  // Default behavior if no armor
        }
    }

    override fun dodgeAttack() {
        val dodged = hasDodgeCard()
        if (dodged) {
            println("$name dodged attack by spending a dodge card.")
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

    override fun playPhase() {
        if (skipPlayPhase) {
            println("$name is skipping the Play Phase.")
            skipPlayPhase = false
        } else {
            println("$name is in the Play Phase.")
            hand.filterIsInstance<EquipmentCard>().forEach { card ->
                playCard(card)
            }
            val effectCard = hand.firstOrNull { it is EffectCard }
            if (effectCard != null) {
                when (effectCard) {
                    is TargetedCard -> {
                        val target = strategy?.whomToAttack(this, GeneralManager.getPlayerList())
                        if (target != null) {
                            effectCard.effect(this, target, GeneralManager.getPlayerList())
                        } else {
                            effectCard.effect(this, GeneralManager.getPlayerList())
                        }
                    }
                    is GroupCard -> {
                        effectCard.effect(this, GeneralManager.getPlayerList())
                    }
                    is SelfCard -> {
                        effectCard.effect(this, GeneralManager.getPlayerList())
                    }
                }
            }
            if (hasAttackCard()) {
                val target = strategy?.whomToAttack(this, GeneralManager.getPlayerList())
                if (target != null) {
                    val targetIdentity = when ((target as General).strategy) {
                        is LordStrategy -> "lord"
                        is LoyalistStrategy -> "loyalist"
                        is RebelStrategy -> "rebel"
                        is SpyStrategy -> "spy"
                        else -> "unknown"
                    }
                    val distance = calculateDistanceTo(target, GeneralManager.getPlayerList().size)
                    val range = calculateAttackRange()
                    if (distance <= range) {
                        //println("$name spends a card to attack a $targetIdentity, ${target.name}")
                        val removedCard = removeCardOfType(AttackCard::class.java)
                        println("$name spends ${removedCard?.Suit} ${removedCard?.Number} - ${removedCard?.Name} to attack a $targetIdentity, ${target.name}")
                        target.beingAttacked()
                    } else {
                        println("$name cannot attack ${target.name} (distance: $distance > range: $range)")
                    }
                }
            } else {
                println("$name has no playable cards (Attack or Acedia).")
            }
            if (hasJudgementCard("Acedia")) {
                val acediaTarget = strategy?.whomToAttack(this, GeneralManager.getPlayerList())
                if (acediaTarget != null) {
                    playJudgementCard(acediaTarget, "Acedia")
                }
            }
            if (hasJudgementCard("Lightning")) {
                val lightningTarget = strategy?.whomToAttack(this, GeneralManager.getPlayerList())
                if (lightningTarget != null) {
                    playJudgementCard(lightningTarget, "Lightning")
                }
            }
        }
    }
}

interface Player {
    val name: String
    val maxHP: Int
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
    fun takeTurn() {
        preparationPhase()
        judgementPhase()
        drawPhase()
        playPhase()
        discardPhase()
        finalPhase()
    }

    fun preparationPhase() {
//        println("$name is in the Preparation Phase.")
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
        val cardsToDiscard = hand.size - currentHP
        if (cardsToDiscard > 0) {
            repeat(cardsToDiscard) {
                val discardedCard = hand.removeAt(0) // 棄掉最左邊的卡
                CardDeck.discardCard(discardedCard) // 加入棄牌堆
            }
        } else {
            println("$name does not need to discard any cards.")
        }
        println("$name discards $cardsToDiscard card(s), now has ${hand.size} card(s).")
    }


    fun finalPhase() {
//        println("$name is in the Final Phase.")
    }
}