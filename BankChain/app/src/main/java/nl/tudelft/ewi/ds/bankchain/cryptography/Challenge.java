package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.Utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Challenge class.
 * A challenge is the following format:
 * "CH:MESSAGE:SIGNATURE:PUBLICKEY"
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
        byte[] vk = ED25519.getPublicKey((EdDSAPrivateKey) privateKey).getEncoded();
        return "CH:" + encodeMessage(cb) + ":" + encodeMessage(sb) + ":" + encodeMessage(vk);
    }

    /**
     * Verifies whether the signature of the Response is valid.
     * @param response The Response String to verify.
     * @return A boolean whether the response is valid.
     */
    public static boolean verifyResponse(String response) {
        String[] responseArray = response.split(":");
        byte[] message = decodeMessage(responseArray[1]);
        byte[] signature = decodeMessage(responseArray[2]);
        byte[] publicKey = decodeMessage(responseArray[3]);
        return responseArray[0].equals("RE") && ED25519.verifySignature(message, signature, publicKey);
    }
}
