package nl.tudelft.ewi.ds.bankchain.Blockchain;

import android.util.Base64;

import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTools;

/**
 * Created by Richard-HP on 01/06/2017.
 */

public class BlockchainTest {
    @Test
    public void testjson() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Blockchain block = new Blockchain("loc");
        String key =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0" +
                "FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/" +
                "3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZwIDAQAB";

        byte[] data = Base64.decode(key, Base64.DEFAULT);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PublicKey pk = fact.generatePublic(spec);

        block.addKey(pk,"NLABNA0123456789","henk",false);
        block.addKey(pk,"NLTRIO0123456789","Steve",true);
        block.save();
    }
}
