package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.util.Optional;
import java.util.stream.StreamSupport;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.AccountService;

/**
 * Bunq implementation of account
 * Created by Richard-HP on 15/05/2017.
 */

public class BunqAccount implements Account {

    private Party party;
    private String iban;
    private int id;

    public BunqAccount(AccountService.ListResponse.BunqBankAccount account, Party party) {
        //retreive IBAN from list of aliases
        java8.util.Optional<AccountService.ListResponse.Alias> ac = java8.util.stream.StreamSupport.stream(account.aliases)
                .filter((c) -> c.type.equals("IBAN"))
                .findFirst();
        id = account.id;
        this.party = party;
        iban = ac.get().value;
    }

    public BunqAccount(String iban, int id, Party party) {
        this.iban = iban;
        this.party = party;
        this.id = id;
    }

    @Override
    public String getIban() {
        return iban;
    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public int getId() {
        return id;
    }


}
