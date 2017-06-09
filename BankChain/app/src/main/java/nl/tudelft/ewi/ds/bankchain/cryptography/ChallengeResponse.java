package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.Utils;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;

import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.createSignature;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.verifySignature;

/**
 * Challenge class.
 * A challenge is the following format:
 * "CH:MESSAGE:SIGNATURE"
 * where CH is a magic string to signify that this transaction is part of our protocol,
 * MESSAGE is the randomly generated string that will be signed,
 * and SIGNATURE is the signature of the MESSAGE.
 */
public final class ChallengeResponse {

    private static final int SEED_LENGTH = 4;

    private static final int DESCRIPTION_LENGTH = 140;
    private static final int SECRET_LENGTH = 8;
    private static final int SIGNATURE_LENGTH = 128;

    private static final String CHALLENGE_TOKEN = "CH";
    private static final String RESPONSE_TOKEN = "RE";
    private static final String SPLIT = ":";

    private static byte[] generateChallengeBytes() {
        return new SecureRandom().generateSeed(SEED_LENGTH);
    }

    private static String encode(byte[] challenge) {
        return Utils.bytesToHex(challenge);
    }

    private static byte[] decode(String challenge) {
        return Utils.hexToBytes(challenge);
    }

    private static boolean isChallenge(String description) {
        return isValidDescriptionFormat(description) && description.split(SPLIT)[0].equals(CHALLENGE_TOKEN);
    }

    public static boolean isValidChallenge(String description, PublicKey publicKey) {
        return isChallenge(description) && verifyChallenge(description, publicKey);
    }

    public static String createChallenge(PrivateKey privateKey) throws SignatureException, InvalidKeyException {
        byte[] cb = generateChallengeBytes();
        byte[] sb = createSignature(cb, privateKey);
        return CHALLENGE_TOKEN + SPLIT + encode(cb) + SPLIT + encode(sb);
    }

    /**
     * Verify the signature of the Challenge.
     * @return Whether the signature is valid.
     */
    private static boolean verifyChallenge(String challenge, PublicKey publicKey) {
        String[] challengeArray = challenge.split(SPLIT);
        byte[] message = decode(challengeArray[1]);
        byte[] signature = decode(challengeArray[2]);
        return challengeArray[0].equals(CHALLENGE_TOKEN) && verifySignature(message, signature, publicKey);
    }

    private static boolean isResponse(String description) {
        return isValidDescriptionFormat(description) && description.split(SPLIT)[0].equals(RESPONSE_TOKEN);
    }

    public static boolean isValidResponse(String description, PublicKey publicKey) {
        return isResponse(description) && ChallengeResponse.verifyResponse(description, publicKey);
    }

    public static String createResponse(String challenge, PrivateKey privateKey)
            throws SignatureException, InvalidKeyException {
        String[] challengeArray = challenge.split(SPLIT);
        byte[] message = decode(challengeArray[1]);
        byte[] signature = createSignature(message, privateKey);
        return RESPONSE_TOKEN + SPLIT + encode(message) + SPLIT + encode(signature);
    }

    /**
     * Verifies whether the signature of the Response is valid.
     * @param response The Response String to verify.
     * @return A boolean whether the response is valid.
     */
    private static boolean verifyResponse(String response, PublicKey publicKey) {
        String[] responseArray = response.split(SPLIT);
        byte[] message = decode(responseArray[1]);
        byte[] signature = decode(responseArray[2]);
        return responseArray[0].equals(RESPONSE_TOKEN) && verifySignature(message, signature, publicKey);
    }

    /**
     * Checks if the given string is the correct format for our system.
     * Our current format:
     * CH:ChallengeString:Signature => for a challenge, and
     * RE:ChallengeString:Signature => for a response.
     *
     * This does not check for the validity of the message or it's signature.
     *
     * @param description The String to check.
     * @return Whether the given string is in the valid format.
     */
    public static boolean isValidDescriptionFormat(String description) {
        String[] array = description.split(SPLIT);
        return ((description.length() == DESCRIPTION_LENGTH) &&
                (description.split(SPLIT).length == 3) &&
                (description.split(SPLIT)[0].equals(CHALLENGE_TOKEN) ||
                        description.split(SPLIT)[0].equals(RESPONSE_TOKEN)) &&
                (array[1].length() == SECRET_LENGTH) &&
                (array[2].length() == SIGNATURE_LENGTH));
    }

}
