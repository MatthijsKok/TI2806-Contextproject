package nl.tudelft.ewi.ds.bankchain.bank;

public abstract class TransactionCounterparty {
    public enum Type {
        IBAN,
        EMAIL
    }

    /**
     * Get the type of counterparty.
     *
     * @return type available in enum
     */
    public abstract Type getType();

    /**
     * Get IBAN of the counterparty, if available.
     *
     * @return IBAN, or null
     */
    public abstract String getIBAN();

    /**
     * Get e-mail of the counterparty, if available.
     *
     * @return Email, or null
     */
    public abstract String getEmail();

    /**
     * Get name of the counterparty, if available
     *
     * @return Name, or null
     */
    public abstract String getName();

    // boolean isIBANValid() { return (new IBANVerifier()).verify(this.iban)}
}
