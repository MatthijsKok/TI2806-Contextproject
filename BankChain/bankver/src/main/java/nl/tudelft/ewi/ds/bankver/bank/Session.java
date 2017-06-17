package nl.tudelft.ewi.ds.bankver.bank;

public abstract class Session {

    /**
     * Get whether the session is still valid.
     *
     * @return true when session is still valid, false otherwise.
     */
    public abstract boolean isValid();
}
