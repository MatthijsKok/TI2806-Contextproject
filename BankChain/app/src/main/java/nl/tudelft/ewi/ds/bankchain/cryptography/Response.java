package nl.tudelft.ewi.ds.bankchain.cryptography;

import java.security.SignatureException;

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
    private String signature;
    private String encryptedResponse;
    private PrivateKey responderPrivateKey;

    public Response(Challenge challenge, PrivateKey responderSK) throws SignatureException {
        this.challenge = challenge;
        this.responderPrivateKey = responderSK;

        decryptChallenge();
        verifyChallengeSignature();
        signResponse();
        encryptResponse();
    }

    private void decryptChallenge() {
        this.secret = this.responderPrivateKey.decodeMessage(challenge.getEncryptedChallenge());
    }

    private boolean verifyChallengeSignature() throws SignatureException {
        if (!challenge.getPublicKey().verifySignedMessage(secret, challenge.getSignature())) {
            throw new SignatureException("The signature of the Challenge is invalid!");
        }
        return true;
    }

    private void signResponse() {
        this.signature = responderPrivateKey.signMessage(secret);
    }

    private void encryptResponse() {
        this.encryptedResponse = this.challenge.getPublicKey().encryptMessage(secret);
    }

    public String getEncryptedResponse() {
        return this.encryptedResponse;
    }

    public String getSignature() {
        return this.signature;
    }

    public PublicKey getPublicKey() {
        return this.responderPrivateKey.getPublicKey();
    }
}
