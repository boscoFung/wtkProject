package General
import Card.*

class ZhugeLiang : General("Zhuge Liang", 3, "Male") {

    // 觀星技能：查看牌堆頂部 5 張牌並重新排列，然後抽取 2 張
    private fun gazingAtTheStars() {
        if (currentHP <= 0) {
            println("$name is defeated and cannot use [Gazing at the Stars].")
            return
        }
        if (CardDeck.getDeckSize() == 0) {
            println("Deck is empty. Cannot use [Gazing at the Stars].")
            return
        }

        val cardsToView = minOf(5, CardDeck.getDeckSize())
        val viewedCards = mutableListOf<Card>()
        println("[Gazing at the Stars] $name activates the skill to view the top $cardsToView card(s).")

        repeat(cardsToView) {
            val card = CardDeck.drawCard()
            if (card != null) {
                viewedCards.add(card)
            }
        }

        println("[Gazing at the Stars] $name views the following cards:")
        viewedCards.forEach { println("${it.Suit} ${it.Number} - ${it.Name}") }

        val prioritizeDodge = currentHP <= maxHP / 2
        viewedCards.sortWith(compareByDescending<Card> {
            when {
                it is PeachCard -> 4
                it is AttackCard -> if (prioritizeDodge) 2 else 3
                it is DodgeCard -> if (prioritizeDodge) 3 else 2
                it is EffectCard -> 1
                else -> 0
            }
        })

        println("[Gazing at the Stars] $name rearranges the cards in the following order:")
        viewedCards.forEach { println("${it.Suit} ${it.Number} - ${it.Name}") }

        viewedCards.reversed().forEach { card ->
            CardDeck.addToTop(card)
        }
        println("[Gazing at the Stars] $name has returned all $cardsToView card(s) to the top of the deck.")

        val cardsToDraw = minOf(2, CardDeck.getDeckSize())
        repeat(cardsToDraw) {
            val card = CardDeck.drawCard()
            if (card != null) {
                hand.add(card)
                println("$name draws: ${card.Suit} ${card.Number} - ${card.Name}")
            }
        }
        println("[Gazing at the Stars] $name has completed rearranging the deck and drawn $cardsToDraw card(s).")
    }

    override fun drawPhase() {
        if (currentHP <= 0) {
            println("$name is defeated and skips the Draw Phase.")
            return
        }
        gazingAtTheStars()
        println("$name now has ${hand.size} card(s).")
        println("Deck Size: ${CardDeck.getDeckSize()}")
    }

    override fun canBeTargeted(source: Player, card: Card): Boolean {
        val isAttackOrDuel = card is AttackCard || card is DuelCard
        if (isAttackOrDuel && hand.isEmpty()) {
            println("[KongCheng] $name has no cards and cannot be targeted by ${card.Name}.")
            return false
        }
        return super.canBeTargeted(source, card) 
    }
}