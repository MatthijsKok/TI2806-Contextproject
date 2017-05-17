package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Party;

/**
 * Created by Richard-HP on 15/05/2017.
 */

public class BunqAccount implements Account {

    private Party party;
    private String iban;

    @Override
    public String getIban() {
        return null;
    }

    public Party getParty() {
        return null;
    }
}
