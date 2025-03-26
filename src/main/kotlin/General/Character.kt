package General
import Card.*
import Equipment.Weapon

import Strategy.*
import General.*
import kotlin.random.Random

//Lord
class CaoCao : WeiGeneral("Cao Cao", 5, "Male") {
    fun entourage(): Boolean {
        println("[Entourage] $name activates Lord Skill Entourage.")
        return next?.handleRequest() ?: false
    }

    override fun dodgeAttack() {
        var dodged = false
        if (!entourage()) {
            println("No Wei general could help. $name attempts to dodge on his own.")
            if (hasDodgeCard()) {
                val dodgeCard = hand.first { it is DodgeCard }
                hand.remove(dodgeCard)
                CardDeck.discardCard(dodgeCard)
                println("$name dodged the attack by spending ${dodgeCard.Suit} ${dodgeCard.Number} - ${dodgeCard.Name}.")
                dodged = true
            } else {
                currentHP--
                println("$name can't dodge the attack, current HP is $currentHP.")
            }
        }
        else {
            dodged = true
        }
        if (strategy is LordStrategy) {
            notifyObservers(dodged)
        }
    }
    override fun attack(attacker: Player) {
        if (currentHP <= 0) {
            println("$name is already defeated and cannot be attacked.")
            return
        }
        println("$name is being attacked by ${attacker.name}.")
        if (eArmor != null) {
            eArmor!!.beingAttacked()
        } else {
            dodgeAttack()
        }
        if (currentHP <= 0) {
            handleDefeat(attacker)
        }
    }
}
class LiuBei : General("Liu Bei", 5,"Male") {
    var state: State = UnhealthyState()

    override fun playPhase() {
        state = if (currentHP > 1) HealthyState() else UnhealthyState()
        state.playNextCard(this)
    }
}
class SunQuan : WuGeneral("Sun Quan", 5, "Male") {
    // 制衡技能：在出牌階段棄置任意數量手牌並摸等量牌
    fun balanceOfPower() {
        if (currentHP <= 0) {
            println("$name is defeated and cannot use [Balance of Power].")
            return
        }
        if (hand.isEmpty()) {
            println("$name has no cards to discard for [Balance of Power].")
            return
        }

        // 模擬中隨機決定棄置的牌數（0 到手牌數量）
        val cardsToDiscard = Random.nextInt(0, hand.size + 1)
        if (cardsToDiscard == 0) {
            println("$name chooses not to discard any cards for [Balance of Power].")
            return
        }

        // 棄置隨機選擇的牌
        val discardedCards = mutableListOf<Card>()
        repeat(cardsToDiscard) {
            if (hand.isNotEmpty()) {
                val card = hand.removeAt(Random.nextInt(hand.size))
                discardedCards.add(card)
                CardDeck.discardCard(card)
            }
        }
        println("$name uses [Balance of Power] to discard $cardsToDiscard card(s): ${discardedCards.joinToString { "${it.Suit} ${it.Number} - ${it.Name}" }}")

        // 摸等量的牌
        var actualCardsDrawn = 0
        repeat(cardsToDiscard) {
            val card = CardDeck.drawCard()
            if (card != null) {
                hand.add(card)
                actualCardsDrawn++
                println("$name draws: ${card.Suit} ${card.Number} - ${card.Name}")
            } else {
                println("The deck is empty. No more cards can be drawn.")
            }
        }
        println("$name used [Balance of Power], discarded $cardsToDiscard card(s) and drew $actualCardsDrawn card(s). Now has ${hand.size} card(s).")
    }

    // 重寫 playPhase 以加入制衡技能
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

        // 先檢查是否需要使用桃
        while (currentHP < maxHP && hasPeachCard() && !defeated && !GeneralManager.isGameOver()) {
            val peachCard = hand.firstOrNull { it is PeachCard } as? PeachCard
            if (peachCard != null) {
                peachCard.use(this)
            }
        }

        // 使用制衡技能
        balanceOfPower()

