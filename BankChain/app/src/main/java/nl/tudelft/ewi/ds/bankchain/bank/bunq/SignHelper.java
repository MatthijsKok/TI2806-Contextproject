package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignHelper {

    private PrivateKey clientPrivateKey;
    private PublicKey clientPublicKey;
    private PublicKey serverPublicKey;

    /**
     * Create a new signing object with client and server keys
     * @param clientPrivateKey Private key of the client
     * @param clientPublicKey Public key of the client
     * @param serverPublicKey Public key of the server
     */
    SignHelper(PrivateKey clientPrivateKey, PublicKey clientPublicKey, PublicKey serverPublicKey) {
        this.clientPrivateKey = clientPrivateKey;
        this.clientPublicKey = clientPublicKey;
        this.serverPublicKey = serverPublicKey;
    }

    SignHelper(KeyPair clientKeyPair, PublicKey serverPublicKey) {
        this.clientPrivateKey = clientKeyPair.getPrivate();
        this.clientPublicKey = clientKeyPair.getPublic();
        this.serverPublicKey = serverPublicKey;
    }

    /**
     * Sign a message to be sent to the server
     */
    public String sign(String message) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(clientPrivateKey);

            // Load bytes into the signing system
            s.update(message.getBytes(StandardCharsets.UTF_8));

            byte[] signature = s.sign();
            byte[] encoded = Base64.encode(signature, Base64.DEFAULT | Base64.NO_WRAP);

            return new String(encoded, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verify a signature from the server
     */
    public boolean verify(String message, String signature) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(serverPublicKey);

            // Convert base64 signature into binary bytes
            byte[] encodedSign = signature.getBytes(StandardCharsets.UTF_8);
            byte[] decodedSign = Base64.decode(encodedSign, Base64.DEFAULT);

            s.update(message.getBytes(StandardCharsets.UTF_8));

            return s.verify(decodedSign);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return false;
    }
}
