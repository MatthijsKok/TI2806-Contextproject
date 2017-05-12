package nl.tudelft.ewi.ds.bankchain.cryptography;

import android.util.Base64;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public final class ED25519 {

    private ED25519() {
    }

    private static final int SEED_LENGTH = 2048;
    private static final int CHALLENGE_LENGTH = 64;

    public static PrivateKey generatePrivateKey() {
        byte[] seed = new SecureRandom().generateSeed(SEED_LENGTH);
        EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, parameterSpec);
        return new EdDSAPrivateKey(privateKeySpec);
    }

    public static PublicKey getPublicKey(PrivateKey privateKey) {
        EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");
        EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(privateKey.getEncoded(), parameterSpec);
        return new EdDSAPublicKey(publicKeySpec);
    }

    public static byte[] generateChallenge() {
        return new SecureRandom().generateSeed(CHALLENGE_LENGTH);
    }

    public static String encodeChallenge(byte[] challenge) {
        return Base64.encodeToString(challenge, Base64.NO_WRAP);
    }

    public static byte[] decodeChallege(String challenge) {
        return Base64.decode(challenge, Base64.NO_WRAP);
    }

    public static byte[] getSignature(byte[] message, PrivateKey privateKey) {
        EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");
        try {
            Signature signature = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
            signature.initSign(privateKey);
            signature.update(message);
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifySignature(byte[] message, byte[] signature, PublicKey publicKey) {
        EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");
        try {
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
            sgr.initVerify(publicKey);
            sgr.update(message);
            return sgr.verify(signature);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }
}
