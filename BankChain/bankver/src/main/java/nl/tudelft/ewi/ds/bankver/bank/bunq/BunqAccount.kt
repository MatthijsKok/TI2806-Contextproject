package nl.tudelft.ewi.ds.bankver.bank.bunq

import java8.util.Optional
import nl.tudelft.ewi.ds.bankver.bank.Account
import nl.tudelft.ewi.ds.bankver.bank.Party
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.AccountService

import java8.util.stream.StreamSupport.*
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.AccountService.*

/**
 * Bunq implementation of account
 */
class BunqAccount : Account {

    override val party: Party
    override val iban: String
    override val id: Int

    constructor(account: ListResponse.BunqBankAccount, party: Party) {
        //retreive IBAN from list of aliases
        val ac = stream<ListResponse.Alias>(account.aliases)
                .filter { c -> c.type == "IBAN" }
                .findFirst()
        id = account.id
        this.party = party
        iban = ac.get().value
    }

    constructor(iban: String, id: Int, party: Party) {
        this.iban = iban
        this.party = party
        this.id = id
    }

    override fun toString(): String {
        return "BunqAccount(party=$party, iban='$iban', id=$id)"
    }
}
