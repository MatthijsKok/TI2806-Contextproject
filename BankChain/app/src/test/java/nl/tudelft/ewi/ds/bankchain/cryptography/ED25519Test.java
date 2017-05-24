package nl.tudelft.ewi.ds.bankchain.cryptography;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.charset.Charset;
import java.util.Arrays;

import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.createSignature;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.generatePrivateKey;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.getPublicKey;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.verifySignature;
import static org.junit.Assert.assertTrue;

public class ED25519Test {

    private static final int SEED_LENGTH = 32;
    private static final byte[] TEST_SEED = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
    private static final byte[] TEST_PK = Utils.hexToBytes("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");
    private static final byte[] TEST_VK = Utils.hexToBytes("302a300506032b65700321003b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");
    private static final byte[] TEST_MSG = "This is a secret message".getBytes(Charset.forName("UTF-8"));
    private static final byte[] TEST_MSG_SIG = Utils.hexToBytes("94825896c7075c31bcb81f06dba2bdcd9dcf16e79288d4b9f87c248215c8468d475f429f3d"
            + "e3b4a2cf67fe17077ae19686020364d6d4fa7a0174bab4a123ba0f");

    private static final EdDSAParameterSpec parameterSpec = EdDSANamedCurveTable.getByName("Ed25519");

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void generatePrivateKeyTest() throws Exception {
        EdDSAPrivateKey pk = ED25519.generatePrivateKey(TEST_SEED);
        assertTrue(Arrays.equals(pk.getAbyte(), TEST_PK));
    }

    @Test
    public void generatePrivateKeyWrongSeedLengthTest() throws Exception {
        exception.expect(IllegalArgumentException.class);
        generatePrivateKey(new byte[]{0, 0, 0});
    }

    @Test
    public void getPublicKeyTest() throws Exception {
        EdDSAPrivateKey privateKey = generatePrivateKey(TEST_SEED);
        EdDSAPublicKey publicKey = getPublicKey(privateKey);

        assertTrue(Arrays.equals(publicKey.getEncoded(), TEST_VK));
    }

    @Test
    public void generatePrivateKeyEmptySeedTest() throws Exception {
        exception.expect(IllegalArgumentException.class);
        generatePrivateKey(new byte[]{});
    }

    @Test
    public void createSignatureTest() throws Exception {
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(TEST_SEED, parameterSpec);
        EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);
        byte[] signature = createSignature(TEST_MSG, privateKey);
        assertTrue(Arrays.equals(signature, TEST_MSG_SIG));
    }

    @Test
    public void verifySignatureTest() throws Exception {
        EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(TEST_PK, parameterSpec);
        EdDSAPublicKey publicKey = new EdDSAPublicKey(publicKeySpec);

        assertTrue(verifySignature(TEST_MSG, TEST_MSG_SIG, publicKey));
    }
}
