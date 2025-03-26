import Card.Card
import Card.AttackCard
import General.Player
import General.WeiGeneral

class SimaYi : WeiGeneral("Sima Yi", 3, "Male") {
    override fun applyDamage(amount: Int, source: Player?, damageCard: Card?) {
        val initialHP = currentHP
        super.applyDamage(amount, source, damageCard) // 執行基礎扣血邏輯

        if (source != null && amount > 0 && currentHP > 0 && !defeated) {
            if (damageCard is AttackCard && initialHP > currentHP) { // 確認是 AttackCard 且造成了傷害
                hand.add(damageCard)
                println("[Feedback] $name takes the attack card from ${source.name}: ${damageCard.Suit} ${damageCard.Number} - ${damageCard.Name}")
            } else if (damageCard !is AttackCard && source.hand.isNotEmpty()) { // 其他情況隨機拿一張牌
                val card = source.hand.removeAt(0)
                hand.add(card)
                println("[Feedback] $name takes a card from ${source.name}: ${card.Suit} ${card.Number} - ${card.Name}")
            } else if (source.hand.isEmpty()) {
                println("[Feedback] $name triggers Feedback, but ${source.name} has no cards to take.")
            }
        }
    }
}