abstract class WeiGeneral(name: String, maxHP: Int) : General(name, maxHP) {
    var next: WeiGeneral? = null
    var forceDodgeForTesting: Boolean = false

    open fun handleRequest(): Boolean {
        if (forceDodgeForTesting || hasDodgeCard() && Math.random() < 0.5) {
            println("$name helps Cao Cao dodge an attack by spending a dodge card.")
            return true
        } else {
            println("$name cannot dodge the attack. Passing to the next general.")
            return next?.handleRequest() ?: false
        }
    }
}