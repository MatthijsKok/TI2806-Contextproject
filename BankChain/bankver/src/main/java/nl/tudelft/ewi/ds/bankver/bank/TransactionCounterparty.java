package nl.tudelft.ewi.ds.bankver.bank;

import nl.tudelft.ewi.ds.bankver.IBAN;

public interface TransactionCounterparty {
    public enum Type {
        IBAN,
        EMAIL
    }

    /**
     * Get the type of counterparty.
     *
     * @return type available in enum
     */
    Type getType();

    /**
     * Get IBAN of the counterparty, if available.
     *
     * @return IBAN, or null
     */
    IBAN getIBAN();

    /**
     * Get e-mail of the counterparty, if available.
     *
     * @return Email, or null
     */
    String getEmail();

    /**
     * Get name of the counterparty, if available
     *
     * @return Name, or null
     */
    String getName();
}
