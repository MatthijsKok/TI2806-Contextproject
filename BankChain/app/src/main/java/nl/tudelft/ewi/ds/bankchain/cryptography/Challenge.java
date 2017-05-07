package nl.tudelft.ewi.ds.bankchain.cryptography;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.SignatureException;

import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PrivateKey;
import nl.tudelft.ewi.ds.bankchain.cryptography.keys.PublicKey;

/**
 * Challenge class.
 * Will be able to encrypt a given random string with a public key.
 * Will also be able to check any Response on it's Challenge.
 */
public class Challenge {

    private static final int SEED_LENGTH = 16;
    private byte[] seed;
    private String secret;
    private String signature;
    private String encryptedSecret;
    private PrivateKey challengerPrivateKey;

    public Challenge(PrivateKey challengerPrivateKey, PublicKey responderPublicKey) {
        this.challengerPrivateKey = challengerPrivateKey;

        generateChallenge();
        signChallenge();
        encryptChallenge(responderPublicKey);
    }

    private void generateChallenge() {
        this.seed = new SecureRandom().generateSeed(SEED_LENGTH);
        this.secret = Base64.encodeToString(seed, Base64.NO_WRAP);
    }

    private void signChallenge() {
        this.signature = challengerPrivateKey.signMessage(secret);
    }

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

    public boolean verifyResponse(Response response) {
        try {
            verifyResponseSignature(response);
        } catch (SignatureException e) {
            return false;
        }
        return secret.equals(challengerPrivateKey.decodeMessage(response.getEncryptedResponse()));
    }

    private boolean verifyResponseSignature(Response response) throws SignatureException {
        if (!response.getPublicKey().verifySignedMessage(secret, response.getSignature())) {
            throw new SignatureException("The signature of the Response is invalid!");
        }
        return true;
    }
}
