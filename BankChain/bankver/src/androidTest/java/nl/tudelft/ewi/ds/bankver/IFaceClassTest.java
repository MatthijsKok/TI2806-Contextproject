package nl.tudelft.ewi.ds.bankver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.Pair;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.ewi.ds.bankver.cryptography.ED25519;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class IFaceClassTest {

    private Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test
    public void testA() {
        MyChain chain = new MyChain();
        IFaceClass c = new IFaceClass(instrumentationCtx, chain);

        c.setProperty(IFaceClass.SettingProperty.BANK_TYPE, IFaceClass.BankType.BUNQ);
        c.setProperty(IFaceClass.SettingProperty.BUNQ_API_KEY, "55ee97968338182ba528595d05ad9ba3eaf6bcd6f8d1c6e805ba1b29c2d1ba7c");

        String resp = c.handleManualMessage(new IBAN("NL29INGB0006108849"), "CH:12345678:49d97746457147c7c0edfed0be26f7955810b3fe165e81d914d43a3c91c1caef91c6a22d45e3d47cfd4abc448c7fb44ffacf98eee2c76aa70ea74be6d010e00f");
        Log.d("IFACETEST", "Send response: " + resp);

        assertNotNull(resp);


        // Create an online challenge
        c.createOnlineChallenge(new IBAN("NL29INGB0006108849"));


        c.handleOnlineChallenges();
    }

    class MyChain implements Blockchain {
        private final byte[] TEST_SEED1 = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
        private final byte[] TEST_SEED2 = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001");
        private final byte[] TEST_SEED3 = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000002");

        private EdDSAPrivateKey privateKey;

        private List<Pair<IBAN, EdDSAPublicKey>> store;

        MyChain() {
            privateKey = ED25519.getPrivateKey(TEST_SEED1);

            store = new ArrayList<>();

            store.add(new Pair<>(new IBAN("DE89370400440532013000"), ED25519.getPublicKey(ED25519.getPrivateKey(TEST_SEED2))));
            store.add(new Pair<>(new IBAN("NL29INGB0006108849"), ED25519.getPublicKey(ED25519.getPrivateKey(TEST_SEED1))));
        }

        @NonNull
        @Override
        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        @NonNull
        @Override
        public PublicKey getPublicKey() {
            return ED25519.getPublicKey(privateKey);
        }

        @Nullable
        @Override
        public PublicKey getPublicKeyForIBAN(IBAN iban) {
            Log.i("IFACECHAIN", "Get public key for " + iban);

            for (Pair<IBAN, EdDSAPublicKey> p : store) {
                if (p.first.equals(iban)) {
                    return p.second;
                }
            }

            return null;
        }

        @Override
        public void setIbanVerified(PublicKey publicKey, IBAN iban, String legalName) {
            Log.i("IFACECHAIN", "SetIbanVerified" + iban + " name: " + legalName);
        }
    }
}
