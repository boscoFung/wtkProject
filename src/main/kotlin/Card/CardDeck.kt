package Card

object CardDeck {
    private var deck: MutableList<Card> = mutableListOf() // 牌庫
    private val discardPile: MutableList<Card> = mutableListOf() //棄牌

    init {
        initializeDeck()
    }

    fun initializeDeck() { //牌庫
        deck = mutableListOf(
                    // Spades
//                    Card("Spades", "A", "Lightning"),
//                    Card("Spades", "A", "Duel"),
//                    Card("Spades", "2", "Eight Trigrams Formation"),
//                    Card("Spades", "2", "Yin-Yang Swords"),
//                    Card("Spades", "3", "Burning Bridges"),
//                    Card("Spades", "3", "Stealing Sheep"),
//                    Card("Spades", "4", "Burning Bridges"),
//                    Card("Spades", "4", "Stealing Sheep"),
//                    Card("Spades", "5", "Green Dragon Blade"),
//                    Card("Spades", "5", "Shadowrunner"),
                    AcediaCard("Spades", "6"),
//                    Card("Spades", "6", "Blue Steel Blade"),
//                    Card("Spades", "7", "Barbarian Invasion"),
                    AttackCard("Spades", "7"),
                    AttackCard("Spades", "8"),
                    AttackCard("Spades", "8"),
                    AttackCard("Spades", "9"),
                    AttackCard("Spades", "9"),
                    AttackCard("Spades", "10"),
                    AttackCard("Spades", "10"),
//                    Card("Spades", "J", "Negation"),
//                    Card("Spades", "J", "Stealing Sheep"),
//                    Card("Spades", "Q", "Serpent Spear"),
//                    Card("Spades", "Q", "Burning Bridges"),
//                    Card("Spades", "K", "Ferghana Horse"),
//                    Card("Spades", "K", "Barbarian Invasion"),
//
//                    // Hearts
//                    Card("Hearts", "A", "Brotherhood"),
//                    Card("Hearts", "A", "Raining Arrows"),
                    DodgeCard("Hearts", "2"),
                    DodgeCard("Hearts", "2"),
                    PeachCard("Hearts", "3"),
//                    Card("Hearts", "3", "Bumper Harvest"),
                    PeachCard("Hearts", "4"),
//                    Card("Hearts", "4", "Bumper Harvest"),
//                    Card("Hearts", "5", "Kirin Bow"),
//                    Card("Hearts", "5", "Red Hare"),
                    PeachCard("Hearts", "6"),
                    AcediaCard("Hearts", "6"),
                    PeachCard("Hearts", "7"),
//                    Card("Hearts", "7", "Something Out of Nothing"),
                    PeachCard("Hearts", "8"),
//                    Card("Hearts", "8", "Something Out of Nothing"),
                    PeachCard("Hearts", "9"),
//                    Card("Hearts", "9", "Something Out of Nothing"),
                    AttackCard("Hearts", "10"),
                    AttackCard("Hearts", "10"),
                    AttackCard("Hearts", "J"),
//                    Card("Hearts", "J", "Something Out of Nothing"),
                    PeachCard("Hearts", "Q"),
//                   Card("Hearts", "Q", "Burning Bridges"),
//                    Card("Hearts", "K", "Flying Lightning"),
                    DodgeCard("Hearts", "K"),

                    // Clubs
//                    Card("Clubs", "A", "Duel"),
//                    Card("Clubs", "2", "Eight Trigrams Formation"),
                    AttackCard("Clubs", "2"),
//                    Card("Clubs", "3", "Burning Bridges"),
                    AttackCard("Clubs", "3"),
//                    Card("Clubs", "4", "Burning Bridges"),
                    AttackCard("Clubs", "4"),
//                    Card("Clubs", "5", "Hex Mark"),
                    AttackCard("Clubs", "5"),
                    AcediaCard("Clubs", "6"),
                    AttackCard("Clubs", "6"),
//                    Card("Clubs", "7", "Barbarian Invasion"),
                    AttackCard("Clubs", "7"),
                    AttackCard("Clubs", "8"),
                    AttackCard("Clubs", "8"),
                    AttackCard("Clubs", "9"),
                    AttackCard("Clubs", "10"),
                    AttackCard("Clubs", "10"),
                    AttackCard("Clubs", "J"),
                    AttackCard("Clubs", "J"),
//                    Card("Clubs", "Q", "Burning Bridges"),
//                    Card("Clubs", "Q", "Negation"),
//                    Card("Clubs", "K", "Burning Bridges"),
//                    Card("Clubs", "K", "Negation"),

                    // Diamonds
//                    Card("Diamonds", "A", "Duel"),
//                    Card("Diamonds", "A", "Zhuge Crossbow"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "3"),
//                    Card("Diamonds", "3", "Stealing Sheep"),
                    DodgeCard("Diamonds", "4"),
                    //                  Card("Diamonds", "4", "Stealing Sheep"),
                    DodgeCard("Diamonds", "5"),
//                    Card("Diamonds", "5", "Rock Cleaving Axe"),
                    AttackCard("Diamonds", "6"),
                    DodgeCard("Diamonds", "6"),
                    AttackCard("Diamonds", "7"),
                    DodgeCard("Diamonds", "7"),
                    AttackCard("Diamonds", "8"),
                    DodgeCard("Diamonds", "8"),
                    AttackCard("Diamonds", "9"),
                    DodgeCard("Diamonds", "9"),
                    AttackCard("Diamonds", "10"),
                    DodgeCard("Diamonds", "J"),
                    AttackCard("Diamonds", "J"),
                    PeachCard("Diamonds", "Q"),
//                    Card("Diamonds", "Q", "Sky Piercing Halberd"),
//                    Card("Diamonds", "K", "Violet Stallion"),
                    AttackCard("Diamonds", "K")
                )
        deck.shuffle()
    }

    fun drawCard(): Card? {
        return if (deck.isNotEmpty()) {
            deck.removeAt(0) // 從牌庫頂部抽牌
        } else {
            null
        }
    }

    fun discardCard(card: Card) {
        discardPile.add(card) // 將卡牌加入棄牌堆
    }

    fun printCard() { //看牌庫
        if (deck.isEmpty()) {
            println("The deck is empty.")
        } else {
            println("Current cards in the deck:")
            deck.forEachIndexed { index, card ->
                println("${card.Suit} ${card.Number} - ${card.Name}")
            }
            println("Deck Size:" + getDeckSize())
        }
    }

    fun getDeckSize(): Int{
        return deck.size
    }
}
