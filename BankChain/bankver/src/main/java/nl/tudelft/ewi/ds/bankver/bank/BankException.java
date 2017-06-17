package nl.tudelft.ewi.ds.bankver.bank;


import android.support.annotation.NonNull;

/**
 * @author Jos Kuijpers
 */
public abstract class BankException extends Exception {
    static final long serialVersionUID = -3387923057392367948L;

    /**
     * Set the bank.
     *
     * Used for getting more exception information.
     *
     * @param bank bank object
     */
    public abstract void setBank(@NonNull Bank bank);
}
