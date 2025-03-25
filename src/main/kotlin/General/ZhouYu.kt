package General

import Card.AttackCard
import Card.CardDeck
import Card.EquipmentCard
import Card.PeachCard
import Strategy.GeneralManager
import kotlin.random.Random

class ZhouYu : WuGeneral("Zhou Yu", 3, "Male") {

    private var stratagemUsedThisTurn = false
    //雄姿
    override fun drawPhase() {
        if (currentHP <= 0) {
            println("$name is defeated and skips the Draw Phase.")
            return
        }
        val cardsDrawn = 3
        var actualCardsDrawn = 0
        for (i in 1..cardsDrawn) {
            val card = CardDeck.drawCard() // 從牌庫抽牌
            if (card != null) {
                hand.add(card) // 加入手牌asfasf
                actualCardsDrawn++
            } else {
                println("The deck is empty. No more cards can be drawn.")
                break
            }
        }
        println("[Heroism] $name draws $actualCardsDrawn card(s) and now has ${hand.size} card(s).")
        println("Deck Size: ${CardDeck.getDeckSize()}")
    }

    // [反間] 指定一個玩家，周瑜以一張卡牌為賭注，目標有25%獲得該手牌，75%扣一點生命。
    fun stratagem(target: General) {
        if (stratagemUsedThisTurn) {
            println("[Stratagem] $name has already used Stratagem this turn.")
            return
        }
        if (hand.isEmpty()) {
            println("[Stratagem] $name has no cards to use for Stratagem.")
            return
        }
        if (target.currentHP <= 0 || target.defeated) {
            println("[Stratagem] Target ${target.name} is already defeated and cannot be targeted.")
            return
        }

        val wagerCard = hand.removeAt(0)
        println("[Stratagem] $name uses ${wagerCard.Suit} ${wagerCard.Number} - ${wagerCard.Name} as the wager against ${target.name}.")

        // 隨機判定結果：25% 獲得手牌，75% 扣 1 點生命
        val chance = Random.nextDouble()
        if (chance < 0.25) {
            target.hand.add(wagerCard)
            println("[Stratagem] ${target.name} wins the wager and gains ${wagerCard.Suit} ${wagerCard.Number} - ${wagerCard.Name}.")
        } else {
            target.reduceHP(1, this)
            println("[Stratagem] ${target.name} loses the wager and takes 1 damage (HP: ${target.currentHP}).")
            CardDeck.discardCard(wagerCard) // 賭注牌棄置
        }
        stratagemUsedThisTurn = true
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
        stratagemUsedThisTurn = false // 重置「謀策」使用狀態

        // 使用桃治療
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

        val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }
        val target = strategy?.whomToAttack(this, alivePlayers) as? General
        if (target != null && hand.isNotEmpty() && !defeated && !GeneralManager.isGameOver()) {
            stratagem(target)
        } else {
            println("[Stratagem] No valid target or no cards available for $name to use Stratagem.")
        }

        // 攻擊階段
        var attemptedTargets = mutableSetOf<Player>()
        while (attacksThisTurn < currentAttackLimit && hasAttackCard() && !defeated && !GeneralManager.isGameOver()) {
            val range = calculateAttackRange()
            val remainingTargets = alivePlayers.filter { it !in attemptedTargets }
            val attackTarget = strategy?.whomToAttack(this, remainingTargets, range)
            if (attackTarget == null || remainingTargets.isEmpty()) {
                println("$name has no valid target to attack within range $range.")
                break
            }
            val distance = calculateDistanceTo(attackTarget, GeneralManager.getAlivePlayerCount())
            if (distance <= range && attackTarget.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                performAttack()
                attemptedTargets.clear()
            } else {
                println("$name cannot attack ${attackTarget.name} (distance: $distance > range: $range or targeting restricted).")
                attemptedTargets.add(attackTarget)
                if (attemptedTargets.size >= remainingTargets.size) {
                    println("$name has no remaining targets within range $range.")
                    break
                }
            }
        }

        if (hasJudgementCard("Acedia")) {
            val acediaTarget = strategy?.whomToAttack(this, alivePlayers)
            if (acediaTarget != null) {
                playJudgementCard(acediaTarget, "Acedia")
            }
        }
        if (hasJudgementCard("Lightning")) {
            val lightningTarget = strategy?.whomToAttack(this, alivePlayers)
            if (lightningTarget != null) {
                playJudgementCard(lightningTarget, "Lightning")
            }
        }
    }
}