package nl.tudelft.ewi.ds.bankver;

import java.io.Serializable;
import java.util.Locale;

import nl.tudelft.ewi.ds.bankver.bank.IBANVerifier;

/**
 * A class that can contain only a valid IBAN.
 *
 * Immutable
 *
 * @author Jos Kuijpers
 */
public class IBAN implements Serializable {
    private static final long serialVersionUID = 8346587324687439867L;

    /**
     * Value of the iban
     * @serial
     */
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
        value = value.trim().toUpperCase(Locale.ENGLISH);

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
