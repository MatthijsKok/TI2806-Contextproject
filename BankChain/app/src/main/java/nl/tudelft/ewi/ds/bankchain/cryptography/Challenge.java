package nl.tudelft.ewi.ds.bankchain.cryptography;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.SignatureException;

import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PrivateKey;
import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PublicKey;

/**
 * Challenge class.
 * Signs a given random string with a private key, and encrypts it with a responder's public key.
 * Will also be able to check any Response on its Challenge.
 */
public class Challenge {

    private static final int SEED_LENGTH = 16;
    private byte[] seed;
    private String secret;
    private String signature;
    private String encryptedSecret;
    private PrivateKey challengerPrivateKey;

    /**
     * Create a Challenge.
     * This will generate a secret, sign it and encrypt it with the responders public key.
     * @param challengerPrivateKey
     * @param responderPublicKey The responders public key to encrypt the secret with.
     */
    public Challenge(PrivateKey challengerPrivateKey, PublicKey responderPublicKey) {
        this.challengerPrivateKey = challengerPrivateKey;

        generateChallenge();
        signChallenge();
        encryptChallenge(responderPublicKey);
    }

    /**
     * Uses SecureRandom to generate a seed.
     * This seed is encoded to the secret as a base64 String.
     */
    private void generateChallenge() {
        this.seed = new SecureRandom().generateSeed(SEED_LENGTH);
        this.secret = Base64.encodeToString(seed, Base64.NO_WRAP);
    }

    /**
     * Create a signature of the secret using your private and store it with the object.
     */
    private void signChallenge() {
        this.signature = challengerPrivateKey.signMessage(secret);
    }

    /**
     * Encrypt the secret using the challengers public key.
     * @param responderPublicKey The challengers public key.
     */
    private void encryptChallenge(PublicKey responderPublicKey) {
        this.encryptedSecret = responderPublicKey.encryptMessage(secret);
    }

    public String getEncryptedChallenge() {
        return this.encryptedSecret;
    }

    public String getSignature() {
        return this.signature;
    }

    public PublicKey getPublicKey() {
        return this.challengerPrivateKey.getPublicKey();
    }

    /**
     * Verifies whether the signature of the Response is valid,
     * and the decrypted secret equals our secret.
     * @param response The Response object to verify.
     * @return A boolean whether the response is valid.
     */
    public boolean verifyResponse(Response response) {
        try {
            verifyResponseSignature(response);
        } catch (SignatureException e) {
            return false;
        }
        return secret.equals(challengerPrivateKey.decodeMessage(response.getEncryptedResponse()));
    }

    /**
     * Verifies the signature of the response.
     * @param response The Response to verify the signature of
     * @return Whether the signature is valid.
     * @throws SignatureException If the signature is invalid.
     * Invalid signatures are signs of malicious activity and therefore throw an Exception.
     */
    private boolean verifyResponseSignature(Response response) throws SignatureException {
        if (!response.getPublicKey().verifySignedMessage(secret, response.getSignature())) {
            throw new SignatureException("The signature of the Response is invalid!");
        }
        return true;
    }
}
