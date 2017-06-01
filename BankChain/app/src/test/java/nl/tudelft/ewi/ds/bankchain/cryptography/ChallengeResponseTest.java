package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;

import org.junit.Test;

import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.createChallenge;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.createResponse;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidChallenge;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidDescriptionFormat;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidResponse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChallengeResponseTest {

    private static final byte[] TEST_SEED = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
    private static final byte[] TEST_VK = Utils.hexToBytes("302a300506032b65700321003b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");
    private static final EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");

    @Test
    public void isValidChallengeTest() throws Exception {
        PublicKey publicKey = new EdDSAPublicKey(new X509EncodedKeySpec(TEST_VK));

        assertFalse(isValidChallenge("", publicKey));
        assertFalse(isValidChallenge("CH:12345678:1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678", publicKey));
        assertFalse(isValidChallenge("RE:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb44ffacf" +
                "98eee2c76aa70ea74be6d010e00f", publicKey));
        assertTrue(isValidChallenge("CH:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb44ffacf9" +
                "8eee2c76aa70ea74be6d010e00f", publicKey));
    }

    @Test
    public void createChallengeTest() throws Exception {
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(TEST_SEED, parameterSpec);
        EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(TEST_VK);
        PublicKey publicKey = new EdDSAPublicKey(x509);
        String challenge = createChallenge(privateKey);
        assertTrue(isValidChallenge(challenge, publicKey));
    }

    @Test
    public void isValidResponseTest() throws Exception {
        PublicKey publicKey = new EdDSAPublicKey(new X509EncodedKeySpec(TEST_VK));

        assertFalse(isValidResponse("", publicKey));
        assertFalse(isValidResponse("RE:12345678:123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678", publicKey));
        assertFalse(isValidResponse("CH:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb44ffacf" +
                "98eee2c76aa70ea74be6d010e00f", publicKey));
        assertTrue(isValidResponse("RE:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb44ffacf" +
                "98eee2c76aa70ea74be6d010e00f", publicKey));
    }

    @Test
    public void createResponseTest() throws Exception {
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(TEST_SEED, parameterSpec);
        EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);
        String response = createResponse("CH:12345678:bla", privateKey);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(TEST_VK);
        PublicKey publicKey = new EdDSAPublicKey(x509);
        assertTrue(isValidResponse(response, publicKey));
    }

    @Test
    public void isValidDescriptionFormatTest() throws Exception {
        assertFalse(isValidDescriptionFormat(""));
        assertFalse(isValidDescriptionFormat("CH:::::::"));
        assertFalse(isValidDescriptionFormat("FU:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7" +
                "fb44ffacf98eee2c76aa70ea74be6d010e00f"));
        assertFalse(isValidDescriptionFormat("RE:12345678W:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c" +
                "7fb44ffacf98eee2c76aa70ea74be6d010e00f"));
        assertFalse(isValidDescriptionFormat("RE:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7f" +
                "b44ffacf98eee2c76aa70ea74be6d010e00fW"));
        assertTrue(isValidDescriptionFormat("RE:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb" +
                "44ffacf98eee2c76aa70ea74be6d010e00f"));
        assertTrue(isValidDescriptionFormat("CH:00000000:000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000"));
    }

}