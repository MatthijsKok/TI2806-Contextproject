package nl.tudelft.ewi.ds.bankver.bank;

public interface Session {

    /**
     * Get whether the session is still valid.
     *
     * @return true when session is still valid, false otherwise.
     */
    boolean isValid();
}
