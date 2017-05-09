package nl.tudelft.ewi.ds.bankchain.bank.mock;

import android.support.annotation.Nullable;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;

/**
 * Created by Richard-HP on 09/05/2017.
 */

public class MockBank extends Bank {

    public MockBank(){

    }


    @Override
    public void createSession() {

    }

    @Nullable
    @Override
    public Session getCurrentSession() {
        return null;
    }
}
