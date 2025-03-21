package Strategy
import Card.CardDeck
import General.General
import General.Player

object GeneralManager {
    private val players: MutableList<Player> = mutableListOf()
    private var gameOver: Boolean = false
    private var winners: List<Player>? = null

    init {
        println("Setting up the general manager.")
    }

    fun addPlayer(player: Player) {
        player.seat = players.size + 1
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
    fun getAlivePlayerList(): List<Player> {
        return players.filter { it.currentHP > 0 }
    }
    fun getAlivePlayerCount(): Int {
        return players.count { it.currentHP > 0 }
    }
    fun randomizeSeats() {
        players.shuffle() //
        players.forEachIndexed { index, player ->
            player.seat = index + 1
        }

        players.sortBy { it.seat }
        println()

        players.forEach {
            println("${it.name} is now seated at position ${it.seat}.")
        }
    }

    fun checkGameOver() {
        val alivePlayers = getAlivePlayerList()
        val aliveLords = alivePlayers.filter { it is General && it.strategy is LordStrategy }
        val aliveLoyalists = alivePlayers.filter { it is General && it.strategy is LoyalistStrategy }
        val aliveRebels = alivePlayers.filter { it is General && it.strategy is RebelStrategy }
        val aliveSpies = alivePlayers.filter { it is General && it.strategy is SpyStrategy }

        // Rebels win if the Lord is dead
        if (aliveLords.isEmpty() && aliveRebels.isNotEmpty()) {
            gameOver = true
            winners = aliveRebels
            println("Game Over! The Rebels (${aliveRebels.joinToString { it.name }}) win by killing the Lord!")
            return
        }

        // Lord and Loyalists win if all Rebels and Spies are dead
        if (aliveRebels.isEmpty() && aliveSpies.isEmpty()) {
            gameOver = true
            winners = aliveLords + aliveLoyalists
            println("Game Over! The Lord and Loyalists (${(aliveLords + aliveLoyalists).joinToString { it.name }}) win by eliminating all Rebels and Spies!")
            return
        }

        // Spy wins if they are the last one standing (Lord, Loyalists, and Rebels are all dead)
        if (aliveSpies.size == 1 && aliveLords.isEmpty() && aliveLoyalists.isEmpty() && aliveRebels.isEmpty()) {
            gameOver = true
            winners = aliveSpies
            println("Game Over! The Spy (${aliveSpies.first().name}) wins by being the last one standing!")
            return
        }

        // If only one player is left (and it's not a Spy victory), they win by default
        if (alivePlayers.size == 1) {
            gameOver = true
            winners = alivePlayers
            println("Game Over! ${alivePlayers.first().name} wins by being the last one standing!")
            return
        }
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun getWinners(): List<Player>? {
        return winners
    }

    fun gameStart() {
        randomizeSeats()
//        CardDeck.initializeDeck()
        CardDeck.printCard()
        println("Total number of players: ${getPlayerCount()}\n")
//        val fourthPlayer = players[3]
//        println("${fourthPlayer.name} being placed the Acedia card.")
//        fourthPlayer.judgementCommands.add(AcediaCommand)

        for (player in players) {
            println("${player.name} is drawing 4 cards...")
            for (i in 1..4) {
                val card = CardDeck.drawCard()
                if (card != null) {
                    player.hand.add(card)
                    println("${player.name} draws: ${card.Suit} ${card.Number} - ${card.Name}")
                }
            }
        }

//        for (player in players) {
//            player.takeTurn()
//            println()
//        }
        // Game loop
        repeat (10) {
            val alivePlayers = getAlivePlayerList().sortedBy { it.seat }
            for (player in alivePlayers) {
                if (gameOver) break // Stop if game over was triggered during a turn
                if (player.currentHP <= 0) {
                    println("${player.name} is already defeated and skips their turn.")
                    continue // Skip defeated players (redundant but added for safety)
                }
                println("\n=== ${player.name}'s Turn (Seat ${player.seat}) ===")
                player.takeTurn()
                if (gameOver) break
            }
        }
    }
}