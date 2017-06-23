package nl.tudelft.ewi.ds.bankchain.Blockchain;

import android.util.Base64;
import android.util.Log;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;

import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTransaction;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ED25519.getPublicKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Richard-HP on 01/06/2017.
 */

public class BlockchainTest {
    @Test
    public void testReadWrite() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Blockchain block = new Blockchain("loc", getInstrumentation().getTargetContext(), false);
        byte[] seed = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
        EdDSAPrivateKey privateKey = ED25519.getPrivateKey(seed);
        EdDSAPublicKey pk = getPublicKey(privateKey);
        block.addKey(pk, "NLABNA0123456789", "henk", false);
        block.addKey(pk, "NLTRIO0123456789", "Steve", true);
        String newBlockchain = block.toString();
        block.save();
        Log.d("READING", "Testing read");
        block.openFile();

        assertEquals(block.toString(), newBlockchain);
    }

    @Test
    public void testGetKey() {
        Blockchain block = new Blockchain("loc", getInstrumentation().getTargetContext(), true);
        byte[] key = Utils.hexToBytes("302a300506032b65700321003b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");
        Log.d("result", block.toString());

        // assertEquals(block.getPublicKeyForIBAN("NLTRIO0123456789").getEncoded(),key);
        assertTrue(Arrays.equals(block.getPublicKeyForIBAN("NLTRIO0123456789").getEncoded(), key));
    }

    @Test
    public void testAddKey() {
        Blockchain block = new Blockchain("new", getInstrumentation().getTargetContext(), false);
        byte[] seed = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
        EdDSAPrivateKey privateKey = ED25519.getPrivateKey(seed);
        EdDSAPublicKey pk = getPublicKey(privateKey);

        block.addKey(pk, "NLABNA0123456789", "henk", false);
        assertTrue(block.getPublicKeyForIBAN("NLABNA0123456789") != null);
    }

    @Test
    public void testSetvalidated() {
        Blockchain block = new Blockchain("new", getInstrumentation().getTargetContext(), false);
        byte[] seed = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
        EdDSAPrivateKey privateKey = ED25519.getPrivateKey(seed);
        EdDSAPublicKey pk = getPublicKey(privateKey);

        block.addKey(pk, "NLABNA0123456789", "henk", false);
        block.setIbanVerified(pk, "NLABNA0123456789", "henk");
        assertTrue(block.isValidated("NLABNA0123456789"));
    }

    @Test
    public void testUnknownIban() {
        Blockchain block = new Blockchain("new", getInstrumentation().getTargetContext(), false);
        byte[] seed = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
        EdDSAPrivateKey privateKey = ED25519.getPrivateKey(seed);
        EdDSAPublicKey pk = getPublicKey(privateKey);

        block.addKey(pk, "NLABNA0123456789", "henk", false);
        assertEquals(block.getPublicKeyForIBAN("hello world"), null);
    }

}




