package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Tools for the Bunq system, used all over.
 *
 * @author Jos Kuijpers
 */
public final class BunqTools {

    private BunqTools(){
    }

    /**
     * Write a string version of given public key, including PEM headers.
     *
     * @param pubkey key to stringify
     * @return string
     */
    public static String publicKeyToString(PublicKey pubkey) {
        return "-----BEGIN PUBLIC KEY-----\n"
                + new String(Base64.encode(pubkey.getEncoded(), 0), StandardCharsets.UTF_8)
                + "-----END PUBLIC KEY-----\n";
    }

    /**
     * Convert a PEM string into a public key.
     * @param string string of the key, possibly with PEM headers
     * @return public key or null on failure to convert
     */
    static PublicKey stringToPublicKey(String string) {
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
    static KeyPair createClientKeyPair(String alias) {
        SecureRandom random = null;
        KeyPair pair = null;
        KeyGenParameterSpec spec;

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            random = SecureRandom.getInstance("SHA1PRNG");

            spec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .setRandomizedEncryptionRequired(true)
                    .build();

            keygen.initialize(spec, random);

            pair = keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // The algorithm is available on all Android versions supported
            // This should never happen
            return null;
        } catch (NoSuchProviderException | InvalidAlgorithmParameterException e) {
            // Both of these exceptions are configuration errors in the hardcoded
            // values used above. Should never happen.
            return null;
        }

        return pair;
    }

    static KeyPair loadClientKeyPair(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");

            // Load store without password
            ks.load(null);

            // Get key without password
            Key key = ks.getKey(alias, null);

            if (key != null && key instanceof PrivateKey) {
                Certificate cert = ks.getCertificate(alias);
                PublicKey pubKey = cert.getPublicKey();

                return new KeyPair(pubKey, (PrivateKey) key);
            } else {
                return null;
            }

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException e) {
            Log.e("BUNQ", e.toString());
        }

        return null;
    }

    static boolean destroyClientKeyPair(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");

            ks.deleteEntry(alias);
        } catch (KeyStoreException e) {
            Log.e("BUNQ", "Failed to destroy keypair: " + e.toString());
            return false;
        }

        return true;
    }
}
