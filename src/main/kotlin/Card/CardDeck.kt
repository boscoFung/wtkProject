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
                    DuelCard("Spades", "A"),
//                    Card("Spades", "2", "Eight Trigrams Formation"),
//                    Card("Spades", "2", "Yin-Yang Swords"),
                    BBQCard("Spades", "3"),
                    StealingSheepCard("Spades", "3"),
                    BBQCard("Spades", "4"),
                    StealingSheepCard("Spades", "4"),
//                    Card("Spades", "5", "Green Dragon Blade"),
//                    Card("Spades", "5", "Shadowrunner"),
                    AcediaCard("Spades", "6"),
//                    Card("Spades", "6", "Blue Steel Blade"),
                    BarbarianInvasionCard("Spades", "7"),
                    AttackCard("Spades", "7"),
                    AttackCard("Spades", "8"),
                    AttackCard("Spades", "8"),
                    AttackCard("Spades", "9"),
                    AttackCard("Spades", "9"),
                    AttackCard("Spades", "10"),
                    AttackCard("Spades", "10"),
//                    Card("Spades", "J", "Negation"),
                    StealingSheepCard("Spades", "J"),
//                    Card("Spades", "Q", "Serpent Spear"),
                    BBQCard("Spades", "Q"),
//                    Card("Spades", "K", "Ferghana Horse"),
                    BarbarianInvasionCard("Spades", "K"),
//
//                    // Hearts
//                    Card("Hearts", "A", "Brotherhood"),
                    RainingArrowsCard("Hearts", "A"),
                    DodgeCard("Hearts", "2"),
                    DodgeCard("Hearts", "2"),
                    PeachCard("Hearts", "3"),
                    BumperHarvestCard("Hearts", "3"),
                    PeachCard("Hearts", "4"),
                    BumperHarvestCard("Hearts", "4"),
//                    Card("Hearts", "5", "Kirin Bow"),
//                    Card("Hearts", "5", "Red Hare"),
                    PeachCard("Hearts", "6"),
                    AcediaCard("Hearts", "6"),
                    PeachCard("Hearts", "7"),
                    SOONCard("Hearts", "7"),
                    PeachCard("Hearts", "8"),
                    SOONCard("Hearts", "8"),
                    PeachCard("Hearts", "9"),
                    SOONCard("Hearts", "9"),
                    AttackCard("Hearts", "10"),
                    AttackCard("Hearts", "10"),
                    AttackCard("Hearts", "J"),
                    SOONCard("Hearts", "J"),
                    PeachCard("Hearts", "Q"),
                    BBQCard("Hearts", "Q"),
//                   Card("Hearts", "K", "Flying Lightning"),
                    DodgeCard("Hearts", "K"),

                    // Clubs
                    DuelCard("Clubs", "A"),
//                    Card("Clubs", "2", "Eight Trigrams Formation"),
                    AttackCard("Clubs", "2"),
                    BBQCard("Clubs", "3"),
                    AttackCard("Clubs", "3"),
                    BBQCard("Clubs", "4"),
                    AttackCard("Clubs", "4"),
//                    Card("Clubs", "5", "Hex Mark"),
                    AttackCard("Clubs", "5"),
                    AcediaCard("Clubs", "6"),
                    AttackCard("Clubs", "6"),
                    BarbarianInvasionCard("Clubs", "7"),
                    AttackCard("Clubs", "7"),
                    AttackCard("Clubs", "8"),
                    AttackCard("Clubs", "8"),
                    AttackCard("Clubs", "9"),
                    AttackCard("Clubs", "10"),
                    AttackCard("Clubs", "10"),
                    AttackCard("Clubs", "J"),
                    AttackCard("Clubs", "J"),
                    BBQCard("Clubs", "Q"),
//                    Card("Clubs", "Q", "Negation"),
                    BBQCard("Clubs", "K"),
//                    Card("Clubs", "K", "Negation"),

                    // Diamonds
                    DuelCard("Diamonds", "A"),
//                    Card("Diamonds", "A", "Zhuge Crossbow"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "3"),
                    StealingSheepCard("Diamonds", "3"),
                    DodgeCard("Diamonds", "4"),
                    StealingSheepCard("Diamonds", "4"),
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
    fun printDiscardPile() {
        if (discardPile.isEmpty()) {
            println("The discard pile is empty.")
        } else {
            println("Current cards in the discard pile:")
            discardPile.forEachIndexed { index, card ->
                println("${card.Suit} ${card.Number} - ${card.Name}")
            }
            println("Discard Pile Size: ${discardPile.size}")
        }
    }
    fun getDeckSize(): Int{
        return deck.size
    }
}
