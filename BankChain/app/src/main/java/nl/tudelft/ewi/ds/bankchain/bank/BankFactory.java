package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.NonNull;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;
import nl.tudelft.ewi.ds.bankchain.bank.mock.MockBank;

/**
 * A factory that creates a new bank based on current app configuration.
 *
 * @author Richard
 */
public class BankFactory {
    private Environment env;

    public BankFactory(@NonNull Environment env) {
        this.env = env;
    }

    public Bank create() {
        switch (env.getBank()) {
            case "Bunq":
                return new BunqBank(env.getBankUrl(), env.getBankApiKey());
            case "Mock":
                return new MockBank();
            default:
                return null;
        }
    }
}
