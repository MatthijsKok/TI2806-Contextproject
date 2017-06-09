package nl.tudelft.ewi.ds.bankchain.bank

interface Party {

    val name: String?
    val id: Int
    val accounts: List<Account>?
}
