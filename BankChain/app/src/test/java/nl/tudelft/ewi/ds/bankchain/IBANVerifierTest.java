package nl.tudelft.ewi.ds.bankchain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Richard-HP on 09/05/2017.
 */
public class IBANVerifierTest {
    @Test
    public void verifyFalse() throws Exception {
        assertFalse(IBANVerifier.verify("helloworld"));
    }

    @Test
    public void  verifyTrue() throws Exception {
        assertTrue(IBANVerifier.verify("GB82WEST12345698765432"));
    }

    @Test
    public void  verifyTrueIng() throws Exception {
        assertTrue(IBANVerifier.verify("NL29INGB0006108849"));
    }

    @Test
    public void verifyEmpty() throws Exception {
        assertFalse(IBANVerifier.verify(""));
    }

    @Test
    public void verifyGerman() throws  Exception {
        assertTrue(IBANVerifier.verify("DE89370400440532013000"));
    }

    @Test
    public void verifySingleDigitError() {
        assertFalse(IBANVerifier.verify("NL29INGB0006105849"));
    }

    @Test
    public void testoversized() {
        assertFalse(IBANVerifier.verify("123456789123456789123456789123456789"));
    }

    @Test
    public void testLongestBigInt() {
        assertFalse(IBANVerifier.verify("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"));
    }
}