package nl.tudelft.ewi.ds.bankver.bank;

import java.math.BigInteger;

/**
 * Verify IBANs
 *
 * Only uses an algorithm to verify the IBAN can exist. It does not
 * verify that an actual account is connected.
 *
 * @author Richard
 */
public class IBANVerifier {

    /**
     * Verify given IBAN string.
     * @param iban string of the IBAN
     * @return true when valid, false otherwise
     */
    public static boolean isValidIBAN(String iban) {
        // Warning: iban length differs per country.

        // Wikipedia https://en.wikipedia.org/wiki/International_Bank_Account_Number#Algorithms:
        /*
        An IBAN is validated by converting it into an integer and performing a basic mod-97
        operation (as described in ISO 7064) on it. If the IBAN is valid, the remainder equals 1.
        [Note 1] The algorithm of IBAN validation is as follows:[8]

        Move the four initial characters to the end of the string
        Replace each letter in the string with two digits, thereby expanding the string, where
        A = 10, B = 11, ..., Z = 35
        Interpret the string as a decimal integer and compute the remainder of that number on
        division by 97
        If the remainder is 1, the check digit test is passed and the IBAN might be valid.

        Example (fictitious United Kingdom bank, sort code 12-34-56, account number 98765432):

        • IBAN:        GB82 WEST 1234 5698 7654 32
        • Rearrange:        W E S T12345698765432 G B82
        • Convert to integer:        3214282912345698765432161182
        • Compute remainder:        3214282912345698765432161182    mod 97 = 1
        */                      //321428291234569876543216118

        // Check if iban is within the allowed length paramaters
        if (iban.length() < 15 || iban.length() > 34) {
            return false;
        }

        // Complete all operations that allow for 11 proof
        BigInteger converted = converter(iban);

        // Perform 11 proof
        return  converted.mod(BigInteger.valueOf(97)).equals(BigInteger.ONE);
    }

    private static BigInteger converter(String iban) {
        // take first foout characters and put them at the back
        String arranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder result = new StringBuilder();

        //interchange any letter with the corresponding digit
        for (char c : arranged.toCharArray()) {

            if (c < 65) {
                result.append(c);
            } else {
                result.append((int) c - 55);
            }
        }
        //return value as a number
        return new BigInteger(result.toString());
    }
}
