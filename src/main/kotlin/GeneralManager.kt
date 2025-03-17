object GeneralManager {
    private val players: MutableList<Player> = mutableListOf()


    init {
        println("Setting up the general manager.")
    }

    fun addPlayer(player: Player) {
        players.add(player)
        println("General ${player.name} created.")
        if (player is General) {
            val identity = when (player.strategy) {
                is LordStrategy -> "lord"
                is LoyalistStrategy -> "loyalist"
                is RebelStrategy -> "rebel"
                is SpyStrategy -> "spy"
                else -> "unknown"
            }
            println("${player.name}, a $identity, has ${player.maxHP} health point(s).")
            if (player.strategy is SpyStrategy) {println("${player.name} is observing lord.")}
        }
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    fun getPlayerCount(): Int {
        return players.size
    }

    fun getPlayerList(): List<Player> {
        return players
    }

    fun gameStart() {
        println("Total number of players: ${getPlayerCount()}\n")
        val fourthPlayer = players[3]
        println("${fourthPlayer.name} being placed the Acedia card.")
        fourthPlayer.judgementCommands.add(AcediaCommand)
        for (player in players) {
            player.takeTurn()
            println()
        }
        for (player in players) {
            player.takeTurn()
            println()
        }
    }
}