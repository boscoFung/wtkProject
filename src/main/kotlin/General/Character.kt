package General
import Card.CardDeck
import Card.DodgeCard

import Strategy.*
import General.*

//Lord
class CaoCao : WeiGeneral("Cao Cao", 1, "Male") {
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
class LiuBei : General("Liu Bei", 4,"Male") {
    var state: State = UnhealthyState()

    override fun playPhase() {
        state = if (currentHP > 1) HealthyState() else UnhealthyState()
        state.playNextCard(this)
    }
}
class SunQuan : General("Sun Quan", 5,"Male") {

}

//Non-lord
class ZhenJi : WeiGeneral("Zhen Ji", 3, "Female")
class ZhugeLiang : General("Zhuge Liang", 3,"Male")
class SimaYi : WeiGeneral("Sima Yi", 3, "Male")
class XuChu : WeiGeneral("Xu Chu", 4, "Male")
class XiahouDun : WeiGeneral("Xiahou Dun", 4, "Male")
class ZhouYu : General("Zhou Yu", 3,"Male") {
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
                hand.add(card) // 加入手牌
                actualCardsDrawn++
            } else {
                println("The deck is empty. No more cards can be drawn.")
                break
            }
        }
        println("[Heroism] $name draws $actualCardsDrawn card(s) and now has ${hand.size} card(s).")
        println("Deck Size: ${CardDeck.getDeckSize()}")
    }
}
class DiaoChan : General("Diao Chan", 3,"Female") {
    override fun discardPhase() {
        if (currentHP <= 0) {
            println("$name is defeated and skips the Discard Phase.")
            return
        }
        super.discardPhase()
        val card = CardDeck.drawCard() // 從牌庫抽牌
        if (card != null) {
            hand.add(card) // 加入手牌
            println("[Beauty Outshining the Moon] $name now has ${hand.size} card(s).")
        } else {
            println("The deck is empty. No more cards can be drawn.")
        }
    }
}
class GuanYu : General ("Guan Yu", 4, "Male")
class ZhangFei : General("Zhang Fei", 4, "Male")