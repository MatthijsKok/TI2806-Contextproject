package nl.tudelft.ewi.ds.bankver;

import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

/**
 * A class that can contain only a valid IBAN.
 *
 * Immutable
 *
 * @author Jos Kuijpers
 */
public class IBAN {
    private String value;

    /**
     * Create a new immutable IBAN that is always valid.
     *
     * @param value IBAN as string
     */
    public IBAN(String value) {
        setValue(value);
    }

    private void setValue(String value) {
        // Normalize
        value = value.trim().toUpperCase();

        if (!IBANVerifier.isValidIBAN(value)) {
            throw new IllegalArgumentException("iban is not valid");
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IBAN && ((IBAN) obj).value.equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
