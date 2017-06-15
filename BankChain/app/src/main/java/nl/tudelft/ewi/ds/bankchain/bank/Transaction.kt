package nl.tudelft.ewi.ds.bankchain.bank

import java.util.Currency
import java.util.Date

interface Transaction {

    val date: Date
    val value: Float
    val currency: Currency
    val counterAccount: Account
    val account: Account
    val description: String
    val id: Int
}
