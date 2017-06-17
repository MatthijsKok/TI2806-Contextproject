package nl.tudelft.ewi.ds.bankver.cryptography


import java.security.PublicKey

import nl.tudelft.ewi.ds.bankver.bank.Account

data class Verification(var account: Account, var publicKey: PublicKey)
