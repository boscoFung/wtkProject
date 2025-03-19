interface Observer {
    fun update(dodged: Boolean)
}

interface Subject {
    fun registerObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObservers(dodged: Boolean)
}

class SpyObserver(val spy: General) : Observer {
    override fun update(dodged: Boolean) {
        val spyStrategy = spy.strategy as? SpyStrategy
        if (spyStrategy != null) {
            if (dodged) {
                spyStrategy.riskLevel = (spyStrategy.riskLevel * 0.5).toInt()
            } else {
                spyStrategy.riskLevel = (spyStrategy.riskLevel * 1.5).toInt()
            }
            println("${spy.name} on Lord's Risk Level: ${spyStrategy.riskLevel}")
        }
    }
}