package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;

public final class BunqBank extends Bank {

    @Override
    public Session createSession() {
        BunqSession session = new BunqSession();

        session.openSession();

        return session;
    }
}
