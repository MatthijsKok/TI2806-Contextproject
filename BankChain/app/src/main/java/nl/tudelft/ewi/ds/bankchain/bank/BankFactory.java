package nl.tudelft.ewi.ds.bankchain.bank;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;
import nl.tudelft.ewi.ds.bankchain.bank.mock.MockBank;

public class BankFactory {
    private Environment env;

    public BankFactory(Environment v){
        env = v;
    }

    public Bank create(){
        switch (env.Bank) {
            case "Bunq":
                return new BunqBank(env.Url);
            case "Mock":
                return new MockBank();
            default:
                return new MockBank();
        }
    }
}
