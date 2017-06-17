package nl.tudelft.ewi.ds.bankver.bank.bunq

import java.util.Currency
import java.util.Date

import nl.tudelft.ewi.ds.bankver.bank.Account
import nl.tudelft.ewi.ds.bankver.bank.Transaction
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.PaymentService

/**
 * A transaction from the Bunq bank
 */
class BunqTransaction : Transaction {

    override var id: Int
    override val date: Date
    override val value: Float
    override val description: String
    override val currency: Currency
    override val account: Account
    override val counterAccount: Account


    constructor(payment: PaymentService.ListResponse.Payment, account: Account) {
        this.id = payment.id
        this.date = Date(22111990) //payment.getCreationDate(); TODO fix payment
        this.value = payment.amount.value!!
        this.description = payment.description
        this.currency = payment.amount.currency
        this.account = account
        this.counterAccount = BunqAccount(payment.counterParty.iban, -1, BunqParty(payment.counterParty.name, -1))
    }

    /**
     * used to create an outgoing transaction
     * @param amount
     * @param account        source of the money
     * @param counterAccount recipient
     * @param currency
     * @param description
     */
    constructor(amount: Float, account: Account, counterAccount: Account, currency: Currency, description: String) {
        this.id = -1
        this.date = Date(0)
        this.value = amount
        this.description = description
        this.currency = currency
        this.account = account
        this.counterAccount = counterAccount
    }

    fun convertToRequest(): PaymentService.PostRequest {
        val p = PaymentService.PostRequest()
        val a = PaymentService.Amount()
        val c = PaymentService.CounterpartyAlias()
        c.name = counterAccount.party.name
        c.value = counterAccount.iban
        a.currency = "EUR"
        a.value = java.lang.Float.toString(Math.abs(value)) //usually the outgoing amount should be negative this is the exception so it's covered here
        p.description = description
        p.amount = a
        p.counterparty_alias = c
        return p
    }
}
