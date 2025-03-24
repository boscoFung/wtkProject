package General
import Card.CardDeck
import Card.PeachCard

abstract class WuGeneral(name: String, maxHP: Int, gender: String) : General(name, maxHP, gender) {
    // 標識此角色為吳勢力
    val isWuFaction: Boolean = true

    // 提供一個方法讓其他吳勢力角色檢查是否能救援
    open fun canRescue(target: Player): Boolean {
        return isWuFaction && target is SunQuan && target.currentHP <= 0
    }
}