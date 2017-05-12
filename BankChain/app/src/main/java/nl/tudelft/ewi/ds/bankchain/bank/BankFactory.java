package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.NonNull;

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
        if (env == null) {
            throw new IllegalArgumentException("env == null");
        }

        this.env = env;
    }

    public Bank create() {
        switch (env.bank) {
            case "Bunq":
                return new BunqBank(env.url, env.apiKey);
            case "Mock":
                return new MockBank();
            default:
                return null;
        }
    }
}
