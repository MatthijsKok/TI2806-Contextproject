package nl.tudelft.ewi.ds.bankchain.bank;

import java.util.Currency;
import java.util.Date;

public abstract class TransactionAbstract {

    public enum Direction {
        IN,
        OUT
    }

    // id => BunqTransaction

    /**
     * Get direction of the transaction: into the account or
     * out fromt he account.
     *
     * @return in or out
     */
    public abstract Direction getDirection();

    /**
     * Get date of the transaction.
     * @return date, or null when transaction is new.
     */
    public abstract Date getDate();

    /**
     * Get the amount of money transferred, in #currency.
     * @return value of the money (defaults to 0)
     */
    public abstract Float getValue();

    /**
     * Get the currency the value is in.
     * @return currency (defaults to EUR)
     */
    public abstract Currency getCurrency();

    /**
     * Get the counterparty for this transaction
     * @return counterparty object.
     */
    public abstract TransactionCounterparty getCounterparty();

    /**
     * Get the description of the transaction.
     * @return description or null
     */
    public abstract String getDescription();
}