        // 繼續其他出牌邏輯（裝備、效果牌、攻擊等）
        hand.filterIsInstance<EquipmentCard>().forEach { card ->
            if (defeated) return
            playCard(card)
        }
        playEffectCards()

        var attemptedTargets = mutableSetOf<Player>()
        while (attacksThisTurn < currentAttackLimit && hasAttackCard() && !GeneralManager.isGameOver()) {
            if (defeated) return
            val range = calculateAttackRange()
            val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this && it !in attemptedTargets }
            val target = strategy?.whomToAttack(this, alivePlayers, range)
            if (target == null) {
                println("$name has no valid target to attack within range $range.")
                break
            }
            val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
            if (distance <= range) {
                performAttack()
                attemptedTargets.clear()
            } else {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range)")
                attemptedTargets.add(target)
                if (attemptedTargets.size >= alivePlayers.size) {
                    println("$name has no remaining targets within range $range.")
                    break
                }
            }
        }
    }

    override fun checkAndUsePeach(killer: Player?) {
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

        // 如果瀕死，觸發救援技能
        if (currentHP <= 0 && !defeated) {
            println("$name is in a dying state (HP: $currentHP). Triggering [Rescue] skill.")
            val wuAllies = GeneralManager.getAlivePlayerList().filter { it is WuGeneral && it != this }
            for (ally in wuAllies) {
                if (ally.currentHP > 0 && ally.hand.any { it is PeachCard }) {
                    val peachCard = ally.hand.first { it is PeachCard } as PeachCard
                    ally.hand.remove(peachCard)
                    CardDeck.discardCard(peachCard)
                    currentHP++
                    println("${ally.name} uses [Rescue] with ${peachCard.Suit} ${peachCard.Number} - ${peachCard.Name} to save $name. Current HP is now $currentHP.")
                    if (currentHP > 0) break // 脫離瀕死狀態
                }
            }

            // 如果仍然瀕死且無人救援，處理死亡
            if (currentHP <= 0 && !defeated) {
                println("$name could not be rescued and remains in a dying state.")
                handleDefeat(killer)
            }
        }
    }
}

    //Non-lord
    class ZhenJi : WeiGeneral("Zhen Ji", 3, "Female")

    class XuChu : WeiGeneral("Xu Chu", 4, "Male")
    class XiahouDun : WeiGeneral("Xiahou Dun", 4, "Male")


    class DiaoChan : General("Diao Chan", 3, "Female") {
        override fun discardPhase() {
            if (currentHP <= 0) {
                println("$name is defeated and skips the Discard Phase.")
                return
            }
            super.discardPhase()
            val card = CardDeck.drawCard() // 從牌庫抽牌asd
            if (card != null) {
                hand.add(card) // 加入手牌
                println("[Beauty Outshining the Moon] $name now has ${hand.size} card(s).")
            } else {
                println("The deck is empty. No more cards can be drawn.")
            }
        }
    }

    class GuanYu : General("Guan Yu", 4, "Male") {
        // Override hasAttackCard to include cards with "heart" or "diamonds" suit
        override fun hasAttackCard(): Boolean {
            return hand.any { it is AttackCard || it.Suit in listOf("Hearts", "Diamonds") }
        }

        // Custom method to remove a card for attacking (prioritize ATTACK, then any "heart" or "diamonds" card)
        private fun removeAttackOrHeartDiamondsCard(): Card? {
            // Prioritize removing an ATTACK card if available
            val attackCard = hand.firstOrNull { it is AttackCard }
            if (attackCard != null) {
                hand.remove(attackCard)
                CardDeck.discardCard(attackCard)
                return attackCard
            }
            // If no ATTACK card, use a "heart" or "diamonds" card
            val heartOrDiamondsCard = hand.firstOrNull { it.Suit in listOf("Hearts", "Diamonds") }
            if (heartOrDiamondsCard != null) {
                hand.remove(heartOrDiamondsCard)
                CardDeck.discardCard(heartOrDiamondsCard)
                return heartOrDiamondsCard
            }
            return null
        }

        override fun performAttack() {
            if (!hasAttackCard()) {
                println("$name has no card to use for attacking.")
                return
            }

            val range = calculateAttackRange()
            val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }
            val target = strategy?.whomToAttack(this, alivePlayers, range)
            if (target == null) {
                println("$name has no valid target to attack within range $range.")
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
            if (distance > range || !target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                return
            }

            if (eWeapon != null) {
                val weapon = eWeapon as Weapon
                if (weapon.canAttack(attacksThisTurn)) {
                    val attackCard = removeAttackOrHeartDiamondsCard()
                    if (attackCard != null) {
                        val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
                        val range = calculateAttackRange()
                        if (attackCard is AttackCard) {
                            println("$name uses ${weapon.name} with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name} (距離: $distance / 攻擊範圍: $range)")
                        } else {
                            println("[Warrior Saint] $name uses ${weapon.name} with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} as an Attack card to attack a $targetIdentity, ${target.name} (距離: $distance / 攻擊範圍: $range)")
                        }
                        attacksThisTurn++
                        weapon.attackTarget(this, target, attackCard)
                    }
                } else {
                    println("$name has reached the attack limit this turn (attacks: $attacksThisTurn) with ${weapon.name}.")
                }
            } else {
                if (attacksThisTurn < currentAttackLimit) {
                    val attackCard = removeAttackOrHeartDiamondsCard()
                    if (attackCard != null) {
                        if (attackCard is AttackCard) {
                            println("$name spends ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name}")
                        } else {
                            println("[Warrior Saint] $name spends ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} as an Attack card to attack a $targetIdentity, ${target.name}")
                        }
                        attacksThisTurn++
                        target.attack(this)
                    }
                } else {
                    println("$name has reached the attack limit this turn (attacks: $attacksThisTurn).")
                }
            }
        }

        // Override playPhase to prevent infinite loop (similar to Zhao Yun and Zhang Fei fixes)
        override fun playPhase() {
            if (skipPlayPhase) {
                println("$name is skipping the Play Phase.")
                skipPlayPhase = false
                return
            }
            println("$name is in the Play Phase.")

            // Automatically use PeachCards if HP is less than maxHP
            while (currentHP < maxHP && hasPeachCard() && !GeneralManager.isGameOver()) {
                val peachCard = hand.first { it is PeachCard } as PeachCard
                peachCard.use(this)
            }

            hand.filterIsInstance<EquipmentCard>().forEach { card ->
                playCard(card)
            }

            playEffectCards()

            // Guan Yu's attack phase: Can attack as long as he has Attack cards or "heart"/"diamonds" cards and valid targets
            var canAttack = true
            val attemptedTargets = mutableSetOf<Player>()
            val totalAlivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }.size // Fixed size for comparison
            while (attacksThisTurn < currentAttackLimit && hasAttackCard() && !GeneralManager.isGameOver() && canAttack) {
                val range = calculateAttackRange()
                val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this && it !in attemptedTargets }
                // Pass the attack range to whomToAttack to ensure only in-range targets are selected
                val target = strategy?.whomToAttack(this, alivePlayers, range)
                if (target == null) {
                    println("$name has no valid target to attack within range $range.")
                    break
                }
                val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
                if (distance > range || !target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                    println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                    attemptedTargets.add(target)
                    if (attemptedTargets.size >= totalAlivePlayers) {
                        println("$name has no remaining targets within range $range.")
                        canAttack = false
                    }
                    continue
                }
                performAttack()
                attemptedTargets.clear() // Reset attempted targets after a successful attack
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
    }
    class ZhangFei : General("Zhang Fei", 4, "Male") {
        override fun playPhase() {
        if (skipPlayPhase) {
            println("$name is skipping the Play Phase.")
            skipPlayPhase = false
            return
        }
        println("$name is in the Play Phase.")

        // Automatically use PeachCards if HP is less than maxHP
        while (currentHP < maxHP && hasPeachCard() && !GeneralManager.isGameOver()) {
            val peachCard = hand.first { it is PeachCard } as PeachCard
            peachCard.use(this)
        }

        hand.filterIsInstance<EquipmentCard>().forEach { card ->
            playCard(card)
        }

        playEffectCards()

        // Zhang Fei's "Berserk" ability: Can attack as long as he has Attack cards and valid targets
        var canAttack = true
        var attemptedTargets = mutableSetOf<Player>()
            val totalAlivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }.size
        while (hasAttackCard() && !GeneralManager.isGameOver() && canAttack) {
            val range = calculateAttackRange()
            val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this && it !in attemptedTargets }
            val target = strategy?.whomToAttack(this, alivePlayers, range)
            if (target == null) {
                println("$name has no valid target to attack.")
                break
            }
            val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
            if (distance > range || !target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted)")
                attemptedTargets.add(target)
                if (attemptedTargets.size >= totalAlivePlayers) {
                    println("$name has no remaining targets within range $range.")
                    canAttack = false
                }
                continue
            }
            performAttack()
            attemptedTargets.clear() // Reset attempted targets after a successful attack
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

            if (distance > range) {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range)")
                return
            }

            if (eWeapon != null) {
                val weapon = eWeapon as Weapon
                // Zhang Fei's "Berserk" ability overrides the weapon's attack limit
                val attackCard = removeCardOfType(AttackCard::class.java)
                if (attackCard != null) {
                    val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
                    val range = calculateAttackRange()
                    println("[Berserk] $name uses ${weapon.name} with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name} (距離: $distance / 攻擊範圍: $range)")
                    attacksThisTurn++
                    weapon.attackTarget(this, target, attackCard)
                }
            } else {
                // No weapon equipped: Zhang Fei's "Berserk" ability allows unlimited attacks
                val attackCard = removeCardOfType(AttackCard::class.java)
                if (attackCard != null) {
                    println("[Berserk] $name spends ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name}")
                    attacksThisTurn++
                    target.attack(this)
                }
            }
        }
    }
    class ZhaoYun : General("Zhao Yun", 4, "Male") {
        // Override hasDodgeCard to include ATTACK cards
        override fun hasDodgeCard(): Boolean {
            return hand.any { it is DodgeCard || it is AttackCard }
        }

        // Override hasAttackCard to include DODGE cards
        override fun hasAttackCard(): Boolean {
            return hand.any { it is AttackCard || it is DodgeCard }
        }

        // Custom method to remove a card for dodging (prioritize DODGE, then ATTACK)
        private fun removeDodgeOrAttackCard(): Card? {
            // Prioritize removing a DODGE card if available
            val dodgeCard = hand.firstOrNull { it is DodgeCard }
            if (dodgeCard != null) {
                hand.remove(dodgeCard)
                CardDeck.discardCard(dodgeCard)
                return dodgeCard
            }
            // If no DODGE card, use an ATTACK card
            val attackCard = hand.firstOrNull { it is AttackCard }
            if (attackCard != null) {
                hand.remove(attackCard)
                CardDeck.discardCard(attackCard)
                return attackCard
            }
            return null
        }

        // Custom method to remove a card for attacking (prioritize ATTACK, then DODGE)
        private fun removeAttackOrDodgeCard(): Card? {
            // Prioritize removing an ATTACK card if available
            val attackCard = hand.firstOrNull { it is AttackCard }
            if (attackCard != null) {
                hand.remove(attackCard)
                CardDeck.discardCard(attackCard)
                return attackCard
            }
            // If no ATTACK card, use a DODGE card
            val dodgeCard = hand.firstOrNull { it is DodgeCard }
            if (dodgeCard != null) {
                hand.remove(dodgeCard)
                CardDeck.discardCard(dodgeCard)
                return dodgeCard
            }
            return null
        }

        override fun dodgeAttack() {
            val dodged = hasDodgeCard()
            if (dodged) {
                val cardUsed = removeDodgeOrAttackCard()
                println("[Dragon Heart] $name dodged attack by using ${cardUsed?.Suit} ${cardUsed?.Number} - ${cardUsed?.Name} as a Dodge card.")
            } else {
                currentHP--
                println("$name can't dodge the attack, current HP is $currentHP.")
            }
            if (strategy is LordStrategy) {
                notifyObservers(dodged)
            }
        }
        override fun playPhase() {
            if (skipPlayPhase) {
                println("$name is skipping the Play Phase.")
                skipPlayPhase = false
                return
            }
            println("$name is in the Play Phase.")

            // Automatically use PeachCards if HP is less than maxHP
            while (currentHP < maxHP && hasPeachCard() && !GeneralManager.isGameOver()) {
                val peachCard = hand.first { it is PeachCard } as PeachCard
                peachCard.use(this)
            }

            hand.filterIsInstance<EquipmentCard>().forEach { card ->
                playCard(card)
            }

            playEffectCards()

            // Zhao Yun's attack phase: Can attack as long as he has Attack or Dodge cards and valid targets
            var canAttack = true
            val attemptedTargets = mutableSetOf<Player>()
            val totalAlivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }.size // Fixed size for comparison
            while (attacksThisTurn < currentAttackLimit && hasAttackCard() && !GeneralManager.isGameOver() && canAttack) {
                val range = calculateAttackRange()
                val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this && it !in attemptedTargets }
                // Pass the attack range to whomToAttack to ensure only in-range targets are selected
                val target = strategy?.whomToAttack(this, alivePlayers, range)
                if (target == null) {
                    println("$name has no valid target to attack within range $range.")
                    break
                }
                val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
                if (distance > range || !target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                    println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                    attemptedTargets.add(target)
                    if (attemptedTargets.size >= totalAlivePlayers) {
                        println("$name has no remaining targets within range $range.")
                        canAttack = false
                    }
                    continue
                }
                performAttack()
                attemptedTargets.clear() // Reset attempted targets after a successful attack
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
        override fun performAttack() {
            if (!hasAttackCard()) {
                println("$name has no card to use for attacking.")
                return
            }

            val range = calculateAttackRange()
            val alivePlayers = GeneralManager.getAlivePlayerList().filter { it != this }
            val target = strategy?.whomToAttack(this, alivePlayers, range)
            if (target == null) {
                println("$name has no valid target to attack within range $range.")
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
            if (distance > range || !target.canBeTargeted(this, AttackCard("Dummy", "0"))) {
                println("$name cannot attack ${target.name} (distance: $distance > range: $range or targeting restricted).")
                return
            }

            if (eWeapon != null) {
                val weapon = eWeapon as Weapon
                if (weapon.canAttack(attacksThisTurn)) {
                    val attackCard = removeAttackOrDodgeCard()
                    if (attackCard != null) {
                        val distance = calculateDistanceTo(target, GeneralManager.getAlivePlayerCount())
                        val range = calculateAttackRange()
                        println("[Dragon Heart] $name uses ${weapon.name} with ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name} (距離: $distance / 攻擊範圍: $range)")
                        attacksThisTurn++
                        weapon.attackTarget(this, target, attackCard)
                    }
                } else {
                    println("$name has reached the attack limit this turn (attacks: $attacksThisTurn) with ${weapon.name}.")
                }
            } else {
                if (attacksThisTurn < currentAttackLimit) {
                    val attackCard = removeAttackOrDodgeCard()
                    if (attackCard != null) {
                        println("[Dragon Heart] $name spends ${attackCard.Suit} ${attackCard.Number} - ${attackCard.Name} to attack a $targetIdentity, ${target.name}")
                        attacksThisTurn++
                        target.attack(this)
                    }
                } else {
                    println("$name has reached the attack limit this turn (attacks: $attacksThisTurn).")
                }
            }
        }
    }