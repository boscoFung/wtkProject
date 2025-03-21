package Card
import General.*

// TargetedCard
abstract class TargetedCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>)

    fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        println("$Name requires a specific target")
    }

    // 提供一個通用的方法來檢查無懈可擊並執行效果
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
            println("${currentPlayer.name} 的 $Name 被無懈可擊取消。")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}

// SelfCard
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
            println("${currentPlayer.name} 的 $Name 被無懈可擊取消。")
            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)
        }
    }
}

// GroupCard
abstract class GroupCard(Suit: String, Number: String, Name: String) : EffectCard(Suit, Number, Name) {
    abstract fun effect(currentPlayer: Player, allPlayers: List<Player>)

    // 為傷害卡提供規避傷害的邏輯，並直接扣除生命值
    protected fun handleDamageWithDodge(
        currentPlayer: Player,
        target: Player,
        allPlayers: List<Player>,
        damageResults: MutableList<String>
    ) {
        // 檢查無懈可擊
        if (checkImpeccable(currentPlayer, target, allPlayers, isBenefit = false)) {
            println("${target.name} is protected from $Name by Impeccable")
            damageResults.add("對 ${target.name} 造成了 0 點傷害")
            return
        }

        // 根據卡片類型檢查規避條件
        val canDodge = when (Name) {
            "Barbarian Invasion" -> target.hand.any { it is AttackCard } // 南蠻入侵只能用「殺」
            "Raining Arrows" -> target.hand.any { it is DodgeCard } // 萬箭齊發只能用「閃」
            else -> target.hand.any { it is AttackCard || it is DodgeCard } // 其他群體傷害卡可以用「殺」或「閃」
        }

        if (canDodge) {
            // 根據卡片類型移除對應的卡片
            val dodgeCard = when (Name) {
                "Barbarian Invasion" -> target.hand.first { it is AttackCard }
                "Raining Arrows" -> target.hand.first { it is DodgeCard }
                else -> target.hand.first { it is AttackCard || it is DodgeCard }
            }
            target.hand.remove(dodgeCard)
            CardDeck.discardCard(dodgeCard)
            println("${target.name} uses ${dodgeCard.Name} to dodge $Name")
            damageResults.add("對 ${target.name} 造成了 0 點傷害")
        } else {
            // 直接扣除生命值，不調用 beingAttacked
            val initialHP = target.currentHP
            target.currentHP--
            val damageDealt = initialHP - target.currentHP
            println("${target.name} can't dodge $Name, current HP is ${target.currentHP}")
            damageResults.add("對 ${target.name} 造成了 $damageDealt 點傷害")
        }
    }
}

// BumperHarvestCard
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

        playersToDraw.forEachIndexed { index, player ->
            if (cards.isNotEmpty()) {
                val card = cards[index % cards.size]
                player.hand.add(card)
                println("${player.name} receives a card from $Name")
                drawResults.add("${player.name} 抽了 1 張牌")
            }
        }
        currentPlayer.hand.remove(this)
        CardDeck.discardCard(this)

        println("")
        if (drawResults.isNotEmpty()) {
            println("${currentPlayer.name} 使用了五穀豐登，${drawResults.joinToString("，")}。")
        } else {
            println("${currentPlayer.name} 使用了五穀豐登，但牌庫已空，無法抽牌。")
        }
    }
}

class BrotherhoodCard(Suit: String, Number: String) : GroupCard(Suit, Number, "Brotherhood") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
        // 首先確定受影響的玩家所有存活玩家
        val affectedPlayers = allPlayers.filter { it.currentHP > 0 }
        if (currentPlayer.currentHP != currentPlayer.maxHP) {
            // 檢查無懈可擊
            val playersToHeal = checkImpeccableForGroup(currentPlayer, affectedPlayers, allPlayers, isBenefit = true)

            val healResults = mutableListOf<String>()
            playersToHeal.forEach { player ->
                if (player.currentHP < player.maxHP) {
                    player.currentHP++
                    healResults.add("${player.name} 回復了 1 點 HP")
                } else {
                    healResults.add("${player.name} 的 HP 已滿，無法回復")
                }
            }

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)

            if (healResults.isNotEmpty()) {
                println("${currentPlayer.name} 使用了桃園結義，${healResults.joinToString("，")}。")
            } else {
                println("${currentPlayer.name} 使用了桃園結義，但沒有玩家需要回復 HP。")
            }
        }
    }
}

