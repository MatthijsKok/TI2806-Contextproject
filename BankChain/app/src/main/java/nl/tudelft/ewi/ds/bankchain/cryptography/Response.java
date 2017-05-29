package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;

import java.security.PrivateKey;

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
    public static boolean verifyChallenge(String challenge) {
        String[] challengeArray = challenge.split(":");
        byte[] message = decodeMessage(challengeArray[1]);
        byte[] signature = decodeMessage(challengeArray[2]);
        byte[] publicKey = decodeMessage(challengeArray[3]);
        return challengeArray[0].equals("CH") && ED25519.verifySignature(message, signature, publicKey);
    }

    public static String createResponse(String challenge, PrivateKey privateKey) {
        String[] challengeArray = challenge.split(":");
        byte[] message = decodeMessage(challengeArray[0]);
        byte[] signature = ED25519.createSignature(message, privateKey);
        byte[] vk = ED25519.getPublicKey((EdDSAPrivateKey) privateKey).getEncoded();
        return "RE:" + encodeMessage(message) + ":" + encodeMessage(signature) + ":" + encodeMessage(vk);
    }
}
