package nl.tudelft.ewi.ds.bankchain.cryptography;

import java.security.SignatureException;

import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PrivateKey;
import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PublicKey;

/**
 * Response class.
 * Decrypts a Challenge with a given private key.
 * Generates a Response based on the Challenge.
 */
public class Response {

    private Challenge challenge;
    private String secret;
    private String signature;
    private String encryptedResponse;
    private PrivateKey responderPrivateKey;

    /**
     * Create a Response object to the given Challenge.
     * Stores a private key.
     * @param challenge The Challenge to respond to.
     * @param responderSK Your private key to sign the Response with.
     * @throws SignatureException If the Challenge signature is invalid. This is a sign of malicious activity.
     */
    public Response(Challenge challenge, PrivateKey responderSK) throws SignatureException {
        this.challenge = challenge;
        this.responderPrivateKey = responderSK;

        decryptChallenge();
        verifyChallengeSignature();
        signResponse();
        encryptResponse();
    }

    /**
     * Decrypt the Challenge's secret with your private key.
     */
    private void decryptChallenge() {
        this.secret = this.responderPrivateKey.decodeMessage(challenge.getEncryptedChallenge());
    }

    /**
     * Verify the signature of the Challenge.
     * @return Whether the signature is valid.
     * @throws SignatureException If the Challenge signature is invalid. This is a sign of malicious activity.
     */
    private boolean verifyChallengeSignature() throws SignatureException {
        if (!challenge.getPublicKey().verifySignedMessage(secret, challenge.getSignature())) {
            throw new SignatureException("The signature of the Challenge is invalid!");
        }
        return true;
    }

    /**
     * Sign the secret of this Response with the private key.
     */
    private void signResponse() {
        this.signature = responderPrivateKey.signMessage(secret);
    }

    /**
     * Encrypt the secret of this Response with the Challengers public key.
     */
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
