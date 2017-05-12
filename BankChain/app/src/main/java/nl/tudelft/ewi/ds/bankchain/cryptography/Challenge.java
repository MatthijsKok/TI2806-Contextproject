package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.Utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Challenge class.
 * A challenge is the following:
 * "MESSAGE:SIGNATURE"
 */
public final class Challenge {

    private static final int CHALLENGE_LENGTH = 40;

    public static byte[] generateChallengeBytes() {
        return new SecureRandom().generateSeed(CHALLENGE_LENGTH);
    }

    public static String encodeMessage(byte[] challenge) {
        return Utils.bytesToHex(challenge);
    }

    public static byte[] decodeMessage(String challenge) {
        return Utils.hexToBytes(challenge);
    }

    public static String createChallenge(PrivateKey privateKey) {
        byte[] cb = generateChallengeBytes();
        byte[] sb = ED25519.createSignature(cb, privateKey);
        return encodeMessage(cb) + ":" + encodeMessage(sb);
    }

    /**
     * Verifies whether the signature of the Response is valid.
     * @param response The Response String to verify.
     * @param publicKey The public key to verify the signature with.
     * @return A boolean whether the response is valid.
     */
    public static boolean verifyResponse(String response, PublicKey publicKey) {
        String[] responseArray = response.split(":");
        byte[] message = decodeMessage(responseArray[0]);
        byte[] signature = decodeMessage(responseArray[1]);
        return ED25519.verifySignature(message, signature, publicKey);
    }
}
