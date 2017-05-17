package nl.tudelft.ewi.ds.bankchain.bank;

/**
 * Created by Richard-HP on 15/05/2017.
 * <p>
 * creates an object that holds the information about a bank account
 */

public interface Account {

    public String getIban();

    public Party getParty();

    public int getId();

}
