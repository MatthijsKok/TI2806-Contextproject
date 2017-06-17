package nl.tudelft.ewi.ds.bankver.bank;

import android.content.Context;
import android.support.annotation.NonNull;

import nl.tudelft.ewi.ds.bankver.Environment;
import nl.tudelft.ewi.ds.bankver.bank.bunq.BunqBank;

/**
 * A factory that creates a new bank based on current app configuration.
 *
 * @author Richard
 */
public class BankFactory {
    private Environment env;
    private Context appContext;

    public BankFactory(@NonNull Environment env, @NonNull Context appContext) {
        this.env = env;
        this.appContext = appContext;
    }

    public Bank create() {
        switch (env.getBank()) {
            case "Bunq":
                return new BunqBank(appContext, env.getBankUrl(), env.getBankApiKey());
            default:
                return null;
        }
    }
}
