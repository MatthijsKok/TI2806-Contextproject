package nl.tudelft.ewi.ds.bankchain;

import android.support.annotation.NonNull;
import android.util.Log;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;

/**
 * Main business logic class: forms a layer between the subsystems and the GUI.
 *
 * @author Jos Kuijpers
 */
public class Manager {
    private static Manager instance = null;

    private Environment environment;
    private Bank bank;

    private Manager() {}

    /**
     * Get the singleton instance.
     *
     * @return instance
     */
    @NonNull
    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }

        return instance;
    }

    /**
     * Set the environment.
     *
     * When this changes, a new bank is created.
     *
     * @param environment environment object
     */
    public void setEnvironment(Environment environment) {
        if (this.environment != environment) {
            this.environment = environment;

            bank = new BankFactory(environment).create();
        }
    }

    /**
     * Get the bank.
     *
     * Always the same so session is kept.
     *
     * @return bank
     */
    @NonNull
    public Bank getBank() {
        if (bank == null) {
            throw new IllegalStateException("bank == null: set the manager environment first");
        }

        return bank;
    }

    /**
     * Get the bank, with a session.
     *
     * When the future completes successfully, there is always a session open.
     * The session is kept open until the session is destroyed.
     *
     * @return future with a bank object.
     */
    public CompletableFuture<Bank> getBankWithSession() {
        if (bank == null) {
            throw new IllegalStateException("bank == null: set the manager environment first");
        }

        if (bank.getCurrentSession() != null) {
            Log.d("MANAGER", "new session");
            return CompletableFuture.completedFuture(bank);
        }

        Log.d("MANAGER", "old session");
        return bank.createSession().thenApply(session -> bank);
    }
}
