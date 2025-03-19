interface State {
    fun playNextCard(player: LiuBei)
}

class HealthyState : State {
    override fun playNextCard(player: LiuBei) {
        println("Liu Bei is now healthy.")
        if (player.hasAttackCard()) {
            val target = player.strategy?.whomToAttack(player, GeneralManager.getPlayerList())
            if (target != null) {
                println("Liu Bei spends a card to attack a rebel, ${target.name}.")
                player.numOfCards--
                target.beingAttacked()
            } else {
                println("Liu Bei has an attack card but no target to attack.")
            }
        } else {
            println("Liu Bei doesn't have an attack card.")
        }
    }
}

class UnhealthyState : State {
    override fun playNextCard(player: LiuBei) {
        println("Liu Bei is not healthy.")
        if (player.numOfCards >= 2) {
            println("[Benevolence] Liu Bei gives away two cards and recovers 1 HP, now his HP is ${player.currentHP + 1}.")
            println("Liu Bei is now healthy.")
            player.currentHP++
            player.numOfCards -= 2
        } else {
            println("Liu Bei doesn't have enough cards to activate [Benevolence].")
        }
    }
}