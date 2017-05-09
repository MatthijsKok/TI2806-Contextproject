package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public final class BunqTools {
    public static String publicKeyToString(PublicKey pubkey) {
        return "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(pubkey.getEncoded(), 0)) + "-----END PUBLIC KEY-----\n";
    }

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

}
