package General
import Card.CardDeck

import Strategy.*
import General.*

//Lord
class CaoCao : WeiGeneral("Cao Cao", 5) {
    fun entourage(): Boolean {
        println("[Entourage] $name activates Lord Skill Entourage.")
        return next?.handleRequest() ?: false
    }

    override fun dodgeAttack() {
        println("[Entourage] $name activates Lord Skill Entourage.")
        var dodged = false
        if (!entourage()) {
            println("No Wei general could help. $name attempts to dodge on his own.")
            if (hasDodgeCard()) {
                println("$name dodged the attack by spending a dodge card.")
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
}
class LiuBei : General("Liu Bei", 1) {
    var state: State = UnhealthyState()

    override fun playPhase() {
        state = if (currentHP > 1) HealthyState() else UnhealthyState()
        state.playNextCard(this)
    }
}
class SunQuan : General("Sun Quan", 5) {

}

//Non-lord
class ZhenJi : WeiGeneral("Zhen Ji", 3)
class ZhugeLiang : General("Zhuge Liang", 3)
class SimaYi : WeiGeneral("Sima Yi", 3)
class XuChu : WeiGeneral("Xu Chu", 4)
class XiahouDun : WeiGeneral("Xiahou Dun", 4)
class ZhouYu : General("Zhou Yu", 3) {
    override fun drawPhase() {
        val cardsDrawn = 3
        for (i in 1..cardsDrawn) {
            val card = CardDeck.drawCard() // 從牌庫抽牌
            if (card != null) {
                hand.add(card) // 加入手牌
            } else {
                println("The deck is empty. No more cards can be drawn.")
            }
        }
        println("[Heroism] $name draws $cardsDrawn card(s) and now has ${hand.size} card(s).")
    }
}
class DiaoChan : General("Diao Chan", 3) {
    override fun discardPhase() {
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
class GuanYu : General ("Guan Yu", 4)
class ZhangFei : General("Zhang Fei", 4)