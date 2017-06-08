package nl.tudelft.ewi.ds.bankchain.cryptography;

import android.util.Log;

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

    private ED25519() {
    }

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
        return new EdDSAPublicKey(new EdDSAPublicKeySpec(privateKey.getAbyte(), parameterSpec));
    }

    public static EdDSAPublicKey getPublicKey(String publicKey) {
        return getPublicKey(Utils.hexToBytes(publicKey));
    }

    public static EdDSAPublicKey getPublicKey(byte[] publicKey) {
        return new EdDSAPublicKey(new EdDSAPublicKeySpec(publicKey, parameterSpec));
    }

    public static boolean isValidPublicKey(String publicKey) {
        return isValidPublicKey(getPublicKey(publicKey));
    }

    public static boolean isValidPublicKey(byte[] publicKey) {
        return isValidPublicKey(getPublicKey(publicKey));
    }

    public static boolean isValidPublicKey(EdDSAPublicKey publicKey) {
        try {
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
            // Will throw InvalidKeyException if key is invalid.
            sgr.initVerify(publicKey);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    static byte[] createSignature(byte[] message, PrivateKey privateKey) {
        try {
            Signature signature = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
            signature.initSign(privateKey);
            signature.update(message);
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            Log.e("CRYPTO", e.getMessage());
        }
        return null;
    }
    
    public static boolean verifySignature(byte[] message, byte[] signature, byte[] publicKey) {
        EdDSAPublicKey vk = getPublicKey(publicKey);
        return verifySignature(message, signature, vk);
    }

    public static boolean verifySignature(byte[] message, byte[] signature, PublicKey publicKey) {
        try {
            Signature sgr = new EdDSAEngine(MessageDigest.getInstance(parameterSpec.getHashAlgorithm()));
            sgr.initVerify(publicKey);
            sgr.update(message);
            return sgr.verify(signature);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            Log.e("CRYPTO", e.getMessage());
        }
        return false;
    }
}
