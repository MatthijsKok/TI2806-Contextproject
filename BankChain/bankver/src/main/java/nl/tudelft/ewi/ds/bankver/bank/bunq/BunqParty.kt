package nl.tudelft.ewi.ds.bankver.bank.bunq

import nl.tudelft.ewi.ds.bankver.bank.Account
import nl.tudelft.ewi.ds.bankver.bank.Party
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.UserService

/**
 * Bunq implementation of a party this can be either a company or person
 */
class BunqParty : Party {
    override val name: String?
    override val id: Int
    override var accounts: List<Account>? = null

    constructor(user: UserService.ListResponse.UserCompany) {
        this.id = user.id
        this.name = user.name
    }

    constructor(name: String, id: Int) {
        this.name = name
        this.id = id
    }
}
