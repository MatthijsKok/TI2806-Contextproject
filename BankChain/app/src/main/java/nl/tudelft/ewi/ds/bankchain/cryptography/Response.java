package nl.tudelft.ewi.ds.bankchain.cryptography;

import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PrivateKey;
import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PublicKey;

/**
 * Response class.
 * Will be able to decrypt a Challenge with a given private key.
 * Will also be able to generate a Response based on the Challenge.
 */
public class Response {

    private Challenge challenge;
    private String secret;
    private String encryptedResponse;
    private PrivateKey responderPrivateKey;

    public Response(Challenge challenge, PrivateKey responderSK) {
        this.challenge = challenge;
        this.responderPrivateKey = responderSK;

        this.secret = decryptChallenge();
        this.encryptedResponse = encryptResponse();
    }

    public String getEncryptedResponse() {
        return this.encryptedResponse;
    }

    public PublicKey getPublicKey() {
        return this.responderPrivateKey.getPublicKey();
    }

    private String decryptChallenge() {
        return this.responderPrivateKey.decodeMessage(challenge.getEncryptedChallenge());
    }

    private String encryptResponse() {
        return challenge.getPublicKey().encryptMessage(secret);
    }
}