// RainingArrowsCard
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
                println("${currentPlayer.name} 使用了萬箭齊發，${damageResults.joinToString("，")}。")
            }
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
            println("${currentPlayer.name} 選擇不使用萬箭齊發，以保護血量為 1 的非敵對玩家。")
        }
    }
}

// BarbarianInvasionCard
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
                println("${currentPlayer.name} 使用了南蠻入侵，${damageResults.joinToString("，")}。")
            }
        } else {
            println("${currentPlayer.name} chooses not to use $Name to protect non-hostile players with 1 HP")
            println("${currentPlayer.name} 選擇不使用南蠻入侵，以保護血量為 1 的非敵對玩家。")
        }
    }
}

class SOONCard(Suit: String, Number: String) : SelfCard(Suit, Number, "Something out of nothing") {
    override fun effect(currentPlayer: Player, allPlayers: List<Player>) {
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

            println("${currentPlayer.name} 使用了無中生有，${currentPlayer.name} 抽了 $actualCardsDrawn 張牌。")
        }
    }
}

class DuelCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Duel") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
            println("${currentPlayer.name} initiates $Name with ${target.name}")
            val results = mutableListOf<String>()
            var duelActive = true
            var targetTurn = true

            while (duelActive) {
                val currentDuelist = if (targetTurn) target else currentPlayer
                val opponentName = if (targetTurn) target.name else currentPlayer.name

                if (!currentDuelist.hasAttackCard()) {
                    val initialHP = currentDuelist.currentHP
                    currentDuelist.currentHP--
                    val damageDealt = initialHP - currentDuelist.currentHP
                    results.add("$opponentName 沒有殺，受到 $damageDealt 點傷害")
                    duelActive = false
                } else {
                    currentDuelist.removeCardOfType(AttackCard::class.java)
                    println("$opponentName responds in $Name with an attack card")
                    results.add("$opponentName 使用了一張殺應對")
                    targetTurn = !targetTurn
                }
            }

            currentPlayer.hand.remove(this)
            CardDeck.discardCard(this)

            println("${currentPlayer.name} 對 ${target.name} 使用了決鬥，${results.joinToString("，")}。")
        }
    }
}

// StealingSheepCard
class StealingSheepCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Stealing Sheep") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        val distance = currentPlayer.calculateDistanceTo(target, allPlayers.size)
        if (target.hand.isNotEmpty() && distance <= 1) {
            executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
                val stolenCard = target.hand.removeAt(0)
                currentPlayer.hand.add(stolenCard)
                println("${currentPlayer.name} uses $Name to steal a card from ${target.name}")
                currentPlayer.hand.remove(this)
                CardDeck.discardCard(this)

                println("${currentPlayer.name} 使用了順手牽羊，從 ${target.name} 處偷了 1 張牌。")
            }
        } else {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (distance: $distance > 1 or target has no cards)")
            println("${currentPlayer.name} 無法對 ${target.name} 使用順手牽羊（距離：$distance > 1 或目標沒有牌）。")
        }
    }
}

// BBQCard
class BBQCard(Suit: String, Number: String) : TargetedCard(Suit, Number, "Burning Bridges") {
    override fun effect(currentPlayer: Player, target: Player, allPlayers: List<Player>) {
        if (target.hand.isNotEmpty()) {
            executeWithImpeccableCheck(currentPlayer, target, allPlayers) {
                val removedCard = target.hand.removeAt(0)
                CardDeck.discardCard(removedCard)
                println("${currentPlayer.name} uses $Name to discard a card from ${target.name}")
                currentPlayer.hand.remove(this)
                CardDeck.discardCard(this)

                println("${currentPlayer.name} 使用了過河拆橋，丟棄了 ${target.name} 的 1 張牌。")
            }
        } else {
            println("${currentPlayer.name} cannot use $Name on ${target.name} (target has no cards)")
            println("${currentPlayer.name} 無法對 ${target.name} 使用過河拆橋（目標沒有牌")
        }
    }
}