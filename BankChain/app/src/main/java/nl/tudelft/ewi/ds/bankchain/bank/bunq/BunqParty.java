package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.util.List;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.UserService;

/**
 * Created by Richard-HP on 15/05/2017.
 * Bunq implementation of a party this can be either a company or person
 */

public class BunqParty implements Party {
    private String name;
    private int id;
    List<Account> accounts;

    public BunqParty(UserService.ListResponse.UserCompany user) {
        id = user.id;
        name = user.name;
    }

    public BunqParty(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<Account> getAccounts() {
        return accounts;
    }
}
