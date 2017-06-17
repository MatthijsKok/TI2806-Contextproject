package nl.tudelft.ewi.ds.bankver.bank

interface Party {

    val name: String?
    val id: Int
    val accounts: List<Account>?
}
