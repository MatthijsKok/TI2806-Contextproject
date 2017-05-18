package nl.tudelft.ewi.ds.bankchain.bank;

import java.util.Currency;
import java.util.Date;

public interface Transaction {

    /**
     * Get date of the transaction.
     *
     * @return date, or null when transaction is new.
     */
    public Date getDate();

    /**
     * Get the amount of money transferred, in #currency.
     *
     * @return value of the money (defaults to 0)
     */
    public Float getValue();

    /**
     * Get the currency the value is in.
     *
     * @return currency (defaults to EUR)
     */
    public Currency getCurrency();

    /**
     * Get the CounterAccount for this transaction
     *
     * @return Account object.
     */
    public Account getCounterAccount();

    /**
     * get the account the money is added to (negative means it's a payment)
     * @return
     */
    public Account getAcount();

    /**
     * Get the description of the transaction.
     *
     * @return description or null
     */
    public String getDescription();
}
