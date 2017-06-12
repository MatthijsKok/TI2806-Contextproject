package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
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

    private ED25519() {}

    private static final int SEED_LENGTH = 32;
    private static final EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");

    public static EdDSAPrivateKey generatePrivateKey() {
        byte[] seed = new SecureRandom().generateSeed(SEED_LENGTH);
        return getPrivateKey(seed);
    }

    public static EdDSAPrivateKey getPrivateKey(String privateKeyString) {
        return getPrivateKey(Utils.hexToBytes(privateKeyString));
    }

    public static EdDSAPrivateKey getPrivateKey(byte[] seed) {
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, parameterSpec);
        return new EdDSAPrivateKey(privateKeySpec);
    }

    public static EdDSAPublicKey getPublicKey(EdDSAPrivateKey privateKey) {
        return getPublicKey(privateKey.getAbyte());
    }

    public static EdDSAPublicKey getPublicKey(String publicKey) {
        return getPublicKey(Utils.hexToBytes(publicKey));
    }

    public static EdDSAPublicKey getPublicKey(byte[] publicKey) {
        try {
            return new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKey, parameterSpec));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidPublicKey(String publicKey) {
        return isValidPublicKey(getPublicKey(publicKey));
    }

    public static boolean isValidPublicKey(byte[] publicKey) {
        return isValidPublicKey(getPublicKey(publicKey));
    }

    public static boolean isValidPublicKey(EdDSAPublicKey publicKey) {
        try {
            Signature sgr = getSignature();
            sgr.initVerify(publicKey); // Will throw InvalidKeyException if key is invalid.
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static byte[] createSignature(byte[] message, PrivateKey privateKey)
            throws InvalidKeyException, SignatureException {
        Signature signature = getSignature();
        if (signature != null) {
            signature.initSign(privateKey);
            signature.update(message);
            return signature.sign();
        } else {
            return new byte[0];
        }
    }
    
    public static boolean verifySignature(byte[] message, byte[] signature, byte[] publicKey) {
        EdDSAPublicKey vk = getPublicKey(publicKey);
        return verifySignature(message, signature, vk);
    }

    public static boolean verifySignature(byte[] message, byte[] signature, PublicKey publicKey) {
        Signature sgr = getSignature();
        try {
            sgr.initVerify(publicKey);
            sgr.update(message);
            return sgr.verify(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private static Signature getSignature() {
        try {
            return new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
