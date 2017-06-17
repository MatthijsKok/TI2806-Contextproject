package nl.tudelft.ewi.ds.bankver.bank

/**
 * creates an object that holds the information about a bank account
 */
interface Account {

    val iban: String
    val party: Party
    val id: Int
}
