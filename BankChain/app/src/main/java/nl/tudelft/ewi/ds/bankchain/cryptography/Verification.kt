package nl.tudelft.ewi.ds.bankchain.cryptography


import java.security.PublicKey

import nl.tudelft.ewi.ds.bankchain.bank.Account

data class Verification(var account: Account, var publicKey: PublicKey)
