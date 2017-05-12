package nl.tudelft.ewi.ds.bankchain.cryptography;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import static nl.tudelft.ewi.ds.bankchain.cryptography.Challenge.decodeMessage;
import static nl.tudelft.ewi.ds.bankchain.cryptography.Challenge.encodeMessage;


/**
 * Response class.
 * Verifies a Challenge with a given public key.
 * Generates a Response based on the Challenge.
 */
public class Response {

    /**
     * Verify the signature of the Challenge.
     * @return Whether the signature is valid.
     */
    public static boolean verifyChallenge(String challenge, PublicKey publicKey) {
        String[] challengeArray = challenge.split(":");
        byte[] message = decodeMessage(challengeArray[0]);
        byte[] signature = decodeMessage(challengeArray[1]);
        return ED25519.verifySignature(message, signature, publicKey);
    }

    public static String createResponse(String challenge, PrivateKey privateKey) {
        String[] challengeArray = challenge.split(":");
        byte[] message = decodeMessage(challengeArray[0]);
        byte[] signature = ED25519.createSignature(message, privateKey);
        return encodeMessage(message) + ":" + encodeMessage(signature);
    }
}
