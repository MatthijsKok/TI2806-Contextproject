package nl.tudelft.ewi.ds.bankchain.bank.bunq;

/**
 * A session store.
 *
 * Used to give a permanent reference to the HTTP tools.
 * It can then always access the latest session.
 */
public class SessionStore {
    private BunqSession session = null;

    public BunqSession get() {
        return session;
    }

    public void set(BunqSession session) {
        this.session = session;
    }

    public boolean hasSession() {
        return this.session != null;
    }

    public boolean hasValidSession() {
        return this.hasSession() && this.session.isValid();
    }
}
