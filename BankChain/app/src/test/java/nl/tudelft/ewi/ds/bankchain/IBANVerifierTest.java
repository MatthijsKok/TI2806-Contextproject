package nl.tudelft.ewi.ds.bankchain;

import org.junit.Test;

import static nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier.isValidIBAN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IBANVerifierTest {
    @Test
    public void verifyFalse() throws Exception {
        assertFalse(isValidIBAN("helloworld"));
    }

    @Test
    public void verifyTrue() throws Exception {
        assertTrue(isValidIBAN("GB82WEST12345698765432"));
    }

    @Test
    public void verifyTrueIng() throws Exception {
        assertTrue(isValidIBAN("NL29INGB0006108849"));
    }

    @Test
    public void verifyEmpty() throws Exception {
        assertFalse(isValidIBAN(""));
    }

    @Test
    public void verifyGerman() throws Exception {
        assertTrue(isValidIBAN("DE89370400440532013000"));
    }

    @Test
    public void verifySingleDigitError() {
        assertFalse(isValidIBAN("NL29INGB0006105849"));
    }

    @Test
    public void testoversized() {
        assertFalse(isValidIBAN("123456789123456789123456789123456789"));
    }

    @Test
    public void testLongestBigInt() {
        assertFalse(isValidIBAN("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"));
    }
}