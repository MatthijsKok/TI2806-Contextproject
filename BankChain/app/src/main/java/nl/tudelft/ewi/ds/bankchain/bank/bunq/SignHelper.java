package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

class SignHelper {

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
    String sign(String message) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(this.clientPrivateKey);

            s.update(message.getBytes(StandardCharsets.UTF_8));

            byte[] signature = s.sign();

            return new String(signature, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verify a signature from the server
     */
    boolean verify(String message, String signature) {
        try {
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(this.serverPublicKey);

            s.update(message.getBytes(StandardCharsets.UTF_8));

            return s.verify(signature.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return false;
    }
}
