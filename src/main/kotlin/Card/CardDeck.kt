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
                    LightningCard("Spades", "A"),
                    DuelCard("Spades", "A"),
                    EightTrigramsCard("Spades", "2"),
//                    WeaponCard("Clubs", "2", "Yin-Yang Swords"),
                    BBQCard("Spades", "3"),
                    StealingSheepCard("Spades", "3"),
                    BBQCard("Spades", "4"),
                    StealingSheepCard("Spades", "4"),
//                    WeaponCard("Clubs", "5", "Green Dragon Blade"),
                    AcediaCard("Spades", "6"),
//                    WeaponCard("Clubs", "6", "Blue Steel Blade"),
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
//                    WeaponCard("Clubs", "7", "Serpent Spear"),
                    BBQCard("Spades", "Q"),
                    HorseCard("Spades", "K", "Ferghana Horse", HorseType.MINUS),
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
//                    WeaponCard("Hearts", "5", "Kirin Bow"),
                    HorseCard("Hearts", "5", "Red Hare", HorseType.PLUS),
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
                    HorseCard("Clubs", "5", "Hex Mark", HorseType.MINUS),
                    DodgeCard("Hearts", "K"),
                    HorseCard("Hearts", "K", "Flying Lightning", HorseType.PLUS),

                    // Clubs
                    WeaponCard("Clubs", "A", "Zhuge Crossbow"),
                    DuelCard("Clubs", "A"),
                    EightTrigramsCard("Clubs", "2"),
                    AttackCard("Clubs", "2"),
                    BBQCard("Clubs", "3"),
                    AttackCard("Clubs", "3"),
                    BBQCard("Clubs", "4"),
                    AttackCard("Clubs", "4"),
//                    Card("Clubs", "5", "Hex Mark"),
                    AttackCard("Clubs", "5"),
                    HorseCard("Clubs", "5", "Shadowrunner", HorseType.PLUS),
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
                    WeaponCard("Diamonds", "A", "Zhuge Crossbow"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "2"),
                    DodgeCard("Diamonds", "3"),
                    StealingSheepCard("Diamonds", "3"),
                    DodgeCard("Diamonds", "4"),
                    StealingSheepCard("Diamonds", "4"),
                    DodgeCard("Diamonds", "5"),
//                    WeaponCard("Diamonds", "5", "Rock Cleaving Axe"),
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
//                    WeaponCard("Diamonds", "Q", "Sky Piercing Halberd"),
                    HorseCard("Diamonds", "K", "Violet Stallion", HorseType.MINUS),
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
