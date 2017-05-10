package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public final class BunqTools {
    /**
     * Write a string version of given public key, including PEM headers.
     *
     * @param pubkey key to stringify
     * @return string
     */
    public static String publicKeyToString(PublicKey pubkey) {
        return "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(pubkey.getEncoded(), 0)) + "-----END PUBLIC KEY-----\n";
    }

    /**
     * Convert a PEM string into a public key.
     * @param string string of the key, possibly with PEM headers
     * @return public key or null on failure to convert
     */
    // TODO: write tests that encode into each other, and make it more robus
    public static PublicKey stringToPublicKey(String string) {
        // Remove headers
        String publicKey = string.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKey = publicKey.replace("-----END PUBLIC KEY-----", "");

        try {
            byte[] data = Base64.decode(publicKey, Base64.DEFAULT);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");

            return fact.generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Create a new keypair
     *
     * @return keypair or null if no key could be created
     */
    public static KeyPair createClientKeyPair() {
        SecureRandom random = null;
        KeyPair pair;

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            random = SecureRandom.getInstance("SHA1PRNG");

            // Bunq requires key size of 2048 bits
            keygen.initialize(2048, random);

            pair = keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        }

        return pair;
    }
}
